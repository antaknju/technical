package technical.core.draw;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import technical.content.TFx;

public class DrawSlapLiquid extends DrawBlock{
    public Liquid drawLiquid;
    public TextureRegion liquid;
    public String suffix = "-liquid";
    public float alpha = 1f;

    public DrawSlapLiquid(Liquid drawLiquid){
        this.drawLiquid = drawLiquid;
    }

    public DrawSlapLiquid(){
    }

    @Override
    public void draw(Building build){
        Liquid drawn = drawLiquid != null ? drawLiquid : build.liquids.current();
        if (build.warmup() > 0f) 
        {
            if (build.timer.get(100f / build.warmup() * Mathf.random(0.5f, 2f)))
                TFx.slapLiquid.at(build.x, build.y, drawn.color);
        }
    }

    @Override
    public void load(Block block)
    {
        liquid = Core.atlas.find(block.name + suffix);
    }
}
