package technical.core;

import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.liquid.LiquidJunction;
import technical.content.TFx;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.type.Liquid;

public class ThermalConduitHeater extends ThermalConduit 
{
    public float heatOutput = 30;
    public float itemDuration = 120f;
    public Effect consumeEffect = TFx.coalSmelt;

    public ThermalConduitHeater(String name)
    {
        super(name);
    }

    @Override
    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
        return otherblock instanceof LiquidJunction
            || ((otherblock.hasLiquids || otherblock.outputsLiquid)
                && Point2.equals(tile.x + Geometry.d4(rotation).x, tile.y + Geometry.d4(rotation).y, otherx, othery)
                || Point2.equals(tile.x - Geometry.d4(rotation).x, tile.y - Geometry.d4(rotation).y, otherx, othery));
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list)
    {
        Draw.scl(1, 1);
        Draw.color(botColor);
        Draw.alpha(0.5f);
        Draw.rect(botRegions[0], plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.color();
        Draw.rect(topRegions[0], plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.scl();
    }

    public class ThermalConduitHeaterBuild extends ThermalConduitBuild
    {
        public float itemTime = itemDuration, warmup;

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid)
        {
            return super.acceptLiquid(source, liquid) && (tile == null || source.tile.absoluteRelativeTo(tile.x, tile.y) == rotation || !source.proximity.contains(this));
        }


        public float targetHeat()
        {
            return heatOutput * warmup();
        }

        @Override
        public void updateTile()
        {
            super.updateTile();

            warmup = Mathf.lerpDelta(warmup, efficiency > 0 && itemTime > 0 ? 1f : 0f, 0.02f);

            itemTime += edelta();
            if(itemTime >= itemDuration && efficiency > 0)
            {
                consume();
                itemTime = 0f;

                consumeEffect.at(x, y);
            }
        }

        @Override
        public float warmup()
        {
            return warmup;
        }

        @Override
        public void write(Writes write)
        {
            super.write(write);
            
            write.f(itemTime);
            write.f(warmup);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            
            itemTime = read.f();
            warmup = read.f();
        }
    }
}
