package technical.core;

import arc.Core;
// import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.Vars;
// import mindustry.*;
// import mindustry.annotations.Annotations.*;
// import mindustry.core.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
// import mindustry.graphics.Drawf;
// import mindustry.graphics.Layer;
// import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.world.*;
// import mindustry.world.blocks.power.BeamNode;
import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.blocks.power.PowerNode;
// import mindustry.world.blocks.production.Drill;
// import mindustry.world.draw.DrawBlock;
// import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.*;

import java.util.*;

import static mindustry.Vars.*;

public class Cogwheel extends PowerBlock{
    public int range = 1;

    public float currentRotation = 0;
    public float rotateSpeed = 1f;
    public float offsetAngle = 30f;

    // public Color laserColor1 = Color.white;
    // public Color laserColor2 = Color.valueOf("ffd9c2");
    // public float pulseScl = 7, pulseMag = 0.05f;
    // public float laserWidth = 0.4f;

    public Cogwheel(String name){
        super(name);
        consumesPower = outputsPower = false;
        drawDisabled = false;
        envEnabled |= Env.space;
        allowDiagonal = false;
        underBullets = true;
        priority = TargetPriority.transport;
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("power", PowerNode.makePowerBalance());
        addBar("batteries", PowerNode.makeBatteryBalance());
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.powerRange, range, StatUnit.blocks);
    }

    @Override
    public void init(){
        super.init();

        updateClipRadius((range + 1) * tilesize);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        for(int i = 0; i < 4; i++){
            int maxLen = range + size/2;
            Building dest = null;
            var dir = Geometry.d4[i];
            int dx = dir.x, dy = dir.y;
            int offset = size/2;
            for(int j = 1 + offset; j <= range + offset; j++){
                var other = world.build(x + j * dir.x, y + j * dir.y);

                //hit insulated wall
                if(other != null && other.isInsulated()){
                    break;
                }

                if(other != null && other.block.hasPower && other.team == Vars.player.team() && !(other.block instanceof PowerNode)){
                    maxLen = j;
                    dest = other;
                    break;
                }
            }

            Drawf.dashLine(Pal.placing,
                x * tilesize + dx * (tilesize * size / 2f + 2),
                y * tilesize + dy * (tilesize * size / 2f + 2),
                x * tilesize + dx * (maxLen) * tilesize,
                y * tilesize + dy * (maxLen) * tilesize
            );

            if(dest != null){
                Drawf.square(dest.x, dest.y, dest.block.size * tilesize/2f + 2.5f, 0f);
            }
        }
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, Core.atlas.find(name + "-top")};
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation, boolean diagonal){
        if(!diagonal){
            Placement.calculateNodes(points, this, rotation, (point, other) -> Math.max(Math.abs(point.x - other.x), Math.abs(point.y - other.y)) <= range);
        }
    }

    public class CogwheelBuild extends Building{
        //current links in cardinal directions
        public Building[] links = new Building[4];
        public Tile[] dests = new Tile[4];
        public int lastChange = -2;
        public float timeRotated = 0f;
        public boolean isRotating = false;

        public boolean inited = false;

        public int rotationDirection = 0;

        @Override
        public void updateTile(){
            if(lastChange != world.tileChanges){
                lastChange = world.tileChanges;
                updateDirections();
            }

            if (!inited)
            {
                rotationDirection = (tile.x % 2 == 0 ^ tile.y % 2 == 0) ? 1 : -1;
                if (rotationDirection == 1) timeRotated = offsetAngle;
                inited = true;
            }

            if (power.graph.getPowerBalance() > 0)
                timeRotated += delta();
            // isRotating = status() != BlockStatus.active;
        }

        @Override
        public BlockStatus status(){
            if(Mathf.equal(power.status, 0f, 0.001f)) return BlockStatus.noInput;
            if(Mathf.equal(power.status, 1f, 0.001f)) return BlockStatus.active;
            return BlockStatus.noOutput;
        }

        @Override
        public void draw(){
            Draw.rect(block.region, x, y);

            if(power.graph.getPowerBalance() > 1)
                Drawf.spinSprite(Core.atlas.find(block.name + "-top"), x, y, timeRotated * rotateSpeed * rotationDirection);
            else
                Draw.rect(block.name + "-top", x, y, timeRotated * rotateSpeed * rotationDirection);
        }


        @Override
        public void pickedUp(){
            Arrays.fill(links, null);
            Arrays.fill(dests, null);
        }

        public void updateDirections()
        {
            for(int i = 0; i < 4; i ++){
                var prev = links[i];
                var dir = Geometry.d4[i];
                links[i] = null;
                dests[i] = null;
                int offset = size/2;

                //find first block with power in range
                for(int j = 1 + offset; j <= range + offset; j++){
                    var other = world.build(tile.x + j * dir.x, tile.y + j * dir.y);

                    //hit insulated wall
                    if(other != null && other.isInsulated()){
                        break;
                    }

                    //power nodes do NOT play nice with beam nodes (cogwheels), do not touch them as that forcefully modifies their links
                    if(other != null && other.block.hasPower && other.block.connectedPower && other.team == team && !(other.block instanceof PowerNode))
                    {
                        links[i] = other;
                        dests[i] = world.tile(tile.x + j * dir.x, tile.y + j * dir.y);
                    }
                }

                var next = links[i];

                if(next != prev){
                    //unlinked, disconnect and reflow
                    if(prev != null){
                        prev.power.links.removeValue(pos());
                        power.links.removeValue(prev.pos());

                        PowerGraph newgraph = new PowerGraph();
                        //reflow from this point, covering all tiles on this side
                        newgraph.reflow(this);

                        if(prev.power.graph != newgraph){
                            //reflow power for other end
                            PowerGraph og = new PowerGraph();
                            og.reflow(prev);
                        }
                    }

                    //linked to a new one, connect graphs
                    if(next != null){
                        power.links.addUnique(next.pos());
                        next.power.links.addUnique(pos());

                        power.graph.addGraph(next.power.graph);
                    }
                }
            }
        }
    }
}
