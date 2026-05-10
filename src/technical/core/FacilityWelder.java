package technical.core;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.entities.Effect;
import technical.util.T;
import technical.content.TFx;

public class FacilityWelder extends FacilityArm
{
    public float weldingTime = 60f;

    public Effect effect = TFx.welding;

    public FacilityWelder(String name) 
    {
        super(name);
    }

    public class FacilityWelderBuild extends FacilityArmBuild
    {
        public float weldingTimer = 0;
        public Vec2 currentTarget = new Vec2();

        @Override
        public void updateAddapter() 
        {
            float craftRadius = T.getContentWorldSize(controller().plan().outputPayload.item) / 2;

            if (weldingTimer <= 0)
            {
                weldingTimer = weldingTime;

                controller().getFacilityCenter(currentTarget);
                currentTarget.add(Tmp.v3.rnd(craftRadius));
            }

            weldingTimer -= delta();
            currentHandPos.approach(currentTarget, handSpeed);

            if (currentHandPos.epsilonEquals(currentTarget))
            {
                if(Mathf.chanceDelta(0.5)) {
                    effect.at(currentHandPos.x, currentHandPos.y, getHandRot() - 180f);
                }
            }
        }
    }
}
