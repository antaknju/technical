package technical.core.dialog;

public class TalkEndedObjective extends DialogObjective
{
    public String talkName;
    public Talk talkRef;

    public TalkEndedObjective(String talkName)
    {
        this.talkName = talkName;
    }

    @Override
    public boolean onComplete()
    {
        if (talkRef == null)
            talkRef = talk.dialog.getTalkRef(talkName);

        return DialogManager.runner.isTalkEnded(talkRef);
    }

    @Override
    public DialogObjective clone()
    {
        var copy = (TalkEndedObjective) super.clone();
        copy.talkName = talkName;

        return copy;
    }

    @Override
    public boolean onNegativeComplete()
    {
        if (talkRef == null)
            talkRef = talk.dialog.getTalkRef(talkName);

        return DialogManager.runner.isTalkRunning(talkRef);
    }
}