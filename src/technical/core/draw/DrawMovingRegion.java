package technical.core.draw;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Vec2;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.world.*;
import mindustry.world.draw.*;

/**
 * @deprecated Use {@link DrawTransformRegion} instead.
 */
@Deprecated()
public class DrawMovingRegion extends DrawBlock 
{
    public String suffix = "";

    public Vec2 offset;
    public Vec2 move;
    public float moveRot = 0f;

    public Interp interp = Interp.slowFast;

    public TextureRegion region;

    public boolean orthoLayering = false;

    public DrawMovingRegion(String suffix, Vec2 offset, Vec2 move, float moveRot)
    {
        this.suffix = suffix;

        this.offset = offset;
        this.move = move;
    
        this.moveRot = moveRot;
    }

    public DrawMovingRegion(String suffix, Vec2 offset, Vec2 move, float moveRot, Interp interp)
    {
        this.suffix = suffix;

        this.offset = offset;
        this.move = move;
    
        this.moveRot = moveRot;

        this.interp = interp;
    }

    @Override
    public void draw(Building build)
    {
        if(region == null) return;

        if (orthoLayering)
            Draw.z(Layer.blockOver - build.tile.y * 0.01f);

        float p = interp.apply(Interp.slope.apply(build.progress()));

        float dx = move.x * p;
        float dy = move.y * p;
        float dr = moveRot * p;

        Draw.rect(
            region,
            build.x + offset.x + dx,
            build.y + offset.y + dy,
            dr
        );
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list)
    {
        if(region == null) return;

        Draw.rect(
            region,
            plan.drawx(),
            plan.drawy()
        );
    }

    @Override
    public TextureRegion[] icons(Block block)
    {
        return region == null ? new TextureRegion[0] : new TextureRegion[]{region};
    }

    @Override
    public void load(Block block)
    {
        region = Core.atlas.find(block.name + suffix);
    }
}
