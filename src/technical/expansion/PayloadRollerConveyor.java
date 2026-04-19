package technical.expansion;

import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.ctype.Content;
import mindustry.entities.*;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.meta.*;
import technical.utility.T;

import static mindustry.Vars.*;
public class PayloadRollerConveyor extends TBlock
{
    public float moveTime = 45f;
    public Interp interp = Interp.linear;
    public float payloadLimit = 3f;
    public boolean pushUnits = true;

    public TextureRegion[] rollerRegions;
    public TextureRegion capRegion;
    public int frameCount = -1;

    public PayloadRollerConveyor(String name)
    {
        super(name);

        solid = false;
        group = BlockGroup.payloads;
        size = 3;
        rotate = true;
        update = true;
        outputsPayload = true;
        noUpdateDisabled = true;
        acceptsUnitPayloads = true;
        priority = TargetPriority.transport;
        envEnabled |= Env.space | Env.underwater;
        sync = true;
        underBullets = true;
    }

    @Override
    public void load()
    {
        super.load();

        rollerRegions = T.loadMultipleRegions(name);
        frameCount = rollerRegions.length;
        capRegion = Core.atlas.find(name + "-cap");
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{rollerRegions[0]};
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(rollerRegions[0], plan.drawx(), plan.drawy(), plan.rotation * 90f);
        Draw.rect(capRegion, plan.drawx(), plan.drawy(), plan.rotation * 90f);
        Draw.rect(capRegion, plan.drawx(), plan.drawy(), plan.rotation * 90f + 180f);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        int ntrns = size;
        for (int i = 0; i < 4; i++) {
            Tile tile = world.tile(x + Geometry.d4x[i] * ntrns, y + Geometry.d4y[i] * ntrns);
            if (tile != null && tile.build != null && tile.isCenter() && tile.build.block.outputsPayload
                    && tile.build.block.size == size
                    && (i == rotation || tile.block().rotate && i == (tile.build.rotation + 2) % 4)) {
                Drawf.selected(tile.x, tile.y, tile.block(), tile.build.team.color);
            }
        }
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.payloadCapacity, StatValues.squared(payloadLimit, StatUnit.blocksSquared));
    }

    @Override
    public void init() {
        super.init();
        clipSize = Math.max(clipSize, size * tilesize * 2.1f);
    }

    public class PayloadConveyorBuild extends Building
    {
        public @Nullable Payload item;
        public float progress, itemRotation, animation;
        public float curInterp, lastInterp;
        public @Nullable Building next;
        public boolean blocked;
        public int step = -1, stepAccepted = -1;

        public TextureRegion currentRollerRegion() {
            int frame = enabled ? (int)(progress / moveTime * frameCount) % frameCount : 0;
            return rollerRegions[frame];
        }

        @Override
        public boolean canControlSelect(Unit unit) {
            return this.item == null && unit.type.allowedInPayloads && !unit.spawnedByCore
                    && unit.hitSize / tilesize <= payloadLimit
                    && unit.tileOn() != null && unit.tileOn().build == this;
        }

        @Override
        public void onControlSelect(Unit player) {
            handleUnitPayload(player, p -> item = p);
        }

        @Override
        public Payload takePayload() {
            Payload t = item;
            item = null;
            return t;
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            Building accept = nearby(Geometry.d4(rotation).x * (size / 2 + 1), Geometry.d4(rotation).y * (size / 2 + 1));
            if (accept != null && (
                    (accept.block.size == size
                            && tileX() + Geometry.d4(rotation).x * size == accept.tileX()
                            && tileY() + Geometry.d4(rotation).y * size == accept.tileY()) ||
                            (accept.block.size > size && (rotation % 2 == 0
                                    ? Math.abs(accept.y - y) <= (accept.block.size * tilesize - size * tilesize) / 2f
                                    : Math.abs(accept.x - x) <= (accept.block.size * tilesize - size * tilesize) / 2f)))) {
                next = accept;
            } else {
                next = null;
            }
            checkBlocked();
        }

        void checkBlocked()
        {
            int ntrns = 1 + size / 2;
            Tile next = tile.nearby(Geometry.d4(rotation).x * ntrns, Geometry.d4(rotation).y * ntrns);
            blocked = (next != null && next.solid() && !(next.block().outputsPayload || next.block().acceptsPayload))
                    || (this.next != null && this.next.payloadCheck(rotation));
        }

        @Override
        public Payload getPayload() {
            return item;
        }

        @Override
        public void updateTile() {
            if (!enabled) return;

            if (item != null) item.update(null, this);

            lastInterp = curInterp;
            curInterp = fract();
            if (lastInterp > curInterp) lastInterp = 0f;
            progress = time() % moveTime;

            updatePayload();
            if (item != null && next == null) {
                TPayloadBlock.pushOutput(item, progress / moveTime);
            }

            int curStep = curStep();
            if (curStep > step) {
                boolean valid = step != -1;
                step = curStep;
                boolean had = item != null;

                if (valid && stepAccepted != curStep && item != null) {
                    checkBlocked();
                    if (next != null) {
                        next.updateTile();
                        if (next != null && next.acceptPayload(this, item)) {
                            next.handlePayload(this, item);
                            item = null;
                            moved();
                        }
                    } else if (!blocked) {
                        if (item.dump()) {
                            item = null;
                            moved();
                        }
                    }
                }

                if (had && item != null) moveFailed();
            }
        }

        public void moveFailed() {}
        public void moved() {}

        public void drawBottom() {
            super.draw();
        }

        @Override
        public void onDestroyed() {
            if (item != null) item.destroyed();
            super.onDestroyed();
        }

        @Override
        public void draw()
        {
            Draw.z(Layer.block - 0.2f);

            Draw.rect(currentRollerRegion(), x, y, rotdeg());


            Draw.z(Layer.block - 0.3f);

            Draw.rect(capRegion, x, y, rotdeg());
            Draw.rect(capRegion, x, y, rotdeg() + 180f);

            Draw.z(Layer.blockOver);
            if (item != null)
            {
                item.draw();
            }
        }

        @Override
        public void payloadDraw() {
            Draw.rect(block.fullIcon, x, y);
        }

        public float time() {
            return Time.time;
        }

        @Override
        public void unitOn(Unit unit)
        {
            if (!pushUnits || !enabled || lastInterp == 0f) return;

            float delta = (curInterp - lastInterp) * size * tilesize;
            Tmp.v1.trns(rotdeg(), delta);
            unit.move(Tmp.v1.x, Tmp.v1.y);
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            return this.item == null
                    && payload.fits(payloadLimit)
                    && (source == this || this.enabled && progress <= 5f);
        }

        @Override
        public void handlePayload(Building source, Payload payload) {
            this.item = payload;
            this.stepAccepted = curStep();
            this.itemRotation = source == this ? rotdeg() : source.angleTo(this);
            this.animation = 0;
            updatePayload();
        }

        @Override
        public double sense(Content content) {
            if (item instanceof UnitPayload up && up.unit.type == content) return 1;
            if (item instanceof BuildPayload bp && bp.build.block == content) return 1;
            return super.sense(content);
        }

        @Override
        public void onRemoved() {
            super.onRemoved();
            if (item != null) item.dump();
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(progress);
            write.f(itemRotation);
            Payload.write(item, write);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            read.f();
            itemRotation = read.f();
            item = Payload.read(read);
        }

        public void updatePayload()
        {
            if (item != null) {
                if (animation > fract()) {
                    animation = Mathf.lerp(animation, 0.8f, 0.15f);
                }
                animation = Math.max(animation, fract());

                float fract = animation;
                float rot = Mathf.slerp(itemRotation, rotdeg(), fract);

                if (fract < 0.5f) {
                    Tmp.v1.trns(itemRotation + 180, (0.5f - fract) * tilesize * size);
                } else {
                    Tmp.v1.trns(rotdeg(), (fract - 0.5f) * tilesize * size);
                }

                item.set(x + Tmp.v1.x, y + Tmp.v1.y, rot);
            }
        }

        public int curStep()
        {
            return (int)(time() / moveTime);
        }

        public float fract()
        {
            return interp.apply(progress / moveTime);
        }
    }
}