package technical.core;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectIntMap;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadSeq;
import mindustry.type.PayloadStack;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumeLiquidsDynamic;
import mindustry.world.consumers.ConsumePowerDynamic;
import mindustry.world.meta.Stat;
import technical.util.T;
import technical.core.FacilityAddapter.FacilityAddapterBuild;
import technical.core.FacilityFloorTile.FacilityFloorTileBuild;
import technical.core.FacilityLoader.FacilityLoaderBuild;
import technical.core.FacilityStep.FacilityStepType;
import technical.core.kinetic.ConsumeKineticDynamic;
import technical.util.TUI;

public class FacilityController extends TPayloadBlock
{
    public Seq<FacilityPlan> plans = new Seq<>();

    public float warmupSpeed = 0.03f;

    protected boolean isItemConsumer = false;
    protected boolean isLiquidsConsumer = false;
    protected boolean isPayloadConsumer = false;
    protected boolean isPowerConsumer = false;
    protected boolean isKineticConsumer = false;

    public FacilityController(String name)
    {
        super(name);
        quickRotate = false;

        hasItems = true;
        itemCapacity = 200;

        blendsInput = false;

        configurable = true;
        saveConfig = true;
        config(Integer.class, (FacilityControllerBuild b, Integer i) -> b.changePlan(i));
    }

    @Override
    public void init() 
    {
        setupFromPlans();
        setupConsumers();

        super.init();
    }

    public void setupFromPlans()
    {
        isItemConsumer = false;
        isPowerConsumer = false;
        isLiquidsConsumer = false;
        isKineticConsumer = false;

        for (var p : plans)
        {
            if (p.inputItems != null) isItemConsumer = true;
            if (p.inputPayloads != null) isPayloadConsumer = true;
            if (p.inputLiquids != null) isLiquidsConsumer = true;
            if (p.inputKinetic != null) isKineticConsumer = true;

            if (p.inputPower > 0) isPowerConsumer = true;
        }
    }

    public void setupConsumers()
    {
        if (isItemConsumer) consume(new ConsumeItemDynamic(
            (FacilityControllerBuild b) -> (b.plan().inputItems != null ? b.plan().inputItems : new ItemStack[0])
        ));
        if (isPayloadConsumer) consume(new ArrayConsumePayloadDynamic(
            (FacilityControllerBuild b) -> (b.plan().inputPayloads != null ? b.plan().inputPayloads : new PayloadStack[0])
        ));
        if (isLiquidsConsumer) consume(new ConsumeLiquidsDynamic(
            (FacilityControllerBuild b) -> (b.plan().inputLiquids != null ? b.plan().inputLiquids : new LiquidStack[0])
        ));
        if (isPowerConsumer) consume(new ConsumePowerDynamic(b ->
            ((FacilityControllerBuild) b).plan().inputPower
        ));
        if (isKineticConsumer) consume(new ConsumeKineticDynamic(b ->
            ((FacilityControllerBuild) b).plan().inputKinetic
        ));
    }

    @Override
    public void setStats() 
    {
        super.setStats();

        stats.add(Stat.output, stat -> TUI.buildRecipesStats(stat, RecipeDrawable.listPlans(plans)));
    }

    @Override
    public void setBars()
    {
        super.setBars();

        addBar("total-progress", (FacilityControllerBuild b) -> new Bar(
                "bar.total-progress",
                Pal.place,
                b::totalProgress
        ));

        addBar("step-progress", (FacilityControllerBuild b) -> new Bar(
                "bar.step-progress",
                Pal.items,
                b::progress
        ));
    }

    public class FacilityControllerBuild extends TPayloadBuild<Payload> implements FacilityBuild
    {
        public PayloadSeq payloads = new PayloadSeq();
        public float progress;
        // public boolean wasOccupied = false;

        public int facilityRadius = -1;

        public float warmup = 0;

        public int currentPlan = 0;

        public int currentStep = 0;

        public Seq<FacilityLoaderBuild> loaders = new Seq<>();
        public Seq<FacilityAddapterBuild> addapters = new Seq<>();

        @Override
        public float progress()
        {
            return progress;
        }

        public void changePlan(int idx)
        {
            currentPlan = idx;

            warmup = 0;
            progress = 0;
            currentStep = 0;
        }

