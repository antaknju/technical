package technical.core;

import static mindustry.Vars.tilesize;
import static technical.util.Debugger.rprint;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Puddles;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import technical.util.Fr;
import technical.content.TFx;
import technical.content.TLiquids;
import technical.util.TCol;

public class Volcano extends EnvBlock
{
    public static final Point2[] offsets = {
            new Point2(0, 0),
            new Point2(1, 0),
            new Point2(1, 1),
            new Point2(0, 1),
            new Point2(-1, 1),
            new Point2(-1, 0),
            new Point2(-1, -1),
            new Point2(0, -1),
            new Point2(1, -1),
    };

    public Block parent = Blocks.air;

    public Effect steamEffect = TFx.volcanoSteam;

    public Color effectColor = Pal.vent;
    public float effectSpacing = 15f;

    public float inactiveTime = Fr.time * 90;
    public float activeTime = Fr.time * 30;

    public static final float eruptGravity = 120f;
    public static final float eruptSplit = 0.5f;

    static
    {
        for(var p : offsets)
        {
            p.sub(1, 1);
        }
    }

    public Volcano(String name)
    {
        super(name);
        variants = 2;
    }

    @Override
    public void drawMain(Tile tile)
    {
        if (parent instanceof Floor floor)
        {
            floor.drawMain(tile);
        }

        if (checkAdjacent(tile))
        {
            Draw.rect(variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))], tile.worldx() - tilesize, tile.worldy() - tilesize);
        }
    }

    @Override
    public boolean shouldCreateBuild(Tile tile)
    {
        return isCenterVent(tile);
    }

    public boolean isCenterVent(Tile tile)
    {
        Tile topRight = tile.nearby(1, 1);
        return topRight != null && topRight.floor() == this && checkAdjacent(topRight);
    }

    @Override
    public EnvBuild newBuild(Tile tile)
    {
        return new VolcanoBuild(tile, this);
    }

    public boolean checkAdjacent(Tile tile)
    {
        for(var point : offsets) {
            Tile other = Vars.world.tile(tile.x + point.x, tile.y + point.y);
            if(other == null || other.floor() != this) {
                return false;
            }
        }
        return true;
    }

    public static Vec2 getLandingSpot(float startX, float startY, float seed)
    {
        Mathf.rand.setSeed((long)seed);

        float ang = Mathf.rand.random(360f);
        float force = Mathf.rand.random(40f, 100f);

        float lx = startX + Mathf.cosDeg(ang) * force;
        float ly = startY + Mathf.sinDeg(ang) * force;

        return Tmp.v1.set(lx, ly);
    }

    public class VolcanoBuild extends EnvBuild
    {
        public float cycleTimer, effectTimer, steamTimer;
        public Seq<LavaDroplet> droplets = new Seq<>();

        public VolcanoBuild(Tile tile, EnvBlock block)
        {
            super(tile, block);
            this.cycleTimer = Mathf.randomSeed(tile.pos(), 0, (int)(activeTime + inactiveTime));
        }

        @Override
        public void draw()
        {
            Draw.z(Layer.effect);

            for (var d : droplets)
            {
                float t = d.time / d.lifetime;
                float size = (1f + 2.5f * Mathf.slope(t));

                for (int j = 0; j < 3; j++)
                {
                    int index = j * 3;
                    if (d.trail.size > index)
                    {
                        Vec2 pos = d.trail.get(index);
                        float trailScl = 1f - (j * 0.25f);

                        Draw.color(TCol.lavaRed);
                        Fill.circle(pos.x, pos.y, size * trailScl);
                    }
                }

                // Main Droplet
                Draw.color(TCol.lavaOrange, TCol.lavaYellow, Mathf.slope(t));
                Fill.circle(d.x, d.y, size);

                Draw.color(TCol.lavaYellow);
                Fill.circle(d.x, d.y + (size * 0.2f), size * 0.4f);
            }

            Draw.reset();
        }

        @Override
        public void updateTile()
        {
            if (!Vars.state.isGame())
                return;

            Volcano v = (Volcano) block;
            float delta = Time.delta;

            cycleTimer = (cycleTimer + delta) % (v.activeTime + v.inactiveTime);
            boolean isActive = cycleTimer < v.activeTime;

            steamTimer += delta;
            if(steamTimer >= 20f)
            {
                steamTimer %= 20f;
                Tile target = tile.nearby(-1, -1);
                if(target != null && target.block() == Blocks.air)
                {
                    TFx.volcanoSteam.at(tile.worldx(), tile.worldy(), v.effectColor);
                }
            }

            if (isActive)
            {
                Puddles.deposit(tile, tile, TLiquids.lava, 0.2f, true, false);
                Damage.damage(tile.x * tilesize, tile.y * tilesize, tilesize * 2, 15f);
            }

            effectTimer += delta;
            if (effectTimer >= v.effectSpacing)
            {
                effectTimer %= v.effectSpacing;
                float x = tile.worldx(), y = tile.worldy();

                Tile targetTile = tile.nearby(-1, -1);
                if (targetTile != null) {
                    if (targetTile.block() == Blocks.air)
                    {
                        if (isActive)
                            v.steamEffect.at(x, y, v.effectColor);
                        else
                            v.steamEffect.at(x, y, v.effectColor);

                        if(isActive)
                        {
                            for (int i = 0; i < 4; i++)
                            {
                                long uniqueSeed = (long)tile.pos() ^ ((long)Time.time * 67676L) + i;

                                float nx = tile.worldx();
                                float ny = tile.worldy();

                                Vec2 target = getLandingSpot(nx, ny, uniqueSeed);

                                droplets.add(new LavaDroplet(nx, ny, target.x, target.y, 70f, uniqueSeed));
                            }
                        }
                    }
                }
            }

            // 4. Update Droplets
            droplets.removeAll(d -> {
                boolean dead = d.update(delta);

                // TODO Damage mid-flight, need to rethink
//                Damage.damage(d.x, d.y, tilesize * 0.8f, 2f);

                if (dead && !Vars.net.client() && Vars.world != null)
                {
                    Tile land = Vars.world.tileWorld(d.targetX, d.targetY);
                    if (land != null && TLiquids.lava != null)
                    {
                        Puddles.deposit(land, land, TLiquids.lava, 20f, true, false);
                        Damage.damage(d.targetX, d.targetY, tilesize, 10f);
                    }
                }
                return dead;
            });
        }

        public class LavaDroplet
        {
            public float x, y, startX, startY, targetX, targetY, time, lifetime, seed;
            public Seq<Vec2> trail = new Seq<>();
            public Color mainColor;

            public LavaDroplet(float x, float y, float targetX, float targetY, float lifetime, float seed) {
                this.startX = this.x = x; this.startY = this.y = y;
                this.targetX = targetX; this.targetY = targetY;
                this.lifetime = lifetime; this.seed = seed;

                this.mainColor = TCol.lavaOrange.cpy().lerp(TCol.lavaRed, Mathf.randomSeed((long)seed, 0f, 0.2f));
            }

            public boolean update(float delta) {
                time += delta;
                float t = time / lifetime;

                x = Mathf.lerp(startX, targetX, t);
                y = Mathf.lerp(startY, targetY, t) + (50f * Mathf.sin(t * Mathf.PI));

                trail.insert(0, new Vec2(x, y));
                if (trail.size > 12) trail.remove(trail.size - 1);

                return time >= lifetime;
            }
        }
    }
}