package technical.expansion;

import arc.graphics.g2d.Draw;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Liquid;

public class ThermalRouter extends ThermalLiquidBlock
{
    public float liquidPadding = 1f;

    public ThermalRouter(String name) 
    {
        super(name);
        floating = true;
    }

    public class ThermalRouterBuild extends ThermalLiquidBuild 
    {
        @Override
        public void updateTile() 
        {
            super.updateTile();

            dumpTLiquid(liquids.current());

            // if(liquids.currentAmount() > 0.0001f && timer(timerFlow, 1) && (efficiency > 0 || !needEfficiency)) 
            // {
                
            // }
        }

        @Override
        public void draw()
        {
            Draw.rect(bottomRegion, x, y);

            if(liquids.currentAmount() > 0.001f){
                drawTiledFrames(size, x, y, liquidPadding, liquids.current(), liquids.currentAmount() / liquidCapacity);
            }

            Draw.rect(region, x, y);
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return (liquids.current() == liquid || liquids.currentAmount() < 0.2f);
        }
    }
}
