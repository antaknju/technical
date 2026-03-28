package technical.content;

// import static mindustry.Vars.fogControl;

// import technical.TPlanetGenerator;
import arc.graphics.*;
// import arc.struct.Seq;
import arc.util.*;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.*;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.*;
import mindustry.world.meta.Env;
import technical.TCol;
import technical.expansion.TPlanetMesh;
import mindustry.content.*;
import mindustry.game.Team;
import mindustry.gen.Musics;

public class TPlanets 
{
    public static Planet tertaris;

    public static void load()
    {
        tertaris = new Planet("tertaris", Planets.sun, 0.98f, 3){{
            generator = new SerpuloPlanetGenerator();

            meshLoader = () -> new TPlanetMesh(this, 67, 6);
            
            // meshLoader = () -> new MultiMesh(
            //     new NoiseMesh(this, 12,
            //                     6, .9f, 4, 1f, .75f, 1.2f,
            //                     Color.valueOf("#333533ff"), Color.valueOf("#565656"),
            //                     4, .5f, .5f, .5f),
                                
            //     new HexSkyMesh(this, 5,     
            //                     1.5f, .12f, 5, TCol.bioPurple, 1, 2f, 2f, .45f)
            // );
            
            // cloudMeshLoader = () -> new MultiMesh(
            //     new HexSkyMesh(this, 11, 0.15f, 0.13f, 5, new Color().set(TCol.bioPurple).mul(0.9f).a(0.75f), 2, 0.45f, 0.9f, 0.38f),
            //     new HexSkyMesh(this, 1, 0.6f, 0.16f, 5, Color.white.cpy().lerp(TCol.bioOrange, 0.55f).a(0.75f), 2, 0.45f, 1f, 0.41f)
            // );

            launchMusic = TMusic.launch;

            sectorSeed = 3;
            
            alwaysUnlocked = true;

            iconColor = TCol.iron;
            
            atmosphereRadIn = 0.1f;
            atmosphereRadOut = 0.4f;
            tidalLock = true;
            hasAtmosphere = true;

            atmosphereColor = Color.valueOf("3c1b8f");
            landCloudColor = Pal.spore.cpy().a(0.5f);

            updateLighting = true;

            prebuildBase = true;
            ruleSetter = r -> {
                r.waveTeam = Team.crux;
                r.placeRangeCheck = true;
                r.onlyDepositCore = false;
                r.showSpawns = false;
                r.coreDestroyClear = true;
                r.unitAmmo = true;
                r.coreIncinerates = true;
                r.fog = true;
                r.staticFog = true;
            };

            defaultEnv = Env.terrestrial;

            allowWaves = true;
            // allowWaveSimulation = true;

            allowSectorInvasion = true;
            clearSectorOnLose = true;

            defaultCore = TBlocks.basic_core;

            allowLaunchSchematics = false;
            allowSelfSectorLaunch = false;
            allowLaunchToNumbered = false;
            launchCapacityMultiplier = 0.5f;
            
            startSector = 0;
            bloom = true;
        }};
    }
}
