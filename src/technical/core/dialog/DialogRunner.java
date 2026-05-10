package technical.core.dialog;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;

import java.util.Objects;

import static technical.core.dialog.DialogManager.dialog;

public class DialogRunner
{
    public Talk currentTalk = null;

    private final ObjectMap<Talk, Integer> talks = new ObjectMap<>();

    public int getTalk(Talk talk)
    {
        if (!talks.containsKey(talk))
            talks.put(talk, -1);

        return talks.get(talk);
    }

    public Message getCurrentMessage()
    {
        return currentTalk.messages.get(getTalk(currentTalk));
    }

    public void incrementTalk(Talk talk)
    {
        if (!talks.containsKey(talk))
            talks.put(talk, -1);

        talks.put(talk, talks.get(talk) + 1);
    }

    /** faster than isTalkEnded(String name) but needs a reference */
    public boolean isTalkEnded(Talk talk)
    {
        return getTalk(talk) >= talk.messages.size - 1;
    }

    public boolean isTalkStarted(Talk talk)
    {
        return getTalk(talk) > -1;
    }

    public boolean isTalkRunning(Talk talk)
    {
        return !isTalkEnded(talk) && isTalkStarted(talk);
    }

    public void update(Dialog dialog)
    {
        if (dialog == null || DialogManager.typingMessage || DialogManager.hiding) return;

        // find new talk
        Talk best = null;
        for (Talk talk : dialog.talks)
        {
            if (isTalkEnded(talk) || !checkObjectives(talk.objectives)) continue;
            if (!checkObjectives(talk.messages.get(getTalk(talk) + 1).objectives)) continue; // we will need incremented one

            if (best == null || talk.priority > best.priority)
                best = talk;
        }

        currentTalk = best;

        if (currentTalk == null || isTalkEnded(currentTalk)) return;

        incrementTalk(currentTalk);

        DialogManager.showMessage(currentTalk.messages.get(getTalk(currentTalk)));
    }

    private boolean checkObjectives(Seq<DialogObjective> objectives)
    {
        if (objectives == null || objectives.isEmpty()) return true;

        for (DialogObjective obj : objectives)
        {
            if (!obj.isStarted)
            {
                obj.start();
            }

            if (!obj.isComplete())
            {
                return false;
            }
        }

        return true;
    }

    public void reset(Dialog dialog)
    {
        currentTalk = null;
        talks.clear();

        for (Talk talk : dialog.talks)
        {
            for (var obj : talk.objectives)
            {
                obj.reset();
            }

            for (var mess : talk.messages)
            {
                for (var obj : mess.objectives)
                {
                    obj.reset();
                }
            }
        }
    }

    public void write(Writes write)
    {
        if (dialog() == null)
        {
            write.i(-1);
            return;
        }

        write.i(dialog().talks.size);

        for (Talk talk : dialog().talks)
        {
            write.str(talk.name);

            boolean isActive = (talk == currentTalk);
            int savedProgress = isActive ? getTalk(talk) - 1 : getTalk(talk);

            write.i(savedProgress);
        }

        write.i(DialogManager.history.size);
        for (Message msg : DialogManager.history)
        {
            boolean found = false;

            outer:
            for (Talk talk : dialog().talks)
            {
                for (int mi = 0; mi < talk.messages.size; mi++)
                {
                    if (talk.messages.get(mi) == msg)
                    {
                        write.i(dialog().talks.indexOf(talk));
                        write.i(mi);
                        found = true;
                        break outer;
                    }
                }
            }

            if (!found)
            {
                write.i(-1);
                write.i(-1);
            }
        }
    }

    public void read(Reads read)
    {
        talks.clear();
        currentTalk = null;

        int talkCount = read.i();

        if (dialog() == null || dialog().talks == null || talkCount == -1) return;

        for (int i = 0; i < talkCount; i++)
        {
            String name = read.str();
            int progress = read.i();

            for (Talk talk : dialog().talks)
            {
                if (Objects.equals(talk.name, name))
                {
                    talks.put(talk, progress);
                    break;
                }
            }
        }

        int historyCount = read.i();
        for (int i = 0; i < historyCount; i++)
        {
            int talkIndex = read.i();
            int msgIndex  = read.i();

            if (talkIndex == -1) continue;

            Talk talk = dialog().talks.get(talkIndex);
            if (talk != null && msgIndex >= 0 && msgIndex < talk.messages.size)
                DialogManager.history.add(talk.messages.get(msgIndex));
        }

        DialogManager.restoreHistory();
    }
}