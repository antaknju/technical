package technical.expansion;

import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;
import static mindustry.type.Liquid.animationFrames;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.content.Blocks;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.input.Placement;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.ChainedBuilding;
import mindustry.world.blocks.distribution.DirectionBridge;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.blocks.liquid.LiquidJunction;
import technical.utility.T;

public class ThermalConduit extends ThermalLiquidBlock 
{
    static final float rotatePad = 6, hpad = rotatePad / 2f / 4f;
    static final float[][] rotateOffsets = {{hpad, hpad}, {-hpad, hpad}, {-hpad, -hpad}, {hpad, -hpad}};

    TextureRegion[] topRegions;
    TextureRegion[] botRegions;
    TextureRegion[] heatRegions;
    TextureRegion capRegion;

    /** indices: [rotation] [fluid type] [frame] */
    public TextureRegion[][][] rotateRegions;

    /** If true, the liquid region is padded at corners, so it doesn't stick out. */
    public @Nullable Block junctionReplacement, bridgeReplacement, rotBridgeReplacement;

    public boolean leaks = true;

    public ThermalConduit(String name)
    {
        super(name);

        rotate = true;
        solid = false;

        floating = true;
        underBullets = true;
        conveyorPlacement = true;
    }

    @Override
    public void init()
    {
        super.init();

        if(junctionReplacement == null) junctionReplacement = Blocks.liquidJunction;
        if(bridgeReplacement == null || !(bridgeReplacement instanceof ItemBridge)) bridgeReplacement = Blocks.bridgeConduit;
    }

