package technical.core.extendable;

import static mindustry.Vars.content;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectIntMap;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Puddles;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatUnit;
import technical.content.TIcons;
import technical.content.TLiquids;
import technical.core.extendable.Extension.ExtensionBuild;
import technical.core.kinetic.KineticBlock;
import technical.core.tech.TechStat;
import technical.util.TBundle;
import technical.util.TCol;
import technical.util.TDraw;

public class Extendable extends Explodable
{
    public DrawBlock drawer = new DrawDefault();

    public float efficiencyCap = 100f;
    public float maxEfficiency = 2f;
    public int maxThermalDelta = 50;
    public float multiblockBonus = 0;

    public float maxUnstability = 100000f;
    public boolean isUnstable = false;

    public ObjectIntMap<ExtensionType> RequiredExtensions = new ObjectIntMap<>();
    public Seq<ExtensionType> AllowedExtensions = new Seq<>();

    public Extendable(String name) {
        super(name);
        update = true;
        solid = true;

        hasItems = true;
        itemCapacity = 30;

        hasLiquids = true;
        liquidCapacity = 10;
    }

    @Override
    public void init()
    {
        super.init();

        if (isUnstable)
            canExplode = true;
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out){
        drawer.getRegionsToOutline(this, out);
    }

    public float maxEfficiency(){
        return maxEfficiency * getTotalStat(TechStat.maxEfficiency);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("efficiency", (ExtendableBuild build) ->
            new Bar(
                () -> Core.bundle.format("bar.efficiency", (int)(build.efficiencyScale() * 100)),
                () -> Pal.lightOrange,
                () -> build.efficiencyScale() / maxEfficiency()
            )
        );

        if (isUnstable) 
        {
            addBar("unstability", (ExtendableBuild build) ->
                new Bar(
                    () -> Core.bundle.format("bar.unstability"),
                    () -> Color.scarlet,
                    () -> build.Unstability / maxUnstability
                )
            );
        }
    }


    @Override
    public void addLiquidBar(Liquid liq){
        addBar("liquid-" + liq.name, entity -> !liq.unlockedNow() ? null : new Bar(
            () -> liq.localizedName,
            liq::barColor,
            () -> entity.liquids.get(liq) / ((ExtendableBuild)entity).getMaximumLiquidAccepted(liq)
        ));
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.maxEfficiency, maxEfficiency() * 100, StatUnit.percent);

        StatCat extensionCat = new StatCat("extension-stats");

        Stat allowedExtensions = new Stat("allowed-extensions", extensionCat);
        Stat requiredExtensions = new Stat("required-extensions", extensionCat);

        stats.add(allowedExtensions, table -> {
            table.defaults().padLeft(60).left();
            if (AllowedExtensions.isEmpty()) {
                table.row().add("None").color(Color.gray);
            } else {
                for (ExtensionType ext : AllowedExtensions) {
                    table.row();
                    table.add(ext.name()).color(TCol.highlight);
                }
            }
        });

