package technical.core;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import technical.Technical;
import technical.core.FacilityController.FacilityControllerBuild;
import technical.core.FacilityStep.FacilityStepType;

public class FacilityAddapter extends TBlock
{
    public TextureRegion baseRegion;
    public FacilityStepType stepType;

    @Override
    public void load()
    {        
        super.load();
        baseRegion = Core.atlas.find(Technical.name + "-tblock-" + size);
    }

    @Override
    public TextureRegion[] icons()
    {
        return new TextureRegion[]{baseRegion, region};
    }

    public FacilityAddapter(String name)
    {
        super(name);
    }

    public class FacilityAddapterBuild extends TBuild implements FacilityBuild
    {
        public FacilityControllerBuild controller;
        public boolean isWorking = false;

        @Override
        public void drawSelect()
        {
            drawSelectFacility(this);
        }

        @Override
        public void updateTile() 
        {
            super.updateTile();
        }
        
        public void updateAddapter()
        {

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

        @Override
        public void draw()
        {
            Draw.rect(baseRegion, x, y);

            drawAddapter();

            Draw.reset();
        }

        public void drawAddapter()
        {
            
        }
    }
}