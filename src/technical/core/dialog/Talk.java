package technical.core.dialog;

import arc.func.Cons;
import arc.struct.Seq;
import technical.TVars;

public class Talk
{
    public Dialog dialog;
    public Seq<Message> messages = new Seq<>();
    public Seq<DialogObjective> objectives = new Seq<>();
    public int priority;
    public String name;

    public Talk(Dialog dialog, String name, int priority)
    {
        this.dialog = dialog;
        this.name = name;
        this.priority = priority;
    }

    public void addMessage(String sender, String message, float waitTime, Cons<Message> msg)
    {
        messages.add(new Message(this, sender, message));

        if (msg != null)
            msg.get(messages.peek());

        // waitTime NEED to be last objective
        if (waitTime > 0)
            messages.peek().addObjective(new WaitObjective(waitTime));
    }

    public void addMessage(String sender, String message, Cons<Message> msg)
    {
        addMessage(sender, message, TVars.messageBaseWaitTime, msg);
    }

    public void addMessage(String sender, String message, float waitTime)
    {
        addMessage(sender, message, waitTime, null);
    }

    public void addMessage(String sender, String message)
    {
        addMessage(sender, message, null);
    }

    /** applies objective to ALL messages contained in this talk (so it can stop mid-talk) */
    public void addObjective(DialogObjective objective)
    {
        objective.talk = this;

        objectives.add(objective);
    }

    public Talk addNegativeObjective(DialogObjective objective)
    {
        objective.talk = this;
        objective.negative = true;

        objectives.add(objective);

        return this;
    }
}