package technical.expansion.ai;

import static mindustry.Vars.world;

import java.util.PriorityQueue;

import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Point3;
import arc.util.Log;
import mindustry.content.Liquids;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

abstract class ResourceType 
{
    int[][] value;
    boolean setuped;

    public ResourceType(){}

    public int GetValue(int x, int y)
    {
        if (setuped)
        {
            if (inBounds(x, y))
                return value[x][y];
        }
        else
        {
            Log.debug("No Setup!");
        }

        return TPathfinder.impassable;
    }

    abstract int CalcResource(int x, int y);

    boolean inBounds(int x, int y) {
        return x >= 0 && x < world.width() && y >= 0 && y < world.height();
    }

    // Point3 (x, y, resource)
    public void CalcValues()
    {
        setuped = false;

        value = new int[world.width()][world.height()];
        for (int x = 0; x < world.width(); x++)
            for (int y = 0; y < world.height(); y++)
                value[x][y] = -999999; // critical fix

        PriorityQueue<Point3> queue = new PriorityQueue<>((a, b) -> Integer.compare(b.z, a.z));

        for (Tile tile : world.tiles) 
        {
            int res = CalcResource(tile.x, tile.y);
            if (TPathfinder.isPassable(tile)) 
            {
                if (res > 0) 
                {
                    queue.add(new Point3(tile.x, tile.y, res));
                    value[tile.x][tile.y] = res;
                } 
            } 
            else 
            {
                value[tile.x][tile.y] = TPathfinder.impassable;
            }
        }

        while (!queue.isEmpty())
        {
            Point3 top = queue.remove();
            if (top.z < value[top.x][top.y]) continue;

            for (Point2 p : Geometry.d4) 
            {
                int nx = top.x + p.x;
                int ny = top.y + p.y;

                if (!inBounds(nx, ny)) continue;
                if (value[nx][ny] == TPathfinder.impassable) continue;

                int newVal = value[top.x][top.y] - 1;
                if (newVal > value[nx][ny]) 
                {
                    value[nx][ny] = newVal;
                    queue.add(new Point3(nx, ny, newVal));
                }
            }
        }

        setuped = true;
    }
}

public class ResourceTypes
{
    static class WaterRes extends ResourceType
    {
        public WaterRes(){}

        @Override
        int CalcResource(int x, int y)
        {
            Floor b = world.tile(x, y).floor();
            if (b.liquidDrop == Liquids.water)
            {
                return (int)(b.liquidMultiplier * 100);
            }

            return 0;
        }
    }
}