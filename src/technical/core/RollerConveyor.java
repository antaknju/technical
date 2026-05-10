package technical.core;

import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.Autotiler;
import mindustry.world.blocks.distribution.ChainedBuilding;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.blocks.distribution.StackConveyor;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import technical.content.TBlocks;
import technical.content.TCustom;
import technical.core.tech.TechStat;
import mindustry.type.Item;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.TargetPriority;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.logic.LAccess;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;

import static mindustry.Vars.content;
import static mindustry.Vars.itemSize;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.g2d.Draw;
import arc.struct.Seq;
import technical.util.TUI;

public class RollerConveyor extends TBlock implements Autotiler
{
    protected static final float itemSpace = 0.4f;

    private TextureRegion[] rollerRegions;
    private TextureRegion capRegion;

    public float speed = 0f;
    public float displayedSpeed = 0f;
    public boolean pushUnits = true;

    public boolean canSleep = true;

    public @Nullable Block junctionReplacement, tunnelReplacement;

    public RollerConveyor(String name) {
        super(name);
        rotate = true;
        update = true;
        group = BlockGroup.transportation;
        hasItems = true;
        itemCapacity = 3;
        priority = TargetPriority.transport;
        conveyorPlacement = true;
        underBullets = true;

        solid = false;

        ambientSound = Sounds.loopConveyor;
        ambientSoundVolume = 0.0022f;
        unloadable = false;
        noUpdateDisabled = false;
    }

    @Override
    public void setStats(){
        super.setStats();
        
        stats.add(Stat.itemsMoved, displayedSpeed * getTotalStat(TechStat.speed), StatUnit.itemsSecond);

        for (var vk : TCustom.ConveyorRecipes)
        {
            TUI.addConveyorRecipeStat(stats, vk.key, vk.value);
        }
    }

    @Override
    public void init()
    {
        super.init();

        if(junctionReplacement == null) junctionReplacement = Blocks.junction;
        if(tunnelReplacement == null || !(tunnelReplacement instanceof RollerTunnel)) tunnelReplacement = TBlocks.roller_tunnel;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list)
    {
        TextureRegion region = rollerRegions[0];
        Draw.rect(region, plan.drawx(), plan.drawy(), region.width * region.scl(), region.height * region.scl(), plan.rotation * 90);

        Draw.rect(capRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.rect(capRegion, plan.drawx(), plan.drawy(), plan.rotation * 90 + 180);
    }

    @Override
    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock){
        return (otherblock.outputsItems() || (lookingAt(tile, rotation, otherx, othery, otherblock) && otherblock.hasItems))
            && lookingAtEither(tile, rotation, otherx, othery, otherrot, otherblock);
    }

    @Override
    public boolean canReplace(Block other){
        return super.canReplace(other) && !(other instanceof StackConveyor);
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans)
    {
        calculateTunnels(plans, (RollerTunnel)tunnelReplacement, b -> b instanceof RollerConveyor);
    }

    public void calculateTunnels(Seq<BuildPlan> plans, RollerTunnel tunnel, Boolf<Block> avoid) 
    {
        if (plans == null || plans.size < 2) return;

        BuildPlan first = plans.first();
        BuildPlan last = plans.peek();

        // Only allow orthogonal placement + unlocked tunnel
        if (!(first.x == last.x || first.y == last.y) || !tunnel.unlockedNow()) return;

        Boolf<BuildPlan> placeable = plan ->
            (plan.placeable(Vars.player.team()) || (plan.tile() != null && plan.tile().block() == plan.block)) &&
            !(plan.build() != null && avoid.get(plan.tile().block()));

        Seq<BuildPlan> result = new Seq<>();

        outer:
        for (int i = 0; i < plans.size; i++) {
            BuildPlan cur = plans.get(i);
            result.add(cur);

            // gap detection: look ahead to find the next placeable plan within maxRange
            if (i < plans.size - 1 && placeable.get(cur) && !placeable.get(plans.get(i + 1))) {
                for (int j = i + 1; j < plans.size; j++) {
                    BuildPlan other = plans.get(j);

                    // check distance along straight line (orthogonal only)
                    int dx = Math.abs(other.x - cur.x);
                    int dy = Math.abs(other.y - cur.y);
                    if (dx + dy > tunnel.maxRange + 1) {
                        // out of range, add skipped plans
                        for (int k = i + 1; k < j; k++) result.add(plans.get(k));
                        i = j - 1;
                        continue outer;
                    }

                    if (placeable.get(other)) {
                        // assign tunnel
                        cur.block = tunnel;
                        other.block = tunnel;

                        // config == isInput, null means no setup
                        cur.config = true;
                        other.config = false;

                        i = j - 1;
                        continue outer;
                    }
                }
            }
        }

        plans.set(result);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{rollerRegions[0]};
    }

