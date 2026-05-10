package technical.core.dialog;

import arc.util.Time;

public class WaitObjective extends DialogObjective
{
    public float startTime;

    public float duration;

    public WaitObjective(float ticks)
    {
        this.duration = ticks;
    }

    @Override
    public void onStart()
    {
        startTime = Time.time;
    }

    @Override
    public boolean onComplete()
    {
        return startTime + duration <= Time.time;
    }

    @Override
    public boolean onNegativeComplete()
    {
        return !onComplete();
    }

    @Override
    public DialogObjective clone()
    {
        var copy = (WaitObjective) super.clone();
        copy.duration = duration;

        return copy;
    }
}