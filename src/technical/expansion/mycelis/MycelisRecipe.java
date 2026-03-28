package technical.expansion.mycelis;

import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;

public class MycelisRecipe 
{
    public ItemStack inputItem;
    public LiquidStack inputLiquid;

    public ItemStack outputItem;
    public LiquidStack outputLiquid;
    
    public float craftTime;

    public MycelisRecipe(ItemStack inputItem, LiquidStack inputLiquid, ItemStack outputItem, LiquidStack outputLiquid, float craftTime) 
    {
        this.inputItem = inputItem;
        this.inputLiquid = inputLiquid;

        this.outputItem = outputItem;
        this.outputLiquid = outputLiquid;

        this.craftTime = craftTime;
    }
}