        public FacilityPlan plan()
        {
            return plans.get(currentPlan);
        }

        public FacilityStep step()
        {
            return plan().steps.get(currentStep);
        }

        @Override
        public PayloadSeq getPayloads()
        {
            return payloads;
        }

        public void updateTile()
        {
            recomputeFacilityRadius();

            if (payload == null && efficiency * efficiencyScale() > 0)
            {
                warmup = Mathf.approachDelta(warmup, 1, warmupSpeed);
                if (warmup >= 0.99f)
                {
                    progress = Mathf.approachDelta(progress, 1, 1 / step().time);
                    warmup = 1;

                    if (progress >= 0.99f)
                    {
                        progress = 0;
                        currentStep += 1;

                        if (currentStep == plan().steps.size)
                        {
                            currentStep = 0;
                            consume();

                            var output = plan().outputPayload;

                            Payload payloadOutput = null;
                            if (output.item instanceof Block)
                                payloadOutput = new BuildPayload((Block)output.item, team);
                            else if (output.item instanceof UnitType)
                                payloadOutput = new UnitPayload(((UnitType)output.item).create(team));
                            
                            Vec2 spawn = getFacilityCenter(Tmp.v4);
                            payload = payloadOutput;
                            payVector.set(spawn.x - x, spawn.y - y);

                            updatePayload();
                        }
                    }
                }
            }
            else
            {
                warmup = Mathf.approachDelta(warmup, 0, warmupSpeed);

                if (warmup <= 0f)
                {
                    progress = 0;
                    currentStep = 0;
                    warmup = 0;
                }
            }

            moveOutPayload();
        }

        @Override
        public float efficiencyScale()
        {
            ObjectIntMap<FacilityStepType> req = new ObjectIntMap<>();
            for (var s : plan().steps)
                req.put(s.type, s.workers);

            for (var a : addapters)
            {
                if (!a.isValid())
                    removeFromFacility(a);

                if (a.efficiency <= 0) continue;
                FacilityStepType t = ((FacilityAddapter)a.block).stepType;

                req.put(t, req.get(t) - 1);
            }

            for (var e : req)
                if (e.value > 0)
                    return 0;

            return 1;
        }

        public FacilityStepType currentStepType()
        {
            if (payload != null)
                return FacilityStepType.Exporting;

            if (efficiency * efficiencyScale() <= 0f)
                return FacilityStepType.Preparing;

            if (warmup < 1f)
                return FacilityStepType.WarmingUp;

            return step().type;
        }

        @Override
        public float totalProgress()
        {
            float time_in = 0;
            for (int i = 0; i < currentStep; i++)
            {
                time_in += plan().steps.get(i).time;
            }
            time_in += progress * step().time;

            return time_in / plan().craftTime;
        }

        public boolean facilityAcceptPayload(UnlockableContent payload) 
        {
            if (!acceptsInput()) return false;

            var stack = plan().getPayloadStack(payload);
            if (stack == null) return false;

            return payloads.get(payload) < stack.amount;
        }

        public void addPayload(UnlockableContent content)
        {
            payloads.add(content);
        }

        @Override
        public Object config() {
            return currentPlan;
        }

        @Override
        public void buildConfiguration(Table table) 
        {
            table.clear();

            Table buttons = new Table();
            buttons.top().defaults().pad(6f).growX();

            for (int i = 0; i < plans.size; i++) 
            {
                int idx = i;
                TUI.addRecipeButton(buttons, RecipeDrawable.tmpRD1.set(plans.get(idx)), () -> changePlan(idx), () -> currentPlan == idx);

                if (i % 2 == 1) buttons.row();
            }

            table.add(buttons).growX().top();
        }

        @Override
        public void draw()
        {
            super.draw();

            if (facilityRadius != -1)
            {
                Draw.draw(Layer.blockBuilding, () -> {
                    Draw.color(Pal.accent, warmup);

                    Shaders.blockbuild.region = plan().outputPayload.item.fullIcon;
                    Shaders.blockbuild.time = Time.time;
                    Shaders.blockbuild.alpha = warmup;

                    // margin due to units not taking up whole region (visual reason)
                    Shaders.blockbuild.progress = totalProgress();//Mathf.clamp(progress + 0.1f);

                    Vec2 spawn = getFacilityCenter(Tmp.v4);
                    Draw.rect(plan().outputPayload.item.fullIcon, spawn.x, spawn.y, 0);
                    Draw.flush();
                    Draw.color();
                    Shaders.blockbuild.alpha = 1f;
                });
            }
        }

