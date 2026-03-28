package technical.expansion.ext;

import static mindustry.Vars.iconSmall;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.EnumSet;
import arc.struct.ObjectIntMap;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import technical.expansion.tech.TechStat;

public class ExtendableDrill extends Extendable 
{
    protected final ObjectIntMap<Item> oreCount = new ObjectIntMap<>();
    protected final Seq<Item> itemArray = new Seq<>();
    protected @Nullable Item returnItem;
    protected int returnCount;

    public int tier;

    public float drillTime = 60f;
    public float hardnessDrillMultiplier = 50f;
    /** How many times faster the drill will progress when boosted by liquid. */
    public float liquidBoostIntensity = 1.6f;
    /** Speed at which the drill speeds up. */
    public float warmupSpeed = 0.015f;

    public Effect drillEffect = Fx.mine;
    /** Drill effect randomness. Block size by default. */
    public float drillEffectRnd = -1f;
    /** Chance of displaying the effect. Useful for extremely fast drills. */
    public float drillEffectChance = 0.02f;

    public Effect consumeEffect = Fx.none;
    public float itemDuration = 60f;

    /** Effect randomly played while drilling. */
    public Effect updateEffect = Fx.pulverizeSmall;
    /** Chance the update effect will appear. */
    public float updateEffectChance = 0.02f;

    public ExtendableDrill(String name) 
    {
        super(name);
        update = true;
        solid = true;
        group = BlockGroup.drills;

        ambientSound = Sounds.loopDrill;
        ambientSoundVolume = 0.018f;
        
        // envEnabled |= Env.space;
        flags = EnumSet.of(BlockFlag.drill);
    }
    
    // @Override
    // public void setBars(){
    //     super.setBars();

    //     addBar("drillspeed", (DrillBuild e) ->
    //          new Bar(() -> Core.bundle.format("bar.drillspeed", Strings.fixed(e.lastDrillSpeed * 60 * e.timeScale(), 2)), () -> Pal.ammo, () -> e.warmup));
    // }

