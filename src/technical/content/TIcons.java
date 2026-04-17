package technical.content;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.TextureRegionDrawable;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.EventType.AtlasPackEvent;
import mindustry.gen.Icon;
import mindustry.ui.Fonts;
import technical.Technical;

public class TIcons
{
    public static TextureRegion speed, torque, question, flip, itemRing, boostPower
    ;

    public static int lastIconCode = 6000;

    public static int boostPowerIcon = ++lastIconCode,
            angularSpeedIcon = ++lastIconCode,
            torqueIcon = ++lastIconCode
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
        registerIcon(boostPowerIcon, "boost-power", boostPower);
        registerIcon(angularSpeedIcon, "angular-speed", speed);
        registerIcon(torqueIcon, "torque", torque);
    }

    private static void registerIcon(int code, String name, TextureRegion region)
    {
        Fonts.registerIcon(name, Technical.name + name, code, region);

        Icon.icons.put(name, new TextureRegionDrawable(region));
    }

    public static String get(int id)
    {
        return " []" + (char)id;
    }
}
