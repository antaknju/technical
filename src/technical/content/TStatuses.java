package technical.content;

// import technical.TCol;
// // import arc.graphics.Color;
// import arc.math.Mathf;
// import mindustry.content.Fx;
// import mindustry.content.StatusEffects;
// import mindustry.type.StatusEffect;

public class TStatuses {
    // public static StatusEffect sulphured;

    public static void load() {
        
    }
}
//sulphured = new StatusEffect("sulphured") {{
        //     color = applyColor = TCol.sulphur;
        //     damage = 5 / 60f;
        //     effect = AourusEffects.sulphured;
        //     transitionDamage = 30f;

        //     outline = true;

        //     alwaysUnlocked = true;

        //     init(() -> {
        //         opposite(StatusEffects.wet);
        //         affinity(StatusEffects.burning, (unit, result, time) -> {
        //             unit.damagePierce(transitionDamage);
        //             Fx.burning.at(unit.x + Mathf.range(unit.bounds() / 2f), unit.y + Mathf.range(unit.bounds() / 2f));
        //             result.set(StatusEffects.burning, Math.min(time + result.time, 300f));
        //         });
        //     });
        // }};