        public void recomputeFacilityRadius() 
        {
            int oldRadius = facilityRadius;
            calculateFacilityRadius();

            Point2 d = T.Rot2Pos(rotation);
            int s2 = size / 2 + 1;

            // Calculate centers
            int oldNx = tile.x - d.x * (oldRadius + s2);
            int oldNy = tile.y - d.y * (oldRadius + s2);
            
            int newNx = tile.x - d.x * (facilityRadius + s2);
            int newNy = tile.y - d.y * (facilityRadius + s2);

            // Determine maximal bounding box
            int maxR = Math.max(oldRadius, facilityRadius);

            int minX = Math.min(oldNx - maxR, newNx - maxR) - 1;
            int maxX = Math.max(oldNx + maxR, newNx + maxR) + 1;
            int minY = Math.min(oldNy - maxR, newNy - maxR) - 1; 
            int maxY = Math.max(oldNy + maxR, newNy + maxR) + 1;

            for (int x = minX; x <= maxX; x++) 
            {
                for (int y = minY; y <= maxY; y++) 
                {
                    Tile tile = world.tile(x, y);
                    if (tile == null || tile.build == null || !tile.build.isValid()) continue;
                    if (!(tile.build instanceof FacilityBuild fb)) continue;
                    if (fb.controller() != this && fb.controller() != null) continue;

                    boolean inOld = fb.controller() == this;

                    boolean inNewCore = inRange(x - newNx, y - newNy, facilityRadius);
                    boolean inNewEdge = !inNewCore && inRange(x - newNx, y - newNy, facilityRadius + 1);
                    boolean inNew = inNewCore || (inNewEdge && !(fb instanceof FacilityFloorTileBuild));

                    if (inNew && !inOld) 
                    {
                        addToFacility(fb);
                    } 
                    else if (!inNew && inOld) 
                    {
                        removeFromFacility(fb);
                    }
                }
            }
        }

        private boolean inRange(int x, int y, int r)
        {
            return Math.abs(x) <= r && Math.abs(y) <= r;
        }

        public void removeFromFacility(FacilityBuild fb)
        {
            fb.controller(null);

            if (fb instanceof FacilityLoaderBuild flb)
            {
                loaders.remove(flb);
            }

            if (fb instanceof FacilityAddapterBuild fab)
            {
                addapters.remove(fab);
            }
        }

        public void addToFacility(FacilityBuild fb)
        {
            if (fb instanceof FacilityLoaderBuild flb)
            {
                if (flb.front() instanceof FacilityFloorTileBuild fftb && fftb.controller() == this)
                {
                    loaders.add(flb);
                    fb.controller(this);
                }

                return;
            }

            if (fb instanceof FacilityAddapterBuild fab)
            {
                addapters.add(fab);
            }

            fb.controller(this);
        }

        public void drawSelect()
        {
            super.drawSelect();

            drawSelectFacility(this);
            
            float r = facilityRadius;
            if (r == -1) return;
            
            Point2 d = T.Rot2Pos(rotation);

            Rect r2 = Tmp.r2.setCentered(x - d.x * tilesize * (r + size / 2 + 1), y - d.y * tilesize * (r + size / 2 + 1), (r + 0.5f) * tilesize * 2);
            Drawf.dashRect(Pal.accent, r2);

            r++;

            Rect r1 = Tmp.r1.setCentered(x - d.x * tilesize * (r + size / 2), y - d.y * tilesize * (r + size / 2), (r + 0.5f) * tilesize * 2);
            Drawf.dashRect(Pal.place, r1);
        }

        public Vec2 getFacilityCenter(Vec2 vec)
        {
            float r = facilityRadius;
            if (r == -1) return null;

            Point2 d = T.Rot2Pos(rotation);
            return vec.set(x - d.x * tilesize * (r + size / 2 + 1), y - d.y * tilesize * (r + size / 2 + 1));
        }

