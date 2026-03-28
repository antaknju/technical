package technical.expansion;

import arc.struct.Seq;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;
import technical.expansion.kinetic.KineticEnergy;

public class RecipeDrawable 
{
    public static RecipeDrawable tmpRD1 = new RecipeDrawable();
    public static RecipeDrawable tmpRD2 = new RecipeDrawable();

    public ItemStack[] inputItems;
    public LiquidStack[] inputLiquids;
    public PayloadStack[] inputPayloads;

    public ItemStack[] outputItems;
    public LiquidStack[] outputLiquids;
    public PayloadStack[] outputPayloads;

    public KineticEnergy inputKinetic;

    public float craftTime;
    public float inputPower;

    public float efficiencyCap;

    public RecipeDrawable(){};

    public static Seq<RecipeDrawable> listRecipes(Seq<Recipe> recipes) 
    {
        Seq<RecipeDrawable> list = new Seq<>();
        for (Recipe r : recipes) 
        {
            list.add(new RecipeDrawable().set(r));
        }
        return list;
    }

    public static Seq<RecipeDrawable> listPlans(Seq<FacilityPlan> plans) 
    {
        Seq<RecipeDrawable> list = new Seq<>();
        for (FacilityPlan p : plans) 
        {
            list.add(new RecipeDrawable().set(p));
        }
        return list;
    }

    public RecipeDrawable set(Recipe recipe) 
    {
        this.inputItems = recipe.inputItems;
        this.inputLiquids = recipe.inputLiquids;
        this.inputPayloads = null;

        this.inputKinetic = recipe.inputKinetic;

        this.outputItems = recipe.outputItems;
        this.outputLiquids = recipe.outputLiquids;
        this.outputPayloads = null;

        this.craftTime = recipe.craftTime;
        this.inputPower = recipe.inputPower;
        this.efficiencyCap = recipe.efficiencyCap;

        return this;
    }

    public RecipeDrawable set(FacilityPlan plan) 
    {
        this.inputItems = plan.inputItems;
        this.inputLiquids = plan.inputLiquids;
        this.inputPayloads = plan.inputPayloads;

        this.inputKinetic = plan.inputKinetic;

        this.outputItems = null;
        this.outputLiquids = null;
        this.outputPayloads = new PayloadStack[]{plan.outputPayload};

        this.craftTime = plan.craftTime;
        this.inputPower = plan.inputPower;
        this.efficiencyCap = 0;

        return this;
    }
}
