package technical.core.dialog;

public class TalkRunningObjective extends DialogObjective
{
    public String talkName;
    public Talk talkRef;

    public TalkRunningObjective(String talkName)
    {
        this.talkName = talkName;
    }

    public TalkRunningObjective(Talk talkRef)
    {
        this.talkRef = talkRef;

        talkName = talkRef.name;
    }

    @Override
    public boolean onComplete()
    {
        if (talkRef == null)
            talkRef = talk.dialog.getTalkRef(talkName);

        return DialogManager.runner.isTalkRunning(talkRef);
    }

    @Override
    public boolean onNegativeComplete()
    {
        if (talkRef == null)
            talkRef = talk.dialog.getTalkRef(talkName);

        return DialogManager.runner.isTalkEnded(talkRef);
    }

    @Override
    public DialogObjective clone()
    {
        var copy = (TalkRunningObjective) super.clone();
        copy.talkName = talkName;

        return copy;
    }
}