package technical.core.draw;

import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import technical.core.ThermalLiquidBlock;
import technical.core.extendable.Extension.ExtensionBuild;

public class DrawLiquidCustom extends DrawBlock
{
    public Liquid drawLiquid;
    public float padding;
    public float padLeft = -1, padRight = -1, padTop = -1, padBottom = -1;
    public float alpha = 1f;

    /** When liquid is Output it's drawn with warmup opacity because of instant liquid removal with pipes */
    public boolean drawFromWarmup = false;

    /** Works only when this is Extension, when it's dependent on Extendable it uses liquid info from Extendable (used mainly with Storage Blocks)  */
    public boolean isExtendableDependant = false;

    public DrawLiquidCustom(Liquid drawLiquid, boolean drawFromWarmup, boolean isExtendableDependant)
    {
        this.drawLiquid = drawLiquid;
        this.drawFromWarmup = drawFromWarmup;
        this.isExtendableDependant = isExtendableDependant;
    }

    public DrawLiquidCustom(boolean drawFromWarmup, boolean isExtendableDependant)
    {
        this.drawFromWarmup = drawFromWarmup;
        this.isExtendableDependant = isExtendableDependant;
    }

    @Override
    public void draw(Building build)
    {
        Liquid drawn = drawLiquid != null ? drawLiquid : build.liquids.current();
        float a = build.liquids.get(drawn) / build.block.liquidCapacity;
        if (isExtendableDependant && build instanceof ExtensionBuild eb)
        {
            if (eb.Extendable != null && eb.Extendable.block.hasLiquids)
            {
                drawn = drawLiquid != null ? drawLiquid : eb.Extendable.liquids.current();
                a = eb.Extendable.liquids.get(drawn) / eb.block.liquidCapacity;
            }
        }

        ThermalLiquidBlock.drawTiledFrames(build.block.size, build.x, build.y, padLeft, padRight, padTop, padBottom, drawn, (drawFromWarmup ? build.warmup() : a) * alpha);
    }

    @Override
    public void load(Block block)
    {
        if(padLeft < 0) padLeft = padding;
        if(padRight < 0) padRight = padding;
        if(padTop < 0) padTop = padding;
        if(padBottom < 0) padBottom = padding;
    }
}