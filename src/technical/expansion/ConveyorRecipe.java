package technical.expansion;

import java.util.Objects;

import arc.util.Log;
import arc.util.Nullable;
import mindustry.type.Item;

public class ConveyorRecipe 
{
    public RecipeItem result;
    public Action[] actions;
    public int times;

    public ConveyorRecipe(int times, RecipeItem result, Action... actions)
    {
        this.result = result;
        this.actions = actions;
        this.times = times;
    }

    public ConveyorRecipe(int times, RecipeItem result, Object... itemAndType)
    {
        this.result = result;
        this.times = times;

        if (itemAndType.length <= 0 || itemAndType.length % 2 != 0) 
        {
            Log.err("Error when creating ConveyorRecipe");
            return;
        }

        actions = new Action[itemAndType.length / 2];

        for (int i = 0; i < actions.length; i++) {
            actions[i] = new Action(
                (Item)itemAndType[i * 2],
                (Action.ActionType)itemAndType[i * 2 + 1]
            );
        }
    }

    public static class Action
    {
        public ActionType actionType;
        public @Nullable Item item;

        public Action(Item item, ActionType actionType)
        {
            this.actionType = actionType;
            this.item = item;
        }

        /// BUNGEN ENUM
        public enum ActionType
        {
            Cutting,
            Applying,
            Riveting
        }

        @Override
        public boolean equals(Object obj) 
        {
            if (this == obj) return true;
            if (!(obj instanceof Action other)) return false;

            return actionType == other.actionType && Objects.equals(item, other.item);
        }
    }
}
