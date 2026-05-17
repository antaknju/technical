package technical;

import arc.Core;
import arc.graphics.Texture;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.io.SaveFileReader;
import mindustry.io.SaveVersion;
import mindustry.mod.*;
import technical.content.*;
import technical.util.Debugger;
import technical.core.dialog.DialogManager;
import technical.core.kinetic.KineticGraph;
import technical.core.train.RailSplineManager;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

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

        TWeathers.load();

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
        DialogManager.load();

        /// VANILLA DEBUG CHANGES
        Vars.renderer.maxZoom = 10f;

        SaveVersion.addCustomChunk(name + "-dialogs", new SaveFileReader.CustomChunk() {
            @Override
            public void write(DataOutput stream) throws IOException
            {
                Writes writes = new Writes(stream);
                DialogManager.runner.write(writes);
            }

            @Override
            public void read(DataInput stream) throws IOException
            {
                Reads reads = new Reads(stream);
                DialogManager.runner.read(reads);
            }
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