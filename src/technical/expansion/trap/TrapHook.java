package technical.expansion.trap;

import arc.func.Cons;
import mindustry.graphics.Drawf;
import mindustry.world.Tile;
import technical.utility.TBundle;
import technical.utility.TCol;
import technical.content.TFx;
import technical.expansion.kinetic.KineticBlock;
import technical.expansion.tech.TechStat;
import technical.expansion.trap.TrapBlock.TrapBlockBuild;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;

import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.ui.Bar;
import technical.utility.TDraw;

public class TrapHook extends KineticBlock
{
    public float range = 10f;
    public float stringCooldownTime = 2 * 60f;

    public Effect stringBreakEffect = Fx.smeltsmoke;
    public Effect stringReplaceEffect = TFx.stringBreak;

    public boolean detectsDestruction = true;

    public TrapHook(String name)
    {
        super(name);
        swapDiagonalPlacement = true;

        configurable = true;
        copyConfig = true;
        saveConfig = true;

        config(Point2.class, (TrapHookBuild build, Point2 point) -> {
            Building other = world.build(build.tile.x + point.x, build.tile.y + point.y);
            if (other instanceof TrapHookBuild hook && other != build) {
                build.connectionPos = hook.pos();
                hook.connectionPos = build.pos();
            }
        });

        configClear((TrapHookBuild build) -> {
            if (build.connection() != null) {
                build.connection().connectionPos = -1;
                build.connectionPos = -1;
            }
        });
    }

    public float cooldownTime()
    {
        return stringCooldownTime * getTotalStat(TechStat.cooldown);
    }

    @Override
    public void setBars()
    {
        super.setBars();

        addBar("ready", (TrapHookBuild build) ->
            new Bar(
                () -> Core.bundle.format(build.cooldownTimer >= cooldownTime() ? "bar.ready" : "bar.not-ready"),
                () -> Pal.ammo,
                () -> build.cooldownTimer / cooldownTime()
            )
        );
    }
    
    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation)
    {
        Placement.calculateNodes(points, this, rotation, 
            (point, other) -> overlaps(world.tile(point.x, point.y), world.tile(other.x, other.y))
        );
    }

    public boolean overlaps(@Nullable Tile src, @Nullable Tile other)
    {
        if(src == null || other == null) return true;

        float sx = src.worldx() + offset;
        float sy = src.worldy() + offset;
        float ox = other.worldx() + offset;
        float oy = other.worldy() + offset;

        float dst = Mathf.dst(sx, sy, ox, oy);
        
        float max = (range - 0.5F) * tilesize;

        return dst < max;
    }

    public class TrapHookBuild extends KineticBuild
    {
        public boolean isCut = false;

        public float cooldownTimer = cooldownTime();

        private int connectionPos = -1;

        public TrapHookBuild connection()
        {
            if (connectionPos == -1)
                return null;

            if (world.tile(connectionPos).build instanceof TrapHookBuild thb)
                return thb;

            return null;
        }

        @Override
        public void updateTile()
        {
            if (efficiency <= 0) return;

            if(isMain() && !isCut)
            {
                Groups.unit.each(u -> {
                    if (u.team != team)
                    {
                        if(Intersector.intersectSegmentCircle(new Vec2(x, y), new Vec2(connection().x, connection().y), new Vec2(u.x, u.y), u.hitSize))
                        {
                            Main().onCut();

                            stringBreakEffect.at(u.x, u.y);
                            cooldownTimer = 0;
                        }
                    }
                });
            }

            if (isCut)
            {
                cooldownTimer += delta();

                if (cooldownTimer >= cooldownTime())
                {
                    isCut = false;

                    if (isMain())
                    {
                        drawLineEffects(x, y, connection().x, connection().y, stringReplaceEffect);
                    }
                }
            }
        }

        void drawLineEffects(float x1, float y1, float x2, float y2, Effect fx)
        {
            float dx = x2 - x1, dy = y2 - y1;
            float len = Mathf.len(dx, dy);

            int count = (int)(len / (float) Vars.tilesize);

            float stepX = dx / count;
            float stepY = dy / count;

            for(int i = 0; i <= count; i++){
                float px = x1 + stepX * i;
                float py = y1 + stepY * i;
                fx.at(px, py);
            }
        }

        public void onCut()
        {
            isCut = true;
            cooldownTimer = 0;

            consume();

            if (!connection().isCut)
                connection().onCut();

            for (var b : proximity)
            {
                if (b instanceof TrapBlockBuild t)
                {
                    t.trap();
                }
                else if (b instanceof TrapHookBuild t)
                {
                    if (!t.isCut)
                    {
                        t.onCut();
                    }
                }
            }
        }

        @Override
        public void drawConfigure()
        {
            super.drawConfigure();

            Draw.color(Pal.accent);

            Drawf.dashCircle(x, y, range * tilesize, Pal.accent);

            if (connection() != null)
                TDraw.highlight(connection(), Pal.place);

            Draw.reset();
        }

        @Override
        public void drawSelect()
        {
            if (connection() == null) return;

            TDraw.highlight(this, Pal.accent);
            TDraw.highlight(connection(), Pal.place);

            Draw.reset();
        }

        public boolean isMain()
        {
            return Main() == this;
        }

        public TrapHookBuild Main()
        {
            if (connection() == null)
                return null;

            if (connection().x > x || connection().x == x && connection().y > y)
                return this;

            return connection();
        }

        @Override
        public void draw()
        {
            super.draw();

            if (connection() == null) return;

            Draw.z(Layer.block + 1);

            if (!isCut)
            {
                Draw.color(TCol.iron);
                Lines.stroke(2);
                Lines.line(x, y, connection().x, connection().y);
                Draw.color(Color.white);
                Lines.stroke(1);
            }
            else
            {
                Draw.color(TCol.from("#aaaaaa7a"));
                Lines.stroke(1);
            }

            Lines.line(x, y, connection().x, connection().y);
            
            Draw.reset();
        }

        @Override
        public boolean onConfigureBuildTapped(Building other_build)
        {
            if (!(other_build instanceof TrapHookBuild other) || other_build == this) return false;

            if (other == connection()) {
                deselect();
                configure(null);
                return false;
            }

            if (connection() != null || other.connection() != null)
            {
                Vars.ui.showInfoToast(TBundle.color(TBundle.error("links"), TCol.error), 1f);
                return false;
            }

            float dst = Mathf.dst(x, y, other.x, other.y);
            if (dst > range * Vars.tilesize)
            {
                Vars.ui.showInfoToast(TBundle.color(TBundle.error("range"), TCol.error), 1f);
                return false;
            }

            deselect();
            configure(new Point2(other.tile.x - tile.x, other.tile.y - tile.y));

            return false;
        }

        @Override
        public void onRemoved()
        {
            if (connection() != null)
            {
                if (detectsDestruction)
                {
                    Main().onCut();
                    drawLineEffects(x, y, connection().x, connection().y, stringReplaceEffect);
                }

                connection().connectionPos = -1;
                connectionPos = -1;
            }
            super.onRemoved();
        }

        @Override
        public Object config()
        {
            if (connection() == null) return null;
            return new Point2(connection().tile.x - tile.x, connection().tile.y - tile.y);
        }


        @Override
        public void write(Writes write)
        {
            super.write(write);

            write.bool(isCut);
            write.f(cooldownTimer);

            write.i(connection() == null ? -1 : connectionPos);
        }

        @Override
        public void read(Reads read, byte revision) 
        {
            super.read(read, revision);

            isCut = read.bool();
            cooldownTimer = read.f();
            connectionPos = read.i();
        }
    }
}