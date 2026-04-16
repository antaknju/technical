package technical.expansion.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.geom.Vec2;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawTransformRegion extends DrawBlock
{
    public String suffix = "";

    public Transform from;
    public Transform to;

    public Interp interp = Interp.slowFast;

    public TextureRegion region;

    public float progress_offset = 0;

    public boolean orthographicLayering = false;

    public DrawTransformRegion(String suffix, Transform from, Transform to, Interp interp, float progress_offset)
    {
        this.suffix = suffix;

        this.from = from;
        this.to = to;

        this.interp = interp;
        this.progress_offset = progress_offset;
    }

    @Override
    public void draw(Building build)
    {
        if(region == null) return;

        if (orthographicLayering)
            Draw.z(Layer.blockOver - build.tile.y * 0.01f);

        float p = interp.apply(Interp.slope.apply((build.progress() + progress_offset) % 1));

        var t = Transform.progress(from, to, p);

        Draw.scl(t.scale.x, t.scale.y);

        Draw.rect(
            region,
            build.x + t.pos.x,
            build.y + t.pos.y,
            t.rotation
        );

        Draw.reset();
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
