package technical.expansion.draw;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.Eachable;
import mindustry.world.*;
import mindustry.world.draw.*;
import technical.Technical;
import technical.content.TFx;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;

public class DrawSteamOperator extends DrawBlock 
{
    public TextureRegion operatorSprite;
    public String suffix = "";
    public Color color = Color.white.cpy();
    public boolean public_bundle = false;
    public float steamChance = 0.2f;
    public int steamCount = 4;
    public float steamRadius = 3f;
    public float sizeMin = 0.85f;
    public float sizeMax = 1f;

    public DrawSteamOperator(String suffix, Color color){
        this.suffix = suffix;
        this.color = color;
    }

    public DrawSteamOperator(String suffix){
        this.suffix = suffix;
    }

    public DrawSteamOperator(){
    }

    @Override
    public void load(Block block){
        operatorSprite = Core.atlas.find((public_bundle ? Technical.name : block.name) + "-" + suffix);
    }

    @Override
    public void draw(Building build)
    {
        float progress = build.efficiencyScale() > 0 ? build.progress() : 0;

        float size = sizeMin + (sizeMax - sizeMin) * (1f - Mathf.sin(progress * Mathf.PI));

        Draw.scl(size);
        Draw.rect(operatorSprite, build.x, build.y);
        Draw.reset();

        if(Mathf.chance(steamChance) && Mathf.within(progress, 0.45f, 0.55f) && build.warmup() > 0){
            for(int i = 0; i < steamCount; i++){
                float angle = i * 360f / steamCount;
                float nx = build.x + Angles.trnsx(angle, build.block.size * steamRadius);
                float ny = build.y + Angles.trnsy(angle, build.block.size * steamRadius);
                TFx.littleSmoke.at(nx, ny, color);
            }
        }
    }

    @Override
    public TextureRegion[] icons(Block block)
    {
        return new TextureRegion[]{operatorSprite};
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list)
    {
        Draw.rect(operatorSprite, plan.drawx(), plan.drawy());
    }
}