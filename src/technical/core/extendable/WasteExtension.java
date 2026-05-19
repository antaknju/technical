package technical.core.extendable;

import arc.Core;
import mindustry.graphics.Pal;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.world.consumers.ConsumeCoolant;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import technical.content.TLiquids;
import technical.util.Fr;

public class WasteExtension extends Extension
{
    public LiquidStack outputLiquid = new LiquidStack(TLiquids.water, 1 * Fr.liquid);

    public boolean explodeOnFull = false;

    public WasteExtension(String name)
    {
        super(name);
    }

    @Override
    public void init()
    {
        super.init();

        if (explodeOnFull)
            canExplode = true;
    }

    @Override
    public void setBars()
    {
        super.setBars();

        addLiquidBar(outputLiquid.liquid);
    }

    @Override
    public void setStats()
    {
        super.setStats();

        stats.add(Stat.output, StatValues.liquids(1f, outputLiquid));
    }

    public class WasteExtensionBuild extends ExtensionBuild
    {
        public void updateTile()
        {
            super.updateTile();

            if (warmup > 0f)
            {
                liquids.add(outputLiquid.liquid, outputLiquid.amount * warmup);
            }

            if (liquids.currentAmount() > 0)
                dumpLiquid(outputLiquid.liquid);
        }

        @Override
        public boolean shouldExplode()
        {
            return explodeOnFull && liquids.get(outputLiquid.liquid) >= liquidCapacity - 0.01f;
        }
    }
}
