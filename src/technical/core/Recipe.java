package technical.core;

import mindustry.type.*;
import technical.core.kinetic.KineticEnergy;

public class Recipe 
{
    public ItemStack[] inputItems;
    public LiquidStack[] inputLiquids;

    public ItemStack[] outputItems;
    public LiquidStack[] outputLiquids;

    public float craftTime;
    public float inputPower;
    public KineticEnergy inputKinetic;

    public float efficiencyCap;

    public Recipe() {}

    public Recipe(ItemStack[] inputItems, LiquidStack[] inputLiquids, ItemStack[] outputItems, LiquidStack[] outputLiquids, float craftTime, float inputPower, float efficiencyCap, KineticEnergy inputKinetic) 
    {
        this.inputItems = inputItems;
        this.inputLiquids = inputLiquids;

        this.inputKinetic = inputKinetic;

        this.outputItems = outputItems;
        this.outputLiquids = outputLiquids;

        this.craftTime = craftTime;
        this.inputPower = inputPower;
        this.efficiencyCap = efficiencyCap;
    }

    public boolean hasKinetic() 
    {
        return inputKinetic != null;
    }

    public boolean hasItems() 
    {
        return inputItems.length > 0 || outputItems.length > 0;
    }

    public boolean hasLiquids() 
    {
        return inputLiquids.length > 0 || outputLiquids.length > 0;
    }
    
    public float maxItemAmount()
    {
        int max = 0;
        for(ItemStack stack : inputItems) max = Math.max(max, stack.amount);
        for(ItemStack stack : outputItems) max = Math.max(max, stack.amount);
        return max;
    }

    public float maxFluidAmount()
    {
        float max = 0f;
        for(LiquidStack stack : inputLiquids) max = Math.max(max, stack.amount);
        for(LiquidStack stack : outputLiquids) max = Math.max(max, stack.amount);
        return max;
    }
}
