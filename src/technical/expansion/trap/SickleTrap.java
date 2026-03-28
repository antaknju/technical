package technical.expansion.trap;

import static mindustry.Vars.tilesize;

import arc.Core;
import arc.func.Cons;
import arc.func.Prov;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Vec2;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.graphics.Layer;
import mindustry.ui.Styles;
import mindustry.world.Tile;
import technical.content.TIcons;

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
            addSelectableIcon(table, TIcons.flip, () -> flipped, this::configure);
        }

        public void addSelectableIcon(Table table, TextureRegion icon, Prov<Boolean> holder, Cons<Boolean> consumer)
        {
            ImageButton button = new ImageButton(Styles.clearTogglei);

            button.getStyle().imageUp = new TextureRegionDrawable(icon);

            button.update(() -> {
                button.setChecked(holder.get());
            });

            button.changed(() -> {
                consumer.get(button.isChecked());
            });

            table.add(button).size(40f);
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

            float offx = ((sickleSize - size) * tilesize) / 2f;
            float px = x + Angles.trnsx(base + currentAngle, offx);
            float py = y + Angles.trnsy(base + currentAngle, offx);

            Draw.scl(1, dir());
            Draw.z(Layer.bullet - 1);

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

                        u.damage(damage);

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
