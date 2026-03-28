package technical.expansion.mycelis;

import java.util.ArrayList;
import java.util.List;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import technical.T;
import technical.content.TBlocks;
import technical.expansion.mycelis.MycelisCord.MycelisCordBuild;

public class MycelisHeart extends MycelisBlock 
{
    public MycelisHeart(String name) 
    {
        super(name);
        update = true;
        hasLiquids = true;
        liquidCapacity = Float.MAX_VALUE;
    }

    public class MycelisHeartBuild extends MycelisBuild 
    {
        private final float beatInterval = 300f; // time between beats in ticks

        public float heartBeatTimer = 0;

        @Override
        public void created() {
            super.created();
            liquids.set(bloodLiquid, liquidCapacity); // infinite blood
        }

        @Override
        public void updateTile() {
            super.updateTile();

            liquids.set(bloodLiquid, liquidCapacity);

            heartBeatTimer += Time.delta;

            if (heartBeatTimer >= beatInterval) 
            {
                onBeat();
                heartBeatTimer = 0;
            }
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount) {
            // Heart never accepts blood
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return false;// Heart never accepts blood
        }

        @Override
        public void onBeat() {
            super.onBeat();

            int half = size / 2;
            List<Point2> options = new ArrayList<>();

            for (Point2 d : Geometry.d4) {
                for (int h = -half; h <= half; h++) 
                {
                    int nx = tile.x + d.x * (half + 1) + d.y * h;
                    int ny = tile.y + d.y * (half + 1) + d.x * h;

                    Tile neighbor = Vars.world.tile(nx, ny);

                    if (neighbor != null && neighbor.build == null) {
                        options.add(new Point2(nx, ny));
                    }
                }
            }

            if (options.isEmpty()) return;

            Point2 chosen = options.get(Mathf.random(options.size() - 1));
            Tile neighbor = Vars.world.tile(chosen.x, chosen.y);

            if (neighbor != null) 
            {
                T.placeBlock(neighbor, TBlocks.mycelis_cord, half, team);

                if (neighbor.build instanceof MycelisCordBuild cb) {

                    float dx = cb.tile.x - tile.x;
                    float dy = cb.tile.y - tile.y;

                    cb.dir.set(dx, dy).nor();

                    float angleOffset = Mathf.range(45f);
                    cb.dir.rotate(angleOffset);

                    cb.source = new Point2(tile.x, tile.y);
                    cb.depth = half + 1;
                }
            }
        }

        @Override
        public void write(Writes write)
        {
            super.write(write);
            
            write.f(heartBeatTimer);
        }

        @Override
        public void read(Reads read, byte revision)
        {
            super.read(read, revision);
            
            heartBeatTimer = read.f();
        }
    }
}
