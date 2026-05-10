package technical.content;

import technical.util.TCol;
import technical.core.RecipeItem;
import mindustry.type.*;

public class TItems 
{
    public static Item 
    // Mineable
    uranium, raw_copper, stone, clay, coal, raw_iron, raw_zinc,
    // Craftable
    brick, iron_plate, dense_ammo, copper_wire, iron_gear, copper_gear, copper_plate, porcelain, flint, copper_rod, iron_rod, zinc_ingot, brass_ingot, flint_arrow, iron_rivet, small_copper_coil,
    // Technology
    metallurgy_xp
    ;
    public static RecipeItem precision_mechanism, iron_frame, primitive_radio_unit;

    public static void load() 
    {
        // Mine able

        uranium = new Item("uranium") {{
            hardness = 6;
            radioactivity = 1f;
            buildable = false;
            hardness = 67;

            color = TCol.uranium;
        }};

        raw_copper = new Item("raw-copper") {{
            cost = 0.4f;
            buildable = false;
            hardness = 2;
            color = TCol.copper;
        }};

        clay = new Item("clay") {{
            hardness = 0;
            buildable = false;
            color = TCol.clay;
        }};

        coal = new Item("coal") {{
            buildable = false;
            flammability = 0.5f;
            explosiveness = 0.1f;
            hardness = 0;

            color = TCol.coal;
        }};

        raw_iron = new Item("raw-iron") {{
            cost = 0.5f;
            buildable = true;
            hardness = 0;
            healthScaling = 0.5f;

            color = TCol.iron;
        }};

        raw_zinc = new Item("raw-zinc") {{
            cost = 0.6f;
            buildable = false;
            hardness = 4;

            color = TCol.zinc;
        }};

        stone = new Item("stone") {{
            cost = 0.1f;
            buildable = true;
            hardness = 0;
            healthScaling = 0.5f;
            color = TCol.stone;
        }};

        // Craftable

        iron_plate = new Item("iron-plate") {{
            cost = 3f;
            buildable = true;
            healthScaling = 1f;
            color = TCol.iron;
        }};

        flint_arrow = new Item("flint-arrow") {{
            buildable = false;
            color = TCol.flint;
        }};

        porcelain = new Item("porcelain") {{
            cost = 3f;
            buildable = true;
            healthScaling = 0.2f;
            color = TCol.porcelain;
        }};

        copper_plate = new Item("copper-plate") {{
            cost = 2f;
            buildable = true;
            color = TCol.copper;
        }};

        brick = new Item("brick") {{
            cost = 3f;
            buildable = true;
            color = TCol.brick;
        }};

        flint = new Item("flint") {{
            cost = 3f;
            buildable = true;
            color = TCol.flint;
        }};

        dense_ammo = new Item("dense-ammo") {{
            buildable = false;
            color = TCol.dense_ammo;
        }};

        copper_wire = new Item("copper-wire") {{
            cost = 3f;
            buildable = true;
            color = TCol.copper;
        }};

        precision_mechanism = new RecipeItem("precision-mechanism") {{
            cost = 15f;
            buildable = true;
            color = TCol.circuit;
        }};

        copper_gear = new Item("copper-gear") {{
            buildable = false;
            cost = 5f;
            color = TCol.copper;
        }};

        iron_gear = new Item("iron-gear") {{
            buildable = false;
            cost = 5f;
            color = TCol.iron;
        }};

        brass_ingot = new Item("brass-ingot") {{
            buildable = true;
            cost = 5f;
            color = TCol.brass;
        }};

        zinc_ingot = new Item("zinc-ingot") {{
            buildable = true;
            cost = 3f;
            color = TCol.zinc;
        }};

        copper_rod = new Item("copper-rod") {{
            buildable = false;
            cost = 5f;
            color = TCol.copper;
        }};

        iron_rod = new Item("iron-rod") {{
            buildable = true;
            cost = 5f;
            color = TCol.iron;
        }};

        small_copper_coil = new Item("small-copper-coil") {{
            buildable = false;
            cost = 5f;
            color = TCol.copper;
        }};

        iron_rivet = new Item("iron-rivet") {{
            buildable = true;
            cost = 1f;
            color = TCol.iron;
        }};

        iron_frame = new RecipeItem("iron-frame") {{
            buildable = true;
            cost = 5f;
            color = TCol.iron;
        }};

        primitive_radio_unit = new RecipeItem("primitive-radio-unit") {{
            buildable = true;
            cost = 5f;
            color = TCol.copper_dark;
        }};

        // XP

        metallurgy_xp = new Item("metallurgy-xp") {{
            buildable = true;
        }};

        // basic_circuit = new RecipeItem("basic-circuit", TCol.circuit) {{
        //     cost = 15f;
        //     buildable = true;
        // }};
    }
}
