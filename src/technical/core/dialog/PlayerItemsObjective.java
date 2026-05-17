package technical.core.dialog;

import mindustry.Vars;
import mindustry.type.Item;
import mindustry.type.ItemStack;

public class PlayerItemsObjective extends DialogObjective
{
    public ItemStack itemStack;
    public ItemStack startItemStack = new ItemStack();

    public PlayerItemsObjective(ItemStack itemStack)
    {
        this.itemStack = itemStack;
    }

    public PlayerItemsObjective(Item item, int count)
    {
        this.itemStack = new ItemStack(item, count);
    }

    @Override
    public void onStart()
    {
        var unit = Vars.player.unit();
        if (unit == null || !unit.isValid()) return;

        startItemStack.set(unit.item(), unit.stack().amount);
    }

    @Override
    public boolean onComplete()
    {
        var unit = Vars.player.unit();
        if (unit == null || !unit.isValid()) return false;

        return unit.item() == itemStack.item && unit.stack().amount >= itemStack.amount;
    }

    @Override
    public boolean onNegativeComplete()
    {
        var unit = Vars.player.unit();
        if (unit == null || !unit.isValid()) return false;

        return unit.item() != itemStack.item && unit.item() != startItemStack.item;
    }

    @Override
    public DialogObjective clone()
    {
        var copy = (PlayerItemsObjective) super.clone();
        copy.itemStack = itemStack;

        return copy;
    }
}
