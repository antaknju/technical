package technical.core.train;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;
import technical.util.T;
import technical.core.train.RailConnector.RailConnectorBuild;

// import static mindustry.Vars.content;
import static mindustry.Vars.tilesize;

public class TrainPayloadLoader extends RailAddapter 
{
    public float payloadSpeed = 0.7f, payloadRotateSpeed = 5f;
    public boolean isUnloader = false;
    public TextureRegion outRegion, inRegion;

    public TrainPayloadLoader(String name) 
    {
        super(name);
        
        sync = true;

        outRegion = Core.atlas.find(name + "-out", "factory-out-" + size);
        inRegion = Core.atlas.find(name + "-in", "factory-in-" + size);
    }

    @Override
    public void setBars() 
    {
        super.setBars();
    }

    public static void pushOutput(Payload payload, float progress){
        float thresh = 0.55f;
        if(progress >= thresh){
            boolean legStep = payload instanceof UnitPayload u && u.unit.type.allowLegStep;
            float size = payload.size(), radius = size/2f, x = payload.x(), y = payload.y(), scl = Mathf.clamp(((progress - thresh) / (1f - thresh)) * 1.1f);

            Groups.unit.intersect(x - size/2f, y - size/2f, size, size, u -> {
                float dst = u.dst(payload);
                float rs = radius + u.hitSize/2f;
                if(u.isGrounded() && u.type.allowLegStep == legStep && dst < rs){
                    u.vel.add(Tmp.v1.set(u.x - x, u.y - y).setLength(Math.min(rs - dst, 1f)).scl(scl));
                }
            });
        }
    }

    public static boolean blends(Building build, int direction){
        int size = build.block.size;
        int trns = build.block.size/2 + 1;
        Building accept = build.nearby(Geometry.d4(direction).x * trns, Geometry.d4(direction).y * trns);
        return accept != null &&
            accept.block.outputsPayload &&

            //if size is the same, block must either be facing this one, or not be rotating
            ((accept.block.size == size
            && Math.abs(accept.tileX() - build.tileX()) % size == 0 //check alignment
            && Math.abs(accept.tileY() - build.tileY()) % size == 0
            && ((accept.block.rotate && accept.tileX() + Geometry.d4(accept.rotation).x * size == build.tileX() && accept.tileY() + Geometry.d4(accept.rotation).y * size == build.tileY())
            || !accept.block.rotate
            || !accept.block.outputFacing)) ||

            //if the other block is smaller, check alignment
            (accept.block.size != size &&
            (accept.rotation % 2 == 0 ? //check orientation; make sure it's aligned properly with this block.
                Math.abs(accept.y - build.y) <= Math.abs(size * tilesize - accept.block.size * tilesize)/2f : //check Y alignment
                Math.abs(accept.x - build.x) <= Math.abs(size * tilesize - accept.block.size * tilesize)/2f   //check X alignment
                )) && (!accept.block.rotate || accept.front() == build || !accept.block.outputFacing) //make sure it's facing this block
            );
    }

    public class TrainPayloadLoaderBuild<P extends Payload> extends RailAddapterBuild 
    {
        public @Nullable P payload;
        public Vec2 payVector = new Vec2();
        public float payRotation;
        public boolean carried;
        public boolean isPortingPayload = false;

        public boolean acceptUnitPayload(Unit unit){
            return false;
        }

        @Override
        public boolean canControlSelect(Unit unit){
            return !unit.spawnedByCore && unit.type.allowedInPayloads && this.payload == null && acceptUnitPayload(unit) && unit.tileOn() != null && unit.tileOn().build == this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onControlSelect(Unit player){
            float x = player.x, y = player.y;
            handleUnitPayload(player, p -> payload = (P)p);
            this.payVector.set(x, y).sub(this).clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);
            this.payRotation = player.rotation;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return this.payload == null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handlePayload(Building source, Payload payload){
            this.payload = (P)payload;
            this.payVector.set(source).sub(this).clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);
            this.payRotation = payload.rotation();

            updatePayload();
        }

        @Override
        public Payload getPayload(){
            return payload;
        }

        @Override
        public void pickedUp(){
            carried = true;
        }

        @Override
        public void drawTeamTop(){
            carried = false;
        }

        @Override
        public Payload takePayload(){
            P t = payload;
            payload = null;
            return t;
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            if(payload != null && !carried) payload.dump();
        }

        public boolean blends(int direction){
            return TrainPayloadLoader.blends(this, direction);
        }

        public void updatePayload(){
            if(payload != null){
                payload.set(x + payVector.x, y + payVector.y, payRotation);
            }
        }

        /** @return true if the payload is in position. */
        public boolean moveInPayload(){
            return moveInPayload(true);
        }

        /** @return true if the payload is in position. */
        public boolean moveInPayload(boolean rotate){
            if(payload == null) return false;

            updatePayload();

            if(rotate){
                payRotation = Angles.moveToward(payRotation, block.rotate ? rotdeg() : 90f, payloadRotateSpeed * delta());
            }
            payVector.approach(Vec2.ZERO, payloadSpeed * delta());

            return hasArrived();
        }

