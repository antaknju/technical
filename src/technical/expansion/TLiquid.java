package technical.expansion;

import mindustry.type.Liquid;

public class TLiquid extends Liquid 
{
    public boolean canMove = true;

    public TLiquid lowerForm;
    public TLiquid upperForm;

    public boolean canLower = false;
    public boolean canUpper = false;

    public float lowerHeat = 0;
    public float upperHeat = 0;

    public TLiquid(String name){
        super(name);
    }

    public void setupLower(TLiquid lower, float changeHeat, float changeMargin)
    {
        canLower = true;
        lowerForm = lower;
        lowerHeat = changeHeat - changeMargin;

        lower.canUpper = true;
        lower.upperForm = this;
        lower.upperHeat = changeHeat + changeMargin;
    }
}