    @Override
    public boolean isAccessible()
    {
        return true;
    }

    @Override
    public void load() 
    {
        super.load();

        rollerRegions = new TextureRegion[4];
        for(int i = 0; i < 4; i++){
            rollerRegions[i] = Core.atlas.find(name + "-" + i);
        }

        capRegion = Core.atlas.find(name + "-cap");
    }

    @Override
    public Block getReplacement(BuildPlan req, Seq<BuildPlan> plans){
        if(junctionReplacement == null) return this;

        Boolf<Point2> cont = p -> plans.contains(o -> o.x == req.x + p.x && o.y == req.y + p.y && (req.block instanceof Conveyor || req.block instanceof Junction));
        return cont.get(Geometry.d4(req.rotation)) &&
            cont.get(Geometry.d4(req.rotation - 2)) &&
            req.tile() != null &&
            req.tile().block() instanceof Conveyor &&
            Mathf.mod(req.tile().build.rotation - req.rotation, 2) == 1 ? junctionReplacement : this;
    }

    public class RollerConveyorBuild extends TBuild implements ChainedBuilding
    {
        //parallel array data
        public Item[] ids = new Item[itemCapacity];
        public int[] craftingState = new int[itemCapacity];
        public float[] xs = new float[itemCapacity], ys = new float[itemCapacity];
        //amount of items, always < capacity
        public int len = 0;
        //next entity
        public @Nullable Building next;
        public @Nullable RollerConveyorBuild nextc;
        //whether the next conveyor's rotation == tile rotation
        public boolean aligned;

        public int lastInserted, mid;
        public float minitem = 1;

        public int blendbits, blending;
        public int blendsclx = 1, blendscly = 1;

        public float clogHeat = 0f;

        public TextureRegion currentRollerRegion()
        {
            int frame = enabled && clogHeat <= 0.5f ? (int)(((Time.time * speed * getTotalStat(TechStat.speed) * 10f * timeScale * efficiency)) % 4) : 0;
            return rollerRegions[frame];
        }

        @Override
        public void draw()
        {
            Draw.z(Layer.block - 0.2f);

            Draw.rect(currentRollerRegion(), x, y, tilesize * blendsclx, tilesize * blendscly, drawrot());

            Draw.rect(capRegion, x, y, rotdeg());
            Draw.rect(capRegion, x, y, rotdeg() + 180);

            Draw.z(Layer.block - 0.1f);
            float layer = Layer.block - 0.1f, wwidth = world.unitWidth(), wheight = world.unitHeight(), scaling = 0.01f;

            for(int i = 0; i < len; i++)
            {
                Item item = ids[i];
                Tmp.v1.trns(drawrot(), tilesize, 0);
                Tmp.v2.trns(drawrot(), -tilesize / 2f, xs[i] * tilesize / 2f);

                float
                ix = (x + Tmp.v1.x * ys[i] + Tmp.v2.x),
                iy = (y + Tmp.v1.y * ys[i] + Tmp.v2.y);

                //keep draw position deterministic.
                Draw.z(layer + (ix / wwidth + iy / wheight) * scaling);
                Draw.rect(item.fullIcon, ix, iy, itemSize, itemSize);
                var rec = TCustom.ConveyorRecipes.get(item);
                if (craftingState[i] > 0 && rec != null && rec.actions.length * rec.times > craftingState[i])
                {
                    var resitm = rec.result;

                    Draw.scl(0.4f);

                    if (resitm != null)
                        Draw.rect(resitm.fullIcon, ix + itemSize / 2, iy - itemSize / 2);
                    
                    Draw.scl();
                }
            }
        }