    public Item getDrop(Tile tile){
        return tile.drop();
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        if(isMultiblock())
        {
            for(Tile other : tile.getLinkedTilesAs(this, tempTiles))
            {
                if(canMine(other)){
                    return true;
                }
            }
            return false;
        }
        else
        {
            return canMine(tile);
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Tile tile = world.tile(x, y);
        if(tile == null) return;

        countOre(tile);

        if(returnItem != null){
            float width = drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f / getDrillTime(returnItem) * returnCount, 2), x, y, valid);
            float dx = x * tilesize + offset - width/2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5, s = iconSmall / 4f;
            Draw.mixcol(Color.darkGray, 1f);
            Draw.rect(returnItem.fullIcon, dx, dy - 1, s, s);
            Draw.reset();
            Draw.rect(returnItem.fullIcon, dx, dy, s, s);

            // if(drawMineItem){
            //     Draw.color(returnItem.color);
            //     Draw.rect(itemRegion, tile.worldx() + offset, tile.worldy() + offset);
            //     Draw.color();
            // }
        }else{
            Tile to = tile.getLinkedTilesAs(this, tempTiles).find(t -> t.drop() != null && t.drop().hardness > tier);// || (blockedItems != null && blockedItems.contains(t.drop())));
            Item item = to == null ? null : to.drop();
            if(item != null){
                drawPlaceText(Core.bundle.get("bar.drilltierreq"), x, y, valid);
            }
        }
    }

    public float getDrillTime(Item item){
        return (drillTime + hardnessDrillMultiplier * item.hardness);// / drillMultipliers.get(item, 1f);
    }

    @Override
    public void setStats(){
        super.setStats();

        // stats.add(Stat.drillTier, StatValues.drillables(drillTime, hardnessDrillMultiplier, size * size, drillMultipliers, b -> b instanceof Floor f && !f.wallOre && f.itemDrop != null &&
        //     f.itemDrop.hardness <= tier && (blockedItems == null || !blockedItems.contains(f.itemDrop)) && (indexer.isBlockPresent(f) || state.isMenu())));

        stats.add(Stat.drillSpeed, 60f / drillTime * size * size, StatUnit.itemsSecond);

        if(liquidBoostIntensity != 1 && findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase){
            stats.remove(Stat.booster);
            stats.add(Stat.booster,
                StatValues.speedBoosters("{0}" + StatUnit.timesSpeed.localized(),
                consBase.amount,
                liquidBoostIntensity * liquidBoostIntensity, false, consBase::consumes)
            );
        }
    }

    protected void countOre(Tile tile){
        returnItem = null;
        returnCount = 0;

        oreCount.clear();
        itemArray.clear();

        for(Tile other : tile.getLinkedTilesAs(this, tempTiles)){
            if(canMine(other)){
                oreCount.increment(getDrop(other), 0, 1);
            }
        }

        for(Item item : oreCount.keys()){
            itemArray.add(item);
        }

        itemArray.sort((item1, item2) -> {
            int type = Boolean.compare(!item1.lowPriority, !item2.lowPriority);
            if(type != 0) return type;
            int amounts = Integer.compare(oreCount.get(item1, 0), oreCount.get(item2, 0));
            if(amounts != 0) return amounts;
            return Integer.compare(item1.id, item2.id);
        });

        if(itemArray.size == 0){
            return;
        }

        returnItem = itemArray.peek();
        returnCount = oreCount.get(itemArray.peek(), 0);
    }

    public boolean canMine(Tile tile){
        if(tile == null || tile.block().isStatic()) return false;
        Item drops = tile.drop();
        return drops != null && drops.hardness <= tier;// && (blockedItems == null || !blockedItems.contains(drops));
    }

    public class ExtendableDrillBuild extends ExtendableBuild
    {
        public float progress;
        public float warmup;

        public int dominantItems;
        public Item dominantItem;

        public float totalProgress;
        public float consumeTime;

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public boolean shouldConsume()
        {
            return enabled && dominantItem != null && hasRequiredExtensions();
        }

        @Override
        public boolean shouldAmbientSound(){
            return enabled && efficiencyScale() > 0f && efficiency > 0f;
        }

        @Override
        public float ambientVolume(){
            return efficiency * (size * size) / 4f;
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            drawItemSelection(dominantItem);
        }

        @Override
        public void pickedUp(){
            dominantItem = null;
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            countOre(tile);
            dominantItem = returnItem;
            dominantItems = returnCount;
        }

        @Override
        public Object senseObject(LAccess sensor){
            if(sensor == LAccess.firstItem) return dominantItem;
            return super.senseObject(sensor);
        }

        public float itemDuration()
        {
            return itemDuration * getTotalStat(TechStat.itemDuration);
        }

        @Override
        public void updateTile()
        {
            super.updateTile();

            consumeTime = Math.max(0f, consumeTime - delta());

            boolean consumesDrills = false;
            for(var cons : consumers){
                if(cons instanceof ConsumeItems ci){
                    for(var stack : ci.items){
                        if(stack.item == dominantItem){
                            consumesDrills = true;
                            break;
                        }
                    }
                }
            }

            if(dominantItem == null || dominantItems <= 0 || efficiency <= 0 || !hasRequiredExtensions())
            {
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                return;
            }

            if (consumeTime <= 0f) 
            {
                consumeTime = itemDuration();
                consumeEffect.at(x, y);

                if (!chance(TechStat.materialSaveChance))
                    consume();
            }

            if(timer(timerDump, dumpTime / timeScale) && items.has(dominantItem) && !consumesDrills)
            {
                dump(dominantItem);
            }

            totalProgress += warmup * delta();

            float delay = getDrillTime(dominantItem);
            float speed = efficiency * efficiencyScale() * getTotalStat(TechStat.speed);

            warmup = Mathf.approachDelta(warmup, speed, warmupSpeed);
            progress += delta() * dominantItems * speed * warmup;

            if(Mathf.chanceDelta(updateEffectChance * warmup))
                updateEffect.at(x + Mathf.range(size * 2f), y + Mathf.range(size * 2f));

            if (progress >= delay)
            {
                if (items.get(dominantItem) == getMaximumAccepted(dominantItem))
                    offload(dominantItem);
                else
                {
                    items.add(dominantItem, 1);

                    if (Mathf.chance(getTotalStat(TechStat.doubleProductionChance)))
                    {
                        items.add(dominantItem, 1);
                    }
                }

                progress -= delay;

                if(wasVisible && Mathf.chanceDelta(drillEffectChance * warmup)) drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
            }
        }

        @Override
        public void handleItem(Building source, Item item) {
            if (acceptItem(source, item))
                this.items.add(item, 1);
        }

        @Override
        public float totalProgress(){
            return totalProgress;
        }

        @Override
        public float progress(){
            return dominantItem == null ? 0f : Mathf.clamp(progress / getDrillTime(dominantItem));
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.progress && dominantItem != null) return progress;
            return super.sense(sensor);
        }

        @Override
        public void drawCracks(){}

        public void drawDefaultCracks(){
            super.drawCracks();
        }

        // @Override
        // public void draw(){
        //     float s = 0.3f;
        //     float ts = 0.6f;

        //     Draw.rect(region, x, y);
        //     Draw.z(Layer.blockCracks);
        //     drawDefaultCracks();

        //     Draw.z(Layer.blockAfterCracks);
        //     if(drawRim){
        //         Draw.color(heatColor);
        //         Draw.alpha(warmup * ts * (1f - s + Mathf.absin(Time.time, 3f, s)));
        //         Draw.blend(Blending.additive);
        //         Draw.rect(rimRegion, x, y);
        //         Draw.blend();
        //         Draw.color();
        //     }

        //     if(drawSpinSprite){
        //         Drawf.spinSprite(rotatorRegion, x, y, timeDrilled * rotateSpeed);
        //     }else{
        //         Draw.rect(rotatorRegion, x, y, timeDrilled * rotateSpeed);
        //     }

        //     Draw.rect(topRegion, x, y);

        //     if(dominantItem != null && drawMineItem){
        //         Draw.color(dominantItem.color);
        //         Draw.rect(itemRegion, x, y);
        //         Draw.color();
        //     }
        // }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
            write.f(warmup);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 1){
                progress = read.f();
                warmup = read.f();
            }
        }
    }
}
