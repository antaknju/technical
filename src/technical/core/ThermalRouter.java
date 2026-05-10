package technical.core;

import mindustry.gen.Building;
import mindustry.type.Liquid;

public class ThermalRouter extends ThermalLiquidBlock
{
    public ThermalRouter(String name) 
    {
        super(name);
        floating = true;
        solid = false;
    }

    public class ThermalRouterBuild extends ThermalLiquidBuild 
    {
        @Override
        public void updateTile() 
        {
            super.updateTile();

            dumpTLiquid(liquids.current());
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return (liquids.current() == liquid || liquids.currentAmount() < 0.2f);
        }
    }
}
