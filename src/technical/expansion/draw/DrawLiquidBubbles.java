package technical.expansion.draw;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.draw.DrawBlock;

public class DrawLiquidBubbles extends DrawBlock
{
    public Liquid liquid;

    public int amount = 12, sides = 8;
    public float strokeMin = 0.2f, spread = 3f, timeScl = 30f;
    public float recurrence = 6f, radius = 3f;
    public boolean fill = false;

    public DrawLiquidBubbles(Liquid liquid)
    {
        this.liquid = liquid;
    }

    public DrawLiquidBubbles() {}

    @Override
    public void draw(Building build){
        if(build.warmup() <= 0.001f) return;

        var col = liquid != null ? liquid.color : build.liquids.current().color;

        Draw.color(col);
        Draw.alpha(build.warmup());

        rand.setSeed(build.id);
        for(int i = 0; i < amount; i++){
            float x = rand.range(spread), y = rand.range(spread);
            float life = 1f - ((Time.time / timeScl + rand.random(recurrence)) % recurrence);

            if(life > 0){
                float rad = (1f - life) * radius;
                if(fill){
                    Fill.circle(build.x + x, build.y + y, rad);
                }else{
                    Lines.stroke(build.warmup() * (life + strokeMin));
                    Lines.poly(build.x + x, build.y + y, sides, rad);
                }
            }
        }

        Draw.color();
    }
}
