package technical.core.train;

import java.util.List;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.ObjectFloatMap;
import arc.struct.ObjectIntMap;
import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.UnitEntity;
import mindustry.type.Item;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.modules.ItemModule;
import technical.core.train.RailConnector.RailConnectorBuild;
import arc.util.*;

public class RailVehicleUnit extends UnitEntity 
{
    public RailConnectorBuild railConnector;
    public List<RailConnectorBuild> path;
    public float t = 0f;
    public float speedMultiplier = 1f;
    public int direction = 1;
    public Vec2 lastPos = null;
    public boolean isClosedLoop = false;
    public ItemModule items = new ItemModule();
    public ObjectFloatMap<UnlockableContent> iconTimes = new ObjectFloatMap<>();
    public float currentConsumeTime = 0;

    public Seq<Payload> payloads = new Seq<>();
    public ObjectIntMap<UnlockableContent> payloadContentCounts = new ObjectIntMap<>();

    public RailVehicleUnit() {}

    public static RailVehicleUnit create() 
    {
        return new RailVehicleUnit();
    }
    
    public void init(RailConnectorBuild rc) 
    {
        if (rc == null) return;
        railConnector = rc;

        if (!(type instanceof RailVehicle)) return;
        RailVehicle trainType = (RailVehicle) type;

        trainType.buildPath(this);
        if (path == null || path.size() < 2) return;

        // Calculate segment lengths
        float[] segmentLengths = trainType.calculateSegmentLengths(this);

        // Find index of the connector in the path
        int index = path.indexOf(railConnector);
        if (index == -1) index = 0; // fallback to start

        // Calculate accumulated length up to this node
        float accumulated = 0f;
        for (int i = 0; i < index; i++) {
            accumulated += segmentLengths[i];
        }

        t = accumulated;
    }

    public int acceptedItemAmount(Item item, int amount)
    {
        int capacity = ((RailVehicle)type).totalItemCapaity;
        amount = Mathf.clamp(amount, 0, capacity - items.total());

        return amount;
    }

    public boolean canAcceptPayload(Payload payload)
    {
        if (payload == null) return false;
        
        int totalSize = 0;

        for (Payload p : payloads)
        {
            totalSize += p.size();
        }

        if (totalSize + payload.size() > ((RailVehicle)type).totalPayloadCapacity) return false;

        return true;
    }

    public boolean addPayload(Payload payload)
    {
        if (payload == null) return false;
        if (!canAcceptPayload(payload)) return false;

        Log.info("payload added to the train: " + payload + " " + payload.size());

        // if (payloads.contains(payload)) return false;

        payloads.add(payload);
        payloadContentCounts.put(payload.content(), payloadContentCounts.get(payload.content(), 0) + 1);
        
        iconTimes.put(payload.content(), iconTimes.get(payload.content(), 0f) + 1f);

        return true;
    }

    public Payload removePayload()
    {
        Payload payload = payloads.remove(payloads.size - 1);

        Log.info("payload removed from the train: " + payload + " " + payload.size());

        payloads.remove(payload);
        int count = payloadContentCounts.get(payload.content(), 0) - 1;
        if (count <= 0) 
            payloadContentCounts.remove(payload.content());
        else
            payloadContentCounts.put(payload.content(), count);
        
        iconTimes.put(payload.content(), iconTimes.get(payload.content(), 0f) + 1f);

        return payload;
    }
}