package technical.core.extendable;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

import arc.Core;
import arc.func.Cons;
import arc.func.Intc2;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.EnumSet;
import arc.util.Eachable;
import arc.util.Nullable;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import technical.content.TAttributes;
import technical.content.TFx;
import technical.content.TItems;
import technical.core.tech.TechStat;

public class ExtendableWallCrafter extends Extendable
{
    static int idx = 0;

    public TextureRegion topRegion;
    public TextureRegion rotatorBottomRegion;
    public TextureRegion rotatorRegion;

    /** Time to produce one item at 100% efficiency. */
    public float drillTime = 150f;
    /** How many times faster the drill will progress when boosted by liquid. */
    public float liquidBoostIntensity = 1.6f;
    /** Effect randomly played while drilling. */
    public Effect updateEffect = Fx.mineWallSmall;
    public float updateEffectChance = 0.02f;
    public float rotateSpeed = 2f;
    /** Attribute to check for wall output. */
    public Attribute attribute = TAttributes.stone;

    public Item output = TItems.stone;

    /** How many times faster the drill will progress when boosted by items. Note: Using item and liquid boosters at once is not supported. */
    // public float itemBoostIntensity = 1.6f;
    public @Nullable Consume itemConsumer;
    public boolean hasLiquidBooster;

    public final int timerUse = timers++;

    public Effect consumeEffect = TFx.coalSmelt;
    public float itemDuration = 120f;

    public ExtendableWallCrafter(String name) 
    {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        rotate = true;
        regionRotated1 = 1;

        group = BlockGroup.drills;

        ambientSound = Sounds.loopDrill;
        ambientSoundVolume = 0.018f;
        
        // envEnabled |= Env.space;
        flags = EnumSet.of(BlockFlag.drill);
    }

    @Override
    public void setBars(){
        super.setBars();

        // addBar("drillspeed", (ExtendableWallCrafterBuild e) ->
        //     new Bar(() -> Core.bundle.format("bar.drillspeed", Strings.fixed(e.lastEfficiency * 60 / drillTime, 2)), () -> Pal.ammo, () -> e.warmup));
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.output, output);
        stats.add(Stat.tiles, StatValues.blocks(attribute, floating, 1f, true, false));
        stats.add(Stat.drillSpeed, 60f / drillTime * size, StatUnit.itemsSecond);

        // boolean consItems = itemConsumer != null;

        // if(consItems) stats.timePeriod = boostItemUseTime;

        // if(consItems && itemConsumer instanceof ConsumeItems coni){
        //     stats.remove(Stat.booster);
        //     stats.add(Stat.booster, StatValues.itemBoosters("{0}" + StatUnit.timesSpeed.localized(), stats.timePeriod, itemBoostIntensity, 0f, coni.items));
        // }

