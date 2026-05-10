package technical.util;

import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.PixmapRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.scene.ui.layout.Scl;
import arc.util.Align;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.ui.Fonts;
import technical.content.TIcons;

import static mindustry.Vars.itemSize;
import static mindustry.Vars.renderer;

public class TDraw
{
    public static Pixmap tint(PixmapRegion region, Color color)
    {
        Pixmap out = region.crop();

        for(int y = 0; y < out.getHeight(); y++)
        {
            for(int x = 0; x < out.getWidth(); x++)
            {
                int pixel = out.get(x, y);

                int a = pixel & 0xFF;
                if(a == 0) continue;

                out.set(x, y, color);
            }
        }

        return out;
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

    public static void highlight(Building b)
    {
        highlight(b, Pal.accent);
    }

    public static void highlight(Building b, Color col)
    {
        Draw.color(col);

        for(int i = 0; i < 4; i++){
            Point2 p = Geometry.d8edge[i];
            float offset = -Math.max(b.block.size - 1, 0) / 2f * Vars.tilesize;
            Draw.rect("block-select", b.x + offset * p.x, b.y + offset * p.y, i * 90);
        }

        Draw.color();
    }
}