        @Override
        public void payloadDraw(){
            Draw.rect(block.fullIcon, x, y);
        }

        @Override
        public void drawCracks(){
            Draw.z(Layer.block - 0.15f);
            super.drawCracks();
        }

        @Override
        public void overwrote(Seq<Building> builds)
        {
            if(builds.first() instanceof RollerConveyorBuild build)
            {
                ids = build.ids.clone();
                craftingState = build.craftingState.clone();
                xs = build.xs.clone();
                ys = build.ys.clone();
                len = build.len;
                clogHeat = build.clogHeat;
                lastInserted = build.lastInserted;
                mid = build.mid;
                minitem = build.minitem;
                items.add(build.items);
            }
        }

        @Override
        public boolean shouldAmbientSound(){
            return clogHeat <= 0.5f;
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            int[] bits = buildBlending(tile, rotation, null, true);
            blendbits = bits[0];
            blendsclx = bits[1];
            blendscly = bits[2];
            blending = bits[4];

            next = front();
            nextc = next instanceof RollerConveyorBuild && next.team == team ? (RollerConveyorBuild)next : null;
            aligned = nextc != null && rotation == next.rotation;
        }

        @Override
        public void unitOn(Unit unit){

            if(!pushUnits || clogHeat > 0.5f || !enabled) return;

            noSleep();

            float mspeed = speed * getTotalStat(TechStat.speed) * tilesize * 55f;
            float centerSpeed = 0.1f;
            float centerDstScl = 3f;
            float tx = Geometry.d4x(rotation), ty = Geometry.d4y(rotation);

            float centerx = 0f, centery = 0f;

            if(Math.abs(tx) > Math.abs(ty)){
                centery = Mathf.clamp((y - unit.y()) / centerDstScl, -centerSpeed, centerSpeed);
                if(Math.abs(y - unit.y()) < 1f) centery = 0f;
            }else{
                centerx = Mathf.clamp((x - unit.x()) / centerDstScl, -centerSpeed, centerSpeed);
                if(Math.abs(x - unit.x()) < 1f) centerx = 0f;
            }

            if(len * itemSpace < 0.9f){
                unit.impulse((tx * mspeed + centerx) * delta(), (ty * mspeed + centery) * delta());
            }
        }

        @Override
        public void updateTile(){
            minitem = 1f;
            mid = 0;

            //skip updates if possible
            if(len == 0 && Mathf.equal(timeScale, 1f)){
                clogHeat = 0f;
                if (canSleep)
                    sleep();
                return;
            }

            float nextMax = aligned ? 1f - Math.max(itemSpace - nextc.minitem, 0) : 1f;
            float moved = speed * edelta();

            for(int i = len - 1; i >= 0; i--){
                float nextpos = (i == len - 1 ? 100f : ys[i + 1]) - itemSpace;
                float maxmove = Mathf.clamp(nextpos - ys[i], 0, moved);

                ys[i] += maxmove;
                if(ys[i] > nextMax) ys[i] = nextMax;

                xs[i] = Mathf.approach(xs[i], 0, moved*2);

                if(ys[i] >= 1f && pass(ids[i])){
                    //align X position if passing forwards
                    if(aligned){
                        nextc.xs[nextc.lastInserted] = xs[i];
                    }
                    //remove last item
                    items.remove(ids[i], len - i);
                    len = Math.min(i, len);
                }else if(ys[i] < minitem){
                    minitem = ys[i];
                }
            }

            if(minitem < itemSpace + (blendbits == 1 ? 0.3f : 0f)){
                clogHeat = Mathf.approachDelta(clogHeat, 1f, 1f / 60f);
            }else{
                clogHeat = 0f;
            }

            noSleep();
        }

        public boolean pass(Item item){
            if(item != null && next != null && next.team == team && next.acceptItem(this, item))
            {
                if (nextc != null)
                    nextc.handleItemStated(this, item, craftingState[len - 1]);
                else
                    next.handleItem(this, item);

                return true;
            }
            return false;
        }

        @Override
        public int removeStack(Item item, int amount){
            noSleep();
            int removed = 0;

            for(int j = 0; j < amount; j++){
                for(int i = 0; i < len; i++){
                    if(ids[i] == item){
                        remove(i);
                        removed ++;
                        break;
                    }
                }
            }

            items.remove(item, removed);
            return removed;
        }

