package technical.expansion;

import static mindustry.Vars.tilesize;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.draw.DrawBlock;

public class FacilityLaser extends FacilityAddapter
{
    public float elevation = 1f;
    public float recoil = 1f;
    public float recoilPow = 1.8f;
    public float recoilTime = 60f;
    public float rotateSpeed = 1f;
    public float shootY = size * tilesize;

    public FacilityLaser(String name)
    {
        super(name);
    }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out)
    {
        out.add(region);
    }

    public class FacilityLaserBuild extends FacilityAddapterBuild
    {
        public float turretRot = 0;
        public float curRecoil = 0;

        public Vec2 recoilOffset = new Vec2();
        public Vec2 nozzleOffset = new Vec2();

        @Override
        public void updateTile()
        {
            curRecoil = Mathf.approachDelta(curRecoil, 0, 1 / recoilTime);
            recoilOffset.trns(turretRot, -Mathf.pow(curRecoil, recoilPow) * recoil);

            nozzleOffset.trns(turretRot - 90f, -shootY);

            if (controller() == null)
            {
                turretRot += rotateSpeed * Time.delta;
                turretRot %= 360;


                isWorking = false;
            }
            else
            {
                Vec2 target = controller().getFacilityCenter(Tmp.v5);
                if(target != null)
                {
                    float targetAngle = Angles.angle(x, y, target.x, target.y) - 90f;

                    turretRot = Angles.moveToward(turretRot, targetAngle, rotateSpeed * Time.delta);

                    isWorking = Angles.within(targetAngle, turretRot, 0.001f);
                }
            }
        }

        @Override
        public void drawAddapter()
        {
            Draw.z(Layer.turret - 0.5f);
            Drawf.shadow(region, x + recoilOffset.x - elevation, y + recoilOffset.y - elevation, turretRot);
            Draw.z(Layer.turret);

            Draw.rect(region, x + recoilOffset.x, y + recoilOffset.y, turretRot);

            if (!isWorking) return;

            float time = Time.time + 2137 * id;

            float duration = 40f; 
            float fin = (time % duration) / duration;
            float fout = 1f - fin;
            
            float finpow = Interp.pow3Out.apply(fin); 

            // Generate a unique ID for each cycle so the particle randomness resets per loop
            long loopId = (long)(time / duration) + id;

            if (controller() == null) return;

            Vec2 target = controller().getFacilityCenter(Tmp.v5);

            float x1 = x + nozzleOffset.x;
            float y1 = y + nozzleOffset.y;
            float x2 = target.x;
            float y2 = target.y;
            
            float pulse = 1f + Mathf.sin(time, 6f, 0.2f);

            // Shift to the effect layer requested
            Draw.z(Layer.effect + 1f);
            Draw.blend(Blending.additive);
            
            // Outer Glow
            Draw.color(Color.red, Color.scarlet, fin);
            Lines.stroke(2.5f * fout * pulse);
            Lines.line(x1, y1, x2, y2);

            // Inner Core
            Draw.color(Color.white);
            Lines.stroke(1f * fout * pulse);
            Lines.line(x1, y1, x2, y2);
            
            // Turn off additive blending
            Draw.blend();

            // Target Sparks
            Draw.color(Color.white, Color.red, fin);
            Angles.randLenVectors(loopId + 1, 5, 15f * finpow, turretRot + 180f + 90f, 50f, (rx, ry) -> {
                Lines.stroke(1f * fout);
                Lines.lineAngle(x2 + rx, y2 + ry, turretRot + 180f + 90f, 3f * fout);
            });

            // Origin Smoke/Faint Circles
            Draw.color(Color.lightGray);
            Draw.alpha(0.3f * fout);
            Angles.randLenVectors(loopId + 2, 2, 8f, turretRot + 90f, 20f, (cx, cy) -> {
                Fill.circle(x1 + cx, y1 + cy, 1f + fout);
            });
        }
    }
}
