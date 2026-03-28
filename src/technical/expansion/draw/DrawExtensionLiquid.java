package technical.expansion.draw;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import technical.expansion.ext.Extension.ExtensionBuild;

public class DrawExtensionLiquid extends DrawBlock {
    public Liquid drawLiquid;
    public TextureRegion liquid;
    public String suffix = "-liquid";
    public float alpha = 0.8f;

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
        Drawf.liquid(liquid, build.x, build.y,
            ext.liquids.get(drawn) / ext.block.liquidCapacity * alpha,
            drawn.color
        );
    }

    @Override
    public void load(Block block)
    {
        liquid = Core.atlas.find(block.name + suffix);
    }
}
