package technical.expansion;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.Texture;
import arc.graphics.g2d.PixmapRegion;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.util.Tmp;
import mindustry.graphics.Drawf;
import mindustry.graphics.MultiPacker;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import technical.utility.TDraw;

import javax.swing.plaf.synth.Region;

public class TStatus extends StatusEffect
{
    public TStatus(String name)
    {
        super(name);

        outline = true;
    }

    @Override
    public void createIcons(MultiPacker packer)
    {
        if (!(uiIcon instanceof TextureAtlas.AtlasRegion at)) return;

        PixmapRegion base = Core.atlas.getPixmap(uiIcon);

        Pixmap tinted = TDraw.tint(base, color);

        PixmapRegion tintedRegion = new PixmapRegion(tinted);

        Pixmap result = Pixmaps.outline(tintedRegion, Pal.gray, 3);

        Drawf.checkBleed(result);

        packer.add(MultiPacker.PageType.ui, at.name, result);

        tinted.dispose();
        result.dispose();
    }
}
