package technical.expansion.kinetic;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;
import static technical.debug.Debugger.print;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import mindustry.game.EventType.ResetEvent;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import technical.utility.Fr;
import technical.utility.TCol;

public class Sprocket extends KineticBlock
{
    public int chainRange = 10;
    public int maxLinks = 2;

    public static final Seq<SprocketBuild> sprockets = new Seq<>();

    public TextureRegion bottomRegion;

    public Sprocket(String name)
    {
        super(name);

        solid = true;
        canOverdrive = false;
        swapDiagonalPlacement = true;
    }

    @Override
    public void load()
    {
        super.load();

        bottomRegion = Core.atlas.find(name + "-bottom");

        Events.on(ResetEvent.class, e -> {
            staticCleanUp();
        });
    }

    public static void staticCleanUp()
    {
        sprockets.clear();
    }

    @Override
    public void setBars()
    {
        addBar("angularSpeed", (SprocketBuild build) ->
            new Bar(
                () -> Core.bundle.format("bar.angular-speed", String.format("%.2f", build.systemSpeed() / Fr.angularSpeed)),
                () -> Pal.lightOrange,
                () -> build.systemSpeed()
            )
        );

        addBar("torque", (SprocketBuild build) ->
            new Bar(
                () -> Core.bundle.format("bar.torque", String.format("%.2f", build.systemTorque() / Fr.torque)),
                () -> Pal.lightishOrange,
                () -> build.systemTorque()
            )
        );

        addBar("kineticEfficiency", (SprocketBuild build) ->
            new Bar(
                () -> Core.bundle.format("bar.kinetic-efficiency", String.format("%.2f", build.systemEfficiency() * 100)),
                () -> Pal.graphiteAmmoBack,
                () -> build.systemEfficiency()
            )
        );

        addBar("inertia", (SprocketBuild build) ->
            new Bar(
                () -> Core.bundle.format("bar.inertia", String.format("%.2f", build.systemInertia() / Fr.inertia)),
                () -> Pal.items,
                () -> build.systemInertia()
            )
        );
    }

    @Override
    public TextureRegion[] icons()
    {
        return new TextureRegion[]{bottomRegion, region};
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid)
    {
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, chainRange * tilesize - tilesize / 2, Pal.accent);
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation)
    {
        Placement.calculateNodes(points, this, rotation, (point, other) -> overlaps(world.tile(point.x, point.y), world.tile(other.x, other.y)));
    }

    public boolean overlaps(@Nullable Tile src, @Nullable Tile other){
        if(src == null || other == null) return true;
        return Intersector.overlaps(Tmp.cr1.set(src.worldx() + offset, src.worldy() + offset, chainRange * tilesize - tilesize), Tmp.r1.setSize(size * tilesize).setCenter(other.worldx() + offset, other.worldy() + offset));
    }

    public class SprocketBuild extends KineticBuild
    {
        private float chainOffset = 0f;

        @Override
        public Seq<Building> proximity()
        {
            return getNearbyLinks().add(proximity);
        }

        @Override
        public void placed()
        {
            super.placed();

            sprockets.add(this);
        }

        @Override
        public void onRemoved()
        {
            super.onRemoved();

            sprockets.remove(this);
        }

        public Seq<Building> getNearbyLinks()
        {
            var seq = new Seq<Building>();

            for (var s : sprockets)
            {
                if (s != this && tile.within(s.tile, chainRange * tilesize))
                {
                    seq.add(s);
                }
            }

            return seq;
        }

        @Override
        public void updateTile()
        {
            super.updateTile();

            float segmentLength = 6f; 
            float speed = (kinetic.graph() != null ? kinetic.graph().currentSpeed() : 0);
            chainOffset += speed * Time.delta * timeScale;
            chainOffset %= (segmentLength * 2);
        }

        @Override
        public void draw() 
        {
            super.draw();

            Draw.rect(bottomRegion, x, y);
            Draw.z(Layer.block + 0.1f);

            int counter = 0;
            Seq<Building> links = getNearbyLinks();
            links.sort(b -> tile.dst2(b.tile));
            for (Building link : links) 
            {
                counter++;
                drawLink(link);

                if (counter >= maxLinks) break;
            }

            Draw.z(Layer.block + 0.2f);
            Draw.rect(region, x, y);
        }

        @Override
        public void drawSelect()
        {
            super.drawSelect();

            Drawf.dashCircle(x, y, chainRange * tilesize - tilesize / 2, Pal.accent);
        }

        private void drawLink(Building target)
        {
            Vec2 start = Tmp.v1.set(tile.worldx(), tile.worldy());
            Vec2 end = Tmp.v2.set(target.tile.worldx(), target.tile.worldy());

            float segmentLength = 6f; 
            float offsetDistance = 2.5f; 
            float totalDist = start.dst(end);
            
            Vec2 dir = Tmp.v3.set(end).sub(start).nor();
            Vec2 normal = Tmp.v4.set(-dir.y, dir.x).scl(offsetDistance);

            int extraSegments = 2;
            int segmentCount = (int)Math.ceil(totalDist / segmentLength) + extraSegments;

            for (int i = -extraSegments; i < segmentCount; i++) {
                // Chain 1
                float pS1 = (i * segmentLength) + chainOffset;
                float pE1 = pS1 + segmentLength;

                float drawS1 = Math.max(0, Math.min(totalDist, pS1));
                float drawE1 = Math.max(0, Math.min(totalDist, pE1));

                if (drawS1 != drawE1) {
                    Draw.color((i % 2 == 0) ? TCol.iron : TCol.ironDark);
                    Lines.line(
                        start.x + dir.x * drawS1 + normal.x, start.y + dir.y * drawS1 + normal.y,
                        start.x + dir.x * drawE1 + normal.x, start.y + dir.y * drawE1 + normal.y
                    );
                }

                // Chain 2 (backward)
                float pS2 = (i * segmentLength) - chainOffset + (segmentLength * 2);
                float pE2 = pS2 + segmentLength;

                float drawS2 = Math.max(0, Math.min(totalDist, pS2));
                float drawE2 = Math.max(0, Math.min(totalDist, pE2));

                if (drawS2 != drawE2) {
                    Draw.color((i % 2 == 0) ? TCol.iron : TCol.ironDark);
                    Lines.line(
                        start.x + dir.x * drawS2 - normal.x, start.y + dir.y * drawS2 - normal.y,
                        start.x + dir.x * drawE2 - normal.x, start.y + dir.y * drawE2 - normal.y
                    );
                }
            }
            Draw.reset();
        }

        public void read(Reads read, byte revision)
        {
            super.read(read, revision);

            sprockets.add(this);
        }
    }
}
