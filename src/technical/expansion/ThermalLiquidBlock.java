package technical.expansion;

import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Puddles;
import mindustry.entities.TargetPriority;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.Autotiler;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import technical.T;
import technical.TCol;

public class ThermalLiquidBlock extends TBlock implements Autotiler
{
    public Color botColor = Color.valueOf("#5b6977");

    public Color coolColor = T.c("#002fffff");
    public Color heatColor = T.c("#ff1e00ff");
    public float heatPulse = 0.3f, heatPulseScl = 50f, glowMult = 5f;

    public final int timerFlow = timers++;

    public float heatOutput = -1f;

    public float heatResistance = 1f;
    public float heatLeakage = 0.02f;
    public float maxHeat = 120;

    public boolean padCorners = true;
    public boolean needEfficiency = false;

    public TextureRegion bottomRegion;
    public TextureRegion liquidRegion;
    public TextureRegion heatRegion;

    public ThermalLiquidBlock(String name)
    {
        super(name);
        solid = true;
        noUpdateDisabled = true;
        canOverdrive = false;
        priority = TargetPriority.transport;

        outputsLiquid = true;
        update = true;
        hasLiquids = true;
        group = BlockGroup.liquids;
        outputsLiquid = true;
        envEnabled |= Env.space | Env.underwater;
    }

    @Override
    public void setStats()
    {
        super.setStats();

        StatCat cat = new StatCat("thermal-stats");

        stats.add(new Stat("max-heat", cat), StatValues.number(maxHeat, StatUnit.heatUnits));
        stats.add(new Stat("heat-leakage", cat), StatValues.number(heatLeakage, StatUnit.perSecond));
        stats.add(new Stat("heat-resistance", cat), StatValues.number(heatResistance, StatUnit.heatUnits));
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{bottomRegion, region};
    }

    @Override
    public void load()
    {
        super.load();
        bottomRegion = Core.atlas.find(name + "-bottom");
        liquidRegion = Core.atlas.find(name + "-liquid");
        heatRegion = Core.atlas.find(name + "-heat");
    }

