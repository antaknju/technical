package technical.core.draw;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.world.draw.*;

public class DrawCenteredBurning extends DrawBlock {
    public Color lightColor = Pal.lightFlame, darkColor = Pal.darkFlame, smokeColor = Color.gray;
    public float flameRad = 2f;
    public float alpha = 0.8f;

    public int particles = 15;
    public float particleLife = 20f;
    public float particleSpeed = 5f;
    public float particleSize = 0.4f;
    public boolean drawCenter = true;

    public DrawCenteredBurning() {}

    @Override
    public void draw(Building build) {
        if (build.warmup() <= 0f) return;

        float w = build.warmup();

        // Center flames
        if (drawCenter) {
            float si = Mathf.absin(3f, 0.3f);
            Draw.color(darkColor, alpha * w);
            Fill.circle(build.x, build.y, flameRad + si);
            Draw.color(lightColor, alpha * w);
            Fill.circle(build.x, build.y, (flameRad + 0.5f + si) * w);
        }

        long id = build.id;
        rand.setSeed(id);
        float timeFactor = Time.time / particleLife;

        for (int i = 0; i < particles; i++) {
            // Fraction of life [0,1]
            float fin = (rand.random(1f) + timeFactor) % 1f;
            float fout = 1f - fin;

            // Angle and distance
            float angle = rand.random(360f);
            float len = particleSpeed * Interp.pow2Out.apply(fin) * w;

            float x = Angles.trnsx(angle, len);
            float y = Angles.trnsy(angle, len);

            // Color fades from light/dark to smoke
            Draw.color(lightColor, darkColor, smokeColor, fin);

            // Particle radius shrinks over life
            Fill.circle(build.x + x, build.y + y, particleSize + fout * 1.2f);
        }

        Draw.reset();
    }
}
