package technical.core.dialog;

import arc.func.Cons;
import arc.struct.Seq;
import mindustry.type.SectorPreset;

public class Dialog
{
    public SectorPreset sector;
    public Seq<Talk> talks = new Seq<>();

    public Dialog(SectorPreset sector, Cons<Dialog> cons)
    {
        this.sector = sector;

        cons.get(this);
    }

    public Talk addTalk(String name, int priority, Cons<Talk> talk)
    {
        talks.add(new Talk(this, name, priority));

        talk.get(talks.peek());

        return talks.peek();
    }

    public Talk getTalkRef(String name)
    {
        return talks.find(talk -> talk.name.equals(name));
    }
}
