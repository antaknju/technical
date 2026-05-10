package technical.core.dialog;

import mindustry.game.Objectives;

public class ObjectiveRunningObjective extends DialogObjective
{
    public DialogObjective objective;

    public ObjectiveRunningObjective(DialogObjective objective)
    {
        this.objective = objective;
    }

    @Override
    public boolean onComplete()
    {
        return objective.isStarted && !objective.isComplete();
    }

    @Override
    public boolean onNegativeComplete()
    {
        return objective.isComplete();
    }

    @Override
    public DialogObjective clone()
    {
        var copy = (ObjectiveRunningObjective) super.clone();
        copy.objective = objective;

        return copy;
    }
}