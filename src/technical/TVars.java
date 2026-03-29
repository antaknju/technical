package technical;

import static mindustry.Vars.tilesize;

import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Effect.EffectContainer;
import mindustry.game.EventType.Trigger;
import mindustry.game.EventType.UnitChangeEvent;
import mindustry.gen.Call;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Binding;
import mindustry.input.MobileInput;

public class TVars 
{
    public static float ControlRange = 12 * tilesize;
    private static Unit lastValidUnit = null;
    private static long lastBlockTime = 0;

    public static void load()
    {
        wrapEffect(Fx.unitControl);
        wrapEffect(Fx.unitSpirit);

        Events.run(Trigger.draw, () -> {
            Unit playerUnit = Vars.player.unit();
            if (playerUnit == null) return;

            if (Core.input.keyDown(Binding.control)) 
            {
                Vec2 mousePos = Core.camera.unproject(Core.input.mouse());

                Draw.z(Layer.overlayUI);
                Drawf.dashCircle(playerUnit.x, playerUnit.y, ControlRange, playerUnit.dst(mousePos) > ControlRange ? Pal.remove : Pal.accent);
            }
        });

        Events.on(UnitChangeEvent.class, event -> {
            if (event.player != Vars.player) return;

            Unit newUnit = event.unit;

            // Accept if they are respawning, or if we have no baseline
            if (lastValidUnit == null || newUnit == null) 
            {
                lastValidUnit = newUnit;
                return;
            }

            // Prevent infinite loops when we force-revert them
            if (newUnit == lastValidUnit) return;

            // If they try to possess something out of range
            if (lastValidUnit.dst(newUnit) > ControlRange) 
            {
                lastBlockTime = Time.millis(); 
                Call.unitControl(Vars.player, lastValidUnit);
            } 
            else 
            {
                lastValidUnit = newUnit;
            }

            Unit hovered = Vars.control.input.selectedUnit();

            if(hovered != null)
            {
                float size = hovered.hitSize * 1.5f;
                boolean inRange = Vars.player.unit().dst(hovered) <= ControlRange;

                Draw.z(Layer.overlayUI + 0.01f);
                Draw.mixcol(inRange ? Pal.accent : Pal.remove, 1f);

                Draw.rect(hovered.type.fullIcon, hovered.x, hovered.y, size, size, hovered.rotation - 90);

                // 2. Draw 4 Rotating Triangles (The custom 4-point design)
                float time = Time.time;
                for(int i = 0; i < 4; i++){
                    // Rotation math: 360 degrees / 4 triangles = 90 degree offsets
                    float angle = (time * 2f) + (i * 90f);
                    
                    // Offset distance scales with the unit's size
                    float tx = hovered.x + Mathf.cosDeg(angle) * (size * 0.8f);
                    float ty = hovered.y + Mathf.sinDeg(angle) * (size * 0.8f);
                    
                    // Drawf.tri(x, y, width, height, angle)
                    Drawf.tri(tx, ty, 4f, 8f, angle);
                }
                
                Draw.reset();
            }
        });
    }
    
    private static void wrapEffect(Effect effect) 
    {
        Cons<EffectContainer> original = effect.renderer;

        effect.renderer = e -> {
            Unit p = Vars.player.unit();
            if (p == null) return;

            float dist = p.dst(e.x, e.y);

            // it's a "ghost" effect from a failed possession. Kill it.
            if (Time.timeSinceMillis(lastBlockTime) < 800) 
            {
                return;
            }

            if (dist <= ControlRange) 
            {
                original.get(e);
            }
        };
    }
}
