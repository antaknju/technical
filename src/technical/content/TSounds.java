package technical.content;

import arc.audio.Sound;
import mindustry.Vars;

public class TSounds 
{
    public static Sound 
    splat = find("splat"), 
    crossbow = find("crossbow")
    ;

    public static Sound find(String name)
    {
        return Vars.tree.loadSound(name);
    }
}