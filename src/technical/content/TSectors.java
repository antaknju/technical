package technical.content;

// import arc.struct.Seq;
// import mindustry.content.Items;
// import mindustry.game.Team;
// // import mindustry.content.Items;
// import mindustry.type.*;


public class TSectors {
    public static void load() {

    }
}

// public static SectorPreset sector0, sturdy_hills, sulphur_lake, deep_forward, against, enclosed
    // ;

    // public static void load() {
    //     sector0 = new SectorPreset("sector0", AourusPlanets.aourus, 0) {{
    //         alwaysUnlocked = true;
    //         addStartingItems = false;
    //         captureWave = 10;
    //         difficulty = 1;
    //         noLighting = true;

    //         rules = rule -> {
    //             rule.teams.get(rule.waveTeam).rtsAi = false;
    //         };
    //     }};
        
    //     against = new SectorPreset("against", AourusPlanets.aourus, 1) {{
    //         alwaysUnlocked = false;
    //         difficulty = 2;
    //         overrideLaunchDefaults = true;
    //         noLighting = true;

    //         rules = AourusPlanets.aourus.ruleSetter;
            
    //         rules = rule -> {
    //             rule.attackMode = true;
    //             rule.teams.get(rule.waveTeam).rtsAi = true;
    //         };
    //     }};

    //     enclosed = new SectorPreset("enclosed", AourusPlanets.aourus, 2) {{
    //         alwaysUnlocked = false;
    //         difficulty = 3;
    //         overrideLaunchDefaults = true;
    //         noLighting = true;

    //         rules = AourusPlanets.aourus.ruleSetter;
            
    //         rules = rule -> {
    //             // rule.attackMode = true;
    //             rule.fog = false;
    //             rule.staticFog = false;
    //             // rule.teams.get(rule.waveTeam).rtsAi = true;
    //         };
    //     }};

        // deep_forward = new SectorPreset("deep-forward", AourusPlanets.aourus, 65) {{
        //     alwaysUnlocked = false;
        //     // addStartingItems = false;
        //     // captureWave = 10;
        //     difficulty = 2;
        //     overrideLaunchDefaults = true;
        //     noLighting = true;
        //     // startWaveTimeMultiplier = 3f;

        //     // rules = rule -> {
        //     //     rule.loadout = Seq.with(ItemStack.with(Items.graphite, 100, Items.lead, 100));
        //     //     rule.waves = false;
        //     //     rule.attackMode = true;

        //     //     // rule.waitEnemies = true;
        //     //     // rule.initialWaveSpacing = 0;
        //     //     // rule.winWave = 15;
        //     // };
        // }};

        // sturdy_hills = new SectorPreset("sturdy-hills", AourusPlanets.aourus, 15) {{
        //     alwaysUnlocked = false;
        //     // addStartingItems = false;
        //     // captureWave = 10;
        //     difficulty = 2;
        //     overrideLaunchDefaults = true;
        //     noLighting = true;
        //     // startWaveTimeMultiplier = 3f;

        //     // rules = rule -> {
        //     //     rule.loadout = Seq.with(ItemStack.with(Items.graphite, 100, Items.lead, 100));
        //     //     // rule.waveSpacing = 30f * 60;
        //     //     rule.waitEnemies = true;
        //     //     rule.attackMode = false;
        //     //     // rule.initialWaveSpacing = 0;
        //     //     rule.winWave = 15;
        //     // };
        // }};

        // sulphur_lake = new SectorPreset("sulphur-lake", AourusPlanets.aourus, 67) {{
        //     alwaysUnlocked = false;
        //     // addStartingItems = false;
        //     // captureWave = 10;
        //     difficulty = 3;
        //     overrideLaunchDefaults = true;
        //     noLighting = true;
        //     // startWaveTimeMultiplier = 3f;

        //     // rules = rule -> {
        //     //     rule.loadout = Seq.with(ItemStack.with(Items.graphite, 100, Items.silicon, 100, Items.lead, 100));
        //     //     // rule.waveSpacing = 30f * 60;
        //     //     rule.waitEnemies = true;
        //     //     // rule.initialWaveSpacing = 0;
        //     //     rule.winWave = 20;
        //     // };
        // }};