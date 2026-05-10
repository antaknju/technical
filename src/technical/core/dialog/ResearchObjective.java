package technical.core.dialog;

import mindustry.Vars;
import mindustry.ctype.UnlockableContent;

public class ResearchObjective extends DialogObjective
{
    public UnlockableContent content;

    public ResearchObjective(UnlockableContent content)
    {
        this.content = content;
    }

    @Override
    public boolean onComplete()
    {
        return content.unlocked();
    }

    @Override
    public DialogObjective clone()
    {
        var copy = (ResearchObjective) super.clone();
        copy.content = content;

        return copy;
    }

    @Override
    public boolean onNegativeComplete()
    {
        return !onComplete();
    }
}