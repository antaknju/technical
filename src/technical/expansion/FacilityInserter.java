package technical.expansion;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import technical.T;
import technical.content.TFx;
import technical.expansion.FacilityLoader.FacilityLoaderBuild;

public class FacilityInserter extends FacilityArm
{
    public int itemCapacity = 10;

    public Effect pickupEffect = Fx.pickup;
    public Effect dropEffect = TFx.drop;

    public float itemSpeed = 0.1f;

    public FacilityInserter(String name) 
    {
        super(name);
    }

    public class FacilityInserterBuild extends FacilityArmBuild
    {
        public ItemStack carriedStack;
        public boolean isHolding = false;
        public FacilityLoaderBuild targetLoader;

        public float itemTime = 0;

        @Override
        public void updateAddapter() 
        {
            float maxDst2 = reachRange * reachRange;

            float minDst2 = Float.MAX_VALUE;
            targetLoader = null;

            for (var l : controller().loaders()) 
            {
                if (l != null && l.isValid()) 
                {
                    float dst2 = Mathf.dst2(x, y, l.x(), l.y()); 
                    
                    if (dst2 <= maxDst2 && dst2 < minDst2) {
                        minDst2 = dst2;
                        targetLoader = l;
                    }
                }
            }

            if (targetLoader == null || !targetLoader.isValid() || !targetLoader.items.any() || !controller().acceptItem(targetLoader, targetLoader.items.first()))
            {
                returnToIdle();
                return;
            }

            Vec2 center = controller().getFacilityCenter(Tmp.v2);

            if (targetLoader != null) 
            {
                Vec2 target;
                if (!isHolding)
                {
                    target = Tmp.v2.set(targetLoader.x, targetLoader.y);

                    itemTime -= itemSpeed * delta();

                    if (currentHandPos.equals(target))
                    {
                        Item itm = targetLoader.items.first();
                        carriedStack = new ItemStack(itm, Math.min(itemCapacity, targetLoader.items.get(itm)));
                        targetLoader.items.remove(carriedStack);

                        isHolding = true;

                        pickupEffect.at(target.x, target.y);
                    }
                }
                else
                {
                    target = center;
                    
                    itemTime += itemSpeed * delta();

                    if (currentHandPos.equals(target))
                    {
                        controller().items.add(carriedStack.item, carriedStack.amount);
                        carriedStack = null;

                        isHolding = false;

                        dropEffect.at(target.x, target.y);
                    }
                }

                itemTime = Mathf.clamp(itemTime);
                currentHandPos.approach(target, handSpeed);
            } 
            else
            {
                returnToIdle();
            }
        }

        public void drawAddapter()
        {
            super.drawAddapter();

            T.drawItemStack(carriedStack, currentHandPos.x, currentHandPos.y, itemTime);
        }
    }
}
