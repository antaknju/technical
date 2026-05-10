package technical.core.dialog;

import mindustry.Vars;

import static mindustry.Vars.renderer;

public class LandedObjective extends DialogObjective
{
    public LandedObjective()
    {

    }

    @Override
    public boolean onComplete()
    {
        return renderer.landTime <= 0f;
    }

    @Override
    public DialogObjective clone()
    {
        var copy = (LandedObjective) super.clone();

        return copy;
    }

    @Override
    public boolean onNegativeComplete()
    {
        return !onComplete();
    }
}