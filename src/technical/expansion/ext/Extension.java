package technical.expansion.ext;

import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.consumers.ConsumeCoolant;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatUnit;
import technical.T;
import technical.content.TIcons;
import technical.expansion.TBlock;
import technical.expansion.ext.Extendable.ExtendableBuild;
import technical.expansion.kinetic.KineticBlock;
import technical.expansion.tech.TechStat;

public class Extension extends KineticBlock 
{
    public DrawBlock drawer = new DrawDefault();
    public int efficiencyBoost = 0;
    public int additionalStorage = 0;
    public float additionalLiquidStorage = 0;

    public Effect consumeEffect = Fx.none;
    public float generateEffectRange = 4f;
    public float itemDuration = 60f;

    public float warmupSpeed = 0.01f;

    public ExtensionType type = ExtensionType.Chimney;

    public Extension(String name) 
    {
        super(name);
        update = true;
        
        hasItems = true;
        itemCapacity = 30;

        solid = true;

        hasLiquids = true;
        liquidCapacity = 10;
    }

    @Override
    public void setBars()
    {
        super.setBars();

        boolean conliq = false;
        for(var con : consumers)
            if(con instanceof ConsumeLiquid || con instanceof ConsumeCoolant)
                conliq = true;

        if (!conliq)
            removeBar("liquid");
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.productionTime, itemDuration() / 60f, StatUnit.seconds);

        StatCat extensionCat = new StatCat("extension-stats");

        Stat extensionType = new Stat("extension-type", extensionCat);
        Stat additionalStorageStat = new Stat("additional-storage", extensionCat);
        Stat additionalLiquidStorageStat = new Stat("additional-liquid-storage", extensionCat);
        Stat boostEffectStat = new Stat("boost-effect", extensionCat);

        // Add formatted stats
        stats.add(extensionType, table -> {
            table.add("[accent]" + T.bundle(type) + "[]");
        });

        stats.add(additionalStorageStat, table -> {
            table.add("+" + additionalStorage + " units");
        });

        stats.add(additionalLiquidStorageStat, table -> {
            table.add("+" + additionalStorage + " units");
        });

        stats.add(boostEffectStat, table -> {
            table.add("+" + efficiencyBoost + TIcons.get(TIcons.boostPowerIcon)).color(Pal.accent);
        });
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


    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    public float itemDuration()
    {
        return itemDuration * getTotalStat(TechStat.itemDuration);
    }

    public class ExtensionBuild extends KineticBuild 
    {
        public ExtendableBuild Extendable;
        float generateTime = 0f;
        float totalProgress = 0f;
        float warmup;

        @Override
        public void updateTile() 
        {
            super.updateTile();

            if (Extendable == null || !Extendable.isValid())
                findExtendable();

            if (block.consumers.length > 0 && Extendable != null)
            {
                if(efficiency > 0 && generateTime <= 0.1f && Extendable.hasRequiredExtensions())
                {
                    if (!chance(TechStat.materialSaveChance)) consume();
                    consumeEffect.at(x + Mathf.range(generateEffectRange), y + Mathf.range(generateEffectRange));
                    generateTime = 1f;
                }

                generateTime -= delta() / itemDuration();
                generateTime = Mathf.clamp(generateTime, 0f, 1f);
            }

            if (Extendable != null && Extendable.efficiency > 0 && Extendable.hasRequiredExtensions() && (efficiency > 0 || block.consumers.length == 0))
            {
                warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);
            }
            else
            {
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            propagateHeat();
            
            totalProgress += warmup() * delta();
        }

        @Override
        public float workingTemperature()
        {
            if (Extendable == null) return 0f;

            return Extendable.workingTemperature();
        }

        void findExtendable()
        {
            for (Building other : proximity)
            {
                if (other != null && other instanceof ExtendableBuild ext) 
                {
                    if (((Extendable)ext.block).AllowedExtensions.contains(type))
                    {
                        Extendable = ext;
                        ext.connectedExtensions.add(this);

                        return;
                    }
                }
            }
            
            Extendable = null;
        }

        @Override
        public void onRemoved()
        {
            if (Extendable != null)
            {
                Extendable.connectedExtensions.remove(this);
                Extendable.removeExtension(this);
            }
        }


        @Override
        public void drawSelect()
        {
            super.drawSelect();

            if (Extendable == null) return;
            
            T.outline(Extendable);
            for (var ext : Extendable.connectedExtensions)
            {
                T.outline(ext);
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item)
        {
            return (Extendable != null && Extendable.acceptItem(source, item)) || super.acceptItem(source, item);
        }

        @Override
        public void handleItem(Building source, Item item)
        {
            if (super.acceptItem(source, item))
            {
                items.add(item, 1);
            }
            else if (Extendable != null && Extendable.acceptItem(source, item))
            {
                Extendable.handleItem(source, item);
            }
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid)
        {
            return (Extendable != null && Extendable.acceptLiquid(source, liquid)) || super.acceptLiquid(source, liquid);
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount)
        {
            if (super.acceptLiquid(source, liquid))
            {
                liquids.add(liquid, amount);
            }
            else if (Extendable != null)
            {
                Extendable.handleLiquid(source, liquid, amount);
            }
        }

        @Override
        public float totalProgress()
        {
            return totalProgress;
        }

        @Override
        public float warmup()
        {
            return warmup;
        }


        public float warmupTarget()
        {
            if (Extendable == null) return 0f;

            return Extendable.efficiencyScale() * efficiency;
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
        
        @Override
        public void write(Writes write){
            super.write(write);

            write.f(generateTime);
            write.f(totalProgress);
            write.f(warmup);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            generateTime = read.f();
            totalProgress = read.f();
            warmup = read.f();
        }
    }
}