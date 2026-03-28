package technical.content;

import arc.graphics.Color;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.type.StatusEffect;
import technical.T;
import technical.TCol;
import technical.expansion.CellTLiquid;
import technical.expansion.TLiquid;

public class TLiquids 
{
    public static TLiquid 
    
    crude_oil, heavy_oil_fraction, wide_oil_fraction, metan, petrol, kerosene, oil, steam, water, ice, molten_iron, molten_copper, solidified_iron, solidified_copper, 
    toxic_waste, bio_fluid, lava
    ;

    public static void load() 
    {
        toxic_waste = new CellTLiquid("toxic-waste") {{
            flammability = 0f;
            explosiveness = 0.3f;
            temperature = 0.70f;
            heatCapacity = 0f;
            viscosity = 0.80f;

            coolant = false;
            moveThroughBlocks = true;
            capPuddles = false;
            spreadTarget = TLiquids.water;

            incinerable = false;
            blockReactive = false;

            canStayOn.addAll(Liquids.water);

            color = TCol.uranium;
            colorFrom = T.c("#2bff00ff");
            colorTo = T.c("#079c0eff");
        }};

        bio_fluid = new CellTLiquid("bio-fluid") {{
            flammability = 0;
            explosiveness = 0;
            temperature = 0.40f;
            heatCapacity = 0f;
            viscosity = 0.6f;

            coolant = false;
            moveThroughBlocks = true;
            capPuddles = false;
            spreadTarget = TLiquids.water;

            incinerable = false;
            blockReactive = false;

            canStayOn.addAll(Liquids.water);

            color = TCol.bioOrange;
            colorFrom = T.c("#ffcd66");
            colorTo = T.c("#ffb166");
        }};

        crude_oil = new TLiquid("crude-oil"){{
            viscosity = 0.75f;
            flammability = 0.6f;
            temperature = 0.5f;
            coolant = false;
            heatCapacity = 0.1f;
            explosiveness = 0.4f;
            effect = StatusEffects.tarred;

            canStayOn.addAll(Liquids.water);

            color = T.c("#202020ff");
        }};

        metan = new TLiquid("metan"){{
            gas = true;

            flammability = 1f;
            temperature = 0.5f;
            coolant = false;
            heatCapacity = 0f;
            explosiveness = 0.6f;

            color = T.c("#ff7d49ff");
        }};

        wide_oil_fraction = new TLiquid("wide-oil-fraction"){{
            viscosity = 0.9f;
            flammability = 0.5f;
            temperature = 0.5f;
            coolant = false;
            heatCapacity = 0.1f;
            explosiveness = 0.5f;

            canStayOn.addAll(Liquids.water);

            color = T.c("#222222ff");
        }};

        solidified_copper = new TLiquid("solidified-copper"){{
            viscosity = 0;
            flammability = 0f;
            temperature = 0.7f;
            coolant = false;
            heatCapacity = 0.5f;
            explosiveness = 0;

            canMove = false;

            color = TCol.copper;
        }};

        solidified_iron = new TLiquid("solidified-iron"){{
            viscosity = 0;
            flammability = 0f;
            temperature = 0.8f;
            coolant = false;
            heatCapacity = 0;
            explosiveness = 0;

            canMove = false;

            color = TCol.iron;
        }};

        molten_copper = new TLiquid("molten-copper"){{
            viscosity = 0.2f;
            flammability = 0f;
            temperature = 1.2f;
            coolant = false;
            heatCapacity = 0.5f;
            explosiveness = 0;

            color = T.c("#ec4b00");

            setupLower(solidified_copper, 80, 5);
        }};

        lava = new CellTLiquid("lava") {{
            flammability = 1f;
            explosiveness = 0.1f;
            temperature = 1.2f;
            heatCapacity = 0f;
            viscosity = 0.50f;

            effect = StatusEffects.melting;

            coolant = false;
            moveThroughBlocks = true;
            capPuddles = false;

            incinerable = false;
            blockReactive = false;

            color = TCol.lavaOrange;
            colorFrom = TCol.lavaRed;
            colorTo = TCol.lavaYellow;
        }};
        
        molten_iron = new TLiquid("molten-iron"){{
            viscosity = 0.2f;
            flammability = 0f;
            temperature = 1.2f;
            coolant = false;
            heatCapacity = 0;
            explosiveness = 0;

            color = T.c("#c7734c");

            setupLower(solidified_iron, 100, 5);
        }};

        ice = new TLiquid("ice"){{
            heatCapacity = 0;
            boilPoint = 999;
            canMove = false;

            color = T.c("#9aacffff");
        }};

        water = new TLiquid("water"){{
            heatCapacity = 0.4f;
            effect = StatusEffects.wet;
            boilPoint = 0.5f;
            gasColor = Color.grays(0.9f);
            canMove = true;

            color = T.c("#596ab8");

            setupLower(ice, -20, 3);
        }};

        steam = new TLiquid("steam"){{
            gas = true;

            flammability = 0f;
            temperature = 1f;
            coolant = false;
            heatCapacity = 0.3f;
            explosiveness = 0f;

            color = T.c("#a0a0a0ff");

            setupLower(water, 20, 3);
        }};
    }
}
