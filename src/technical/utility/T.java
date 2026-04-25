package technical.utility;

import static mindustry.Vars.itemSize;
import static mindustry.Vars.renderer;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

import java.util.ArrayList;
import java.util.Objects;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.scene.ui.layout.Scl;
import arc.struct.ObjectIntMap;
import arc.struct.ObjectMap;
import arc.util.Align;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Liquids;
import mindustry.ctype.Content;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.UnitType;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
// import mindustry.game.EventType.ResetEvent;
// import mindustry.game.EventType.Trigger;
// import mindustry.game.EventType.UnitControlEvent;
// import mindustry.game.EventType.WorldLoadEvent;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.Stats;
import technical.Technical;
import technical.content.TIcons;
import technical.content.TLiquids;
import technical.expansion.ConveyorRecipe;
import technical.expansion.TLiquid;

public class T
{
    public static TLiquid getTLiquidDrop(Tile tile)
    {
        if (tile == null || tile.floor() == null || tile.floor().liquidDrop == null) return null;

        Liquid liq = tile.floor().liquidDrop;

        if (liq instanceof TLiquid tliq)
        {
            return tliq;
        }
        else
        {
            if (liq == Liquids.water)
            {
                return TLiquids.water;
            }
            else
            {
                return null;
            }
        }
    }

    public static boolean isTechnical(Content content)
    {
        return Objects.equals(content.minfo.mod.name, Technical.name);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> ObjectMap<K, V> mapOf(Object... keysAndValues) {
        if (keysAndValues.length % 2 != 0)
            throw new IllegalArgumentException("Must have even number of arguments (key, value pairs)");

        ObjectMap<K, V> map = new ObjectMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            map.put((K) keysAndValues[i], (V) keysAndValues[i + 1]);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public static <K> ObjectIntMap<K> mapIntOf(Object... keysAndValues) {
        if (keysAndValues.length % 2 != 0)
            throw new IllegalArgumentException("Must have even number of arguments (key, value pairs)");

        ObjectIntMap<K> map = new ObjectIntMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            map.put((K) keysAndValues[i], (int)keysAndValues[i + 1]);
        }
        return map;
    }

    public static float getContentWorldSize(UnlockableContent content)
    {
        if (content instanceof Block b)
            return b.size * tilesize;
        if (content instanceof UnitType b)
            return b.hitSize;

        return 0;
    }

    public static boolean placeBlock(Tile tile, Block block, int rotation, Team team)
    {
        if (!block.canPlaceOn(tile, team, rotation)) return false;
       
        tile.setBlock(block, team);
        tile.build.rotation = rotation;
        
        for (var b : tile.build.proximity)
            b.onProximityAdded();

        tile.build.onProximityUpdate();

        return true;
    }

    public static int Pos2Rot(int dx, int dy)
    {
        return Math.abs(dy - 1) + Math.abs(dx - 1) + 1;
    }
    public static int Pos2Rot(Point2 dp)
    {
        return Pos2Rot(dp.x, dp.y);
    }


    public static Point2 Rot2Pos(int rotation)
    {
        return switch (rotation) {
            case 0 -> new Point2(1, 0);
            case 1 -> new Point2(0, 1);
            case 2 -> new Point2(-1, 0);
            case 3 -> new Point2(0, -1);
            default -> new Point2(0, 0);
        };

    }
    public static Tile Rot2Tile(Building myBuilding, int rotation)
    {
        return world.tile(T.Rot2Pos(rotation).x + myBuilding.tile.x, T.Rot2Pos(rotation).y + myBuilding.tile.y);
    }

    public static int[] range(int b)
    {
        return range(0, b);
    }

    public static int[] range(int a, int b) 
    {
        if (b < a)
        {
            Log.err("T.range() not used properly", new Throwable());
            return new int[0];
        }

        int[] arr = new int[b - a + 1];
        for (int i = a; i <= b; i++) {
            arr[i - a] = i;
        }
        return arr;
    }

    public static boolean isSandbox()
    {
        return state.isGame() && state.rules.infiniteResources;
    }


    
    /**
     * Loads multiple regions from the atlas with names based on the baseName, automatically counting sprites.
     * ❗Should not contain mod name prefix.❗
     * @param baseName name
     * @return Array of found TextureRegions
     */
    public static TextureRegion[] loadMultipleRegions(String baseName)
    {
        // String baseName = Technical.name + "-" + def.name; // atlas base name
        ArrayList<TextureRegion> found = new ArrayList<>();

        for(int i = 0;; i++)
        {
            TextureRegion region = Core.atlas.find(baseName + "-" + i);
            if(region == null || !region.found()) break;
            found.add(region);
        }

        if(found.isEmpty()){
            found.add(Core.atlas.find(baseName));
        }
        
        return found.toArray(new TextureRegion[0]);
    }
}