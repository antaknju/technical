package technical.core.mycelis;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumeLiquidsDynamic;
import technical.util.T;
import technical.util.TCol;
import technical.content.TFx;

public class MycelisRecipeCrafter extends MycelisBlock
{
    public Seq<MycelisRecipe> recipes = new Seq<>();

    public boolean isItemConsumer = false;
    public boolean isLiquidsConsumer = false;

    public Effect craftEffect = TFx.smokeCloud.wrap(TCol.bioOrange);

    public TextureRegion[] topRegions;

    public MycelisRecipeCrafter(String name) 
    {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        topRegions = T.loadMultipleRegions(name + "-top");
    }

    @Override
    public void init() 
    {
        setupFromRecipes();
        setupConsumers();

        super.init();
    }

    public void setupFromRecipes()
    {
        isItemConsumer = false;
        isLiquidsConsumer = false;

        for (var r : recipes)
        {
            if (r.inputItem != null) isItemConsumer = true;
            if (r.inputLiquid != null) isLiquidsConsumer = true;
        }
    }

    public void setupConsumers()
    {
        if (isItemConsumer) consume(new ConsumeItemDynamic(
            (MycelisRecipeCrafterBuild b) -> (b.recipe() == null || b.recipe().inputItem != null ? new ItemStack[0] : new ItemStack[]{b.recipe().inputItem})
        ));
        if (isLiquidsConsumer) consume(new ConsumeLiquidsDynamic(
            (MycelisRecipeCrafterBuild b) -> (b.recipe() == null || b.recipe().inputLiquid != null ? new LiquidStack[0] : new LiquidStack[]{b.recipe().inputLiquid})
        ));
    }

    @Override
    public void setBars() {
        super.setBars();
        
        addBar("progress", (MycelisRecipeCrafterBuild b) -> new Bar(
                "bar.progress",
                Pal.accent,
                b::progress
        ));
    }

    @Override
    public boolean outputsItems() {
        return isItemConsumer;
    }

    public class MycelisRecipeCrafterBuild extends MycelisBuild
    {
        // -1 means searching for new recipe
        public int currentRecipe = -1;
        public float progress = 0f;

        public int frame = 0;

        public float progress()
        {
            return progress;
        }

        public void updateTile()
        {
            super.updateTile();

            if (efficiency < 0)
            {
                currentRecipe = -1;
                progress = 0f;
                return;
            }

            MycelisRecipe r = recipe();

            if (r == null)
            {
                for (int i = 0; i < recipes.size; i++)
                {
                    MycelisRecipe rec = recipes.get(i);

                    boolean canCraft = true;

                    if (rec.inputItem != null)
                    {
                        if (items.get(rec.inputItem.item) < rec.inputItem.amount)
                        {
                            canCraft = false;
                            continue;
                        }
                    }

                    if (rec.inputLiquid != null)
                    {
                        if (liquids.get(rec.inputLiquid.liquid) < rec.inputLiquid.amount)
                        {
                            canCraft = false;
                            continue;
                        }
                    }

                    if (canCraft)
                    {
                        currentRecipe = i;
                        progress = 0f;
                        return;
                    }
                }
            }

            r = recipe();

            if (r != null)
            {
                progress += edelta() / r.craftTime;

                frame = (int)((progress / r.craftTime) * topRegions.length) % topRegions.length;

                if (progress >= 1f)
                {
                    craft();
                }
            }
        }

        public void craft()
        {
            consume();

            for(int i = 0; i < recipe().outputItem.amount; i++){
                offload(recipe().outputItem.item);
            }

            if(wasVisible){
                craftEffect.at(x, y);
            }

            progress %= 1f;
            currentRecipe = -1;
        }

        public MycelisRecipe recipe()
        {
            return currentRecipe == -1 ? null : recipes.get(currentRecipe);
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
            if (!isItemConsumer) return false;

            MycelisRecipe r = recipe();

            if (r == null)
            {
                for (MycelisRecipe rec : recipes)
                {
                    if (rec.inputItem != null && rec.inputItem.item == item)
                    {
                        return true;
                    }
                }
            }
            else if (r.inputItem != null && r.inputItem.item == item)
            {
                return true;
            }
            
            return false;
        }

        public boolean consumesLiquid(Liquid liquid)
        {
            if (!isLiquidsConsumer) return false;

            MycelisRecipe r = recipe();

            if (r == null)
            {
                for (MycelisRecipe rec : recipes)
                {
                    if (rec.inputLiquid != null && rec.inputLiquid.liquid == liquid)
                    {
                        return true;
                    }
                }
            }
            else if (r.inputLiquid != null && r.inputLiquid.liquid == liquid)
            {
                return true;
            }

            return false;
        }

        public float getMaximumLiquidAccepted(Liquid liquid)
        {
            return liquidCapacity;
        }


        @Override
        public void drawTop()
        {            
            Draw.rect(topRegions[frame], x, y);
        }
    }
}