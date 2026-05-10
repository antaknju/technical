package technical.core.draw;

import arc.math.geom.Vec2;

public class Transform
{
    public static Transform tmp = new Transform();
    public static Transform tmp_progress = new Transform();

    public Vec2 scale;
    public Vec2 pos;
    public float rotation;

    private Transform()
    {

    }

    public Transform(float scale_x, float scale_y, float pos_x, float pos_y, float rotation)
    {
        this.scale = new Vec2(scale_x,scale_y);
        this.pos = new Vec2(pos_x, pos_y);
        this.rotation = rotation;
    }

    public Transform(Vec2 scale, Vec2 pos, float rotation)
    {
        this.scale = scale;
        this.pos = pos;
        this.rotation = rotation;
    }

    public static Transform zero()
    {
        var t = new Transform();
        t.scale = new Vec2(0, 0);
        t.pos = new Vec2(0, 0);
        t.rotation = 0;
        return t;
    }

    public Transform set(Vec2 scale, Vec2 pos, float rotation)
    {
        this.scale = scale;
        this.pos = pos;
        this.rotation = rotation;

        return this;
    }

    public static Transform progress(Transform from, Transform to, float progress)
    {
        float clamped_progress = Math.max(0, Math.min(1, progress));

        Vec2 scale = new Vec2(
                from.scale.x + (to.scale.x - from.scale.x) * clamped_progress,
                from.scale.y + (to.scale.y - from.scale.y) * clamped_progress
        );

        Vec2 pos = new Vec2(
                from.pos.x + (to.pos.x - from.pos.x) * clamped_progress,
                from.pos.y + (to.pos.y - from.pos.y) * clamped_progress
        );

        float rotation = from.rotation + (to.rotation - from.rotation) * clamped_progress;

        return tmp_progress.set(scale, pos, rotation);
    }
}
