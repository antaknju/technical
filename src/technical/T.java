package technical;

import static mindustry.Vars.itemSize;
import static mindustry.Vars.renderer;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

import java.util.ArrayList;
import java.util.Locale;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Scl;
import arc.struct.ObjectIntMap;
import arc.struct.ObjectMap;
import arc.util.Align;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Liquids;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.input.Binding;
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

    public static Color c(String name) {
        return Color.valueOf(name);
    }

    public static String stat(String bundle)
    {
        return Core.bundle.get("stat." + bundle);
    }

    public static String bundle(String bundle)
    {
        return Core.bundle.get(Technical.name + "." + bundle.toLowerCase(Locale.ROOT));
    }

    public static String bundle(String bundle, String color)
    {
        return "[" + color + "]" + bundle(bundle) + "[]";
    }

    public static String bundle(Enum<?> en)
    {
        return T.bundle("enum." + kebab(en.getClass().getSimpleName()) + "." + kebab(en.name()));
    }

    public static String kebab(String s)
    {
        StringBuilder bun = new StringBuilder();

        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);

            if (i > 0 && Character.isUpperCase(c))
                bun.append('-');

            bun.append(Character.toLowerCase(c));
        }

        return bun.toString();
    }

    public static void outline(Building b) {outline(b, Pal.accent);}
    public static void outline(Building b, Color col)
    {
        Draw.color(col);

        for(int i = 0; i < 4; i++){
            Point2 p = Geometry.d8edge[i];
            float offset = -Math.max(b.block.size - 1, 0) / 2f * Vars.tilesize;
            Draw.rect("block-select", b.x + offset * p.x, b.y + offset * p.y, i * 90);
        }

        Draw.color();
    }

    public static void drawItemStack(ItemStack stack, float x, float y, float itemTime)
    {
        if(stack != null && stack.amount > 0)
        {
            float sin = Mathf.absin(Time.time, 5f, 1f);
            float size = (itemSize + sin) * itemTime;

            Draw.mixcol(Pal.accent, sin * 0.1f);
            Draw.rect(stack.item.fullIcon, x, y, size, size);
            Draw.mixcol();

            float ringSize = ((3f + sin) * itemTime + 0.5f) * 2;
            Draw.color(Pal.accent);
            Draw.rect(TIcons.itemRing, x, y, ringSize, ringSize);

            if(!renderer.pixelate && itemTime > 0){
                Fonts.outline.draw(stack.amount + "",
                    x, y - 3,
                    Pal.accent, 0.25f * itemTime / Scl.scl(1f), false, Align.center
                );
            }
        }
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

    public static Point2 Rot2Pos(int rotation)
    {
        switch (rotation) {
            case 0:
                return new Point2(1, 0);
            case 1:
                return new Point2(0, 1);
            case 2:
                return new Point2(-1, 0);
            case 3:
                return new Point2(0, -1);
        }

        return new Point2(0, 0);
    }

    public static int Pos2Rot(int dx, int dy)
    {
        return Pos2Rot(new Point2(dx, dy));
    }

    public static Tile Rot2Tile(Building myBuilding, int rotation)
    {
        return world.tile(T.Rot2Pos(rotation).x + myBuilding.tile.x, T.Rot2Pos(rotation).y + myBuilding.tile.y);
    }

    public static int Pos2Rot(Point2 dp)
    {
        return Math.abs(dp.y - 1) + Math.abs(dp.x - 1) + 1;
    }

    public static int[] arr(int... x)
    {
        return x;
    }

    public static int[] range(int b) {return range(0, b);}
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
     * Loads multiple regions from the atlas with names based on the baseName, automaticly counting sprites.
     * ❗Should not contain mod name prefix.❗
     * @param baseName
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

        if(found.size() == 0){
            found.add(Core.atlas.find(baseName));
        }
        
        return found.toArray(new TextureRegion[0]);
    }

    public static void addRecipeStat(Stats stats, Item startingItem, ConveyorRecipe recipe) 
    {
        Stat recipeStat = new Stat("recipe", StatCat.crafting);

        stats.add(recipeStat, table -> {
            table.row();

            table.table(Styles.grayPanel, t -> {

                t.image(startingItem.unlockedNow() ? startingItem.uiIcon : TIcons.question)
                    .size(40).padLeft(10);

                for (int i = 0; i < recipe.actions.length; i++) {
                    ConveyorRecipe.Action act = recipe.actions[i];

                    t.table(arrow -> {
                        arrow.add(bundle(act.type)).padBottom(4f).row();
                        arrow.image(Icon.right).size(40);
                        arrow.row();

                        if (act.item != null) {
                            TextureRegion icon = act.item.unlockedNow() ? act.item.uiIcon : TIcons.question;
                            arrow.image(icon).size(30).padTop(4f);
                        } else {
                            arrow.table(b -> {}).size(30).padTop(4f);
                        }
                    }).padTop(10).padBottom(10);

                    Item after = (i == recipe.actions.length - 1 ? recipe.result : startingItem);
                    TextureRegion afterIcon = after.unlockedNow() ? after.uiIcon : TIcons.question;

                    t.image(afterIcon).size(40).padRight(10);
                }

            }).growX().pad(6);

            table.add("[accent]" + recipe.times + "x").bottom().left().padLeft(-40).padBottom(12);
        });
    }
}