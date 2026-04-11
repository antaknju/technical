package technical.expansion;

import arc.scene.ui.layout.*;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.Stat;
import technical.expansion.ext.ExtendableCrafter;
import technical.expansion.kinetic.ConsumeKineticDynamic;
import technical.utility.TUI;

/* Thanks to multicrafterlib for example use of DynamicConsumers */
public class RecipeCrafter extends ExtendableCrafter 
{
    public Seq<Recipe> recipes = new Seq<>();
    public int defaultRecipeIndex = 0;

    protected boolean isItemConsumer = false;
    protected boolean isLiquidsConsumer = false;
    protected boolean isPowerConsumer = false;
    protected boolean isKineticConsumer = false;

    public RecipeCrafter(String name) {
        super(name);
        configurable = true;
        saveConfig = true;
        ignoreLiquidFullness = true;

        config(Integer.class, (RecipeCrafterBuild b, Integer i) -> b.changePlan(i));
    }

    @Override
    public void init() 
    {
        defaultRecipeIndex = 0;

        setupFromRecipes();
        setupConsumers();

        super.init();
    }

    public void setupConsumers()
    {
        if (isItemConsumer) consume(new ConsumeItemDynamic(
            (RecipeCrafterBuild b) -> (b.recipe().inputItems != null ? b.recipe().inputItems : new ItemStack[0])
        ));
        if (isLiquidsConsumer) consume(new ConsumeLiquidsDynamic(
            (RecipeCrafterBuild b) -> (b.recipe().inputLiquids != null ? b.recipe().inputLiquids : new LiquidStack[0])
        ));
        if (isPowerConsumer) consume(new ConsumePowerDynamic(b ->
            ((RecipeCrafterBuild) b).recipe().inputPower
        ));
        if (isKineticConsumer) consume(new ConsumeKineticDynamic(b ->
            ((RecipeCrafterBuild) b).recipe().inputKinetic
        ));
    }

    public void setupFromRecipes()
    {
        isItemConsumer = false;
        isPowerConsumer = false;
        isLiquidsConsumer = false;
        isKineticConsumer = false;

        for (var r : recipes)
        {
            if (r.inputItems != null) isItemConsumer = true;
            if (r.inputLiquids != null) isLiquidsConsumer = true;
            if (r.inputKinetic != null) isKineticConsumer = true;

            if (r.inputPower > 0) isPowerConsumer = true;
            if (r.outputLiquids != null) outputsLiquid = true;
        }
    }

    @Override
    public void setBars() {
        super.setBars();
        
        addBar("progress", (RecipeCrafterBuild b) -> new Bar(
                "bar.progress",
                Pal.accent,
                b::progress
        ));
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.remove(Stat.productionTime);

        stats.add(Stat.output, stat -> TUI.buildRecipesStats(stat, RecipeDrawable.listRecipes(recipes)));
    }

    @Override
    public boolean outputsItems() {
        return isItemConsumer;
    }

    public class RecipeCrafterBuild extends ExtendableCrafterBuild 
    {
        public int recipeIndex = defaultRecipeIndex;

        @Override
        public ItemStack[] outputItems() {
            return recipe().outputItems;
        }

        @Override
        public LiquidStack[] outputLiquids() {
            return recipe().outputLiquids;
        }

        @Override
        public float craftTime() {
            return recipe().craftTime;
        }

        @Override
        public float efficiencyCap() {
            return recipe().efficiencyCap;
        }

        public Recipe recipe() {
            return recipes.get(recipeIndex);
        }

        @Override
        public void buildConfiguration(Table table) {
            table.clear();

            Table buttons = new Table();
            buttons.top().defaults().pad(6f).growX();

            for (int i = 0; i < recipes.size; i++) 
            {
                int idx = i;
                TUI.addRecipeButton(buttons, RecipeDrawable.tmpRD1.set(recipes.get(idx)), () -> changePlan(idx), () -> recipeIndex == idx);

                if (i % 2 == 1) buttons.row();
            }

            table.add(buttons).growX().top();
        }

        public void changePlan(int index) 
        {
            recipeIndex = Math.max(0, Math.min(index, recipes.size - 1));
            progress = 0f;
        }

        @Override
        public Object config() {
            return recipeIndex;
        }

        @Override
        public boolean acceptItem(Building source, Item item)
        {
            return consumesItem(item) && this.items.get(item) < this.getMaximumAccepted(item);
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid)
        {
            return consumesLiquid(liquid) && this.liquids.get(liquid) < this.getMaximumLiquidAccepted(liquid);
        }

        public boolean consumesItem(Item item)
        {
            Recipe r = recipe();
            if (r.inputItems == null) return false;

            for(ItemStack stack : r.inputItems){
                if(stack.item == item) return true;
            }
            return false;
        }

        public boolean consumesLiquid(Liquid liquid)
        {
            Recipe r = recipe();
            if (r.inputLiquids == null) return false;
            
            for(LiquidStack stack : r.inputLiquids){
                if(stack.liquid == liquid) return true;
            }
            return false;
        }

        @Override
        public void write(Writes write)
        {
            super.write(write);
            
            write.i(recipeIndex);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            
            recipeIndex = read.i();
        }
    }
}


        // @Override
        // public void buildConfiguration(Table table) {
        //     table.clear();
        //     Table buttons = new Table();
        //     buttons.top().defaults().pad(6f).growX();

        //     for (int i = 0; i < recipes.size; i++) {
        //         Recipe r = recipes.get(i);
        //         int finalI = i;

        //         ImageButton button = new ImageButton(Styles.clearTogglei);
        //         Table ioTable = new Table();
        //         ioTable.add(getRecipeIcon(r.inputItems, r.inputLiquids)).size(32f).pad(4f);
        //         ioTable.image(mindustry.gen.Icon.right).pad(4f);
        //         ioTable.add(getRecipeIcon(r.outputItems, r.outputLiquids)).size(32f).pad(4f);

        //         button.add(ioTable).grow(); // <-- add table properly inside button
        //         button.changed(() -> setRecipeIndex(finalI));
        //         button.update(() -> button.setChecked(recipeIndex == finalI));

        //         buttons.add(button);
        //         if (i % 2 == 1) buttons.row();
        //     }

        //     table.add(buttons).growX().top();
        // }

        // private Table getRecipeIcon(ItemStack[] items, LiquidStack[] liquids) {
        //     Table t = new Table();
        //     if (items != null && items.length > 0) t.add(new Image(items[0].item.uiIcon)).size(32f);
        //     else if (liquids != null && liquids.length > 0) t.add(new Image(liquids[0].liquid.uiIcon)).size(32f);
        //     else t.add(new Image(mindustry.gen.Icon.cancel.getRegion())).size(32f);
        //     return t;
        // }
