package technical.expansion.train;

import mindustry.world.Tile;
import mindustry.world.meta.BlockGroup;
import technical.T;
import technical.expansion.TBlock;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.input.Placement;

public class RailConnector extends TBlock 
{
    public float minRange = 15f;
    public float maxRange = 25f;

    public RailConnector(String name) 
    {
        super(name);
        update = true;
        configurable = true;
        group = BlockGroup.transportation;
        solid = false;
        
        alwaysReplace = true;
        swapDiagonalPlacement = true;
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
        // float min = (minRange + 0.5F) * tilesize;
        float max = (maxRange - 0.5F) * tilesize;

        return dst < max;
    }

    public class RailConnectorBuild extends TBuild
    {
        public Seq<RailConnectorBuild> links = new Seq<>();

        public boolean configuring = false;

        @Override
        public boolean onConfigureBuildTapped(Building otherbuild) {
            if (!(otherbuild instanceof RailConnectorBuild) || otherbuild == null || otherbuild == this) return false;
            RailConnectorBuild other = (RailConnectorBuild) otherbuild;

            if (links.contains(other)) {
                this.links.remove(other);
                other.links.remove(this);
                return false;
            } 

            if (links.size >= 2 || other.links.size >= 2) {
                Vars.ui.showInfoToast(T.bundle("err.links", "scarlet"), 1f);
                return false;
            }

            float dst = Mathf.dst(x, y, other.x, other.y) / Vars.tilesize;
            if (dst < minRange || dst > maxRange) {
                Vars.ui.showInfoToast(T.bundle("err.range", "scarlet"), 1f);
                return false;
            }

            if (!isPathClear(this, other)) {
                Vars.ui.showInfoToast(T.bundle("err.path-blocked", "scarlet"), 1f);
                return false;
            }

            this.links.add(other);
            other.links.add(this);
            return false;
        }

        @Override
        public void drawConfigure()
        {
            super.drawConfigure();

            Draw.color(Pal.accent);

            Lines.stroke(0.8F);
            Lines.circle(x, y, maxRange * tilesize);
            Lines.circle(x, y, minRange * tilesize);

            for (RailConnectorBuild link : links) {
                T.outline(link, Pal.place);
            }

            Draw.reset();

            configuring = true;
        }

        @Override
        public void updateTile()
        {
            configuring = false;
        }

        @Override
        public void drawSelect()
        {
            if (links.size <= 0 || configuring) return;

            T.outline(this);

            for (RailConnectorBuild link : links) {
                T.outline(link, Pal.place);
            }

            Draw.reset();
        }

        @Override
        public void onRemoved() 
        {
            for (RailConnectorBuild link : links) {
                link.links.remove(this);
            }
            links.clear();

            if (RailSplineManager.nodes.contains(this))
                RailSplineManager.nodes.remove(this);
        }

        @Override
        public void placed()
        {
            super.placed();

            if (!RailSplineManager.nodes.contains(this))
                RailSplineManager.nodes.add(this);
        }

        private boolean isPathClear(RailConnectorBuild a, RailConnectorBuild b) 
        {
            int x1 = a.tile.x;
            int y1 = a.tile.y;
            int x2 = b.tile.x;
            int y2 = b.tile.y;

            int dx = Math.abs(x2 - x1);
            int dy = Math.abs(y2 - y1);
            int sx = x1 < x2 ? 1 : -1;
            int sy = y1 < y2 ? 1 : -1;
            int err = dx - dy;

            while (true) {
                Tile t = Vars.world.tile(x1, y1);
                if (t == null) return false;

                if (t.floor().isLiquid || (t.block() != null && t.solid())) {
                    return false;
                }

                if (x1 == x2 && y1 == y2) break;

                int e2 = 2 * err;
                if (e2 > -dy) {
                    err -= dy;
                    x1 += sx;
                }
                if (e2 < dx) {
                    err += dx;
                    y1 += sy;
                }
            }

            return true;
        }

        @Override
        public void write(Writes write) 
        {
            super.write(write);

            write.i(links.size);
            
            for (RailConnectorBuild link : links) {
                write.i(link.tile.x);
                write.i(link.tile.y);
            }
        }

        @Override
        public void read(Reads read, byte revision) 
        {
            super.read(read, revision);

            // if (!RailSplineManager.clearedRenderer) {
            //     RailSplineManager.clear(); // clear old splines
            //     RailSplineManager.clearedRenderer = true;
            //     Log.info("Cleared old splines");
            // }

            int count = read.i();
            links.clear();

            for (int i = 0; i < count; i++) {
                int tx = read.i();
                int ty = read.i();

                Building b = world.build(tx, ty);
                if (b instanceof RailConnectorBuild && b != this) {
                    RailConnectorBuild link = (RailConnectorBuild) b;
                    links.add(link);

                    if (!link.links.contains(this)) link.links.add(this);
                }
            }
            
            if (!RailSplineManager.nodes.contains(this))
                RailSplineManager.nodes.add(this);
        }
    }
}