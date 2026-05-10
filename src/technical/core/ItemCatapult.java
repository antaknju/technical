package technical.core;

import static mindustry.Vars.renderer;
import static mindustry.Vars.world;

import java.util.ArrayList;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.scene.ui.layout.Scl;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Fonts;
import mindustry.world.Tile;

public class ItemCatapult extends TBlock 
{
    public TextureRegion itemCircleRegion;
    public TextureRegion arrowRegion;
    public TextureRegion[] topRegions;

    public float topAnimSpeed = 10f;

    public float throwInterval = 180f;
    public float flyingItemSpeed = 0.5f;
    public int maxStackSize = 10; 
    public int Range = 5;

    public ItemCatapult(String name){
        super(name);
        solid = true;
        hasItems = true;
        rotate = true;
        drawArrow = true;
        update = true;
        solid = true;

        itemCapacity = 30;

        // configurable = true;
    }

    @Override
    public void setStats(){
        super.setStats();
        // stats.add(Stat.output, 1, StatUnit.items);
    }

    @Override
    public void load()
    {
        super.load();
        itemCircleRegion = Core.atlas.find("ring-item");
        arrowRegion = Core.atlas.find("bridge-conveyor-arrow");

        ArrayList<TextureRegion> found = new ArrayList<>();
        for(int i = 0; ; i++){
            TextureRegion region = Core.atlas.find(name + "-top" + i);
            if(region == null || region.found() == false) break;
            found.add(region);
        }

        topRegions = found.toArray(new TextureRegion[0]);

        if(topRegions.length == 0){
            topRegions = new TextureRegion[]{Core.atlas.find(name + "-top")};
        }
    }

    public class ItemCatapultBuild extends TBuild 
    {
        public Seq<FlyingItem> flying = new Seq<>();
        public float throwCooldown = 0;

        public ItemCatapultBuild targetDepot;
        public ItemCatapultBuild inputDepot;

        public float topAnim = 0f;
        public boolean animating = false;

        @Override
        public void updateTile() {
            super.updateTile();

            findTargetDepot();

            if(animating) {
                topAnim -= topAnimSpeed * delta();
                if(topAnim <= 0) {
                    topAnim = 0f;
                    animating = false;
                }
            }

            if (targetDepot == null)
            {
                dump();
                return;
            }

            throwCooldown = Math.max(0, throwCooldown - delta());

            if(throwCooldown <= 0f)
            {
                if(items.total() > 0 && targetDepot != null)
                {
                    throwItem();
                    throwCooldown = throwInterval;
                }
            }

            for(int i = flying.size - 1; i >= 0; i--)
            {
                FlyingItem fi = flying.get(i);
                fi.update(delta());

                if(fi.arrived())
                {
                    for (int j = 0; j < fi.stack.amount; j++) 
                    {
                        if(!targetDepot.acceptItem(this, fi.stack.item)) break;

                        targetDepot.handleItem(this, fi.stack.item);
                    }

                    flying.remove(i);
                }
            }
        }

        public void findTargetDepot()
        {
            int dx = Geometry.d4x(rotation);
            int dy = Geometry.d4y(rotation);

            for (int i = 1; i <= Range; i++) {
                int checkX = tile.x + dx * i;
                int checkY = tile.y + dy * i;
                Building b = world.build(checkX, checkY);
                if (b instanceof ItemCatapultBuild icb) {
                    targetDepot = icb;
                    icb.inputDepot = this;
                    return;
                }
                if (b != null && b.block.isStatic() && b.isValid()) break;
            }

            if (targetDepot != null)
            {
                targetDepot.inputDepot = null;
                targetDepot = null;
            }
        }

        public void throwItem()
        {
            // take one item from inventory
            Item itm = items.first();
            if(itm == null) return;

            int am = Math.min(maxStackSize, items.get(itm));
            if (am <= 0) return;

            items.remove(itm, am);

            // spawn flying item
            flying.add(new FlyingItem(
                new ItemStack(itm, am),
                x, y,
                targetDepot.x, targetDepot.y,
                flyingItemSpeed
            ));

            animating = true;
            topAnim = topRegions.length - 1;
        }

        @Override
        public void drawSelect() 
        {
            if (inputDepot != null) {
                drawLink(inputDepot, false);
            }

            if (targetDepot != null) {
                drawLink(targetDepot, true);
            }
        }

        private void drawLink(ItemCatapultBuild other, boolean linked) 
        {
            if (other == null || tile == null) return;

            Tmp.v2.trns(tile.angleTo(other.tile), 2f);
            float tx = tile.drawx(), ty = tile.drawy();
            float ox = other.tile.drawx(), oy = other.tile.drawy();

            float alpha = Math.abs((linked ? 100 : 0) - (Time.time * 2f) % 100f) / 100f;
            float x = Mathf.lerp(ox, tx, alpha);
            float y = Mathf.lerp(oy, ty, alpha);

            Tile otherLink = linked ? other.tile : tile;
            int rel = (linked ? tile : other.tile).absoluteRelativeTo(otherLink.x, otherLink.y);

            // draw background
            Draw.color(Pal.gray);
            Lines.stroke(2.5f);
            Lines.square(ox, oy, 2f, 45f);
            Lines.stroke(2.5f);
            Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y);

            // draw foreground
            float color = (linked ? Pal.place : Pal.accent).toFloatBits();
            Draw.color(color);
            Lines.stroke(1f);
            Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y);

            Lines.square(ox, oy, 2f, 45f);

            // optional arrow
            Draw.mixcol(color);
            Draw.color();
            Draw.rect(arrowRegion, x, y, rel * 90);
            Draw.mixcol();
        }


        @Override
        public boolean acceptItem(Building source, Item item)
        {
            return items.get(item) < getMaximumAccepted(item);
        }

        @Override
        public void draw() {
            super.draw();

            if(topRegions != null && topRegions.length > 0) {
                int frame = (int)topAnim;
                Log.info(frame);
                Draw.rect(topRegions[frame], x, y);
            }

            // draw flying items
            Draw.z(Layer.block + 1);
            for(FlyingItem fi : flying){
                float sin = Mathf.absin(Time.time, 4f, 1f);
                float itemX = fi.x;
                float itemY = fi.y;
                float size = 8f; // scale as needed

                Draw.mixcol(Pal.accent, sin * 0.1f);
                Draw.rect(fi.stack.item.fullIcon, itemX, itemY, size, size);
                Draw.mixcol();

                float ringSize = ((3f + sin) * fi.time + 0.5f) * 2;
                Draw.color(Pal.accent);
                Draw.rect(itemCircleRegion, itemX, itemY, ringSize, ringSize);

                if(!renderer.pixelate && fi.time > 0){
                    Fonts.outline.draw(
                        fi.stack.amount + "",
                        itemX, itemY - 3,
                        Pal.accent, 0.25f * fi.time / Scl.scl(1f), false, Align.center
                    );
                }
            }
        }
    }
}