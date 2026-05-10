package technical.core.draw;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import technical.core.ThermalLiquidBlock;
import technical.core.ThermalLiquidBlock.ThermalLiquidBuild;

public class DrawThermalHeatOverlay extends DrawBlock
{
    public TextureRegion heatRegion;

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{};
    }

    @Override
    public void load(Block block)
    {
        super.load(block);
        heatRegion = Core.atlas.find(block.name + "-heat");
    }

    @Override
    public void draw(Building build) 
    {
        drawHeatOverlay(heatRegion, (ThermalLiquidBuild)build);
    }

    protected void drawHeatOverlay(TextureRegion reg, ThermalLiquidBuild build) 
    {
        float heat = build.heat;
        float aheat = Math.abs(heat);
        ThermalLiquidBlock block = (ThermalLiquidBlock)build.block;
        
        if(aheat <= 0f || reg == null) return;

        float frac = aheat / block.maxHeat; frac *= 3;

        Draw.z(Layer.blockAdditive);
        Draw.blend(Blending.additive);

        Color color = heat >= 0f ? block.heatColor : block.coolColor;
        Draw.color(color, frac * (color.a * (1f - block.heatPulse + Mathf.absin(block.heatPulseScl, block.heatPulse))));

        Draw.rect(reg, build.x, build.y, build.drawrot());
        Draw.blend();
        Draw.color();
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list)
    {
        
    }
}