        stats.add(requiredExtensions, table -> {
            table.defaults().padLeft(60).left();
            if (RequiredExtensions.isEmpty()) {
                table.row().add("None").color(Color.gray);
            } else {
                for (var ext : RequiredExtensions) {
                    table.row();
                    
                    String name = TBundle.color(ext.key.name() + ": ", TCol.highlight) + ext.value + TIcons.get(TIcons.boostPowerIcon);
                    table.add(name);
                }
            }
        });
    }

    public class ExtendableBuild extends ExplodableBuild
    {
        public Seq<ExtensionBuild> connectedExtensions = new Seq<>();
        public float Unstability = 0f;

        @Override
        public void updateTile()
        {
            super.updateTile();

            if (hasRequiredExtensions() && efficiency > 0f) 
            {
                int thermalDelta = currentThermalDelta();
                if (Math.abs(thermalDelta) > maxThermalDelta()) 
                {
                    Unstability = Mathf.approachDelta(Unstability, maxUnstability, (Math.abs(thermalDelta) - maxThermalDelta));
                } 
                else 
                {
                    Unstability = Mathf.approachDelta(Unstability, 0f, (maxThermalDelta() - Math.abs(thermalDelta)));
                }
            }

            propagateHeat();
        }

        @Override
        public boolean shouldExplode()
        {
            return Unstability >= maxUnstability && isUnstable;
        }

        public ObjectIntMap<ExtensionType> getMissingExtensions() 
        {
            ObjectIntMap<ExtensionType> req = new ObjectIntMap<>();
            for (ObjectIntMap.Entry<ExtensionType> e : RequiredExtensions)
                req.put(e.key, e.value);

            for (ExtensionBuild ext : connectedExtensions) {
                Extension e = (Extension)ext.block;
                int required = req.get(e.type, 0);
                int used = Math.min(e.efficiencyBoost, required);
                int remaining = required - used;
                if (remaining > 0)
                    req.put(e.type, remaining);
                else
                    req.remove(e.type);
            }

            return req;
        }

        public void removeExtension(ExtensionBuild ext)
        {
            Extension t = (Extension)ext.block;
            if (t.additionalStorage > 0)
            {
                for (Item item : content.items())
                {
                    if (items.get(item) > 0)
                    {
                        int amount = Math.min(getMaximumAccepted(item), items.get(item));
                        items.remove(item, items.get(item) - amount);
                    }
                }
            }

            if (t.additionalStorage > 0)
            {
                for (Liquid liq : content.liquids())
                {
                    if (liquids.get(liq) > 0)
                    {
                        float amount = Math.min(getMaximumLiquidAccepted(liq), liquids.get(liq));
                        liquids.remove(liq, liquids.get(liq) - amount);
                    }
                }
            }
        }

        public float maxThermalDelta() {
            return maxThermalDelta * getTotalStat(TechStat.maxThermalDelta);
        }

        @Override
        public void display(Table table) 
        {
            super.display(table);

            if (this.team != Vars.player.team()) return;

            ObjectIntMap<ExtensionType> missing = getMissingExtensions();
            if (!missing.isEmpty()) {
                table.row();
                table.table(t -> {
                    t.left();
                    t.label(() -> TBundle.error("missing-extensions")).color(Pal.accent).left().wrap();
                    t.row();
                    for (ObjectIntMap.Entry<ExtensionType> e : missing) 
                    {
                        ExtensionType type = e.key;
                        int count = e.value;

                        t.label(() -> TBundle.color(TBundle.get_enum(type), Color.lightGray) + ": " + TBundle.color(count + TIcons.get(TIcons.boostPowerIcon), TCol.error)).wrap().left().padLeft(20f);
                        t.row();
                    }
                }).growX();
            }

            if (isUnstable) 
            {
                float delta = currentThermalDelta();

                if (Math.abs(delta) > maxThermalDelta()) {
                    table.row();
                    table.table(t -> {
                        t.left();
                        t.label(() -> TBundle.error("unstability-warning")).color(Pal.accent).left().wrap();
                        t.row();

                        if (delta > 0)
                            t.label(() -> TCol.str(TCol.error) + TBundle.error("heating") + (delta - maxThermalDelta()) + TIcons.get(TIcons.boostPowerIcon)).wrap().left().padLeft(20f);
                        else
                            t.label(() -> TCol.str(TCol.error) + TBundle.error("cooling") + (-delta - maxThermalDelta()) + TIcons.get(TIcons.boostPowerIcon)).wrap().left().padLeft(20f);

                        t.row();
                    }).growX();
                }
            }
        }

        public boolean hasRequiredExtensions()
        {
            ObjectIntMap<ExtensionType> map = new ObjectIntMap<>();

            for (ExtensionBuild ext : connectedExtensions)
            {
                if (ext.efficiency > 0)
                {
                    var e = ((Extension)ext.block);
                    map.put(e.type, map.get(e.type, 0) + e.efficiencyBoost);
                }
            }

            for (var req : RequiredExtensions)
            {
                if (map.get(req.key, 0) < req.value)
                {
                    return false;
                }
            }

            return true;
        }

        @Override
        public void drawSelect()
        {
            super.drawSelect();
            
            if (connectedExtensions.isEmpty()) return;
            
            TDraw.highlight(this);
            for (var ext : connectedExtensions)
            {
                TDraw.highlight(ext);
            }
        }

        public int currentThermalDelta()
        {
            ObjectIntMap<ExtensionType> req = new ObjectIntMap<>();
            for (ObjectIntMap.Entry<ExtensionType> e : RequiredExtensions)
                req.put(e.key, e.value);

            int thermalDelta = 0;
            for (ExtensionBuild ext : connectedExtensions)
            {
                if (ext.efficiency <= 0) continue;
                Extension e = (Extension)ext.block;
                if (e.type != ExtensionType.Heater && e.type != ExtensionType.Cooler) continue;

                int required = req.get(e.type, 0);
                int used = Math.min(e.efficiencyBoost, required);
                int extra = e.efficiencyBoost - used;
                int remaining = required - used;

                thermalDelta += extra * (e.type == ExtensionType.Heater ? 1 : -1);

                if (remaining > 0)
                    req.put(e.type, remaining);
                else
                    req.remove(e.type);
            }

            return thermalDelta;
        }

        public float efficiencyCap()
        {
            return efficiencyCap;
        }

        @Override
        public float efficiencyScale()
        {
            ObjectIntMap<ExtensionType> req = new ObjectIntMap<>();
            for (ObjectIntMap.Entry<ExtensionType> e : RequiredExtensions)
                req.put(e.key, e.value);

            float multiblockEff = 0;
            for (var b : proximity)
            {
                if (b.block == block)
                {
                    if (b.warmup() >= 1f - 0.001f)
                    {
                        multiblockEff += multiblockBonus;
                    }
                }
            }

            int eff = 0;
            for (ExtensionBuild ext : connectedExtensions)
            {
                if (ext.efficiency <= 0) continue;
                Extension e = (Extension)ext.block;

                int required = req.get(e.type, 0);
                int used = Math.min(e.efficiencyBoost, required);
                int extra = e.efficiencyBoost - used;

                eff += extra;
                int remaining = required - used;

                if (remaining > 0)
                    req.put(e.type, remaining);
                else
                    req.remove(e.type);
            }

            if (!req.isEmpty()) return 0;

            float final_eff = Mathf.clamp(1 + (eff / efficiencyCap() * getTotalStat(TechStat.efficiencyCap)) + multiblockEff * getTotalStat(TechStat.multiblockEfficiency), 0f, maxEfficiency());

            return super.efficiencyScale() * final_eff;
        }

        @Override
        public float getProgressIncrease(float baseTime)
        {
            return super.getProgressIncrease(baseTime) * efficiencyScale() * getTotalStat(TechStat.speed);
        }

        public boolean acceptLiquid(Building source, Liquid liquid) {
            return this.block.hasLiquids && this.block.consumesLiquid(liquid) && getMaximumLiquidAccepted(liquid) > liquids.get(liquid);
        }

        public float getMaximumLiquidAccepted(Liquid liquid)
        {
            float additional = 0;
            for (ExtensionBuild ext : connectedExtensions) 
            {
                if (ext.efficiency > 0)
                    additional += ((Extension)ext.block).additionalLiquidStorage;
            }

            return (liquidCapacity + additional) * getTotalStat(TechStat.liquidCapacity);
        }

        @Override
        public int getMaximumAccepted(Item item)
        {
            int additional = 0;
            for (ExtensionBuild ext : connectedExtensions) 
            {
                if (ext.efficiency > 0)
                    additional += ((Extension)ext.block).additionalStorage;
            }

            return (int)((itemCapacity + additional) * getTotalStat(TechStat.itemCapacity));
        }

        @Override
        public void draw(){
            drawer.draw(this);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }
    }
}