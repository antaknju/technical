package technical.expansion.draw;

import arc.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.DrawBlock;
import technical.Technical;
import technical.expansion.ext.ExtendableCrafter;

/** This must be used in conjunction with another DrawBlock; it only draws outputs. */
public class DrawLiquidOutputsExtendable extends DrawBlock 
{
    public TextureRegion[][] liquidOutputRegions;
    public boolean flip = false;
    public boolean public_bundle = true;

    @Override
    public void draw(Building build){
        ExtendableCrafter crafter = (ExtendableCrafter)build.block;
        if(crafter.outputLiquids == null) return;

        for(int i = 0; i < crafter.outputLiquids.length; i++){
            int side = i < crafter.liquidOutputDirections.length ? crafter.liquidOutputDirections[i] : -1;
            if(side != -1){
                int realRot = (side + build.rotation) % 4;
                Draw.rect(liquidOutputRegions[realRot > 1 ? 1 : 0][i], build.x, build.y, realRot * 90);
            }
        }
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        ExtendableCrafter crafter = (ExtendableCrafter)block;
        if(crafter.outputLiquids == null) return;

        for(int i = 0; i < crafter.outputLiquids.length; i++){
            int side = i < crafter.liquidOutputDirections.length ? crafter.liquidOutputDirections[i] : -1;
            if(side != -1){
                int realRot = (side + plan.rotation) % 4;
                Draw.rect(liquidOutputRegions[realRot > 1 ? 1 : 0][i], plan.drawx(), plan.drawy(), realRot * 90);
            }
        }
    }

    @Override
    public void load(Block block){
        if(!(block instanceof ExtendableCrafter crafter)) return;

        if(crafter.outputLiquids == null) return;

        liquidOutputRegions = new TextureRegion[2][crafter.outputLiquids.length];
        for(int i = 0; i < crafter.outputLiquids.length; i++){
            if (flip)
            {
                for(int j = 1; j <= 2; j++){
                    liquidOutputRegions[j - 1][i] = Core.atlas.find((public_bundle ? Technical.name : block.name) + "-" + crafter.outputLiquids[i].liquid.name + "-output" + j);
                }
            }
            else
            {
                for(int j = 1; j <= 2; j++){
                    liquidOutputRegions[j - 1][i] = Core.atlas.find((public_bundle ? Technical.name: block.name) + "-" + crafter.outputLiquids[i].liquid.name + "-output");
                }
            }
        }
    }
}
