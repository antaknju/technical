package technical.expansion;

import static mindustry.Vars.itemSize;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

import java.util.concurrent.atomic.AtomicBoolean;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.world.Edges;
import mindustry.world.Tile;
import technical.T;
import technical.content.TCustom;
import technical.debug.Debugger;
import technical.expansion.tech.TechStat;

public class RollerTunnel extends RollerConveyor
{
    public TextureRegion inRegion;
    public TextureRegion outRegion;

    public TextureRegion[] rollerInRegions;
    public TextureRegion[] rollerOutRegions;
    public TextureRegion rollerCapRegion;

    public TextureRegion arrowRegion;

    public int maxRange = 5;

    public RollerTunnel(String name)
    {
        super(name);
        quickRotate = false;
        conveyorPlacement = true;
        canSleep = false;
    }

    @Override
    public void load()
    {
        inRegion = Core.atlas.find(name + "-in");
        outRegion = Core.atlas.find(name + "-out");
        arrowRegion = Core.atlas.find("bridge-arrow");

        rollerCapRegion = Core.atlas.find(name + "-cap");

        rollerInRegions = T.loadMultipleRegions(name + "-in");
        rollerOutRegions = T.loadMultipleRegions(name + "-out");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{rollerInRegions[0], inRegion};
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list)
    {
        TextureRegion rollerInRegion = rollerInRegions[0];
        TextureRegion rollerOutRegion = rollerOutRegions[0];

        // Tunnel preview (preview doesn't have config to set input/output yet)
        AtomicBoolean isPreview = new AtomicBoolean(true);
        list.each(p -> {
            if (plan != p && p.block == plan.block)
            {
                isPreview.set(false);
            }
        });

        if ((plan.config != null && (boolean)plan.config) || isPreview.get())
        {
            Draw.rect(rollerInRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
            Draw.rect(inRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
        }
        else
        {
            Draw.rect(rollerOutRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
            Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
        }
    }

    private RollerTunnelBuild findTarget(Tile tile, int rotation, Team team) 
    {
        for(int i = 1; i <= maxRange + 1; i++)
        {
            int tx = tile.x + Geometry.d4x(rotation) * i;
            int ty = tile.y + Geometry.d4y(rotation) * i;
            
            Tile t = world.tile(tx, ty);
            if(t == null) break;

            if((t.build instanceof RollerTunnelBuild build) && t.block() == tile.block())
            {
                if (build.team != team) continue;
                if (build.rotation != rotation) continue;
                if (build.link != null) continue;

                return build;
            }
        }
        return null;
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans)
    {
        int keep = 2;
        int skip = maxRange;
        int counter = 1;

        for(int i = 0; i < plans.size;)
        {
            if(counter < keep || i == plans.size - 1)
            {
                if (counter == 1)
                {
                    plans.get(i).config = true; // input
                }
                else
                {
                    plans.get(i).config = false; // output
                }
                
                counter++;
                i++;
            }
            else
            {
                // remove this point
                plans.remove(i);
                counter++;
                if(counter >= keep + skip)
                {
                    counter = 0; // reset the cycle
                }
            }
        }
    }

    public class RollerTunnelBuild extends RollerConveyorBuild
    {
        public boolean isInput = false;
        public RollerTunnelBuild link = null;

        @Override
        public TextureRegion currentRollerRegion()
        {
            int frame = enabled && clogHeat <= 0.5f ? (int)(((Time.time * speed * getTotalStat(TechStat.speed) * 10f * timeScale * efficiency)) % 4) : 0;
            return isInput || link == null ? rollerInRegions[frame] : rollerOutRegions[frame];
        }

        @Override
        public void draw()
        {
            Draw.z(Layer.block - 0.2f);

            Draw.rect(currentRollerRegion(), x, y, tilesize * blendsclx, tilesize * blendscly, drawrot());

            if (isInput || link == null) 
                Draw.rect(rollerCapRegion, x, y, rotdeg() + 180);
            else
                Draw.rect(rollerCapRegion, x, y, rotdeg());

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

                // Don't draw items 'outside' of the tunnel
                if (ys[i] > 0.5f && isInput) continue;

                //keep draw position deterministic
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
            Draw.z(Layer.block + 1);
            Draw.rect(isInput || link == null ? inRegion : outRegion, x, y, drawrot());
        }

        @Override
        public boolean acceptItem(Building source, Item item) 
        {
            if(len >= itemCapacity) return false;
            
            if (isInput)
            {
                Tile facing = Edges.getFacingEdge(source.tile, tile);
                if(facing == null) return false;

                int direction = Math.abs(facing.relativeTo(tile.x, tile.y) - rotation);

                return direction == 0 && minitem >= itemSpace;
            }
            else
            {
                return source == link;
            }
        }

        @Override
        public void updateTile()
        {
            if (link != null && !link.isValid())
            {
                link = null;
            }

            if (link == null)
            {
                link = findTarget(tile, rotation, team);
                if (link != null)
                {
                    isInput = true;
                    next = nextc = link;
                    aligned = true;

                    link.isInput = false;
                    link.link = this;
                }
            }

            if (link != null && !isInput)
            {
                next = front();
                if (next != null)
                {
                    nextc = next instanceof RollerConveyorBuild && next.team == team ? (RollerConveyorBuild)next : null;
                    aligned = nextc != null && rotation == next.rotation;
                }
            }

            super.updateTile();
        }

        @Override
        public void drawSelect() 
        {
            if (link == null || !link.isValid()) return;

            Vec2 start = new Vec2(tile.drawx(), tile.drawy());
            Vec2 end = new Vec2(link.tile.drawx(), link.tile.drawy());

            if (!isInput) {
                Vec2 temp = start;
                start = end;
                end = temp;
            }

            Vec2 dir = end.cpy().sub(start).nor();
            Vec2 perp = new Vec2(-dir.y, dir.x);
            float laneOffset = 2.8f;

            for (float offset : new float[]{laneOffset, -laneOffset}) {
                Vec2 laneStart = start.cpy().add(perp.cpy().scl(offset));
                Vec2 laneEnd = laneStart.cpy().add(dir.cpy().scl(start.dst(end)));

                Draw.color(Pal.gray);
                Lines.stroke(1f);
                Lines.line(laneStart.x, laneStart.y, laneEnd.x, laneEnd.y);

                Draw.color(isInput ? Pal.accent : Pal.place);
                Lines.stroke(0.5f);
                Lines.line(laneStart.x, laneStart.y, laneEnd.x, laneEnd.y);
            }

            float arrowSpacing = 5f;
            float margin = 1.3f;
            float totalDistance = start.dst(end) - margin * 2f;
            int arrowCount = (int)(totalDistance / arrowSpacing);

            for (int i = 0; i < arrowCount; i++) {
                float t = (i + 0.5f) * arrowSpacing / totalDistance; // +0.5 to center arrows in each segment
                Vec2 pos = start.cpy().lerp(end, margin / start.dst(end) + t * (totalDistance / start.dst(end)));

                Draw.color(Pal.gray);
                Draw.scl(0.7f);
                Draw.rect(arrowRegion, pos.x, pos.y, dir.angle());

                Draw.color(isInput ? Pal.accent : Pal.place);
                Draw.scl(0.6f);
                Draw.rect(arrowRegion, pos.x, pos.y, dir.angle());
            }


            Draw.reset();
        }

        @Override
        public void overwrote(Seq<Building> builds) 
        {
            Debugger.print("Overwriting Roller Tunnel Build: " + this + " with builds: " + builds);
            super.overwrote(builds);
            if (builds.first() instanceof RollerTunnelBuild build)
            {
                if (build.link != null)
                {
                    build.link.link = null;
                }
            }
        }

        @Override
        public void onProximityUpdate()
        {
            noSleep();

            int[] bits = buildBlending(tile, rotation, null, true);
            blendbits = bits[0];
            blendsclx = bits[1];
            blendscly = bits[2];
            blending = bits[4];
        }
    }   
}



    // Tile tile = plan.tile();
    // int rot = plan.rotation;
    // int rrot = (rot + 2) % 4;
    // Team team = Vars.player.team();

    // AtomicBoolean backwardLink = new AtomicBoolean(false);

    // for(int i = 2; i <= maxRange + 1; i++)
    // {
    //     int tx = tile.x + Geometry.d4x(rrot) * i;
    //     int ty = tile.y + Geometry.d4y(rrot) * i;
        
    //     Tile t = world.tile(tx, ty);
    //     if (t == null) continue;

    //     if((t.build instanceof RollerTunnelBuild build) && t.block() == tile.block())
    //     {
    //         if (build.team != team) continue;
    //         if (build.rotation != rot) continue;
    //         if (build.link != null) continue;

    //         backwardLink.set(true);
    //         break;
    //     }
    //     else 
    //     {
    //         list.each(p -> {
    //             if (p.x == tx && p.y == ty && p.block instanceof RollerTunnel && p.block == plan.block && p.rotation == rot)
    //             {
    //                 backwardLink.set(true);
    //             }
    //         });
    //     }
    // }
    
    // @Override
    // public void changePlacementPath(Seq<Point2> points, int rotation){
    //     int keep = 2;           // number of consecutive points to keep
    //     int skip = maxRange;    // number of points to skip after kept points
    //     int counter = 1;        // One first point (idx 0) is always skipped, so it start with single

    //     for(int i = 0; i < points.size;)
    //     {
    //         if(counter < keep || i == points.size - 1)
    //         {
    //             // keep this point
    //             counter++;
    //             i++;
    //         }
    //         else
    //         {
    //             // remove this point
    //             points.remove(i);
    //             counter++;
    //             if(counter >= keep + skip)
    //             {
    //                 counter = 0; // reset the cycle
    //             }
    //         }
    //     }
    // }