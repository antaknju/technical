package technical.core;

import mindustry.gen.Building;
import technical.core.FacilityController.FacilityControllerBuild;
import technical.util.TDraw;

public interface FacilityBuild
{
    FacilityControllerBuild controller();
    void controller(FacilityControllerBuild fcb);

    default void drawSelectFacility(Building build)
    {
        if (controller() == null) return;

        TDraw.highlight(controller());
    }
}
