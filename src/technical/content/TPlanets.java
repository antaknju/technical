package technical.content;

import arc.graphics.*;
// import arc.struct.Seq;
import mindustry.graphics.Pal;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.*;
import mindustry.world.meta.Env;
import technical.core.Satellite;
import technical.util.TCol;
import technical.core.TPlanetMesh;
import mindustry.content.*;
import mindustry.game.Team;

public class TPlanets 
{
    public static Planet mycelius, mother_ship;

    public static void load()
    {
        mycelius = new Planet("mycelius", Planets.sun, 0.98f, 3){{
            generator = new SerpuloPlanetGenerator(); // Safety net

            meshLoader = () -> new TPlanetMesh(this, 67, 6);

            launchMusic = TMusic.launch;

            alwaysUnlocked = true;

            iconColor = TCol.bioPurple;

            orbitSpacing = 100f;
            
            atmosphereRadIn = 0.1f;
            atmosphereRadOut = 0.4f;
            tidalLock = false;
            hasAtmosphere = true;

            atmosphereColor = Color.valueOf("3c1b8f");
            landCloudColor = Pal.spore.cpy().a(0.5f);

            updateLighting = true;

            prebuildBase = true;

            ruleSetter = r -> {
                r.waveTeam = Team.malis;
                r.placeRangeCheck = false;
                r.showSpawns = true;
                r.fog = true;
                r.staticFog = true;
                r.unitAmmo = true;
                r.lighting = true;
                r.coreDestroyClear = true;
                r.onlyDepositCore = false;

                r.coreIncinerates = true;
            };

            clearSectorOnLose = true;
            campaignRuleDefaults.fog = true;
            campaignRuleDefaults.showSpawns = true;
            campaignRuleDefaults.rtsAI = true;

            unlockedOnLand.add(TBlocks.basic_core);

            defaultEnv = Env.terrestrial;

            allowSectorInvasion = true;
            clearSectorOnLose = true;

            defaultCore = TBlocks.basic_core;

            allowLaunchSchematics = false;
            allowSelfSectorLaunch = false;
            allowLaunchToNumbered = false;
            launchCapacityMultiplier = 0.5f;
            
            startSector = 2;
        }};

        mother_ship = new Satellite("mother-ship", mycelius, 0.4f){{
            launchMusic = TMusic.launch;
            tidalLock = true;

            defaultEnv = Env.space;
        }};
    }
}

// import static mindustry.Vars.fogControl;

// import technical.TPlanetGenerator;

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
