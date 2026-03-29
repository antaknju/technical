package technical.content;

import technical.TCol;
import technical.expansion.RecipeItem;
import mindustry.type.*;

public class TItems 
{
    public static Item 
    // Mineable
    uranium, raw_copper, stone, clay, coal, raw_iron, raw_zinc,
    // Craftable
    brick, iron_plate, dense_ammo, copper_wire, iron_gear, copper_gear, copper_plate, porcelain, flint, copper_rod, iron_rod, zinc_ingot, brass_ingot,
    // Technology
    metallurgy_xp
    ;
    public static RecipeItem basic_circuit, precision_mechanism;

    public static void load() 
    {
        // Mine able

        uranium = new Item("uranium", TCol.uranium) {{
            hardness = 6;
            radioactivity = 1f;
            buildable = false;
        }};

        raw_copper = new Item("raw-copper", TCol.copper) {{
            cost = 0.4f;
            buildable = false;
            hardness = 3;
        }};

        clay = new Item("clay", TCol.clay) {{
            hardness = 1;
            buildable = false;
        }};

        coal = new Item("coal", TCol.coal) {{
            buildable = false;
            flammability = 0.5f;
            explosiveness = 0.1f;
        }};

        raw_iron = new Item("raw-iron", TCol.iron) {{
            cost = 0.5f;
            buildable = true;
            hardness = 2;
            healthScaling = 0.5f;
        }};

        raw_zinc = new Item("raw-zinc", TCol.zinc) {{
            cost = 0.6f;
            buildable = false;
            hardness = 4;
        }};

        stone = new Item("stone", TCol.stone) {{
            cost = 0.1f;
            buildable = true;
            hardness = 1;
            healthScaling = 0.5f;
        }};

        // Craftable

        iron_plate = new Item("iron-plate", TCol.iron) {{
            cost = 3f;
            buildable = true;
            healthScaling = 1f;
        }};

        porcelain = new Item("porcelain", TCol.porcelain) {{
            cost = 3f;
            buildable = true;
            healthScaling = 0.2f;
        }};

        copper_plate = new Item("copper-plate", TCol.copper) {{
            cost = 2f;
            buildable = true;
        }};

        brick = new Item("brick", TCol.brick) {{
            cost = 3f;
            buildable = true;
        }};

        flint = new Item("flint", TCol.flint) {{
            cost = 3f;
            buildable = true;
        }};

        dense_ammo = new Item("dense-ammo", TCol.dense_ammo) {{
            buildable = false;
        }};

        copper_wire = new Item("copper-wire", TCol.copper) {{
            cost = 3f;
            buildable = true;
        }};

        precision_mechanism = new RecipeItem("precision-mechanism", TCol.circuit) {{
            cost = 15f;
            buildable = true;
        }};

        copper_gear = new Item("copper-gear", TCol.copper) {{
            buildable = false;
            cost = 5f;
        }};

        iron_gear = new Item("iron-gear", TCol.iron) {{
            buildable = false;
            cost = 5f;
        }};

        brass_ingot = new Item("brass-ingot", TCol.brass) {{
            buildable = true;
            cost = 5f;
        }};

        zinc_ingot = new Item("zinc-ingot", TCol.zinc) {{
            buildable = true;
            cost = 3f;
        }};

        copper_rod = new Item("copper-rod", TCol.copper) {{
            buildable = false;
            cost = 5f;
        }};

        iron_rod = new Item("iron-rod", TCol.iron) {{
            buildable = true;
            cost = 5f;
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