    @Override
    public void load()
    {
        topRegions = T.loadMultipleRegions(name + "-top");
        botRegions = T.loadMultipleRegions(name + "-bottom");
        heatRegions = T.loadMultipleRegions(name + "-heat");
        capRegion = Core.atlas.find(name + "-cap");

        rotateRegions = new TextureRegion[4][2][animationFrames];
        if(renderer != null){
            float pad = rotatePad;
            var frames = renderer.getFluidFrames();
            for(int rot = 0; rot < 4; rot++){
                for(int fluid = 0; fluid < 2; fluid++){
                    for(int frame = 0; frame < animationFrames; frame++){
                        TextureRegion base = frames[fluid][frame];
                        TextureRegion result = new TextureRegion();
                        result.set(base);
                        if(rot == 0){ result.setX(result.getX() + pad); result.setHeight(result.height - pad);}
                        else if(rot == 1){ result.setWidth(result.width - pad); result.setHeight(result.height - pad);}
                        else if(rot == 2){ result.setWidth(result.width - pad); result.setY(result.getY() + pad);}
                        else { result.setX(result.getX() + pad); result.setY(result.getY() + pad);}
                        rotateRegions[rot][fluid][frame] = result;
                    }
                }
            }
        }
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        int[] bits = getTiling(plan, list);
        if(bits == null) return;
        Draw.scl(bits[1], bits[2]);
        Draw.color(botColor);
        Draw.alpha(0.5f);
        Draw.rect(botRegions[bits[0]], plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.color();
        Draw.rect(topRegions[bits[0]], plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.scl();
    }

    @Override
    public Block getReplacement(BuildPlan req, Seq<BuildPlan> plans){
        if(junctionReplacement == null) return this;
        Boolf<Point2> cont = p -> plans.contains(o -> o.x == req.x + p.x && o.y == req.y + p.y && (req.block instanceof ThermalConduit || req.block instanceof LiquidJunction));
        return cont.get(Geometry.d4(req.rotation)) &&
            cont.get(Geometry.d4(req.rotation - 2)) &&
            req.tile() != null &&
            req.tile().block() instanceof ThermalConduit &&
            Mathf.mod(req.build().rotation - req.rotation, 2) == 1 ? junctionReplacement : this;
    }

    @Override
    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock){
        return super.blends(tile, rotation, otherx, othery, otherrot, otherblock)
            && (otherblock.outputsLiquid || lookingAt(tile, rotation, otherx, othery, otherblock))
            && lookingAtEither(tile, rotation, otherx, othery, otherrot, otherblock);
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans){
        if(bridgeReplacement == null) return;
        if(rotBridgeReplacement instanceof DirectionBridge duct){
            Placement.calculateBridges(plans, duct, true, b -> b instanceof ThermalConduit);
        }else{
            Placement.calculateBridges(plans, (ItemBridge)bridgeReplacement, true, b -> b instanceof ThermalConduit);
        }
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{Core.atlas.find("conduit-bottom"), topRegions[0]};
    }

    public class ThermalConduitBuild extends ThermalLiquidBuild implements ChainedBuilding 
    {
        public boolean capped, backCapped = false;

        @Override
        public void draw(){
            int r = this.rotation;

            //draw extra conduits facing this one for tiling purposes
            Draw.z(Layer.blockUnder);
            for(int i = 0; i < 4; i++){
                if((blending & (1 << i)) != 0){
                    int dir = r - i;
                    drawAt(x + Geometry.d4x(dir) * tilesize*0.75f, y + Geometry.d4y(dir) * tilesize*0.75f, 0, i == 0 ? r : dir, i != 0 ? SliceMode.bottom : SliceMode.top);
                }
            }

            Draw.z(Layer.block);
            Draw.scl(xscl, yscl);
            drawAt(x, y, blendbits, r, SliceMode.none);
            drawHeatOverlay(heatRegions[blendbits]);
            Draw.reset();

            if(capped && capRegion.found()) Draw.rect(capRegion, x, y, rotdeg());
            if(backCapped && capRegion.found()) Draw.rect(capRegion, x, y, rotdeg() + 180);
        }

        protected void drawAt(float x, float y, int bits, int rotation, SliceMode slice)
        {
            float angle = rotation * 90f;
            Draw.color(botColor);
            Draw.rect(sliced(botRegions[bits], slice), x, y, angle);
            
            Liquid currentLiquid = liquids.current();
            
            int offset = yscl == -1 ? 3 : 0;
            int frame = currentLiquid.getAnimationFrame();
            int gas = currentLiquid.gas ? 1 : 0;
            float ox = 0f, oy = 0f;
            int wrapRot = (rotation + offset) % 4;
            TextureRegion liquidr = bits == 1 && padCorners ? rotateRegions[wrapRot][gas][frame] : renderer.fluidFrames[gas][frame];
            
            if(bits == 1 && padCorners){
                ox = rotateOffsets[wrapRot][0];
                oy = rotateOffsets[wrapRot][1];
            }

            float xscl = Draw.xscl, yscl = Draw.yscl;
            if(currentLiquid instanceof TLiquid tl && !tl.canMove)
            {
                Draw.color(tl.color, currentLiquid.color.write(Tmp.c1).a * smoothLiquid);
                Draw.rect(sliced(botRegions[bits], slice), x, y, angle);
                Draw.color();
            }
            else
            {
                Draw.scl(1f, 1f);
                Drawf.liquid(sliced(liquidr, slice), x + ox, y + oy, smoothLiquid, currentLiquid.color.write(Tmp.c1).a(1f));
            }

            Draw.scl(xscl, yscl);
            Draw.rect(sliced(topRegions[bits], slice), x, y, angle);
        }

        @Override
        public void onProximityUpdate()
        {
            super.onProximityUpdate();

            Building next = front(), prev = back();
            capped = next == null || next.team != team || !next.block.hasLiquids;
            backCapped = blendbits == 0 && (prev == null || prev.team != team || !prev.block.hasLiquids);
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid)
        {
            return super.acceptLiquid(source, liquid) && (tile == null || source == this || (source.relativeTo(tile.x, tile.y) + 2) % 4 != rotation);
        }

        @Override
        public void updateTile() 
        {
            super.updateTile();

            if(liquids.currentAmount() > 0.0001f && timer(timerFlow, 1) && (efficiency > 0 || !needEfficiency)) 
            {
                moveTLiquidForward(leaks, liquids.current());
                noSleep();
            }
        }

        @Nullable @Override
        public Building next()
        {
            Tile next = tile.nearby(rotation);
            if(next != null && next.build instanceof ThermalConduitBuild){
                return next.build;
            }
            return null;
        }
    }
}
