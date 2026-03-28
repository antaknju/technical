package technical.expansion.draw;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import technical.TCol;

public class DrawReactorCore extends DrawBlock
{
    public float lightRadius = 100f, lightAlpha = 0.70f, lightSinScl = 10f, lightSinMag = 5;
    public float flameRadius = 3f, flameRadiusIn = 1.9f, flameRadiusScl = 5f, flameRadiusMag = 1f, flameRadiusInMag = 0.5f;
    public float flameX = 0, flameY = 0;

    public float orbitRadiusX1 = 5f, orbitRadiusY1 = 2.5f, orbitSpeed1 = 1.5f, orbitSize1 = 1.2f;
    public float orbitRadiusX2 = 3f, orbitRadiusY2 = 4.5f, orbitSpeed2 = -1.1f, orbitSize2 = 1.4f;

    public DrawReactorCore() {}

    @Override
    public void load(Block block){
        block.emitLight = true;
        block.lightClipSize = Math.max(block.lightClipSize, (lightRadius + lightSinMag) * 2f * block.size);
    }

    @Override
    public void draw(Building build){
        float eff = Mathf.clamp(build.efficiencyScale() * build.efficiency);
        if(eff > 0f){

            float g = 0.3f;
            float r = 0.06f;
            float cr = Mathf.random(0.1f);

            Draw.color(TCol.uranium);

            Draw.z(Layer.block + 0.01f);

            Draw.alpha(eff);
            // Draw.rect(top, build.x, build.y);

            Draw.alpha(((1f - g) + Mathf.absin(Time.time, 8f, g) + Mathf.random(r) - r) * eff);

            Fill.circle(build.x + flameX, build.y + flameY, flameRadius + Mathf.absin(Time.time, flameRadiusScl, flameRadiusMag) + cr);
            Draw.color(TCol.uranium, eff);
            Fill.circle(build.x + flameX, build.y + flameY, flameRadiusIn + Mathf.absin(Time.time, flameRadiusScl, flameRadiusInMag) + cr);

            // First Orbit
            float angle1 = Time.time * orbitSpeed1;
            float ox1 = Mathf.cosDeg(angle1) * orbitRadiusX1;
            float oy1 = Mathf.sinDeg(angle1) * orbitRadiusY1;
            Draw.color(TCol.uranium.cpy().shiftHue(Mathf.absin(Time.time, 1f, 5f)));
            Fill.circle(build.x + flameX + ox1, build.y + flameY + oy1, orbitSize1*eff);

            // Second Orbit
            float angle2 = Time.time * orbitSpeed2 + 13f;
            float ox2 = Mathf.cosDeg(angle2) * orbitRadiusX2;
            float oy2 = Mathf.sinDeg(angle2) * orbitRadiusY2;
            Draw.color(TCol.uranium.cpy().shiftHue(Mathf.absin(Time.time + 20f, 1f, 5f)));
            Fill.circle(build.x + flameX + ox2, build.y + flameY + oy2, orbitSize2*eff);

            Draw.color();
        }
    }

    @Override
    public void drawLight(Building build){
        Drawf.light(build.x + flameX, build.y + flameY, (lightRadius + Mathf.absin(lightSinScl, lightSinMag)) * build.efficiencyScale() * build.block.size, TCol.uranium, lightAlpha);
    }
}
