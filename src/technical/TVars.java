package technical;

import static mindustry.Vars.tilesize;

import arc.Events;
import arc.func.Cons;
import arc.util.Reflect;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Effect.EffectContainer;
import mindustry.entities.Sized;
import mindustry.game.EventType.UnitChangeEvent;
import mindustry.gen.Call;
import mindustry.gen.Unit;
import technical.core.TOverlayRenderer;

public class TVars
{
    public static final float messageBaseWaitTime = 30f;

    public static float ControlRange = 12 * tilesize;

    private static Unit lastValidUnit = null;
    private static float lastBlockTick = -9999f;
    private static boolean isRespawning  = false;

    private static final float suppressTicks = 90f;

    public static TOverlayRenderer overlays = new TOverlayRenderer();

    public static void init()
    {
        Reflect.set(Vars.renderer, "overlays", overlays);
    }

    public static void load()
    {
        wrapControlEffect(Fx.unitControl);
        wrapControlEffect(Fx.unitSpirit);
        wrapControlEffect(Fx.unitDespawn);
        wrapControlEffect(Fx.spawn);
        wrapControlEffect(Fx.unitSpawn);

        Events.on(UnitChangeEvent.class, event -> {
            if (event.player != Vars.player) return;

            Unit newUnit = event.unit;

            if (newUnit == null) {
                isRespawning = true;
                lastBlockTick = Time.time; // ADDED: Start suppression on death

                if (lastValidUnit != null) {
                    Fx.massiveExplosion.at(lastValidUnit.x, lastValidUnit.y);
                    Fx.unitLand.at(lastValidUnit.x, lastValidUnit.y);
                }

                lastValidUnit = null;
                return;
            }

            if (lastValidUnit == null) {
                // We just spawned at the core.
                // Keep isRespawning true for one more frame or rely on lastBlockTick
                isRespawning = false;
                lastBlockTick = Time.time; // ADDED: Start suppression on spawn
                lastValidUnit = newUnit;
                return;
            }

            if (newUnit == lastValidUnit) return;

            if (lastValidUnit.dst(newUnit) > ControlRange) {
                lastBlockTick = Time.time;
                Call.unitControl(Vars.player, lastValidUnit);
            } else {
                lastValidUnit = newUnit;
            }
        });
    }

    public static boolean isInPlayerRange(Sized select)
    {
        Unit p = Vars.player.unit();
        if (p == null) return false;

        return p.dst(select.getX(), select.getY()) <= ControlRange;
    }

    private static void wrapControlEffect(Effect effect)
    {
        Cons<EffectContainer> original = effect.renderer;

        effect.renderer = e -> {
            if (isRespawning) return;
            if (Time.time - lastBlockTick < suppressTicks) return;

            Unit p = Vars.player.unit();
            if (p == null) return;

            if (p.dst(e.x, e.y) <= ControlRange)
            {
                original.get(e);
            }
        };
    }
}