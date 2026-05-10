package technical.core.dialog;

import arc.func.Cons;

public abstract class DialogObjective implements Cloneable
{
    public boolean isStarted = false;

    public Talk talk;

    public boolean negative = false;

    public boolean isEnded = false;

    public boolean isComplete()
    {
        return isStarted && (negative ? onNegativeComplete() : onComplete());
    }

    public boolean onComplete()
    {
        return true;
    }

    public boolean onNegativeComplete()
    {
        return false;
    }

    public void start()
    {
        isStarted = true;
        onStart();
    }

    public void end()
    {
        isEnded = true;
    }

    public void reset()
    {
        isStarted = false;
        onReset();
    }

    public void onReset()
    {

    }

    public void onStart()
    {

    }

    public void addFeedback(String name, int priority, Cons<Talk> cons)
    {
        talk.dialog.addTalk(name, priority, cons).addNegativeObjective(this.clone()).addObjective(new ObjectiveRunningObjective(this));
    }

    @Override
    public DialogObjective clone()
    {
        try {
            DialogObjective clone = (DialogObjective) super.clone();
            clone.talk = this.talk;
            clone.negative = this.negative;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
