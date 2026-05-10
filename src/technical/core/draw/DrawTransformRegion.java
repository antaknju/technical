package technical.core.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
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
    public int times = 1;

    public boolean orthographicLayering = false;

    public DrawTransformRegion(String suffix, Transform from, Transform to, Interp interp, float progress_offset)
    {
        this.suffix = suffix;

        this.from = from;
        this.to = to;

        this.interp = interp;
        this.progress_offset = progress_offset;
    }

    public DrawTransformRegion(String suffix, Transform from, Transform to, Interp interp, float progress_offset, int times)
    {
        this.suffix = suffix;

        this.from = from;
        this.to = to;

        this.interp = interp;
        this.progress_offset = progress_offset;
        this.times = times;
    }

    @Override
    public void draw(Building build)
    {
        if(region == null) return;

        if (orthographicLayering)
            Draw.z(Layer.blockOver - build.tile.y * 0.01f);

//        float p = interp.apply(Interp.slope.apply((build.progress() + progress_offset) % 1) * times);

        float t = ((build.progress() * times) + progress_offset) % 1f;

        float ping_pong = t < 0.5f ? t * 2f : (1f - t) * 2f;

        float p = interp.apply(Interp.slope.apply(ping_pong));

        var tp = Transform.progress(from, to, p);

        Draw.scl(tp.scale.x, tp.scale.y);

        Draw.rect(
            region,
            build.x + tp.pos.x,
            build.y + tp.pos.y,
            tp.rotation
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
