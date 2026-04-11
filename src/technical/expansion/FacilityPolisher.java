package technical.expansion;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Effect;
import technical.utility.T;
import technical.content.TFx;

public class FacilityPolisher extends FacilityArm
{
    public float polishingTime = 60f;

    public Effect effect = TFx.polishing;

    public FacilityPolisher(String name) 
    {
        super(name);
    }

    public class FacilityPolisherBuild extends FacilityArmBuild
    {
        public float polishingTimer = 0;
        public Vec2 currentTarget = new Vec2();

        @Override
        public void updateAddapter() 
        {
            float craftRadius = T.getContentWorldSize(controller().plan().outputPayload.item) / 2;

            float speedMultiplier = 1f;
            float sweep = Mathf.sin(Time.time * speedMultiplier, 2, 2);

            if (polishingTimer <= 0)
            {
                polishingTimer = polishingTime;

                controller().getFacilityCenter(currentTarget);
                currentTarget.add(Tmp.v3.rnd(craftRadius));
            }

            currentTarget.add(sweep, 0);

            polishingTimer -= delta();
            currentHandPos.approach(currentTarget, handSpeed);

            if (currentHandPos.epsilonEquals(currentTarget))
            {
                effect.at(currentHandPos.x, currentHandPos.y, getHandRot() - 180f);
            }
        }
    }
}
