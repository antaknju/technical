package technical.expansion.tech;

public enum TechStat 
{
    speed(TechStatType.multiplier),
    powerEfficiency(TechStatType.multiplier),
    liquidEfficiency(TechStatType.multiplier),

    materialSaveChance(TechStatType.addictive),
    doubleProductionChance(TechStatType.addictive),

    damage(TechStatType.multiplier),
    
    efficiencyCap(TechStatType.multiplier),
    maxEfficiency(TechStatType.multiplier),
    maxThermalDelta(TechStatType.multiplier),
    multiblockEfficiency(TechStatType.multiplier),

    itemDuration(TechStatType.multiplier),

    cooldown(TechStatType.multiplier),
    
    itemCapacity(TechStatType.multiplier),
    liquidCapacity(TechStatType.multiplier);

    public final TechStatType type;

    TechStat(TechStatType type){
        this.type = type;
    }

    public static float defVal(TechStat stat)
    {
        return stat.type == TechStatType.multiplier ? 1f : 0f;
    }

    public float defVal()
    {
        return defVal(this);
    }
}