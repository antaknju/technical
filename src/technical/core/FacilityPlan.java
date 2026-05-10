package technical.core;

import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;
import technical.core.kinetic.KineticEnergy;

public class FacilityPlan
{
    public ItemStack[] inputItems;
    public LiquidStack[] inputLiquids;
    public PayloadStack[] inputPayloads;

    public PayloadStack outputPayload;

    public float craftTime;
    public float inputPower;
    public KineticEnergy inputKinetic;

    public Seq<FacilityStep> steps;

    public FacilityPlan(ItemStack[] inputItems, LiquidStack[] inputLiquids, PayloadStack[] inputPayloads, PayloadStack outputPayload, float inputPower, KineticEnergy inputKinetic, Seq<FacilityStep> steps) 
    {
        this.inputItems = inputItems;
        this.inputLiquids = inputLiquids;
        this.inputPayloads = inputPayloads;

        this.inputKinetic = inputKinetic;

        this.outputPayload = outputPayload;

        this.inputPower = inputPower;

        this.steps = steps;

        this.craftTime = 0;
        for (var step : steps)
        {
            craftTime += step.time;
        }
    }

    public boolean hasItem(Item item) 
    {
        if (inputItems == null) return false;
        
        for (ItemStack stack : inputItems) 
        {
            if (stack.item == item) return true;
        }

        return false;
    }

    public ItemStack getItemStack(Item item) 
    {
        if (inputItems == null) return null;
        
        for (ItemStack stack : inputItems) 
        {
            if (stack.item == item) return stack;
        }

        return null;
    }

    public boolean hasPayload(UnlockableContent item) 
    {
        if (inputPayloads == null) return false;
        
        for (var stack : inputPayloads) 
        {
            if (stack.item == item) return true;
        }

        return false;
    }

    public PayloadStack getPayloadStack(UnlockableContent item) 
    {
        if (inputPayloads == null) return null;
        
        for (var stack : inputPayloads) 
        {
            if (stack.item == item) return stack;
        }

        return null;
    }

    FacilityPlan(){}
}