        public void moveOutPayload(){
            if(payload == null) return;

            updatePayload();

            Vec2 dest = Tmp.v1.trns(rotdeg(), size * tilesize/2f);

            payRotation = Angles.moveToward(payRotation, rotdeg(), payloadRotateSpeed * delta());
            payVector.approach(dest, payloadSpeed * delta());

            Building front = front();
            boolean canDump = front == null || !front.tile.solid();
            boolean canMove = front != null && (front.block.outputsPayload || front.block.acceptsPayload);

            if(canDump && !canMove){
                pushOutput(payload, 1f - (payVector.dst(dest) / (size * tilesize / 2f)));
            }

            if(payVector.within(dest, 0.001f)){
                payVector.clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);

                if(canMove){
                    if(movePayload(payload)){
                        payload = null;
                    }
                } else if(canDump){
                    dumpPayload();
                }
            }
        }

        public void moveInPayloadFromTrain(RailVehicleUnit train)
        {          
            if(rotate){
                payRotation = Angles.moveToward(payRotation, block.rotate ? rotdeg() : 90f, payloadRotateSpeed * delta());
            }
            payVector.approach(Vec2.ZERO, payloadSpeed * delta());

            Log.info("payload added to the block: " + payload + " " + payVector);

            if(hasArrived()){
                Log.info("payload added to the block: " + payload + " " + payload.size());
                payVector.clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);

                train.speedMultiplier = 1f;
                isPortingPayload = false;
            }
        }

        public void moveOutPayloadToTrain(RailVehicleUnit train){
            if(payload == null) return;

            updatePayload();

            Vec2 dest = Tmp.v1.trns(rotdeg(), size * tilesize/2f);

            payRotation = Angles.moveToward(payRotation, rotdeg(), payloadRotateSpeed * delta());
            payVector.approach(dest, payloadSpeed * delta());

            if(payVector.within(dest, 0.001f)){
                payVector.clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);

                if (train.addPayload(payload))
                {
                    payload = null;
                    train.speedMultiplier = 1f;
                    isPortingPayload = false;
                }
            }
        }

        public void dumpPayload(){
            //translate payload forward slightly
            float tx = Angles.trnsx(payload.rotation(), 0.1f), ty = Angles.trnsy(payload.rotation(), 0.1f);
            payload.set(payload.x() + tx, payload.y() + ty, payload.rotation());

            if(payload.dump()){
                payload = null;
            }else{
                payload.set(payload.x() - tx, payload.y() - ty, payload.rotation());
            }
        }

        public boolean hasArrived(){
            return payVector.isZero(0.01f);
        }

        public void drawPayload(){
            if(payload != null){
                updatePayload();

                Draw.z(Layer.blockOver);
                payload.draw();
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(payVector.x);
            write.f(payVector.y);
            write.f(payRotation);
            Payload.write(payload, write);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            payVector.set(read.f(), read.f());
            payRotation = read.f();
            payload = Payload.read(read);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void updateTile() 
        {
            if(payload != null)
            {
                payload.update(null, this);
            }

            if (!isPortingPayload && payload != null && !isUnloader && !hasArrived())
            {
                moveInPayload();
            }

            Tile rcTile = getRailConnector(tile, team, rotation);
            RailConnectorBuild rc = rcTile != null ? (RailConnectorBuild) rcTile.build : null;

            if (rc != null)
            {
                int dx = (tile.x + T.Rot2Pos(rotation).x * (size / 2 + 1)) * tilesize;
                int dy = (tile.y + T.Rot2Pos(rotation).y * (size / 2 + 1)) * tilesize;

                for (RailVehicleUnit train : getAllTrains(dx, dy, 2f)) 
                {
                    if (!isUnloader)
                    {
                        if (isPortingPayload || hasArrived())
                        {
                            if (payload != null && train.canAcceptPayload(payload)) 
                            {
                                if (!isPortingPayload)
                                {
                                    train.speedMultiplier = 0;
                                    isPortingPayload = true;
                                }

                                moveOutPayloadToTrain(train);
                            }
                        }
                    } 
                    else 
                    {
                        if (payload == null && train.payloads.size > 0)
                        {
                            if (!isPortingPayload)
                            {
                                payload = (P)train.removePayload();
                                handlePayload(rc, payload);
                                train.speedMultiplier = 0;
                                isPortingPayload = true;
                            }
                        }

                        if (isPortingPayload)
                        {
                            moveInPayloadFromTrain(train);
                        }
                    }
                }
            }

            if (payload != null && isUnloader && hasArrived()) {
                if (dumpPayload(payload)) payload = null;
            }
        }

        public Seq<RailVehicleUnit> getAllTrains(int x, int y, float range) {
            Seq<RailVehicleUnit> nearby = new Seq<>();
            Units.nearby(x - range, y - range, range * 2, range * 2, unit -> {
                if (unit instanceof RailVehicleUnit rvu) nearby.add(rvu);
            });
            return nearby;
        }

        @Override
        public void draw() {
            Draw.rect(block.region, x, y, rotation * 90);

            drawPayload();

            Draw.rect(Core.atlas.find(block.name + "-top"), x, y, rotation * 90);
        }
    }
}
