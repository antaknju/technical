package technical.core.dialog;

public class TalkProgressObjective extends DialogObjective
{
    public String talkName;
    public Talk talkRef;

    public int minProgress;
    public int maxProgress;

    public TalkProgressObjective(String talkName, int minProgress, int maxProgress)
    {
        this.talkName = talkName;
        this.minProgress = minProgress;
        this.maxProgress = maxProgress;
    }

    public TalkProgressObjective(Talk talk, int minProgress, int maxProgress)
    {
        this.talkName = talk.name;
        this.talkRef = talk;

        this.minProgress = minProgress;
        this.maxProgress = maxProgress;
    }

    @Override
    public boolean onComplete()
    {
        if (talkRef == null)
            talkRef = talk.dialog.getTalkRef(talkName);

        int progress = DialogManager.runner.getTalk(talkRef);

        return progress >= minProgress && progress <= maxProgress;
    }

    @Override
    public DialogObjective clone()
    {
        var copy = (TalkProgressObjective) super.clone();
        copy.talkName = talkName;
        copy.minProgress = minProgress;
        copy.maxProgress = maxProgress;

        return copy;
    }

    @Override
    public boolean onNegativeComplete()
    {
        return !onComplete();
    }
}