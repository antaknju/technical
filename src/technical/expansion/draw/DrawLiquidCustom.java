package technical.expansion.draw;

import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.draw.DrawBlock;
import technical.expansion.TLiquid;
import technical.expansion.ThermalLiquidBlock;
import technical.expansion.ext.Extension.ExtensionBuild;

public class DrawLiquidCustom extends DrawBlock
{
    public Liquid drawLiquid;
    public float padding;
    public float padLeft = -1, padRight = -1, padTop = -1, padBottom = -1;
    public float alpha = 1f;

    public boolean isLiquidOutput = false;
    public boolean isDependant = false;

    public DrawLiquidCustom(Liquid drawLiquid, boolean isLiquidOutput, boolean isDependant)
    {
        this.drawLiquid = drawLiquid;
        this.isLiquidOutput = isLiquidOutput;
        this.isDependant = isDependant;
    }

    public DrawLiquidCustom(boolean isLiquidOutput, boolean isDependant)
    {
        this.isLiquidOutput = isLiquidOutput;
        this.isDependant = isDependant;
    }

    @Override
    public void draw(Building build)
    {
        Liquid drawn = drawLiquid != null ? drawLiquid : build.liquids.current();
        float a = build.liquids.get(drawn) / build.block.liquidCapacity;
        if (isDependant && build instanceof ExtensionBuild eb)
        {
            if (eb.Extendable != null && eb.Extendable.block.hasLiquids)
            {
                drawn = drawLiquid != null ? drawLiquid : eb.Extendable.liquids.current();
                a = eb.Extendable.liquids.get(drawn) / eb.block.liquidCapacity;
            }
        }

        ThermalLiquidBlock.drawTiledFrames(build.block.size, build.x, build.y, padLeft, padRight, padTop, padBottom, drawn, (isLiquidOutput ? build.warmup() : a) * alpha);
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