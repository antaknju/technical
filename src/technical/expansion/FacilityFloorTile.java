package technical.expansion;

import static technical.debug.Debugger.print;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import mindustry.entities.units.BuildPlan;
import mindustry.world.Tile;
import mindustry.world.blocks.TileBitmask;
import technical.expansion.FacilityController.FacilityControllerBuild;

public class FacilityFloorTile extends TBlock
{
    public TextureRegion[] autotileRegions;

    public FacilityFloorTile(String name)
    {
        super(name);

        underBullets = true;
        solid = false;

        allowRectanglePlacement = true;
    }

    @Override
    public void load()
    {
        super.load();

        autotileRegions = TileBitmask.load(name);
        region = autotileRegions[39];
    }

    // @Override
    // public void handlePlacementLine(Seq<BuildPlan> plans)
    // {
    //     if(plans.isEmpty()) return;

    //     BuildPlan first = plans.first();
    //     BuildPlan last = plans.peek();

    //     int minX = Math.min(first.x, last.x);
    //     int maxX = Math.max(first.x, last.x);
    //     int minY = Math.min(first.y, last.y);
    //     int maxY = Math.max(first.y, last.y);

    //     plans.clear();

    //     for(int x = minX; x <= maxX; x++){
    //         for(int y = minY; y <= maxY; y++){
    //             plans.add(new BuildPlan(x, y));
    //         }
    //     }
    // }

    public class FacilityFloorTileBuild extends TBuild implements FacilityBuild
    {
        public FacilityControllerBuild controller;
        
        public void drawSelect()
        {
            drawSelectFacility(this);
        }

        int computeAutotileBit()
        {
            int bits = 0;

            for(int i = 0; i < 8; i++)
            {
                Tile other = tile.nearby(Geometry.d8[i]);
                if(other != null && other.build != null && other.build instanceof FacilityBuild fb && fb.controller() == controller){
                    bits |= 1 << i;
                }
            }

            return TileBitmask.values[bits];

        }

        boolean blendsDirection(int bit, int direction){
            Tile other = tile.nearby(Geometry.d4[direction]);
            return other != null && other.build != null && other.build instanceof FacilityBuild fb && fb.controller() == controller;
        }

        @Override
        public void draw()
        {
            int bit = computeAutotileBit();
            TextureRegion reg = autotileRegions[bit];

            Draw.rect(reg, x, y);
        }

        @Override
        public FacilityControllerBuild controller() 
        {
            if (controller != null && !controller.isValid()) controller = null;

            return controller;
        }

        @Override
        public void controller(FacilityControllerBuild fcb) 
        {
            controller = fcb;
        }
    }
}