package technical.core.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import technical.core.ThermalLiquidBlock.ThermalLiquidBuild;

public class DrawThermalOverlay extends DrawBlock
{
    public TextureRegion heatRegion;

    @Override
    public void load(Block block)
    {
        heatRegion = Core.atlas.find(block.name + "-heat");
    }

    @Override
    public void draw(Building build) 
    {
        ThermalLiquidBuild tlb = (ThermalLiquidBuild)build;

        tlb.drawHeatOverlay(heatRegion);

        Draw.reset();
    }
}