        private void calculateFacilityRadius()
        {
            Point2 d = T.Rot2Pos(rotation);
            int r = 0;

            // TODO now it's checking the same tiles multiple times
            while(true)
            {
                int nx = tile.x - d.x * (r + size / 2 + 1);
                int ny = tile.y - d.y * (r + size / 2 + 1);

                for(int mx = -r; mx <= r; mx++)
                {
                    for(int my = -r; my <= r; my++)
                    {
                        Tile tile = world.tile(nx + mx, ny + my);
                        if (tile.build == null || !tile.build.isValid() || !(tile.build instanceof FacilityFloorTileBuild))
                        {
                            facilityRadius = r - 1;
                            return;
                        }
                    }
                }

                r++;
            }
        }

        @Override
        public @Nullable FacilityControllerBuild controller() 
        {
            if (!isValid()) return null;

            return this;
        }

        public @Nullable Seq<FacilityLoaderBuild> loaders()
        {
            return loaders;
        }

        public @Nullable Seq<FacilityAddapterBuild> addapters()
        {
            return addapters;
        }

        @Override
        public void controller(FacilityControllerBuild fcb) 
        {
            // No possiblity to change
        }

        public boolean acceptsInput()
        {
            return payload == null && progress <= 0;
        }

        @Override
        public boolean acceptItem(Building source, Item item) 
        {
            return acceptsInput() && items.get(item) < getMaximumAccepted(item) && plan().hasItem(item);
        }

        @Override
        public int getMaximumAccepted(Item item)
        {
            var stack = plan().getItemStack(item);
            if (stack == null) return 0;

            return stack.amount;
        }

        @Override
        public void write(Writes write)
        {
            super.write(write);
            
            write.i(currentPlan);
            write.i(currentStep);

            write.f(warmup);
            write.f(progress);

            payloads.write(write);
        }

