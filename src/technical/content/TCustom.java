package technical.content;

import arc.struct.ObjectMap;
import mindustry.type.Item;
import technical.utility.T;
import technical.expansion.ConveyorRecipe;
import technical.expansion.ConveyorRecipe.Action.Type;
import technical.expansion.tech.Tech;
import technical.expansion.tech.TechStat;
import technical.expansion.tech.TechType;

import static mindustry.type.ItemStack.*;

public class TCustom
{
    public static ObjectMap<Item, ConveyorRecipe> ConveyorRecipes;
    public static Tech crude_metallurgy;

    public static void load()
    {
        /// BUNGEN IGNORE
        ConveyorRecipes = T.mapOf(
            TItems.iron_plate, new ConveyorRecipe(3, TItems.precision_mechanism,
                TItems.iron_gear, Type.Applying,
                TItems.copper_gear, Type.Applying,
                TItems.iron_plate, Type.Applying
            )
        );

        crude_metallurgy = new Tech("crude-metallurgy"){{
            type = TechType.Metallurgy;

            mapStats(T.mapOf(
                TechStat.speed, 1.05f,
                TechStat.doubleProductionChance, 0.1f
            ));

            researchCost(with(TItems.metallurgy_xp, 100));
        }};
    }
}

/* Progression:
 * Crude
 * Primitive
 * Untreated
 * Basic
 * Standard
 * Refined
 * Precise
 * Specialized
 * Advanced
 * High-Grade
 * High-End
 * Superior
 * Next-Gen
 */


// TItems.basic_circuit, new ConveyorRecipe(2, TItems.basic_circuit,
//     null, Type.Cutting,
//     TItems.iron_plate, Type.Applying,
//     TItems.copper_wire, Type.Applying,
//     TItems.iron_plate, Type.Applying
// ),