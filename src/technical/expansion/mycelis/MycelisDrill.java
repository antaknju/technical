package technical.expansion.mycelis;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.type.Item;
import technical.utility.T;

public class MycelisDrill extends MycelisBlock {

    public TextureRegion[] topRegions;
    public int actionFrame = 5; // frame at which ore is generated
    public float shake = 0.5f;

    public MycelisDrill(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        topRegions = T.loadMultipleRegions(name + "-top");
    }

    public class MycelisDrillBuild extends MycelisBuild 
    {
        public float progress = 0f;
        public float drillTime = 60f; // time to complete one drill action
        public int lastFrame = -1;

        public Item dominantItem = null;
        public int dominantCount = 0;

        @Override
        public void placed(){
            super.placed();
            recalculateDominantItem();
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            recalculateDominantItem();
        }

        private void recalculateDominantItem()
        {
            ObjectMap<Item, Integer> counts = new ObjectMap<>();

            for(int dx = 0; dx < size; dx++)
            {
                for(int dy = 0; dy < size; dy++)
                {
                    int wx = tile.x + dx - size / 2;
                    int wy = tile.y + dy - size / 2;

                    var t = Vars.world.tile(wx, wy);
                    if(t == null) continue;

                    Item drop = t.drop();
                    if(drop == null) continue;

                    counts.put(drop, counts.get(drop, 0) + 1);
                }
            }

            dominantItem = null;
            dominantCount = 0;

            for(var e : counts)
            {
                if(e.value > dominantCount){
                    dominantItem = e.key;
                    dominantCount = e.value;
                }
            }
        }


        @Override
        public void updateTile() {
            super.updateTile();

            if(!enabled) return;

            progress += delta();
            int frame = (int)(progress / (drillTime / topRegions.length));

            // Generate ore only once per actionFrame
            if(frame >= actionFrame && lastFrame < actionFrame) {
                mineOreBurst();
            }

            lastFrame = frame;

            if(progress >= drillTime) progress %= drillTime;
        }

        private void mineOreBurst()
        {
            if(dominantItem == null || dominantCount <= 0) return;

            int amount = Mathf.clamp(
                dominantCount,
                1,
                itemCapacity - items.total()
            );

            for(int i = 0; i < amount; i++){
                offload(dominantItem);
            }

            Effect.shake(shake, shake, this);
        }

        @Override
        public void drawTop()
        {            
            int frame = (int)((progress / drillTime) * topRegions.length) % topRegions.length;
            Draw.rect(topRegions[frame], x, y);
        }
    }
}