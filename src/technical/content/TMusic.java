package technical.content;

import arc.Events;
// import arc.Core;
import arc.audio.Music;
import arc.files.Fi;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.audio.SoundControl;
import mindustry.core.GameState;
import mindustry.game.EventType.MusicRegisterEvent;
import mindustry.game.EventType.StateChangeEvent;
import mindustry.game.EventType.WorldLoadEvent;

// Erekir Music Credits for versatile file searching and on planet music changing
public class TMusic
{
    public static Seq<Music> vAmbient;
    public static Seq<Music> vDark;
    public static Seq<Music> vBoss;

    public static SoundControl control;

    public static Seq<Music> ambient = new Seq<>();
    public static Seq<Music> dark = new Seq<>();
    public static Seq<Music> boss = new Seq<>();
    public static Music launch;

    public static void load() 
    {
        control = Vars.control.sound;

        Events.on(MusicRegisterEvent.class, e -> {
            // Save copies of vanilla music lists.
            vAmbient = control.ambientMusic.copy();
            vDark = control.darkMusic.copy();
            vBoss = control.bossMusic.copy();
        });

        Events.on(WorldLoadEvent.class, e -> {
            if (Vars.state.rules.planet == TPlanets.mycelius) {
                // Inject custom music here.
                control.ambientMusic = TMusic.ambient;
                control.darkMusic = TMusic.dark;
                control.bossMusic = TMusic.boss;
            }
        });

        Events.on(StateChangeEvent.class, e -> {
            if (e.from != GameState.State.menu && e.to == GameState.State.menu) {
                // Reset music upon going to main menu.
                control.ambientMusic = vAmbient;
                control.darkMusic = vDark;
                control.bossMusic = vBoss;
            }
        });

        // Music categories: ambient, dark, boss, launch
        Fi musicRoot = Vars.mods.locateMod("technical").root.child("music");
        for (var cat : musicRoot.list()) {
            for (var mFile : cat.findAll(f -> f.extEquals("ogg") || f.extEquals("mp3"))) {
                var music = Vars.tree.loadMusic(cat.name() + "/" + mFile.nameWithoutExtension());
                switch (cat.name()) {
                    case "ambient" -> ambient.add(music);
                    case "dark" -> dark.add(music);
                    case "boss" -> boss.add(music);
                    case "launch" -> launch = music;
                }
            }
        }
    }
}