        if(liquidBoostIntensity != 1 && findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase){
            stats.remove(Stat.booster);
            stats.add(Stat.booster,
                StatValues.speedBoosters("{0}" + StatUnit.timesSpeed.localized(),
                consBase.amount,
                liquidBoostIntensity, false, consBase::consumes)
            );
        }
    }

    @Override
    public void init(){
        super.init();

        hasLiquidBooster = findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) != null;
        itemConsumer = findConsumer(f -> f instanceof ConsumeItems);
    }

    @Override
    public void load()
    {
        super.load();
        // region = Core.atlas.find(name);
        topRegion = Core.atlas.find(name + "-top");
        rotatorRegion = Core.atlas.find(name + "-rotator");
        rotatorBottomRegion = Core.atlas.find(name + "-rotator-bottom");
    }

    @Override
    public boolean outputsItems(){
        return true;
    }

    @Override
    public boolean rotatedOutput(int x, int y){
        return false;
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, topRegion};
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(topRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        float eff = getRawBlockEfficiency(x, y, rotation, null, null);

        drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f / drillTime * eff, 2), x, y, valid);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return getRawBlockEfficiency(tile.x, tile.y, rotation, null, null) > 0;
    }

    float getRawBlockEfficiency(int tx, int ty, int rotation, @Nullable Cons<Tile> ctile, @Nullable Intc2 cpos){
        float eff = 0f;
        int cornerX = tx - (size-1)/2, cornerY = ty - (size-1)/2, s = size;

        for(int i = 0; i < size; i++){
            int rx = 0, ry = 0;

            switch(rotation){
                case 0 -> {
                    rx = cornerX + s;
                    ry = cornerY + i;
                }
                case 1 -> {
                    rx = cornerX + i;
                    ry = cornerY + s;
                }
                case 2 -> {
                    rx = cornerX - 1;
                    ry = cornerY + i;
                }
                case 3 -> {
                    rx = cornerX + i;
                    ry = cornerY - 1;
                }
            }

            if(cpos != null){
                cpos.get(rx, ry);
            }

            Tile other = world.tile(rx, ry);
            if(other != null && other.solid()){
                float at = other.block().attributes.get(attribute);
                eff += at;
                if(at > 0 && ctile != null){
                    ctile.get(other);
                }
            }
        }
        return eff;
    }

    public float itemDuration()
    {
        return itemDuration * getTotalStat(TechStat.itemDuration);
    }

    public class ExtendableWallCrafterBuild extends ExtendableBuild
    {
        public float time, warmup, totalTime, lastEfficiency;//, consumeTime;

        @Override
        public void updateTile()
        {
            super.updateTile();

            boolean cons = shouldConsume();
            boolean itemValid = itemConsumer != null && itemConsumer.efficiency(this) > 0;

            warmup = Mathf.approachDelta(warmup, Mathf.num(efficiency > 0 && hasRequiredExtensions()), 1f / 40f);
            float dx = Geometry.d4x(rotation) * 0.5f, dy = Geometry.d4y(rotation) * 0.5f;

            float eff = getRawBlockEfficiency(tile.x, tile.y, rotation, dest -> {
                if(wasVisible && cons && Mathf.chanceDelta(updateEffectChance * warmup)){
                    updateEffect.at(
                        dest.worldx() + Mathf.range(3f) - dx * tilesize,
                        dest.worldy() + Mathf.range(3f) - dy * tilesize,
                        dest.block().mapColor
                    );
                }
            }, null) * Mathf.lerp(1f, liquidBoostIntensity, hasLiquidBooster ? optionalEfficiency : 0f) * efficiencyScale();

            if(itemValid && eff * efficiency > 0 && timer(timerUse, itemDuration())){
                if (!chance(TechStat.materialSaveChance)) consume();
                consumeEffect.at(x, y);
            }

            lastEfficiency = eff * timeScale * efficiency;

            if(cons && (time += edelta() * eff) >= drillTime){
                offload(output);
                time %= drillTime;
            }

            totalTime += edelta() * warmup * (eff <= 0f ? 0f : 1f);

            if(timer(timerDump, dumpTime / timeScale)){
                dump(output);
            }
        }

        @Override
        public boolean shouldConsume(){
            return super.shouldConsume() && items.get(output) < itemCapacity && hasRequiredExtensions();
        }

        @Override
        public void draw()
        {
            Draw.rect(block.region, x, y);
            Draw.rect(topRegion, x, y, rotdeg());
            float ds = 0.6f, dx = Geometry.d4x(rotation) * ds, dy = Geometry.d4y(rotation) * ds;

            int bs = (rotation == 0 || rotation == 3) ? 1 : -1;
            idx = 0;
            getRawBlockEfficiency(tile.x, tile.y, rotation, null, (cx, cy) -> {
                int sign = idx++ >= size/2 && size % 2 == 0 ? -1 : 1;
                float vx = (cx - dx) * tilesize, vy = (cy - dy) * tilesize;
                Draw.z(Layer.blockOver);
                Draw.rect(rotatorBottomRegion, vx, vy, totalTime * rotateSpeed * sign * bs);
                Draw.rect(rotatorRegion, vx, vy);
            });
        }
    }
}
