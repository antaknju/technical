package technical;

import arc.Core;
import arc.Events;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.mod.*;
import technical.content.*;
import technical.debug.Debugger;
import technical.expansion.kinetic.KineticGraph;
import technical.expansion.train.RailSplineManager;

public class Technical extends Mod
{
    public static final String name = "technical";

    public Technical() {}

    @Override
    public void init()
    {
        TVars.init();
        TUnits.init();

        // Adding linear interpolation of custom atlas pages
        for (Texture tex : Core.atlas.getTextures()) {
            tex.setFilter(Texture.TextureFilter.linear);
        }
    }

    @Override
    public void loadContent()
    {
        TIcons.load();
        Debugger.load();
        TShaders.load();

        TVars.load();
        TStatuses.load();

        TItems.load();
        TLiquids.load();

        TCustom.load();

        TUnits.load();

        TLoadouts.load();

        TBlocks.load();

        TMusic.load();
        TPlanets.load();

        RailSplineManager.load();
        KineticGraph.load();

        TSectors.load();

        TTechTree.load();

        TVars.load();

        /// VANILLA DEBUG CHANGES
        Vars.renderer.maxZoom = 10f;
        
        // UnitTypes.mace.weapons.clear();
        // UnitTypes.elude.weapons.clear();
        // UnitTypes.fortress.weapons.clear();
        // UnitTypes.scepter.weapons.clear();
        // UnitTypes.reign.weapons.clear();


        // Vars.testMobile = true;

        Events.on(EventType.ContentInitEvent.class, e -> {
             var info = TStatuses.wet.uiIcon;
             Log.info("Region: " + info);
             Log.info("Region: " + info);
             Log.info("Region: " + info);
             Log.info("Region: " + info);
             Log.info("Region: " + info);
             Log.info("Region: " + info);
             Log.info("Region: " + info);
         });
    }
}

/*
 * Building:
 *  - efficiency is the multiplier of available items/liquids/power consumption
 *  - proximity are Buildings touching this Building
 *  - the Draw will glow only on special layers
 *  - Block.update = true filed makes block breakable
 *
 * Spriting:
 *  - When drawing environmental builds use only 2 colors for floors, 3 colors for 3D objects
 *  - When drawing huge builds first main part, then smaller size, and smaller, and smaller
 *  - When drawing environment use simple, but not ideal shapes: triangles,
 */