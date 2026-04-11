package technical.expansion.mycelis;

import static mindustry.Vars.tilesize;

import arc.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.TileBitmask;
import mindustry.world.meta.*;
import technical.utility.T;
import technical.content.TBlocks;
import technical.debug.Debugger.DebugLine;

public class MycelisCord extends MycelisBlock {

    public float speed = 5f;

    public TextureRegion[] autotileRegions;
    public TextureRegion platingRegion;

    public Block evolution;

    public String regionName = null;
    public String platingRegionName = null;

    public static enum State
    {
        growing,
        branched,
        forwarded,
    }

    /*
        growing -> forwarded -> branched
    */

    public MycelisCord(String name)
    {
        super(name);

        group = BlockGroup.transportation;
        solid = false;
        update = true;

        hasLiquids = true;
        hasItems = true;

        itemCapacity = 1;
        liquidCapacity = 10f / 60f;

        underBullets = true;
        noSideBlend = true;

        health = 250;
    }

    @Override
    public void load(){
        super.load();

        if (regionName == null)
            regionName = name;

        if (platingRegionName != null)
            platingRegion = Core.atlas.find(platingRegionName);

        autotileRegions = TileBitmask.load(regionName);
        region = autotileRegions[39];
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.itemsMoved, 60f / speed, StatUnit.itemsSecond);
        stats.add(Stat.liquidCapacity, liquidCapacity, StatUnit.liquidUnits);
    }

    public class MycelisCordBuild extends MycelisBuild 
    {
        public float progress;

        public Vec2 dir = new Vec2();
        public Point2 source;

        public int depth = 0;

        public State state = State.growing;

        int computeAutotileBit()
        {
            int bits = 0;

            for(int i = 0; i < 8; i++){
                Tile other = tile.nearby(Geometry.d8[i]);
                if(other != null && other.block() instanceof MycelisBlock){
                    bits |= 1 << i;
                }
            }

            return TileBitmask.values[bits];

        }

        boolean blendsDirection(int bit, int direction){
            Tile other = tile.nearby(Geometry.d4[direction]);
            return other != null && other.block() instanceof MycelisBlock;
        }

        @Override
        public void drawBase()
        {
            int bit = computeAutotileBit();
            TextureRegion reg = autotileRegions[bit];

            Draw.rect(reg, x, y);

            if (platingRegion != null)
            {
                for(int i = 0; i < 4; i++){
                    if(!blendsDirection(bit, i)){
                        Draw.rect(platingRegion, x, y, i * 90);
                    }
                }
            }
        }

        // @Override
        // public boolean acceptItem(Building source, Item item){
        //     return current == null && items.total() == 0;
        // }

        // @Override
        // public void handleItem(Building source, Item item){
        //     current = item;
        //     progress = -1f;
        //     items.add(item, 1);
        // }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return liquid == bloodLiquid && liquids.get(liquid) < liquidCapacity;
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount){
            if(liquid == bloodLiquid){
                super.handleLiquid(source, liquid, amount);
            }
        }

        @Override
        public void onBeat()
        {
            super.onBeat();

            if (beatCount > 1 && beatCount < 20 && state != State.branched)
            {
                if (state == State.growing)
                {
                    // Place cord in the main stream direction
                    Point2 pos = getPosAtDepth(depth + 1, dir.x, dir.y, source.x, source.y);
                    placeCord(pos.x, pos.y, dir, source, depth + 1);

                    state = State.forwarded;
                }

                if (Mathf.chance(0.05) && state == State.forwarded && depth > 10)
                {
                    float rotation = Mathf.randomSign() * 60f;
                    float ndirx = dir.x * Mathf.cosDeg(rotation) - dir.y * Mathf.sinDeg(rotation);
                    float ndiry = dir.x * Mathf.sinDeg(rotation) + dir.y * Mathf.cosDeg(rotation);

                    Point2 pos = getPosAtDepth(1, ndirx, ndiry, tile.x, tile.y);
                    placeCord(pos.x, pos.y, new Vec2(ndirx, ndiry), new Point2(tile.x, tile.y), 0);

                    state = State.branched;
                }
            }
            else if (beatCount >= 60 && evolution != null)
            {
                T.placeBlock(tile, evolution, rotation, team);
                if (tile.build instanceof MycelisCordBuild cb)
                {
                    cb.dir = dir;
                    cb.source = source;

                    cb.depth = depth;
                    cb.beatCount = beatCount;
                    cb.state = state;

                    cb.liquids = liquids;
                    cb.items = items;
                }
            }
        }


        @Override
        public void display(Table table) 
        {
            super.display(table);

            if (this.team != Vars.player.team()) return;

            table.row();
            table.table(t -> {
                t.left();
                t.label(() -> "[DEBUG] Beat Count: " + beatCount).wrap();;
                t.row();
                t.label(() -> "[DEBUG] State: " + state).wrap();;
                t.row();
                t.label(() -> "[DEBUG] ShortageTmr: " + beatShortageTimer).wrap();;
            }).growX();
        }

        public Point2 getPosAtDepth(int _depth, float dirX, float dirY, int sourceX, int sourceY) 
        {
            float absDirX = Math.abs(dirX);
            float absDirY = Math.abs(dirY);

            int nx = sourceX;
            int ny = sourceY;

            if (absDirX > absDirY) {
                // X is dominant
                int stepX = _depth * (dirX > 0 ? 1 : -1);
                nx += stepX;

                // Compute Y using slope, round carefully
                float exactY = sourceY + stepX * (dirY / dirX);
                ny = Math.round(exactY);
            } else {
                // Y is dominant
                int stepY = _depth * (dirY > 0 ? 1 : -1);
                ny += stepY;

                // Compute X using slope, round carefully
                float exactX = sourceX + stepY * (dirX / dirY);
                nx = Math.round(exactX);
            }

            int dx = nx - tile.x;
            int dy = ny - tile.y;

            // Determine which axis has the bigger absolute difference
            if (Math.abs(dx) > Math.abs(dy)) {
                // Move 1 step along X toward nx
                int stepX = dx > 0 ? 1 : -1;
                return new Point2(tile.x + stepX, tile.y);
            } else if (Math.abs(dy) > 0) {
                // Move 1 step along Y toward ny
                int stepY = dy > 0 ? 1 : -1;
                return new Point2(tile.x, tile.y + stepY);
            }

            return null;
        }

        public MycelisCordBuild placeCord(int nx, int ny, Vec2 _dir, Point2 _source, int depth)
        {
            Tile neighbor = Vars.world.tile(nx, ny);
            if (neighbor != null && neighbor.build == null && neighbor.block() == Blocks.air) 
            {
                T.placeBlock(neighbor, TBlocks.mycelis_cord, 0, team);

                if (neighbor.build instanceof MycelisCordBuild cb) {
                    cb.dir = _dir;
                    cb.source = _source;

                    cb.depth = depth + 1;

                    return cb;
                }
            }

            return null;
        }

        @Override
        public void updateTile()
        {
            super.updateTile();
            DebugLine.from(new Vec2(x, y), new Vec2(source.x * tilesize, source.y * tilesize)).color(Color.white).draw();
            DebugLine.dir(new Vec2(x, y), dir, 3f * tilesize).color(Color.black).draw();
        }

        @Override
        public void write(Writes write)
        {
            super.write(write);
            
            write.i(depth);

            write.f(dir.x);
            write.f(dir.y);

            write.i(source.x);
            write.i(source.y);

            write.i(state.ordinal());
        }

        @Override
        public void read(Reads read, byte revision)
        {
            super.read(read, revision);
            
            depth = read.i();

            dir = new Vec2(read.f(), read.f());

            source = new Point2(read.i(), read.i());

            state = State.values()[read.i()];
        }
    }
}
