package technical.core.trap;

import static mindustry.Vars.tilesize;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import technical.content.TIcons;
import technical.util.TUI;

public class SickleTrap extends TrapBlock 
{
    // Sickle Size in Tiles
    public float sickleSize = 6;

    public float damage = 1f;

    public float rotationSpeed = 5f;

    public TextureRegion topRegion;
    public TextureRegion topBlurRegion;

    public SickleTrap(String name)
    {
        super(name);

        rotate = true;
        update = true;
        solid = true;

        rotateDraw = true;
        drawArrow = false;

        configurable = true;
        saveConfig = true;
        copyConfig = true;

        config(Boolean.class, (SickleTrapBuild build, Boolean f) -> build.flipped = f);
        configClear((SickleTrapBuild build) -> build.flipped = false);
    }

    @Override
    public void load()
    {
        super.load();
        topRegion = Core.atlas.find(name + "-top");
        topBlurRegion = Core.atlas.find(name + "-top-blur");
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list)
    {
        Draw.rect(region, plan.drawx(), plan.drawy());

        float scl_x = Draw.xscl;
        float scl_y = Draw.yscl;

        Draw.scl(1, (boolean)plan.config ? -1 : 1);

        float base_rot = plan.rotation * 90f;

        float off_x = ((sickleSize - size) * tilesize) / 2f;
        float px = plan.drawx() + Angles.trnsx(base_rot, off_x);
        float py = plan.drawy() + Angles.trnsy(base_rot, off_x);

        Draw.rect(topRegion, px, py, base_rot);

        Draw.scl(scl_x, scl_y);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, topRegion};
    }

    public class SickleTrapBuild extends TrapBlock.TrapBlockBuild 
    {
        boolean spinning = false;

        float currentAngle = 0f;

        public boolean flipped = false;

        @Override
        public void buildConfiguration(Table table)
        {
            TUI.addSelectableIcon(table, TIcons.flip, () -> flipped, this::configure);
        }

        public int dir()
        {
            return flipped ? -1 : 1;
        }

        @Override
        public void draw()
        {
            super.draw();

            float base = rotation * 90f;

            float off_x = ((sickleSize - size) * tilesize) / 2f;
            float px = x + Angles.trnsx(base + currentAngle, off_x);
            float py = y + Angles.trnsy(base + currentAngle, off_x);

            Draw.scl(1, dir());
            Draw.z(Layer.blockOver);

            if (spinning)
                Draw.rect(topBlurRegion, px, py, base + currentAngle);
            else
                Draw.rect(topRegion, px, py, base);

            Draw.reset();
        }
        @Override
        public void updateTile()
        {
            super.updateTile();

            if(spinning)
            {
                currentAngle += rotationSpeed * dir();

                float maxRad = (sickleSize - 1) * tilesize;
                float base = rotation * 90f;

                float px = x + Angles.trnsx(base + currentAngle, maxRad);
                float py = y + Angles.trnsy(base + currentAngle, maxRad);

                Units.nearbyEnemies(team, x, y, maxRad, u -> {
                    if(Intersector.intersectSegmentCircle(new Vec2(x, y), new Vec2(px, py), new Vec2(u.x, u.y), u.hitSize))
                    {
                        float d = Mathf.dst(x, y, u.x, u.y);

                        float ux = x + Angles.trnsx(base + currentAngle + rotationSpeed * dir(), d);
                        float uy = y + Angles.trnsy(base + currentAngle + rotationSpeed * dir(), d);

                        u.damagePierce(damage);

                        Tile tile = Vars.world.tileWorld(ux, uy);

                        if (!tile.solid())
                        {
                            u.x = ux;
                            u.y = uy;
                        }
                    }
                });

                if(Math.abs(currentAngle) >= 360f)
                {
                    spinning = false;
                    currentAngle = 0f;
                }
            }
        }

        @Override
        public Object config()
        {
            return flipped;
        }

        @Override
        public void onTrap()
        {
            spinning = true;
            currentAngle = 0f;
        }

        @Override
        public boolean canTrap()
        {
            return !spinning;
        }

        @Override
        public void write(Writes write)
        {
            super.write(write);

            write.bool(flipped);
        }

        @Override
        public void read(Reads read, byte revision)
        {
            super.read(read, revision);

            flipped = read.bool();
        }
    }
}
