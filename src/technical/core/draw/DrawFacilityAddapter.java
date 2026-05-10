package technical.core.draw;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import technical.Technical;

public abstract class DrawFacilityAddapter extends DrawBlock
{
    public TextureRegion baseRegion;

    @Override
    public void load(Block block)
    {        
        baseRegion = Core.atlas.find(Technical.name + "-tblock-" + block.size);
    }

    @Override
    public TextureRegion[] icons(Block block)
    {
        return new TextureRegion[]{baseRegion, block.region};
    }

    @Override
    public void getRegionsToOutline(Block block, Seq<TextureRegion> out)
    {
        out.add(block.region);
    }

    
}
