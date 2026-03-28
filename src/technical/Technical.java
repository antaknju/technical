package technical;

import mindustry.Vars;
import mindustry.content.UnitTypes;
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
        TUnits.init();

        TVars.init();
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

        /// VANILLA CHANGES
        
        // UnitTypes.mace.weapons.clear();
        // UnitTypes.elude.weapons.clear();
        // UnitTypes.fortress.weapons.clear();
        // UnitTypes.scepter.weapons.clear();
        // UnitTypes.reign.weapons.clear();

        Vars.renderer.maxZoom = 10f;

        // Vars.testMobile = true;

        // Events.on(ClientLoadEvent.class, event -> {
        //     BaseDialog dialog = new BaseDialog("Welcome to Technical!");

        //     Table content = dialog.cont;
            
        //     Image icon = new Image(Items.copper.uiIcon);
        //     content.add(icon).size(64f).pad(10f);

        //     content.add(new Image(TIcons.question)).size(64f).pad(10f);

        //     content.row();
        //     content.add(new Label("Hello! This window opens on game start.")).pad(10f);
            
        //     dialog.buttons.defaults().size(160f, 50f);
        //     dialog.buttons.button("Close", dialog::hide);
            
        //     dialog.show();
        // });
    }
}

/*
 * Building
 *  - efficiency is the multiplier of avaliable items/liquids/power consumption
 *  - proximity are Buildings touching this Building
 *  - the Draw will glow only on special layers
 *  - Block.update = true filed makes block breakable
 */