package technical.expansion.draw;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import technical.T;

public class DrawAnimatedRegion extends DrawBlock {
    public TextureRegion[] regions;
    public String suffix = "";
    public float animationSpeed = 6f; // frames per second
    public boolean spinSprite = false;
    public boolean drawPlan = true;
    public boolean buildingRotate = false;
    public float rotateSpeed, x, y, rotation;
    public boolean turnOff = false;

    public DrawAnimatedRegion(String suffix, float animSpeed){
        this.suffix = suffix;
        this.animationSpeed = animSpeed;
    }

    public DrawAnimatedRegion(String suffix, float rotateSpeed, boolean spinSprite){
        this.suffix = suffix;
        this.spinSprite = spinSprite;
        this.rotateSpeed = rotateSpeed;
    }

    public DrawAnimatedRegion(){}

    @Override
    public void draw(Building build){
        if(regions == null || regions.length == 0) return;

        float warm = build.warmup();

        // --- TURN OFF MODE ENABLED ---
        if(turnOff && warm <= 0.001f){
            // Hard snap to frame 0
            int frame = 0;
            float rotationSum = rotation + (buildingRotate ? build.rotdeg() : 0f);

            if(spinSprite){
                Drawf.spinSprite(regions[frame], build.x + x, build.y + y, rotationSum);
            }else{
                Draw.rect(regions[frame], build.x + x, build.y + y, rotationSum);
            }
            return;
        }

        // --- NORMAL MODE (warmup > 0 or turnOff == false) ---
        // Use totalProgress so animation speed is consistent and tied to production/warmup
        float progress = build.totalProgress();

        // frame = progress * animationSpeed  (frames per second)
        float frameFloat = progress * (animationSpeed / 60f);  // 60 ticks per second
        int frame = (int)(frameFloat % regions.length);

        float rotationSum = build.totalProgress() * rotateSpeed
                + rotation
                + (buildingRotate ? build.rotdeg() : 0f);

        if(spinSprite){
            Drawf.spinSprite(regions[frame], build.x + x, build.y + y, rotationSum);
        }else{
            Draw.rect(regions[frame], build.x + x, build.y + y, rotationSum);
        }
    }


    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        if(!drawPlan || regions == null || regions.length == 0) return;
        Draw.rect(regions[0], plan.drawx() + x, plan.drawy() + y, rotation + (buildingRotate ? plan.rotation * 90f : 0f));
    }

    @Override
    public TextureRegion[] icons(Block block){
        if(regions == null || regions.length == 0) return new TextureRegion[0];
        return new TextureRegion[]{regions[0]};
    }

    @Override
    public void load(Block block)
    {
        regions = T.loadMultipleRegions(block.name + suffix);
    }
}
