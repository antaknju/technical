package technical.content;

import arc.struct.ObjectMap;
import mindustry.type.Item;
import technical.util.T;
import technical.core.ConveyorRecipe;
import technical.core.ConveyorRecipe.Action.ActionType;
import technical.core.tech.Tech;
import technical.core.tech.TechStat;
import technical.core.tech.TechType;

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
                    TItems.iron_gear,   ActionType.Applying,
                    TItems.copper_gear, ActionType.Applying,
                    TItems.iron_plate,  ActionType.Applying
            ),
            TItems.iron_rod, new ConveyorRecipe(5, TItems.iron_frame,
                    TItems.iron_rivet, ActionType.Riveting,
                    TItems.iron_rod,   ActionType.Applying,
                    TItems.iron_rivet, ActionType.Riveting,
                    TItems.iron_rod,   ActionType.Applying,
                    TItems.iron_rivet, ActionType.Riveting,
                    TItems.iron_rod,   ActionType.Applying
            ),
            TItems.precision_mechanism, new ConveyorRecipe(3, TItems.primitive_radio_unit,
                    TItems.small_copper_coil, ActionType.Soldering,
                    TItems.copper_plate, ActionType.Applying,
                    TItems.iron_rod, ActionType.Applying
            )
        );

        crude_metallurgy = new Tech("crude-metallurgy"){{
            researchCost(with(TItems.metallurgy_xp, 100));
            type = TechType.Metallurgy;

            mapStats(T.mapOf(
                TechStat.speed, 1.05f,
                TechStat.doubleProductionChance, 0.1f
            ));
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