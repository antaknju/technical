package technical.core.dialog;

import mindustry.Vars;
import mindustry.type.Item;
import mindustry.type.ItemStack;

public class CoreItemsObjective extends DialogObjective
{
    public ItemStack itemStack;

    public int totalItemCount;
    public int startCount;

    public CoreItemsObjective(ItemStack itemStack)
    {
        this.itemStack = itemStack;
    }

    public CoreItemsObjective(Item item, int count)
    {
        this.itemStack = new ItemStack(item, count);
    }

    @Override
    public void onStart()
    {
        var core = Vars.player.team().core();

        if (core == null || !core.isValid()) return;

        startCount = core.items.get(itemStack.item);
        totalItemCount = core.items.total();
    }

    @Override
    public boolean onComplete()
    {
        var core = Vars.player.team().core();
        if (core == null || !core.isValid()) return false;

        return core.items.get(itemStack.item) - startCount >= itemStack.amount;
    }

    @Override
    public boolean onNegativeComplete()
    {
        var core = Vars.player.team().core();
        if (core == null || !core.isValid()) return false;

        return core.items.total() > totalItemCount && core.items.get(itemStack.item) - startCount <= 0;
    }

    @Override
    public DialogObjective clone()
    {
        var copy = (CoreItemsObjective) super.clone();
        copy.itemStack = itemStack;

        return copy;
    }
}
