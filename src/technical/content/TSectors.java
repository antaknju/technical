package technical.content;

import mindustry.type.ItemStack;
import mindustry.type.SectorPreset;
import technical.core.dialog.*;
import technical.core.TSector;
import technical.util.Fr;

public class TSectors
{
    public static TSector cradle_slate, technical_test;
    public static SectorPreset mother_ship;

    public static void load()
    {
        cradle_slate = new TSector("cradle-slate", 2){{
            alwaysUnlocked = true;
            addStartingItems = true;
            difficulty = 0;

            dialog = new Dialog(this, d -> {
                d.addTalk("start", 10, t -> {
                    t.addObjective(new LandedObjective());

                    t.addMessage("master", "0");
                    t.addMessage("master", "1");
                    t.addMessage("master", "2");
                    t.addMessage("master", "3");
                    t.addMessage("master", "4");
                });

                d.addTalk("stone", 10, t -> {
                    t.addMessage("master", "0", m -> {
                        m.addObjective(new TalkEndedObjective("start"));
                    });
                    t.addMessage("master", "1");
                    t.addMessage("master", "2");
                    t.addMessage("master", "3", m -> {
                        m.addObjective(new CoreItemsObjective(new ItemStack(TItems.stone, 1)), o -> {
                            o.addFeedback("stone-feedback", 10, f -> {
                                f.addMessage("master", "0");
                                f.addMessage("master", "1");
                            });
                        });
                    });
                    t.addMessage("master", "4", m -> {
                        m.addObjective(new CoreItemsObjective(new ItemStack(TItems.stone, 40)));
                    });
                });

                d.addTalk("stone-waiting", 5, t -> {
                    t.addObjective(new TalkProgressObjective("stone", 2, 3));
                    t.addMessage("master", "0", Fr.time * 35);
                    t.addMessage("master", "1");
                });

                d.addTalk("clay", 10, t -> {
                    t.addMessage("master", "0", m -> {
                        m.addObjective(new TalkEndedObjective("stone"));
                    });
                    t.addMessage("master", "1");
                    t.addMessage("master", "2");
                    t.addMessage("master", "3");
                    t.addMessage("master", "4", m -> {
                        m.addObjective(new PlayerItemsObjective(new ItemStack(TItems.clay, 10)), o -> {
                            o.addFeedback("clay-feedback", 10, f -> {
                                f.addMessage("master", "0");
                                f.addMessage("master", "1");
                            });
                        });
                    });
                    t.addMessage("master", "5", m -> {
                        m.addObjective(new ResearchObjective(TBlocks.drying_pad));
                    });
                    t.addMessage("master", "6", m -> {
                        m.addObjective(new PlaceObjective(TBlocks.drying_pad));
                    });
                    t.addMessage("master", "7");
                    t.addMessage("master", "8", m -> {
                        m.addObjective(new ResearchObjective(TItems.brick));
                    });
                    t.addMessage("master", "9", m -> {
                        m.addObjective(new CoreItemsObjective(new ItemStack(TItems.brick, 10)));
                    });
                });
            });
        }};

        technical_test = new TSector("technical-test", 10){{
            hideDatabase = true;
            difficulty = 0;
        }};

        mother_ship = new TSector("mother-ship", TPlanets.mother_ship, 0){{
            hideDatabase = true;
        }};
    }
}