package technical.core;

import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Groups;
import mindustry.gen.WeatherState;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.world.draw.DrawBlock;
import mindustry.world.meta.Env;
import technical.core.kinetic.KineticBlock;

public class PrecipitationCollector extends KineticBlock
{
    public float liquidGatherIntensity = 0.01f;
    public float warmupSpeed = 0.01f;

    public DrawBlock drawer;

    public PrecipitationCollector(String name)
    {
        super(name);

        hasLiquids = true;

        envEnabled |= Env.terrestrial;
    }

    @Override
    public void load()
    {
        super.load();
        drawer.load(this);
    }

    @Override
    public TextureRegion[] icons()
    {
        return drawer.finalIcons(this);
    }

    public class PrecipitationCollectorBuild extends KineticBuild
    {
        public LiquidStack collectedLiquid = new LiquidStack(null, 0);
        public float warmup;

        private void updateRainIntensity()
        {
            float maxIntensity = 0f;
            Liquid maxLiquid = null;

            for (WeatherState ws : Groups.weather)
            {
                if (ws.weather instanceof TWeather tw)
                {
                    if (ws.intensity > maxIntensity)
                    {
                        maxIntensity = ws.intensity;
                        maxLiquid = tw.liquid;
                    }
                }
            }

            collectedLiquid.set(maxLiquid, maxIntensity);
        }

        @Override
        public void updateTile()
        {
            super.updateTile();

            updateRainIntensity();

            if (collectedLiquid.liquid != null && collectedLiquid.amount > 0)
            {
                float am = collectedLiquid.amount * liquidGatherIntensity;
                liquids.add(collectedLiquid.liquid, am);

                warmup = Mathf.approachDelta(warmup, 1f, am * warmupSpeed);
            }
            else
            {
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            if (liquids.currentAmount() > 0)
            {
                dumpLiquid(liquids.current());
            }
        }

        @Override
        public float warmup()
        {
            return warmup;
        }

        @Override
        public void draw()
        {
            drawer.draw(this);
        }

        @Override
        public void drawLight()
        {
            super.drawLight();
            drawer.drawLight(this);
        }
    }
}
