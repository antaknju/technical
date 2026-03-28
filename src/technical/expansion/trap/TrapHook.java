package technical.expansion.trap;

import mindustry.world.Tile;
import technical.T;
import technical.TCol;
import technical.content.TFx;
import technical.expansion.TBlock;
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
        update = true;
        configurable = true;
        
        solid = true;
        
        swapDiagonalPlacement = true;
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
    public void changePlacementPath(Seq<Point2> points, int rotation){
        Placement.calculateNodes(points, this, rotation, 
            (point, other) -> overlaps(world.tile(point.x, point.y), world.tile(other.x, other.y))
        );
    }

    public boolean overlaps(@Nullable Tile src, @Nullable Tile other){
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
        public TrapHookBuild connection;
        public boolean configuring = false;

        public boolean isCut = false;

        public float cooldownTimer = cooldownTime();

        @Override
        public void updateTile()
        {
            if(isMain() && !isCut)
            {
                Groups.unit.each(u -> {

                    if (u.isGrounded() && u.team != team) // && u.team != team
                    {
                        if(Intersector.intersectSegmentCircle(new Vec2(x, y), new Vec2(connection.x, connection.y), new Vec2(u.x, u.y), u.hitSize))
                        {
                            onCut();

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
                        drawLineEffects(x, y, connection.x, connection.y, tilesize, stringReplaceEffect);
                    }
                }
            }

            configuring = false;
        }

        void drawLineEffects(float x1, float y1, float x2, float y2, float spacing, Effect fx){
            float dx = x2 - x1, dy = y2 - y1;
            float len = Mathf.len(dx, dy);

            int count = (int)(len / spacing);

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

            if (isMain()) connection.onCut();

            for (var b : proximity)
            {
                if (b instanceof TrapBlockBuild t)
                {
                    t.trap();
                }
            }
        }


        @Override
        public boolean onConfigureBuildTapped(Building otherbuild)
        {
            if (!(otherbuild instanceof TrapHookBuild) || otherbuild == null || otherbuild == this) return false;
            
            TrapHookBuild other = (TrapHookBuild)otherbuild;

            if (other == connection) {
                connection.connection = null;
                connection = null;
                return false;
            } 

            if (connection != null || other.connection != null) {
                Vars.ui.showInfoToast(T.bundle("err.links", "scarlet"), 1f);
                return false;
            }

            float dst = Mathf.dst(x, y, other.x, other.y) / Vars.tilesize;
            if (dst > range) {
                Vars.ui.showInfoToast(T.bundle("err.range", "scarlet"), 1f);
                return false;
            }

            connection = other;
            connection.connection = this;

            return false;
        }

        @Override
        public void drawConfigure()
        {
            super.drawConfigure();

            Draw.color(Pal.accent);
            Lines.stroke(0.8F);
            Lines.circle(x, y, range * tilesize);
            Draw.color();

            if (connection != null)
                T.outline(connection, Pal.place);

            Draw.reset();

            configuring = true;
        }

        @Override
        public void drawSelect()
        {
            if (connection == null || configuring) return;

            T.outline(this, Pal.accent);
            T.outline(connection, Pal.place);

            Draw.reset();
        }

        public boolean isMain()
        {
            return connection != null && (connection.x > x || (connection.x == x && connection.y > y));
        }

        @Override
        public void draw()
        {
            super.draw();

            if (connection == null) return;

            Draw.z(Layer.block + 1);
            if (isMain())
            {
                if (!isCut)
                {
                    Draw.color(TCol.iron);
                    Lines.stroke(2);
                    Lines.line(x, y, connection.x, connection.y);
                    Draw.color(Color.white);
                    Lines.stroke(1);
                }
                else
                {
                    Draw.color(T.c("#aaaaaa7a"));
                    Lines.stroke(1);
                }

                Lines.line(x, y, connection.x, connection.y);
            }
            
            Draw.reset();
        }

        @Override
        public void onRemoved() 
        {
            if (connection == null) return;

            if (detectsDestruction) 
            {
                // A little hacky method...
                onCut();
                if (!isMain()) connection.onCut();
                drawLineEffects(x, y, connection.x, connection.y, tilesize, stringReplaceEffect);
            }

            connection.connection = null;
            connection = null;
        }


        @Override
        public void write(Writes write) {
            super.write(write);
            
            write.i(connection != null ? connection.tile.x : -1);
            write.i(connection != null ? connection.tile.y : -1);

            write.bool(isCut);
            write.f(cooldownTimer);
        }

        @Override
        public void read(Reads read, byte revision) 
        {
            super.read(read, revision);

            int x = read.i();
            int y = read.i();

            Building b = world.build(x, y);
            if (b != null)
            {
                connection = (TrapHookBuild)b;
                connection.connection = this;
            }

            isCut = read.bool();
            cooldownTimer = read.f();
        }
    }
}