        @Override
        public void getStackOffset(Item item, Vec2 trns){
            trns.trns(rotdeg() + 180f, tilesize / 2f);
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source){
            return Math.min((int)(minitem / itemSpace), amount);
        }

        @Override
        public void handleStack(Item item, int amount, Teamc source){
            amount = Math.min(amount, itemCapacity - len);

            for(int i = amount - 1; i >= 0; i--){
                add(0);
                xs[0] = 0;
                ys[0] = i * itemSpace;
                ids[0] = item;
                items.add(item, 1);
            }

            noSleep();
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            if(len >= itemCapacity) return false;
            Tile facing = Edges.getFacingEdge(source.tile, tile);
            if(facing == null) return false;
            int direction = Math.abs(facing.relativeTo(tile.x, tile.y) - rotation);
            return (((direction == 0) && minitem >= itemSpace) || ((direction % 2 == 1) && minitem > 0.7f)) && !(source.block.rotate && next == source);
        }

        @Override
        public void handleItem(Building source, Item item)
        {
            handleItemStated(source, item, 0);
        }

        public void handleItemStated(Building source, Item item, int crafting_state)
        {
            if(len >= itemCapacity) return;

            int r = rotation;
            Tile facing = Edges.getFacingEdge(source.tile, tile);
            int ang = ((facing.relativeTo(tile.x, tile.y) - r));
            float x = (ang == -1 || ang == 3) ? 1 : (ang == 1 || ang == -3) ? -1 : 0;

            noSleep();
            items.add(item, 1);

            if(Math.abs(facing.relativeTo(tile.x, tile.y) - r) == 0){ //idx = 0
                add(0);
                xs[0] = x;
                ys[0] = 0;
                ids[0] = item;
                craftingState[0] = crafting_state;
            }else{ //idx = mid
                add(mid);
                xs[mid] = x;
                ys[mid] = 0.5f;
                ids[mid] = item;
                craftingState[mid] = crafting_state;
            }
        }

        @Override
        public void write(Writes write)
        {
            super.write(write);
            write.i(len);

            for(int i = 0; i < len; i++)
            {
                write.s(ids[i].id);
                write.i(craftingState[i]);
                write.b((byte)(xs[i] * 127));
                write.b((byte)(ys[i] * 255 - 128));
            }
        }

        @Override
        public void read(Reads read, byte revision)
        {
            super.read(read, revision);
            int amount = read.i();
            len = Math.min(amount, itemCapacity);

            for(int i = 0; i < amount; i++)
            {
                ids[i] = content.item(read.s());
                craftingState[i] = read.i();
                xs[i] = (float)read.b() / 127f;
                ys[i] = ((float)read.b() + 128f) / 255f;
            }

            updateTile();
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.progress){
                if(len == 0) return 0;
                return ys[len - 1];
            }
            return super.sense(sensor);
        }

        @Override
        public Object senseObject(LAccess sensor){
            if(sensor == LAccess.firstItem && len > 0) return ids[len - 1];
            return super.senseObject(sensor);
        }

        @Override
        public void setProp(UnlockableContent content, double value){
            if(content instanceof Item item && items != null){
                int amount = Math.min((int)value, itemCapacity);
                if(items.get(item) != amount){
                    if(items.get(item) < amount){
                        handleStack(item, amount - items.get(item), null);
                    }else if(amount >= 0){
                        removeStack(item, items.get(item) - amount);
                    }
                }
            }else super.setProp(content, value);
        }

        public final void add(int o){
            for(int i = Math.max(o + 1, len); i > o; i--){
                ids[i] = ids[i - 1];
                craftingState[i] = craftingState[i - 1];
                xs[i] = xs[i - 1];
                ys[i] = ys[i - 1];
            }

            len++;
        }

        public final void remove(int o){
            for(int i = o; i < len - 1; i++){
                ids[i] = ids[i + 1];
                craftingState[i] = craftingState[i + 1];
                xs[i] = xs[i + 1];
                ys[i] = ys[i + 1];
            }

            len--;
        }

        @Nullable
        @Override
        public Building next(){
            return nextc;
        }
    }
}