        @Override
        public void read(Reads read, byte revision)
        {
            super.read(read, revision);
            
            currentPlan = read.i();
            currentStep = read.i();

            warmup = read.f();
            progress = read.f();

            payloads.read(read);
        }
    }
}



    // public static class FacilityPlan
    // {
    //     public PayloadStack output;
    //     @Nullable public Seq<PayloadStack> requirements;
    //     @Nullable public ItemStack[] itemReq;
    //     @Nullable public LiquidStack[] liquidReq;
    //     public float time;

    //     public FacilityPlan(PayloadStack output, float time, Seq<PayloadStack> requirements){
    //         this.output = output;
    //         this.time = time;
    //         this.requirements = requirements;
    //     }

    //     FacilityPlan(){}
    // }


        // public void recomputeFacilityRadius()
        // {
        //     if (facilityRadius == -1) return;
        //     int oldFacilityRadius = facilityRadius;

        //     calculateFacilityRadius();

        //     int r = Math.max(facilityRadius, oldFacilityRadius) + 1; // r must grow outside facility to contain the edge addapters
            
        //     Point2 d = T.Rot2Pos(rotation);

        //     int nx = tile.x - d.x * (r + size / 2);
        //     int ny = tile.y - d.y * (r + size / 2);

        //     for(int mx = -r; mx <= r; mx++)
        //     {
        //         for(int my = -r; my <= r; my++)
        //         {
        //             Tile tile = world.tile(nx + mx, ny + my);
        //             if (tile != null && tile.build != null && tile.build.isValid() && tile.build instanceof FacilityBuild fb && (fb.controller() == this || fb.controller() == null))
        //             {
        //                 if (inRange(mx, my, facilityRadius) && !inRange(mx, my, oldFacilityRadius))
        //                 {
        //                     if (inRange(mx, my, facilityRadius - 1) || !(fb instanceof FacilityFloorTileBuild))
        //                         addToFacility(fb);
        //                 }
        //                 else if (!inRange(mx, my, facilityRadius) && inRange(mx, my, oldFacilityRadius))
        //                 {
        //                     if (inRange(mx, my, facilityRadius - 1) || !(fb instanceof FacilityFloorTileBuild))
        //                         removeFromFacility(fb);
        //                 }
        //             }
        //         }
        //     }
        // }

        // public void recomputeFacilityRadius()
        // {
        //     forEachFacilityBuild(fb -> removeFromFacility(fb));

        //     int r = facilityRadius();
        //     facilityRadius = r;
        //     forEachFacilityBuild(fb -> addToFacility(fb));
        // }

        // public void forEachFacilityBuild(Cons<FacilityBuild> cons)
        // {
        //     if (facilityRadius == -1) return;
        //     int r = facilityRadius + 1;
            
        //     Point2 d = T.Rot2Pos(rotation);

        //     // tile.x - d.x * (r + size / 2 + 1) would be for smaller square
        //     int nx = tile.x - d.x * (r + size / 2); 
        //     int ny = tile.y - d.y * (r + size / 2);

        //     for(int mx = -r; mx <= r; mx++)
        //     {
        //         for(int my = -r; my <= r; my++)
        //         {
        //             Tile tile = world.tile(nx + mx, ny + my);
        //             if (tile != null && tile.build != null && tile.build.isValid() && tile.build instanceof FacilityBuild fb && (fb.controller() == this || fb.controller() == null))
        //                 if ((Math.abs(mx) < r && Math.abs(my) < r) || !(tile.build instanceof FacilityFloorTileBuild))
        //                     cons.get(fb);
        //         }
        //     }
        // }



        // public void recomputeFacilityRadius()
        // {
        //     int old = facilityRadius + 1;

        //     facilityRadius = facilityRadius();

        //     int nw = facilityRadius + 1;

        //     int r = Math.max(old, nw);
            
        //     Point2 d = T.Rot2Pos(rotation);

        //     // tile.x - d.x * (r + size / 2 + 1) would be for smaller square
        //     int nx = tile.x - d.x * (r + size / 2); 
        //     int ny = tile.y - d.y * (r + size / 2);

        //     for(int mx = -r; mx <= r; mx++)
        //     {
        //         for(int my = -r; my <= r; my++)
        //         {
        //             Tile tile = world.tile(nx + mx, ny + my);
        //             if (tile != null && tile.build != null && tile.build.isValid() && tile.build instanceof FacilityBuild fb)
        //             {
        //                 if (Math.abs(mx) <= nw && Math.abs(my) <= nw)
        //                 {
        //                     fb.controller(this);
        //                 }
        //                 else if (Math.abs(mx) <= nw + 1 && Math.abs(my) <= nw + 1 && !(tile.build instanceof FacilityFloorTileBuild))
        //                 {
        //                     fb.controller(this);
        //                 }
        //                 else if (fb.controller() == this)
        //                 {
        //                     fb.controller(null);
        //                 }
        //             }
        //         }
        //     }
        // }


        // public void recomputeFacilityRadius() 
        // {
        //     int oldRadius = facilityRadius;

        //     calculateFacilityRadius();
        //     if (oldRadius == facilityRadius) return;

        //     Point2 d = T.Rot2Pos(rotation);
        //     int s2 = size / 2 + 1;

        //     // Calculate centers
        //     int oldNx = tile.x - d.x * (oldRadius + s2);
        //     int oldNy = tile.y - d.y * (oldRadius + s2);
            
        //     int newNx = tile.x - d.x * (facilityRadius + s2);
        //     int newNy = tile.y - d.y * (facilityRadius + s2);

        //     // Determine maximal bounding box
        //     int maxR = Math.max(oldRadius, facilityRadius);

        //     int minX = Math.min(oldNx - maxR, newNx - maxR);
        //     int maxX = Math.max(oldNx + maxR, newNx + maxR);
        //     int minY = Math.min(oldNy - maxR, newNy + maxR);
        //     int maxY = Math.max(oldNy + maxR, newNy + maxR);

        //     for (int x = minX; x <= maxX; x++) 
        //     {
        //         for (int y = minY; y <= maxY; y++) 
        //         {
        //             Tile tile = world.tile(x, y);
        //             if (tile == null || tile.build == null || !tile.build.isValid()) continue;
        //             if (!(tile.build instanceof FacilityBuild fb)) continue;
        //             if (fb.controller() != this && fb.controller() != null) continue;

        //             // Check relative offsets for both states
        //             boolean inOld = inRange(x - oldNx, y - oldNy, oldRadius);
        //             boolean inNew = inRange(x - newNx, y - newNy, facilityRadius);

        //             if (inNew && !inOld) 
        //             {
        //                 addToFacility(fb);
        //             } 
        //             else if (!inNew && inOld) 
        //             {
        //                 removeFromFacility(fb);
        //             }
        //         }
        //     }
        // }