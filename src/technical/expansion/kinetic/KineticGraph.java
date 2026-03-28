package technical.expansion.kinetic;

import arc.Events;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.IntMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType.ResetEvent;
import technical.Fr;
import technical.debug.Debugger.DebugLine;
import technical.expansion.kinetic.KineticBlock.KineticBuild;

public class KineticGraph
{
    private static int lastId = 0;
    public static final IntMap<KineticGraph> graphs = new IntMap<>();

    public long lastTick = -1;

    public int id;
    public Seq<KineticBuild> builds = new Seq<>();
    
    // public float efficiency = 0f;

    public float totalInertia;

    public float targetTorque;
    public float targetSpeed;

    public float currentSpeed;
    public float currentTorque;

    public KineticGraph(float currentSpeed, float currentTorque)
    {
        this.id = lastId++;
        this.currentSpeed = currentSpeed;
        this.currentTorque = currentTorque;
        graphs.put(id, this);
    }

    public KineticGraph(int id, float currentSpeed, float currentTorque)
    {
        this.id = id;
        this.currentSpeed = currentSpeed;
        this.currentTorque = currentTorque;
        graphs.put(id, this);
        lastId = Math.max(lastId, id + 1);
    }

    public static void load()
    {
        Events.on(ResetEvent.class, e -> {
            staticCleanUp();
        });
    }

    public static KineticGraph revive(int id, float curSpeed, float curTorque)
    {
        var g = graphs.get(id);
        if (g == null)
        {
            g = new KineticGraph(id, curSpeed, curTorque);
        }
        return g;
    }

    public static void staticCleanUp() 
    {
        graphs.clear();
        lastId = 0;
    }

    public void add(KineticBuild build)
    {        
        if (build.kinetic.graph() != null) 
        {
            build.kinetic.graph().remove(build);
        }

        builds.add(build);
        build.kinetic.setGraphId(this.id);
    }

    public void remove(KineticBuild build) 
    {
        builds.remove(build);
        build.kinetic.setGraphId(-1);
        
        if (builds.isEmpty())
        {
            destroy();
        }
    }

    public void destroy() 
    {
        graphs.remove(id);
        builds.clear();
    }

    public KineticGraph mergeWith(KineticGraph other) 
    {
        if (other == this || other == null) return this;

        if (other.builds.size > this.builds.size) 
        {
            return other.mergeWith(this);
        }

        for (var build : other.builds) 
        {
            build.kinetic.setGraphId(this.id);
            this.builds.add(build);
        }

        other.builds.clear();
        other.destroy();
        
        return this;
    }

    public void splitCheck(KineticBuild removedBlock) 
    {
        if (builds.isEmpty()) return;

        // Get neighbors of the removed block that are IN THIS GRAPH
        Seq<KineticBuild> neighbors = new Seq<>();
        for (var p : removedBlock.proximity()) 
        {
            if (p.isValid() && p instanceof KineticBuild kb && kb.kinetic != null && kb.kinetic.graph() == this) 
            {
                neighbors.add(kb);
            }
        }

        if (neighbors.size <= 1) return;
        
        destroy();

        for (var kb : neighbors)
        {
            if (kb.kinetic.graph() == null) 
            {
                rebuildFloodFill(kb);
            }
        }
    }

    private void rebuildFloodFill(KineticBuild start) 
    {
        KineticGraph newGraph = new KineticGraph(currentSpeed, currentTorque);
        
        Seq<KineticBuild> queue = new Seq<>();
        ObjectSet<KineticBuild> visited = new ObjectSet<>();
        
        queue.add(start);
        visited.add(start);
        
        while (!queue.isEmpty()) 
        {
            var cur = queue.pop();
            newGraph.add(cur);

            for (var neighbor : cur.proximity()) 
            {
                if (neighbor.isValid() && neighbor instanceof KineticBuild kb && kb.kinetic != null && kb.kinetic != null) 
                {
                    if (kb.kinetic.graph() == null && !visited.contains(kb)) 
                    {
                        visited.add(kb);
                        queue.add(kb);
                    }
                }
            }
        }
    }

    public void update() 
    {
        long currentTick = Math.round(Vars.state.tick);
        if (lastTick == currentTick) return;
        lastTick = currentTick;

        totalInertia = 0;
        targetSpeed = 0;
        targetTorque = 0;

        for (var b : builds)
        {
            totalInertia += b.kinetic.data.inertia;
            
            if (b.efficiency > 0 && b.kinetic.data.isOutput) 
            {
                targetSpeed += b.kinetic.data.output.speed * b.efficiencyScale();
                targetTorque += b.kinetic.data.output.torque * b.efficiencyScale();
            }

            if (b.kinetic.data.isInput && b.kinetic.data.input(b) != null)
            {
                targetSpeed -= b.kinetic.data.input(b).speed * b.efficiencyScale();
                targetTorque -= b.kinetic.data.input(b).torque * b.efficiencyScale();
            }

            DebugLine.point(new Vec2(b.x, b.y)).color(id).draw();
        }
        
        approachSpeedTorque();
    }

    public void approachSpeedTorque()
    {
        float k = Time.delta / totalInertia;
        k = Mathf.clamp(k, 0f, 1f);

        // first-order inertial response
        currentSpeed  += (targetSpeed  - currentSpeed)  * k;
        currentTorque += (targetTorque - currentTorque) * k;

        // kill denormals
        if (currentSpeed < 0) currentSpeed  = 0f;
        if (currentTorque < 0) currentTorque = 0f;
    }

    public float currentSpeed()
    {
        return currentSpeed;
    }

    public float currentTorque()
    {
        return currentTorque;
    }

    public float currentEfficiency()
    {
        return Math.max(0, Math.min(currentSpeed / (targetSpeed != 0 ? targetSpeed : 0.0001f), currentTorque / (targetTorque != 0 ? targetTorque : 0.0001f)));
    }
}