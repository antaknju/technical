package technical.content;

import mindustry.content.StatusEffects;
import mindustry.gen.Sounds;
import mindustry.type.weather.RainWeather;
import mindustry.world.meta.Attribute;
import technical.core.TRainWeather;
import technical.core.TUnitFactory;
import technical.core.TWeather;
import technical.util.TCol;

public class TWeathers
{
    public static TWeather rain;

    public static void load()
    {
        rain = new TRainWeather("rain"){{
            attrs.set(Attribute.light, -0.2f);
            attrs.set(Attribute.water, 0.2f);

            status = TStatuses.wet;
            sound = Sounds.rain;
            soundVol = 0.25f;

            liquid = TLiquids.water;
            color = TCol.water;
        }};
    }
}