    @Override
    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock){
        return otherblock.hasLiquids;
    }


    public Color heatColor(ThermalLiquidBuild b)
    {
        return b.heat >= 0 ? Pal.lightOrange : TCol.sky;
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("heat", (ThermalLiquidBuild b) -> new Bar(
            () -> Core.bundle.format(b.heat >= 0 ? "bar.heatamount" : "bar.coolamount", Math.round(Math.abs(b.heat))),
            () -> heatColor(b),
            () -> Math.abs(b.heat) / maxHeat
        ));
    }

    public static void drawTiledFrames(int size, float x, float y, float padding, Liquid liquid, float alpha)
    {
        drawTiledFrames(size, x, y, padding, padding, padding, padding, liquid, alpha);
    }

    public static void drawTiledFrames(int size, float x, float y, float padLeft, float padRight, float padTop, float padBottom, Liquid liquid, float alpha)
    {
        TextureRegion region = renderer.fluidFrames[liquid.gas ? 1 : 0][liquid.getAnimationFrame()];
        TextureRegion toDraw = Tmp.tr1;

        float leftBounds = size/2f * tilesize - padRight;
        float bottomBounds = size/2f * tilesize - padTop;
        Color color = Tmp.c1.set(liquid.color).a(1f);

        for(int sx = 0; sx < size; sx++){
            for(int sy = 0; sy < size; sy++){
                float relx = sx - (size-1)/2f, rely = sy - (size-1)/2f;

                toDraw.set(region);

                //truncate region if at border
                float rightBorder = relx*tilesize + padLeft, topBorder = rely*tilesize + padBottom;
                float squishX = rightBorder + tilesize/2f - leftBounds, squishY = topBorder + tilesize/2f - bottomBounds;
                float ox = 0f, oy = 0f;

                if(squishX >= 8 || squishY >= 8) continue;

                //cut out the parts that don't fit inside the padding
                if(squishX > 0){
                    toDraw.setWidth(toDraw.width - squishX * 4f);
                    ox = -squishX/2f;
                }

                if(squishY > 0){
                    toDraw.setY(toDraw.getY() + squishY * 4f);
                    oy = -squishY/2f;
                }

                Drawf.liquid(toDraw, x + rightBorder + ox, y + topBorder + oy, alpha, color);
            }
        }
    }

    public class ThermalLiquidBuild extends TBuild
    {
        public float smoothLiquid;
        public float heat = targetHeat();
        public boolean hasHeated = false;

        public int blendbits, xscl = 1, yscl = 1, blending;

        public float targetHeat()
        {
            return 0;
        }

        @Override
        public void draw() 
        {
            Draw.rect(bottomRegion, x, y);

            Drawf.liquid(liquidRegion, x, y, smoothLiquid, liquids.current().color);

            Draw.rect(region, x, y);

            drawHeatOverlay(heatRegion);
        }

        @Override
        public void onProximityUpdate()
        {
            super.onProximityUpdate();
            int[] bits = buildBlending(tile, rotation, null, true);
            blendbits = bits[0];
            xscl = bits[1];
            yscl = bits[2];
            blending = bits[4];
        }

        public void drawHeatOverlay(TextureRegion reg) 
        {
            float aheat = Math.abs(heat);
            if(aheat <= 0f || reg == null) return;

            float frac = aheat / maxHeat; frac *= 3;

            Draw.z(Layer.blockAdditive);
            Draw.blend(Blending.additive);

            Color color = heat >= 0f ? heatColor : coolColor;
            Draw.color(color, frac * (color.a * (1f - heatPulse + Mathf.absin(heatPulseScl, heatPulse))));

            Draw.rect(reg, x, y, drawrot());
            Draw.blend();
            Draw.color();
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid)
        {
            noSleep();
            return (liquids.current() == liquid || liquids.currentAmount() < 0.2f);
        }

        public void dumpTLiquid(Liquid liquidToDump) 
        {
            for (var pro : proximity)
            {
                moveTLiquidStated(pro.tile, false, liquids.current(), 1f / proximity.size);
            }
            noSleep();
        }

        public float moveTLiquidForward(boolean leaks, Liquid liquid) 
        {
            return moveTLiquidStated(tile.nearby(rotation), leaks, liquid);
        }

        public float moveTLiquidStated(Tile next, boolean leaks, Liquid liquid)
        {
            return moveTLiquidStated(next, leaks, liquid, 1f);
        }
        
        public float moveTLiquidStated(Tile next, boolean leaks, Liquid liquid, float amountScaling)
        {
            if(!(liquid instanceof TLiquid tl)) return moveLiquidLeak(next, leaks, liquid, liquid, amountScaling);

            TLiquid nextLiquid = tl;
            if(tl.canUpper && heat > tl.upperHeat) {
                nextLiquid = tl.upperForm;
            } else if(tl.canLower && heat < tl.lowerHeat) {
                nextLiquid = tl.lowerForm;
            }
            if (nextLiquid == tl && !tl.canMove) return 0;

            if (moveLiquidLeak(next, leaks, tl, nextLiquid, amountScaling) <= 0)
            {
                return moveTLiquid(this, tl, nextLiquid, amountScaling);
            }
            return moveLiquidLeak(next, leaks, liquid, nextLiquid, amountScaling);
        }

        public float moveLiquidLeak(Tile next, boolean leaks, Liquid liquidToRemove, Liquid liquidToAdd, float amountScaling) {
            if (next == null) {
                return 0.0F;
            } else if (next.build != null) {
                return this.moveTLiquid(next.build, liquidToRemove, liquidToAdd, amountScaling);
            } else {
                if (leaks && !next.block().solid && !next.block().hasLiquids) {
                    float leakAmount = this.liquids.get(liquidToRemove) / 1.5F;
                    // Changed to deposit the liquidToAdd instead of liquidToRemove
                    Puddles.deposit(next, this.tile, liquidToAdd, leakAmount, true, true);
                    this.liquids.remove(liquidToRemove, leakAmount);
                    
                    return leakAmount;
                }

                return 0.0F;
            }
        }

        // public float moveTLiquid(Building next, Liquid liquidToRemove, Liquid liquidToAdd) 
        // {
        //     return moveTLiquid(next, liquidToRemove, liquidToAdd, 1f);
        // }

        public float moveTLiquid(Building next, Liquid liquidToRemove, Liquid liquidToAdd, float amountScaling) 
        {
            if (next == null) return 0.0F;
            
            if (next == this) 
            {
                float amountA = liquids.get(liquidToRemove);
                float amountB = liquids.get(liquidToAdd);

                if (amountA <= 0f && amountB <= 0f) return 0f;

                liquids.set(liquidToRemove, amountB);
                liquids.set(liquidToAdd, amountA);

                return Math.min(amountA + amountB, block.liquidCapacity);
            } 
            else 
            {
                next = next.getLiquidDestination(this, liquidToRemove);
                
                if (next.team == this.team && next.block.hasLiquids && this.liquids.get(liquidToRemove) > 0.0F) 
                {
                    float ofract = next.liquids.get(liquidToAdd) / next.block.liquidCapacity;
                    float fract = this.liquids.get(liquidToRemove) / this.block.liquidCapacity * this.block.liquidPressure;
                    
                    float maxAvailable = this.liquids.get(liquidToRemove) * amountScaling;
                    
                    float flow = Math.min(Mathf.clamp(fract - ofract) * this.block.liquidCapacity, maxAvailable);

                    flow = Math.min(flow, next.block.liquidCapacity - next.liquids.get(liquidToAdd));
                    
                    if (flow > 0.0F && ofract <= fract && next.acceptLiquid(this, liquidToAdd)) {
                        next.handleLiquid(this, liquidToAdd, flow);
                        this.liquids.remove(liquidToRemove, flow);
                        return flow;
                    }

                    if (!next.block.consumesLiquid(liquidToAdd) && next.liquids.currentAmount() / next.block.liquidCapacity > 0.1F && fract > 0.1F) {
                        float fx = (this.x + next.x) / 2.0F;
                        float fy = (this.y + next.y) / 2.0F;
                        Liquid other = next.liquids.current();
                        
                        // Check reactions for both liquids
                        boolean removeReactive = liquidToRemove.blockReactive && other.blockReactive;
                        boolean addReactive = liquidToAdd.blockReactive && other.blockReactive;
                        
                        if (removeReactive || addReactive) {
                            Liquid reactiveLiquid = removeReactive ? liquidToRemove : liquidToAdd;
                            
                            if ((!(other.flammability > 0.3F) || !(reactiveLiquid.temperature > 0.7F)) && 
                                (!(reactiveLiquid.flammability > 0.3F) || !(other.temperature > 0.7F))) {
                                
                                if (reactiveLiquid.temperature > 0.7F && other.temperature < 0.55F || 
                                    other.temperature > 0.7F && reactiveLiquid.temperature < 0.55F) {
                                    
                                    this.liquids.remove(reactiveLiquid, Math.min(this.liquids.get(reactiveLiquid), 0.7F * Time.delta));
                                }
                            } else {
                                this.damageContinuous(1.0F);
                                next.damageContinuous(1.0F);
                                if (Mathf.chanceDelta(0.1)) {
                                    Fx.fire.at(fx, fy);
                                }
                            }
                        }
                    }
                }

                return 0.0F;
            }
        }

        // public float moveTLiquid(Building next, Liquid liquidToRemove, Liquid liquidToAdd) {
        //     if (next == null) {
        //         return 0.0F;
        //     } 
        //     if (next == this) {
        //         float amountA = liquids.get(liquidToRemove);
        //         float amountB = liquids.get(liquidToAdd);

        //         if (amountA <= 0f && amountB <= 0f) return 0f;

        //         liquids.set(liquidToRemove, amountB);
        //         liquids.set(liquidToAdd, amountA);

        //         return Math.min(amountA + amountB, block.liquidCapacity);
        //     } else {
        //         next = next.getLiquidDestination(this, liquidToRemove);
        //         if (next.team == this.team && next.block.hasLiquids && this.liquids.get(liquidToRemove) > 0.0F) {
        //             float ofract = next.liquids.get(liquidToAdd) / next.block.liquidCapacity;
        //             float fract = this.liquids.get(liquidToRemove) / this.block.liquidCapacity * this.block.liquidPressure;
        //             float flow = Math.min(Mathf.clamp(fract - ofract) * this.block.liquidCapacity, this.liquids.get(liquidToRemove));
        //             flow = Math.min(flow, next.block.liquidCapacity - next.liquids.get(liquidToAdd));
                    
        //             if (flow > 0.0F && ofract <= fract && next.acceptLiquid(this, liquidToAdd)) {
        //                 next.handleLiquid(this, liquidToAdd, flow);
        //                 this.liquids.remove(liquidToRemove, flow);
        //                 return flow;
        //             }

        //             if (!next.block.consumesLiquid(liquidToAdd) && next.liquids.currentAmount() / next.block.liquidCapacity > 0.1F && fract > 0.1F) {
        //                 float fx = (this.x + next.x) / 2.0F;
        //                 float fy = (this.y + next.y) / 2.0F;
        //                 Liquid other = next.liquids.current();
                        
        //                 // Check reactions for both liquids
        //                 boolean removeReactive = liquidToRemove.blockReactive && other.blockReactive;
        //                 boolean addReactive = liquidToAdd.blockReactive && other.blockReactive;
                        
        //                 if (removeReactive || addReactive) {
        //                     Liquid reactiveLiquid = removeReactive ? liquidToRemove : liquidToAdd;
                            
        //                     if ((!(other.flammability > 0.3F) || !(reactiveLiquid.temperature > 0.7F)) && 
        //                         (!(reactiveLiquid.flammability > 0.3F) || !(other.temperature > 0.7F))) {
                                
        //                         if (reactiveLiquid.temperature > 0.7F && other.temperature < 0.55F || 
        //                             other.temperature > 0.7F && reactiveLiquid.temperature < 0.55F) {
                                    
        //                             this.liquids.remove(reactiveLiquid, Math.min(this.liquids.get(reactiveLiquid), 0.7F * Time.delta));
        //                         }
        //                     } else {
        //                         this.damageContinuous(1.0F);
        //                         next.damageContinuous(1.0F);
        //                         if (Mathf.chanceDelta(0.1)) {
        //                             Fx.fire.at(fx, fy);
        //                         }
        //                     }
        //                 }
        //             }
        //         }

        //         return 0.0F;
        //     }
        // }

        @Override
        public void updateTile() 
        {
            super.updateTile();

            smoothLiquid = Mathf.lerpDelta(smoothLiquid, liquids.currentAmount() / liquidCapacity, 0.05f);

            if (Math.abs(heat) > heatResistance)
            {
                for (var b : proximity) {
                    if (b instanceof ThermalLiquidBuild tcb) {
                        if (blends(tile, rotation, tcb.tile.x, tcb.tile.y, tcb.rotation, tcb.block))
                        {
                            if (heat > 0)
                                tcb.acceptHeat(heat - heatResistance);
                            else if (heat < 0)
                                tcb.acceptHeat(heat + heatResistance);
                        }
                    }
                }
            }

            heat = Mathf.approachDelta(heat, targetHeat(), heatLeakage);
            heat = Mathf.clamp(heat, -maxHeat, maxHeat);
        }

        public void acceptHeat(float amount)
        {
            if (Math.abs(heat) >= Math.abs(amount)) return;

            heat = Mathf.approachDelta(heat, amount, 0.2f);
            noSleep();
        }

        @Override
        public void write(Writes write)
        {
            super.write(write);
            
            write.f(heat);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            
            heat = read.f();
        }
    }
}
