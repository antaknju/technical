package technical.core.dialog;

import arc.func.Cons;
import arc.struct.Seq;
import technical.util.TBundle;

public class Message
{
    public Talk talk;

    public String message;
    public String sender;

    public Seq<DialogObjective> objectives = new Seq<>();

    public Message(Talk talk, String sender, String message)
    {
        this.talk = talk;

        this.message = TBundle.message(talk.dialog.sector, talk, message);
        this.sender = TBundle.sender(sender);
    }

    public void addObjective(DialogObjective objective, Cons<DialogObjective> callback)
    {
        objective.talk = talk;

        objectives.add(objective);

        if (callback != null)
            callback.get(objectives.peek());
    }

    public void addObjective(DialogObjective objective)
    {
        addObjective(objective, null);
    }
}