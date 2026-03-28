package technical.expansion;

import mindustry.gen.Building;
import technical.T;
import technical.expansion.FacilityController.FacilityControllerBuild;

public interface FacilityBuild
{
    FacilityControllerBuild controller();
    void controller(FacilityControllerBuild fcb);

    default void drawSelectFacility(Building build)
    {
        if (controller() == null) return;

        T.outline(controller());
    }
}
