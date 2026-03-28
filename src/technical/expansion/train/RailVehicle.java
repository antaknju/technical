package technical.expansion.train;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Scl;
import arc.struct.ObjectFloatMap;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
// import mindustry.ctype.Object;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.ui.Fonts;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import technical.content.TFx;
import technical.expansion.train.RailConnector.RailConnectorBuild;

// import static mindustry.Vars.content;
import static mindustry.Vars.renderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RailVehicle extends UnitType
{
    public int totalItemCapaity = 100;
    public int totalPayloadCapacity = 20;
    public float itemVisualSize = 5;
    public int itemVisualAmount = 3;
    public float speed = 1f;

    public @Nullable Item consumedItem = null;
    public float consumedItemDuration = 10f;
    public Effect consumeEffect = TFx.coalSmelt;

    public RailVehicle(String name) {
        super(name);
        constructor = RailVehicleUnit::create;
        drawItems = false; // We won't use generic item drawer

        stats.useCategories = true;
    }

    public void setStats() 
    {
        super.setStats();

        StatCat cat = new StatCat("rail-vehicle");

        Stat itemCap = new Stat("total-item-cap", cat);
        Stat payCap = new Stat("total-payload-cap", cat);

        stats.add(itemCap, table -> {
            table.add("[accent]" + totalItemCapaity + "[]");
        });

        stats.add(payCap, table -> {
            table.add("[accent]" + totalPayloadCapacity + "[]");
        });
    }

    public Seq<UnlockableContent> maxIconTimes(RailVehicleUnit train) 
    {
        Seq<UnlockableContent> sorted = new Seq<>();

        for (ObjectFloatMap.Entry<UnlockableContent> entry : train.iconTimes) 
        {
            sorted.add(entry.key);
        }

        // sort descending by icon time
        sorted.sort((a, b) -> Float.compare(train.iconTimes.get(b, 0f), train.iconTimes.get(a, 0f)));

        Seq<UnlockableContent> top = new Seq<>();
        for (int i = 0; i < Math.min(itemVisualAmount, sorted.size); i++) {
            top.add(sorted.get(i));
        }

        return top;
    }

    @Override
    public void draw(Unit unit)
    {
        super.draw(unit);

        RailVehicleUnit train = (RailVehicleUnit)unit;

        int i = 0;
        Seq<UnlockableContent> mx = maxIconTimes(train);
        for (UnlockableContent item : mx) {
            float offset = (i - (mx.size - 1) / 2f) * itemVisualSize * 2f;
            float size = (itemVisualSize + Mathf.absin(Time.time, 5f, 1f)) * train.iconTimes.get(item, 0f);

            Draw.mixcol(Pal.accent, Mathf.absin(Time.time, 5f, 0.1f));

            TextureRegion reg = new TextureRegion();
            reg = item.fullIcon;

            Draw.rect(reg,
                unit.x + Angles.trnsx(unit.rotation + 180f, offset),
                unit.y + Angles.trnsy(unit.rotation + 180f, offset),
                size, size, 0);
            Draw.mixcol();

            size = ((3f + Mathf.absin(Time.time, 5f, 1f)) * train.iconTimes.get(item, 0f) + 0.5f) * 2;
            Draw.color(Pal.accent);
            Draw.rect(itemCircleRegion,
                unit.x + Angles.trnsx(unit.rotation + 180f, offset),
                unit.y + Angles.trnsy(unit.rotation + 180f, offset),
                size, size);

            if (!renderer.pixelator.enabled()) {
                int count = 0;
                if (item instanceof Item it)
                    count = train.items.get(it);
                else
                    count = train.payloadContentCounts.get(item);

                Fonts.outline.draw(count + "",
                    unit.x + Angles.trnsx(unit.rotation + 180f, offset),
                    unit.y + Angles.trnsy(unit.rotation + 180f, offset) - 3,
                    Pal.accent, 0.25f * train.iconTimes.get(item, 0f) / Scl.scl(1f) + 0.01f, false, Align.center
                );
            }

            Draw.reset();
            i++;
        }
    }

    @Override
    public void update(Unit unit) 
    {
        if (!(unit instanceof RailVehicleUnit)) {
            Log.info("Destroying old train");
            unit.remove();
            return;
        }

        RailVehicleUnit train = (RailVehicleUnit) unit;

        if (train.railConnector == null || !train.railConnector.isValid()) {
            Log.info("No rail connector No train");
            train.remove();
            Fx.unitDespawn.at(train.x, train.y, train.rotation, train);
            return;
        }

        
        float dt = Time.delta / 60f;

        boolean canRun = true;
        if (consumedItem != null)
        {
            if (train.currentConsumeTime > 0)
            {
                train.currentConsumeTime = Math.max(train.currentConsumeTime - dt, 0);
            }
            else
            {
                if (train.items.get(consumedItem) > 0)
                {
                    train.items.remove(consumedItem, 1);
                    train.currentConsumeTime = consumedItemDuration;
                    consumeEffect.at(train.x, train.y);
                }
                else
                {
                    canRun = false;
                }
            }
        }

        updateMovement(train, canRun);

        float itemSpeed = dt * 4f;

        for (ObjectFloatMap.Entry<UnlockableContent> entry : train.iconTimes) {
            boolean hasObject = false;

            if (entry.key instanceof Item it)
            {
                hasObject = train.items.get(it) > 0;
            }
            else
            {
                hasObject = train.payloadContentCounts.get(entry.key, 0) > 0;
            }

            float direction = hasObject ? 1f : -1f;
            float value = entry.value;

            value = Mathf.clamp(value + itemSpeed * direction);

            train.iconTimes.put(entry.key, value);

            if (!hasObject && value <= 0)
                train.iconTimes.remove(entry.key, 0);
        }
    }

    public void updateMovement(RailVehicleUnit train, boolean canRun)
    {
        if (speed * train.speedMultiplier <= 0.01f) return;

        // Build Path
        buildPath(train);

        // Calculate Segment Lengths
        float[] segmentLengths = calculateSegmentLengths(train);
        float totalLength = 0;
        for (float f : segmentLengths) totalLength += f;

        float offset = 0.01f;
        Vec2 pos = getPosition(train, 0f);
        Vec2 nextPos = getPosition(train, offset);

        float dt = Time.delta / 60f;

        float dx = nextPos.x - pos.x;
        float dy = nextPos.y - pos.y;
        float curveSpeed = (float)Math.sqrt(dx * dx + dy * dy);

        // float minCurveSpeed = segmentLengths[segmentIndex] * 0.1f;
        float step = 0;
        if (canRun)
        {
            step = (speed * dt * train.speedMultiplier) / Math.max(curveSpeed, 0.001f);

            step = Math.max(step, 1);
        }

        if (train.isClosedLoop) {
            train.t = (train.t + step * train.direction) % totalLength;
            if (train.t < 0) train.t += totalLength;
        } else {
            train.t += step * train.direction;
            if (train.t >= totalLength) {
                train.t = totalLength;
                train.direction = -1;
            }
            if (train.t <= 0) {
                train.t = 0;
                train.direction = 1;
            }
        }

        // Update unit position and rotation
        pos = getPosition(train, 0f);
        train.set(pos.x, pos.y);

        nextPos = getPosition(train, 0.1f * train.direction);
        dx = nextPos.x - pos.x;
        dy = nextPos.y - pos.y;
        train.rotation = (float)Math.toDegrees(Math.atan2(dy, dx));
    }

    public void buildPath(RailVehicleUnit train)
    {
        Set<RailConnectorBuild> visited = new HashSet<>();
        List<RailConnectorBuild> fullPath = new ArrayList<>();

        RailSplineManager.buildPath(train.railConnector, visited, fullPath);

        train.isClosedLoop = RailSplineManager.isClosedLoop(fullPath);
        train.path = fullPath;
    }

    public float[] calculateSegmentLengths(RailVehicleUnit train) {
        int pathSize = train.path.size();
        int segmentsCount = train.isClosedLoop ? pathSize : pathSize - 1;
        float[] segmentLengths = new float[segmentsCount];

        for (int i = 0; i < segmentsCount; i++) {
            RailConnectorBuild a = train.path.get(i);
            RailConnectorBuild b = train.path.get((i + 1) % pathSize);

            float dx = b.x - a.x;
            float dy = b.y - a.y;
            segmentLengths[i] = (float) Math.sqrt(dx * dx + dy * dy);
        }

        return segmentLengths;
    }

    public Vec2 getPosition(RailVehicleUnit train, float offset) 
    {
        if (train.path == null || train.path.size() < 2) return new Vec2(train.railConnector.x, train.railConnector.y);

        float targetT = train.t + offset;

        float[] segmentLengths = calculateSegmentLengths(train);

        // Determine which segment we're on
        int segmentIndex = 0;
        float accumulated = 0f;
        while (segmentIndex < segmentLengths.length) {
            if (accumulated + segmentLengths[segmentIndex] >= targetT) break;
            accumulated += segmentLengths[segmentIndex];
            segmentIndex++;
        }

        if (segmentIndex >= segmentLengths.length) segmentIndex = segmentLengths.length - 1;

        float localT = (targetT - accumulated) / Math.max(segmentLengths[segmentIndex], 0.001f);

        int pathSize = train.path.size();
        int i1 = segmentIndex;
        int i2 = i1 + 1;
        int i0, i3;

        if (train.isClosedLoop) {
            // Closed loop: wrap indices
            i0 = (i1 - 1 + pathSize) % pathSize;
            i2 = i2 % pathSize;
            i3 = (i2 + 1) % pathSize;
        } else {
            // Open loop: clamp endpoints
            i0 = Math.max(i1 - 1, 0);
            i2 = Math.min(i2, pathSize - 1);
            i3 = Math.min(i1 + 2, pathSize - 1);
        }

        // Get node positions
        float x0 = train.path.get(i0).x, y0 = train.path.get(i0).y;
        float x1 = train.path.get(i1).x, y1 = train.path.get(i1).y;
        float x2 = train.path.get(i2).x, y2 = train.path.get(i2).y;
        float x3 = train.path.get(i3).x, y3 = train.path.get(i3).y;

        // Catmull-Rom interpolation
        float px = RailSplineManager.catmullRom(x0, x1, x2, x3, localT);
        float py = RailSplineManager.catmullRom(y0, y1, y2, y3, localT);

        return new Vec2(px, py);
    }
}
