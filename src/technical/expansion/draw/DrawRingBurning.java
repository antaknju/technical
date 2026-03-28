package technical.expansion.draw;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.world.draw.*;

public class DrawRingBurning extends DrawBlock {
    public Color lightColor = Pal.lightFlame, midColor = Pal.darkFlame, endColor = Color.darkGray;

    public float ringRadius = 3f;
    public float minCenterRadius = 0f;
    public float alpha = 0.9f;

    public int clusters = 6;
    public int clusterSizeMin = 3;
    public int clusterSizeMax = 6;

    public float particleLife = 30f;
    public float particleSize = 0.5f;
    public float particleGrow = 1.2f;

    public DrawRingBurning(){}

    @Override
    public void draw(Building build){
        if(build.warmup() <= 0f) return;
        float w = build.warmup();

        long seed = build.id;
        float base = Time.time / particleLife;
        rand.setSeed(seed);

        for(int c = 0; c < clusters; c++){
            float clusterAngle = rand.random(360f);
            int amount = rand.random(clusterSizeMin, clusterSizeMax);

            for(int i = 0; i < amount; i++){
                float fin = (rand.random(1f) + base) % 1f;
                float fout = 1f - fin;

                // swirl motion + cluster spread
                float angle = clusterAngle + rand.range(25f) + fin * 40f;

                // inward interpolation but never reaches pure zero
                float inward = Mathf.lerp(ringRadius, minCenterRadius, Interp.pow2In.apply(fin)) * w;

                // small noise offset near the center for variation
                float noiseX = rand.range(1.2f) * fout;
                float noiseY = rand.range(1.2f) * fout;

                float x = Angles.trnsx(angle, inward) + noiseX;
                float y = Angles.trnsy(angle, inward) + noiseY;

                // better color transition: warm on ring → hottest mid → dark center
                float heat = Interp.pow3Out.apply(fout);
                Draw.color(
                    lightColor,
                    midColor,
                    endColor,
                    heat * 0.8f + 0.2f
                );

                Fill.circle(
                    build.x + x,
                    build.y + y,
                    particleSize + fout * particleGrow + rand.range(0.25f)
                );
            }
        }

        Draw.color();
    }
}
