// package technical.expansion.mycelis;

// import static mindustry.Vars.world;

// import arc.*;
// import arc.graphics.*;
// import arc.graphics.g2d.*;
// import arc.math.*;
// import arc.math.geom.Point2;
// import arc.util.*;
// import mindustry.Vars;
// import mindustry.ai.Pathfinder.Flowfield;
// import mindustry.gen.*;
// import mindustry.graphics.Layer;
// import mindustry.type.*;
// import mindustry.world.Block;
// import mindustry.world.Tile;
// import mindustry.world.blocks.Autotiler;
// import mindustry.world.meta.*;
// import technical.utility.T;
// import technical.utility.TCol;
// import technical.expansion.ai.TPathfinder;

// public class MycelisCordOld extends MycelisBlock implements Autotiler {

//     public float speed = 5f;

//     public TextureRegion[] topRegions = new TextureRegion[5];
//     public TextureRegion[] botRegions = new TextureRegion[5];
//     public TextureRegion[] liqRegions = new TextureRegion[5];

//     public MycelisCordOld(String name) {
//         super(name);

//         group = BlockGroup.transportation;
//         solid = false;
//         update = true;

//         hasLiquids = true;
//         hasItems = true;
        
//         rotate = true;
//         conveyorPlacement = true;

//         itemCapacity = 1;
//         liquidCapacity = 10 / 60f;

//         underBullets = true;

//         noSideBlend = true;

//         health = 250;
//     }

//     @Override
//     public void load() {
//         super.load();
        
//         for (int i = 0; i < 5; i++) {
//             topRegions[i] = Core.atlas.find(name + "-top-" + i);
//             botRegions[i] = Core.atlas.find(name + "-bottom-" + i, "duct-bottom-" + i);
//             liqRegions[i] = Core.atlas.find(name + "-liquid-" + i);
//         }
//     }

//     @Override
//     public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock){
//         return (otherblock.outputsItems() || (lookingAt(tile, rotation, otherx, othery, otherblock) && otherblock.hasItems))
//             && lookingAtEither(tile, rotation, otherx, othery, otherrot, otherblock);
//     }

//     @Override
//     public void setStats() {
//         super.setStats();

//         stats.add(Stat.itemsMoved, 60f / speed, StatUnit.itemsSecond);
//         stats.add(Stat.liquidCapacity, liquidCapacity, StatUnit.liquidUnits);
//     }

//     public class MycelisCordOldBuild extends MycelisBuild 
//     {
//         public float progress;
//         public Item current;
//         public Building next;
//         public int blendbits, xscl, yscl, blending;

//         @Override
//         public void updateTile() {
//             super.updateTile();

//             if (current != null && next != null) 
//             {
//                 if (progress >= (1f - 1f / speed) && moveForward(current)) {
//                     items.remove(current, 1);
//                     current = null;
//                     progress %= (1f - 1f / speed);
//                 }
//             }

//             if (current == null && items.total() > 0) {
//                 current = items.first();
//             }
//         }

//         @Override
//         public void onBeat()
//         {
//             super.onBeat();

//             var nTile = T.Rot2Tile(this, rotation);
//             Log.debug(nTile);

//             if (next == null && TPathfinder.isPassable(nTile))
//             {
//                 Log.debug("trying to grow");
//                 growCord();
//                 return;
//             }
            
//             if (next instanceof MycelisBuild mb && !(next instanceof MycelisCordBuild))
//                 mb.onBeat();
//             else if (next instanceof MycelisCordBuild mc)
//             {
//                 if (mc.nearby(mc.rotation) != this)
//                     mc.onBeat();
//             }

//             Liquid cur = liquids.current();
//             float curAmount = liquids.currentAmount();

//             if (cur != null && curAmount > 0.001f && next != null && next.block.hasLiquids && next.nearby(next.rotation) != this) {
//                 float flow = Math.min(curAmount, 0.1f * edelta());
//                 if (flow > 0 && next.acceptLiquid(this, cur)) {
//                     next.handleLiquid(this, cur, flow);
//                     liquids.remove(cur, flow);
//                 }
//             }
//         }


//         public void growCord()
//         {
//             Tile current = this.tile;
//             Tile frontTile = T.Rot2Tile(this, rotation);

//             if (frontTile == null || frontTile.solid()) return;

//             int bestRot = -1, bestRes = TPathfinder.impassable;
//             for (int r = 0; r <= 3; r++) 
//             {
//                 int nx = current.x + T.Rot2Pos(r).x;
//                 int ny = current.y + T.Rot2Pos(r).y;

//                 Tile n = Vars.world.tile(nx, ny);
//                 if (n == null) continue;

//                 int res = T.pathfinder.GetResourceValue(nx, ny, 0);

//                 Log.debug(res);
//                 if (res > bestRes) 
//                 {
//                     bestRes = res;
//                     bestRot = r;
//                 }
//             }
//             if (bestRot == -1) return;
            
//             T.placeBlock(frontTile, block, bestRot, team);
//         }


//         public int getCostAt(Flowfield field, int x, int y){
//             if(x < 0 || y < 0 || x >= field.width || y >= field.height) return -1;
//             return field.weights[x + y * field.width];
//         }

//         @Override
//         public void draw() 
//         {
//             float rotdeg = rotdeg();
//             Draw.z(Layer.blockUnder);

//             Draw.scl(xscl * (1f + beatScale * 0.3f), yscl * (1f + beatScale * 0.3f));

//             Draw.rect(botRegions[blendbits], x, y, rotdeg);

//             float fill = liquids.get(bloodLiquid) / liquidCapacity;
//             Color bloodCol = Tmp.c1.set(TCol.bioOrange).a(Mathf.clamp(fill * 0.3f));

//             Draw.color(bloodCol);
//             Draw.rect(liqRegions[blendbits], x, y, rotdeg);
//             Draw.color();
            
//             Draw.rect(topRegions[blendbits], x, y, rotdeg);

//             if (current != null) {
//                 Draw.z(Layer.blockUnder + 0.1f);
//                 Tmp.v1.trns(rotdeg, progress * 4f - 4f);
//                 Draw.rect(current.fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, 4f, 4f);
//             }

//             Draw.scl();
//         }

//         public boolean moveForward(Item item) {
//             if (next != null && next.acceptItem(this, item)) {
//                 next.handleItem(this, item);
//                 return true;
//             }
//             return false;
//         }

//         @Override
//         public void onProximityUpdate() {
//             super.onProximityUpdate();

//             next = front();

//             int[] bits = buildBlending(tile, rotation, null, true);
//             blendbits = bits[0];
//             xscl = bits[1];
//             yscl = bits[2];
//             blending = bits[4];
//         }

//         public boolean canBlend(Building other) {
//             if (other == null) return false;
//             // only blend with other MycelisCords or any MycelisBlock type
//             return other.block instanceof MycelisBlock;
//         }


//         @Override
//         public boolean acceptItem(Building source, Item item) {
//             return current == null && items.total() == 0;
//         }

//         @Override
//         public void handleItem(Building source, Item item) {
//             current = item;
//             progress = -1f;
//             items.add(item, 1);
//         }

//         @Override
//         public boolean acceptLiquid(Building source, Liquid liquid) {
//             return liquid == bloodLiquid && liquids.get(liquid) < liquidCapacity;
//         }

//         @Override
//         public void handleLiquid(Building source, Liquid liquid, float amount) {
//             if (liquid == bloodLiquid) {
//                 super.handleLiquid(source, liquid, amount);
//             }
//         }
//     }
// }