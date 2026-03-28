package technical.expansion.draw;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import technical.Technical;
import technical.expansion.FacilityAddapter;
import technical.expansion.FacilityAddapter.FacilityAddapterBuild;

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
