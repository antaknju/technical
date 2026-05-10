package technical.core;

import arc.Core;
import arc.graphics.Pixmap;
import arc.graphics.g2d.TextureRegion;
import arc.util.Nullable;
import mindustry.graphics.MultiPacker;
import mindustry.world.Block;
import mindustry.world.blocks.environment.Floor;

public class TShallowLiquid extends Floor {
    public @Nullable Floor liquidBase, floorBase;
    public float liquidOpacity = 0.35f;

    public TShallowLiquid(String name){
        super(name);
    }

    public void set(Block liquid, Block floor){
        this.liquidBase = liquid.asFloor();
        this.floorBase = floor.asFloor();

        isLiquid = true;
        variants = floorBase.variants;
        status = liquidBase.status;
        liquidDrop = liquidBase.liquidDrop;
        cacheLayer = liquidBase.cacheLayer;
        shallow = true;
    }

    @Override
    public void createIcons(MultiPacker packer)
    {
        // Why the heck there is this line commented? Spent 1h debugging this one line of code...
        super.createIcons(packer);

        if(liquidBase != null && floorBase != null){
            var overlay = Core.atlas.getPixmap(liquidBase.region);
            int index = 0;
            for(TextureRegion region : floorBase.variantRegions()){
                var res = Core.atlas.getPixmap(region).crop();
                for(int x = 0; x < res.width; x++){
                    for(int y = 0; y < res.height; y++){
                        res.setRaw(x, y, Pixmap.blend((overlay.getRaw(x, y) & 0xffffff00) | (int)(liquidOpacity * 255), res.getRaw(x, y)));
                    }
                }

                String baseName = this.name + (++index);
                packer.add(MultiPacker.PageType.environment, baseName, res);

                res.dispose();
            }
        }
    }
}
