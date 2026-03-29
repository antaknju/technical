package technical.expansion.draw;

import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.draw.DrawBlock;
import technical.expansion.ext.Extension;
import technical.expansion.ext.Extension.ExtensionBuild;

public class DrawExtensionLiquid extends DrawBlock
{
    public Liquid drawLiquid;
    public float padding;
    public float padLeft = -1, padRight = -1, padTop = -1, padBottom = -1;
    public float alpha = 1f;

    public DrawExtensionLiquid(Liquid drawLiquid, float padding)
    {
        this.drawLiquid = drawLiquid;
        this.padding = padding;
    }

    public DrawExtensionLiquid(Liquid drawLiquid)
    {
        this.drawLiquid = drawLiquid;
    }

    public DrawExtensionLiquid() {}

    @Override
    public void draw(Building build)
    {
        if (((ExtensionBuild)build).Extendable == null) return;
        var ext = ((ExtensionBuild)build).Extendable;
        if(!ext.block.hasLiquids) return;

        Liquid drawn = drawLiquid != null ? drawLiquid : ext.liquids.current();
        LiquidBlock.drawTiledFrames(build.block.size, build.x, build.y, padLeft, padRight, padTop, padBottom, drawn, ext.liquids.get(drawn) / ((Extension)build.block).additionalLiquidStorage * alpha);
    }

    @Override
    public void load(Block block){
        if(padLeft < 0) padLeft = padding;
        if(padRight < 0) padRight = padding;
        if(padTop < 0) padTop = padding;
        if(padBottom < 0) padBottom = padding;
    }
}


//public class DrawExtensionLiquid extends DrawBlock {
//    public Liquid drawLiquid;
//    public TextureRegion liquid;
//    public String suffix = "-liquid";
//    public float alpha = 0.8f;
//
//    public DrawExtensionLiquid(Liquid drawLiquid)
//    {
//        this.drawLiquid = drawLiquid;
//    }
//
//    public DrawExtensionLiquid() {}
//
//    @Override
//    public void draw(Building build)
//    {
//        if (((ExtensionBuild)build).Extendable == null) return;
//        var ext = ((ExtensionBuild)build).Extendable;
//        if(!ext.block.hasLiquids) return;
//
//        Liquid drawn = drawLiquid != null ? drawLiquid : ext.liquids.current();
//        Drawf.liquid(liquid, build.x, build.y,
//            ext.liquids.get(drawn) / ext.block.liquidCapacity * alpha,
//            drawn.color
//        );
//    }
//
//    @Override
//    public void load(Block block)
//    {
//        liquid = Core.atlas.find(block.name + suffix);
//    }
//}
