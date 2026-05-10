package technical.core;

import arc.struct.Seq;
import mindustry.type.Item;
import technical.content.TCustom;
import technical.util.TUI;

public class RecipeItem extends Item
{
    public Seq<Item> startingItems = new Seq<>();
    public Seq<ConveyorRecipe> recipes = new Seq<>();

    public RecipeItem(String name)
    {
        super(name);
    }

    public void FindRecipes()
    {
        startingItems.clear();
        recipes.clear();

        for (var pair : TCustom.ConveyorRecipes)
        {
            if (pair.value.result == this)
            {
                startingItems.add(pair.key);
                recipes.add(pair.value);
            }
        }
    }

    @Override
    public void setStats()
    {
        super.setStats();

        FindRecipes();

        if(recipes.size <= 0) return;

        for (int i = 0; i < recipes.size; i++)
        {
            TUI.addConveyorRecipeStat(stats, startingItems.get(i), recipes.get(i));
        }
    }
}
