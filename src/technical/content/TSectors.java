package technical.content;

// import arc.struct.Seq;
// import mindustry.content.Items;
// import mindustry.game.Team;
// // import mindustry.content.Items;
// import mindustry.type.*;


import mindustry.type.SectorPreset;

public class TSectors
{
    public static TSector cradle_slate, technical_test;

    public static void load()
    {
        cradle_slate = new TSector("cradle-slate", 2){{
            alwaysUnlocked = true;
            addStartingItems = true;
            difficulty = 0;
        }};

        technical_test = new TSector("technical-test", 10){{
            hideDatabase = true;
            difficulty = 0;
        }};
    }

    public static class TSector extends SectorPreset
    {
        public TSector(String name, int sector)
        {
            super(name, TPlanets.mycelius, sector);
        }
    }
}