package technical.content;

import mindustry.type.StatusEffect;
import technical.util.TCol;
import technical.core.TStatus;

public class TStatuses
{
    public static StatusEffect wet;

    public static void load()
    {
        wet = new TStatus("wet"){{
            color = TCol.water;

            speedMultiplier = 0.8f;
            effect = TFx.wet;
            effectChance = 0.09f;
        }};
    }
}