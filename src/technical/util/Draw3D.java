package technical.util;

import arc.graphics.Color;
import arc.graphics.g3d.VertexBatch3D;
import arc.math.Angles;
import arc.math.geom.Vec3;


public class Draw3D
{
    private static final Vec3
            r1 = new Vec3(), r2 = new Vec3(), r3 = new Vec3(), r4 = new Vec3(),
            r5 = new Vec3(), r6 = new Vec3(), r7 = new Vec3(), r8 = new Vec3();

    private static Vec3 rotate(Vec3 v, float pitch, float yaw, float roll)
    {
        if (pitch == 0 && yaw == 0 && roll == 0) return v;

        v.rotate(Vec3.X, pitch);
        v.rotate(Vec3.Y, yaw);
        v.rotate(Vec3.Z, roll);

        return v;
    }

    public static void box(VertexBatch3D batch, Vec3 pos, Vec3 scale, Color color)
    {
        box(batch, pos, scale, color, 0, 0, 0);
    }

    public static void box(VertexBatch3D batch, Vec3 pos, Vec3 scale, Color color, float pitch, float yaw, float roll)
    {
        float hx = scale.x / 2f, hy = scale.y / 2f, hz = scale.z / 2f;

        r1.set(-hx, -hy,  hz); r2.set( hx, -hy,  hz);
        r3.set( hx,  hy,  hz); r4.set(-hx,  hy,  hz);
        r5.set(-hx, -hy, -hz); r6.set( hx, -hy, -hz);
        r7.set( hx,  hy, -hz); r8.set(-hx,  hy, -hz);

        // Apply rotation and position
        Vec3[] verts = {r1, r2, r3, r4, r5, r6, r7, r8};
        for (Vec3 v : verts) {
            rotate(v, pitch, yaw, roll).add(pos);
        }

        // Faces
        batch.quad(r1, r2, r3, r4, color);
        batch.quad(r6, r5, r8, r7, color);
        batch.quad(r5, r6, r2, r1, color);
        batch.quad(r4, r3, r7, r8, color);
        batch.quad(r5, r1, r4, r8, color);
        batch.quad(r2, r6, r7, r3, color);
    }

    public static void cylinder(VertexBatch3D batch, Vec3 pos, float radius, float length, int segments, Color color, float pitch, float yaw, float roll)
    {
        float hl = length / 2f;
        float step = 360f / segments;

        for (int i = 0; i < segments; i++)
        {
            float a1 = i * step;
            float a2 = (i + 1) * step;

            float cx1 = Angles.trnsx(a1, radius), cy1 = Angles.trnsy(a1, radius);
            float cx2 = Angles.trnsx(a2, radius), cy2 = Angles.trnsy(a2, radius);

            // Side quad
            r1.set(cx1, cy1, -hl);
            r2.set(cx2, cy2, -hl);
            r3.set(cx2, cy2, hl);
            r4.set(cx1, cy1, hl);

            rotate(r1, pitch, yaw, roll).add(pos);
            rotate(r2, pitch, yaw, roll).add(pos);
            rotate(r3, pitch, yaw, roll).add(pos);
            rotate(r4, pitch, yaw, roll).add(pos);

            batch.quad(r1, r2, r3, r4, color);

            // Back Cap
            Vec3 center1 = r5.set(0, 0, -hl);
            rotate(center1, pitch, yaw, roll).add(pos);
            Vec3 p1 = r6.set(cx1, cy1, -hl);
            Vec3 p2 = r7.set(cx2, cy2, -hl);
            rotate(p1, pitch, yaw, roll).add(pos);
            rotate(p2, pitch, yaw, roll).add(pos);
            batch.tri(center1, p2, p1, color);

            // Front Cap
            Vec3 center2 = r5.set(0, 0, hl);
            rotate(center2, pitch, yaw, roll).add(pos);
            Vec3 p3 = r6.set(cx1, cy1, hl);
            Vec3 p4 = r7.set(cx2, cy2, hl);
            rotate(p3, pitch, yaw, roll).add(pos);
            rotate(p4, pitch, yaw, roll).add(pos);
            batch.tri(center2, p3, p4, color);
        }
    }
}