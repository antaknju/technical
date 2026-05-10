package technical.core;


import mindustry.type.Planet;
import mindustry.type.SectorPreset;
import technical.content.TPlanets;
import technical.core.dialog.Dialog;

public class TSector extends SectorPreset
{
    public Dialog dialog;

    public TSector(String name, int sector)
    {
        this(name, TPlanets.mycelius, sector);
    }
    public TSector(String name, Planet planet, int sector)
    {
        super(name, planet, sector);
    }
}