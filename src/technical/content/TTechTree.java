package technical.content;

import static mindustry.content.TechTree.*;

public class TTechTree {
    public static void load() {
        TPlanets.mycelius.techTree = nodeRoot("trabatros", TBlocks.basic_core, () -> {
            nodeProduce(TItems.metallurgy_xp, () -> {

            });

            node(TCustom.crude_metallurgy, () -> {

            });
        });
    }
}