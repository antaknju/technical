package technical.content;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.TextureRegionDrawable;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.EventType.AtlasPackEvent;
import mindustry.gen.Icon;
import mindustry.ui.Fonts;
import technical.Technical;
import technical.util.T;

import static technical.util.Debugger.print;
import static technical.util.Debugger.rprint;
import static technical.util.T.isTechnical;

public class TIcons
{
    public static TextureRegion speed, torque, question, flip, itemRing, boostPower
    ;

    public static int lastIconCode = 65000;

    public static int boostPowerIcon, angularSpeedIcon, torqueIcon
    ;

    public static void load()
    {
        Events.on(AtlasPackEvent.class, e -> {
            loadTextures();
        });

        Events.on(ClientLoadEvent.class, e -> {
            loadIcons();
        });
    }

    public static void loadTextures()
    {
        speed = Core.atlas.find(Technical.name + "-icon-speed");
        torque = Core.atlas.find(Technical.name + "-icon-torque");
        question = Core.atlas.find(Technical.name + "-icon-question");
        flip = Core.atlas.find(Technical.name + "-icon-flip");
        boostPower = Core.atlas.find(Technical.name + "-boost-power");
        itemRing = Core.atlas.find("ring-item");
    }

    public static void loadIcons()
    {
        boostPowerIcon = ++lastIconCode;
        angularSpeedIcon = ++lastIconCode;
        torqueIcon = ++lastIconCode;

        registerIcon(boostPowerIcon, "boost-power", boostPower);
        registerIcon(angularSpeedIcon, "angular-speed", speed);
        registerIcon(torqueIcon, "torque", torque);
    }

    private static void registerIcon(int code, String name, TextureRegion region)
    {
        Fonts.registerIcon(name, Technical.name + "-" + name, code, region);

        Icon.icons.put(name, new TextureRegionDrawable(region));
    }

    public static String get(int id)
    {
        return " []" + (char)id;
    }
}