package technical.expansion;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Items;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import technical.content.TCustom;

import static mindustry.Vars.*;

public class RollerRouter extends RollerConveyor
{
    public int itemCount = 9;

    public TextureRegion baseRegion;
    public TextureRegion rollerCapRegion;

    public RollerRouter(String name)
    {
        super(name);
    }

    @Override
    public void load()
    {
        super.load();

        baseRegion = Core.atlas.find(name + "-base");
        rollerCapRegion = Core.atlas.find(name + "-cap");
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list)
    {
        Draw.rect(baseRegion, plan.drawx(), plan.drawy());

        super.drawPlanRegion(plan, list);
    }

    @Override
    public TextureRegion[] icons()
    {
        return new TextureRegion[]{baseRegion, super.icons()[0]};
    }

    public class RollerRouterBuild extends RollerConveyorBuild
    {
        public float smoothRot;

        public int currentRotation = 0;

        public int itemsMoved = 0;

        @Override
        public void created()
        {
            super.created();

            smoothRot = rotdeg();
            currentRotation = rotation;

            pickNext(null);
        }

        @Override
        public void handleItemStated(Building source, Item item, int crafting_state)
        {
            super.handleItemStated(source, item, crafting_state);

            itemsMoved++;

            if(itemsMoved >= itemCount)
            {
                pickNext(item);

                itemsMoved = 0;
            }
        }

        @Override
        public void onProximityUpdate()
        {
            super.onProximityUpdate();

//            if (next == null)
//            {
//                pickNext(null);
//            }
        }

        public void pickNext(Item item)
        {
            if (item == null)
                item = Items.copper;

            int tries = 0;
            Building target;

            do
            {
                currentRotation = (currentRotation + 1) % 4;

                target = nearby(currentRotation);
                tries++;

                if (target != null && target.acceptItem(this, item))
                {
                    break;
                }
            }
            while (tries < 4);

            rotation = currentRotation;
            updateProximity();
        }

        @Override
        public void updateTile()
        {
            super.updateTile();

            smoothRot = Mathf.slerpDelta(smoothRot, rotdeg(), 0.2f);

            if (clogHeat >= 0.5f)
            {
                pickNext(items.first());
            }
        }

        @Override
        public void draw()
        {
            Draw.z(Layer.block - 0.3f);

            Draw.rect(baseRegion, x, y);

            Draw.z(Layer.block - 0.2f);

            Draw.rect(currentRollerRegion(), x, y, tilesize * blendsclx, tilesize * blendscly, smoothRot);

            Draw.rect(rollerCapRegion, x, y, smoothRot);
            Draw.rect(rollerCapRegion, x, y, smoothRot + 180);

            Draw.z(Layer.block - 0.1f);
            float layer = Layer.block - 0.1f, wwidth = world.unitWidth(), wheight = world.unitHeight(), scaling = 0.01f;

            for(int i = 0; i < len; i++)
            {
                Item item = ids[i];
                Tmp.v1.trns(smoothRot, tilesize, 0);
                Tmp.v2.trns(smoothRot, -tilesize / 2f, xs[i] * tilesize / 2f);

                float
                        ix = (x + Tmp.v1.x * ys[i] + Tmp.v2.x),
                        iy = (y + Tmp.v1.y * ys[i] + Tmp.v2.y);

                // keep draw position deterministic.
                Draw.z(layer + (ix / wwidth + iy / wheight) * scaling);
                Draw.rect(item.fullIcon, ix, iy, itemSize, itemSize);

                var rec = TCustom.ConveyorRecipes.get(item);
                if (craftingState[i] > 0 && rec != null && rec.actions.length * rec.times > craftingState[i])
                {
                    var resitm = rec.result;

                    Draw.scl(0.4f);

                    if (resitm != null)
                        Draw.rect(resitm.fullIcon, ix + itemSize / 2, iy - itemSize / 2);

                    Draw.scl();
                }
            }
        }

        @Override
        public void write(Writes write)
        {
            super.write(write);

            write.i(itemsMoved);
        }

        @Override
        public void read(Reads read, byte revision)
        {
            super.read(read, revision);

            itemsMoved = read.i();
        }
    }
}