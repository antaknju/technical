package technical.expansion.draw;

import arc.func.Cons;
import arc.func.Func;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.Effect;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.world.draw.DrawBlock;
import technical.content.TFx;

public class DrawEffect extends DrawBlock 
{
    public Func<Building, Color> color;
    public float effectIntensity;
    public Effect effect;
    public int groupMin = 1;
    public int groupMax = 1;
    public float offset = 0;
    public boolean randomRotation = false;

    public DrawEffect(Color col, float intensity, Effect effect)
    {
        this.color = (Building build) -> col;

        this.effectIntensity = intensity;
        this.effect = effect;
    }

    public DrawEffect(Func<Building, Color> color, float intensity, Effect effect)
    {
        this.color = color;
        this.effectIntensity = intensity;
        this.effect = effect;
    }

    public DrawEffect(float intensity, Effect effect)
    {
        this.color = (Building build) -> Color.white;
        this.effectIntensity = intensity;
        this.effect = effect;
    }

    public DrawEffect offset(float offset)
    {
        this.offset = offset;
        return this;
    }

    public DrawEffect group(int min, int max)
    {
        this.groupMin = min;
        this.groupMax = max;
        return this;
    }

    @Override
    public void draw(Building build)
    {
        Color col = color.get(build);

        if (build.warmup() > 0f) 
        {
            if ((int)(Time.time + build.id) % (int)(60f / (effectIntensity * build.efficiencyScale())) == 0)
            {
                int group = Mathf.random(groupMin, groupMax);
                for (int i = 0; i < group; i++)
                {
                    float nx = build.x + Mathf.range(-offset, offset);
                    float ny = build.y + Mathf.range(-offset, offset);

                    if (randomRotation)
                    {
                        effect.at(nx, ny, Mathf.random(0, 360f), col);
                    }
                    else
                    {
                        effect.at(nx, ny, col);
                    }
                }
            }
        }
    }
}
