package technical.expansion;

import mindustry.gen.Building;
import technical.utility.T;
import technical.expansion.FacilityController.FacilityControllerBuild;
import technical.utility.TDraw;

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
