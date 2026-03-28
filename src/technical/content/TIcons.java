package technical.content;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.TextureRegion;
import mindustry.game.EventType.AtlasPackEvent;
import technical.Technical;

public class TIcons
{
    public static TextureRegion speed, torque, question, flip, itemRing
    ;

    public static void load()
    {
        Events.on(AtlasPackEvent.class, e -> {
            loadIcons();
        });
    }

    public static void loadIcons()
    {
        speed = Core.atlas.find(Technical.name + "-icon-speed");
        torque = Core.atlas.find(Technical.name + "-icon-torque");
        question = Core.atlas.find(Technical.name + "-icon-question");
        flip = Core.atlas.find(Technical.name + "-icon-flip");
        itemRing = Core.atlas.find("ring-item");
    }
}
