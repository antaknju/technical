package technical.core.ai;

import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.world.Tile;
import technical.core.ai.ResourceTypes.WaterRes;

/*
 * cost = 1 is one block move cost
 * value < 0 means that target is not worth it (too dangerous) or is not possible to go
 * the higher value the better target tile is
 */

public class TPathfinder implements Runnable 
{
    Thread thread;

    public static final int impassable = -100;

    Seq<ResourceType> ResourceTypes = new Seq<>();

    public void start()
    {
        stop();

        Log.debug("Started TPathfinder");

        if (!Vars.net.client()) {
            thread = new Thread(this, "Pathfinder");
            thread.setPriority(1);
            thread.setDaemon(true);
            thread.start();

            setup();
        }
    }

    public void setup()
    {
        ResourceTypes = Seq.with(
            new WaterRes()
        );

        calculateValues();
    }

    public void stop()
    {
        if (thread != null) 
        {
            Log.debug("Stopped TPathfinder");

            thread.interrupt();
            thread = null;
        }
    }

    public void run()
    {
        while(!Vars.net.client()) {
            try {
                if (Vars.state.isPlaying()) 
                {
                    calculateValues();
                }

                try {
                    Thread.sleep(4000L);
                } catch (InterruptedException var3) {
                    return;
                }
            } catch (Throwable var4) {
                var4.printStackTrace();
            }
        }
    }

    public void calculateValues()
    {
        for (var res : ResourceTypes)
        {
            res.CalcValues();
        }
    }

    public int GetResourceValue(int x, int y, int res)
    {
        return ResourceTypes.get(res).GetValue(x, y);
    }

    public static boolean isPassable(Tile tile)
    {
        return tile != null && (tile.block() == null || !tile.block().solid) && tile.build == null;// && canPlaceOn(tile, team, 0);
    }
}
