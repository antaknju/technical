package technical.content;

import arc.Core;
import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives;
import technical.core.TLiquid;
import technical.util.TBundle;

import static mindustry.content.TechTree.*;

public class TTechTree
{
    public static void load()
    {
        TPlanets.mycelius.techTree = nodeRoot(TBundle.techtree(TPlanets.mycelius), TBlocks.basic_core, () -> {
            node(TBlocks.drying_pad, () -> {
                node(TBlocks.stone_chimney, () -> {
                    node(TBlocks.brick_furnace, () -> {

                    });
                });
            });

            node(TBlocks.roller_conveyor, () -> {

            });

            node(TBlocks.iron_drill, () -> {

            });

            nodeProduce(TItems.stone, () -> {
                node(TItems.clay, () -> {
                    nodeProduce(TItems.brick, () -> {

                    });

                    nodeProduce(TItems.porcelain, () -> {

                    });
                });

                nodeProduce(TItems.coal, () -> {

                });

                nodeProduce(TItems.raw_iron, () -> {
                    nodeProduce(TItems.iron_plate, () -> {

                    });
                });
            });

            nodeProduce(TLiquids.water, () -> {

            });
        });
    }
}