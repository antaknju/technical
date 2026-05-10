package technical.core.extendable;

import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.struct.EnumSet;
import arc.struct.ObjectFloatMap;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.EventType.GeneratorPressureExplodeEvent;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.world.consumers.ConsumeItemEfficiency;
import mindustry.world.consumers.ConsumeItemFilter;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import technical.core.tech.TechStat;

public class ExtendableGenerator extends Extendable
{
    public float powerProduction;
    public Stat generationType = Stat.basePowerGeneration;

    public float itemDuration = 120f;

    public float warmupSpeed = 0.05f;
    public float effectChance = 0.01f;
    public Effect generateEffect = Fx.none, consumeEffect = Fx.none;
    public float generateEffectRange = 3f;
    public float baseLightRadius = 65f;

    public @Nullable LiquidStack outputLiquid;
    public boolean explodeOnFull = false;

    public @Nullable ConsumeItemFilter filterItem;
    public @Nullable ConsumeLiquidFilter filterLiquid;
    
    public ObjectFloatMap<Item> itemDurationMultipliers = new ObjectFloatMap<>();

    public ExtendableGenerator(String name) 
    {
        super(name);
        update = true;
        solid = true;
        hasPower = true;
        group = BlockGroup.power;

        consumesPower = false;
        outputsPower = true;

        sync = true;
        baseExplosiveness = 5f;
        flags = EnumSet.of(BlockFlag.generator);
    }
    
    public float getDisplayedPowerProduction(){
        return powerProduction;
    }

    @Override
    public void setStats()
    {
        stats.timePeriod = itemDuration();

        super.setStats();
        stats.add(generationType, powerProduction * 60.0f, StatUnit.powerSecond);

        if(hasItems){
            stats.add(Stat.productionTime, itemDuration() / 60f, StatUnit.seconds);
        }

        if(outputLiquid != null){
            stats.add(Stat.output, StatValues.liquid(outputLiquid.liquid, outputLiquid.amount * 60f, true));
        }
    }

    public float itemDuration()
    {
        return itemDuration * getTotalStat(TechStat.itemDuration);
    }

    @Override
    public void setBars(){
        super.setBars();

        if(hasPower && outputsPower){
            addBar("power", (ExtendableGeneratorBuild entity) -> new Bar(() ->
            Core.bundle.format("bar.poweroutput",
            Strings.fixed(entity.getPowerProduction() * 60 * entity.timeScale(), 1)),
            () -> Pal.powerBar,
            () -> entity.productionEfficiency));
        }

        if(outputLiquid != null){
            addLiquidBar(outputLiquid.liquid);
        }
    }

    @Override
    public void init(){
        filterItem = findConsumer(c -> c instanceof ConsumeItemFilter);
        filterLiquid = findConsumer(c -> c instanceof ConsumeLiquidFilter);

        //pass along the duration multipliers to the consumer, so it can display them properly
        if(filterItem instanceof ConsumeItemEfficiency eff){
            eff.itemDurationMultipliers = itemDurationMultipliers;
        }

        if(outputLiquid != null){
            outputsLiquid = true;
            hasLiquids = true;
        }

        if(explodeOnFull && outputLiquid != null && explosionPuddleLiquid == null){
            explosionPuddleLiquid = outputLiquid.liquid;
        }

        emitLight = true;
        lightRadius = baseLightRadius * size;
        super.init();
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    public class ExtendableGeneratorBuild extends ExtendableBuild
    {
        public float generateTime;
        public float productionEfficiency = 0.0f;
        public float warmup, totalTime, efficiencyMultiplier = 1f, itemDurationMultiplier = 1;

        @Override
        public void updateEfficiencyMultiplier(){
            efficiencyMultiplier = 1f;
            if(filterItem != null){
                float m = filterItem.efficiencyMultiplier(this);
                if(m > 0) efficiencyMultiplier = m;
            }else if(filterLiquid != null){
                float m = filterLiquid.efficiencyMultiplier(this);
                if(m > 0) efficiencyMultiplier = m;
            }
            efficiencyMultiplier *= efficiencyScale();
        }

        @Override
        public void updateTile(){
            super.updateTile();

            boolean valid = efficiency > 0 && hasRequiredExtensions();

            warmup = Mathf.lerpDelta(warmup, valid ? 1f : 0f, warmupSpeed);

            productionEfficiency = efficiency * efficiencyMultiplier;
            totalTime += warmup * Time.delta;

            //randomly produce the effect
            if(valid && Mathf.chanceDelta(effectChance)){
                generateEffect.at(x + Mathf.range(generateEffectRange), y + Mathf.range(generateEffectRange));
            }

            //make sure the multiplier doesn't change when there is nothing to consume while it's still running
            if(filterItem != null && valid && itemDurationMultipliers.size > 0 && filterItem.getConsumed(this) != null){
                itemDurationMultiplier = itemDurationMultipliers.get(filterItem.getConsumed(this), 1);
            }

            //take in items periodically
            if(hasItems && valid && generateTime <= 0f){
                if (!chance(TechStat.materialSaveChance)) consume();
                consumeEffect.at(x + Mathf.range(generateEffectRange), y + Mathf.range(generateEffectRange));
                generateTime = 1f;
            }

            if(outputLiquid != null){
                float added = Math.min(productionEfficiency * delta() * outputLiquid.amount, liquidCapacity - liquids.get(outputLiquid.liquid));
                liquids.add(outputLiquid.liquid, added);
                dumpLiquid(outputLiquid.liquid);

                if(explodeOnFull && liquids.get(outputLiquid.liquid) >= liquidCapacity - 0.01f){
                    kill();
                    Events.fire(new GeneratorPressureExplodeEvent(this));
                }
            }

            //generation time always goes down, but only at the end so consumeTriggerValid doesn't assume fake items
            generateTime -= delta() / (itemDuration() * itemDurationMultiplier);
        }

        @Override
        public void onDestroyed(){
            super.onDestroyed();

            createExplosion();
        }

        @Override
        public float ambientVolume(){
            return Mathf.clamp(productionEfficiency);
        }

        @Override
        public float getPowerProduction(){
            return enabled ? powerProduction * productionEfficiency : 0f;
        }
        
        @Override
        public boolean consumeTriggerValid(){
            return generateTime > 0;
        }

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public float totalProgress(){
            return totalTime;
        }

        @Override
        public void drawLight()
        {
            super.drawLight();
            // Drawf.light(x, y, (60f + Mathf.absin(10f, 5f)) * size, TCol.uranium, 0.5f * warmup);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(productionEfficiency);
            write.f(generateTime);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            productionEfficiency = read.f();
            generateTime = read.f();
        }
    }
}
