package technical.core.kinetic;

public class KineticComponent 
{
    public KineticComponentData data;
    private int graphId = -1;

    public KineticComponent(KineticComponentData data) 
    {
        this.data = data;
    }

    public void update() 
    {
        KineticGraph g = graph();
        if (g != null) 
        {
            g.update();
        }
    }

    public KineticGraph graph() 
    {
        if (graphId == -1) return null;

        var graph = KineticGraph.graphs.get(graphId);

        if (graph == null) graphId = -1;

        return graph;
    }

    public void setGraphId(int id) 
    {
        this.graphId = id;
    }
    
    @Override
    public String toString()
    {
        return "data=[" + //input=" + (data.isInput ? "{speed=" + data.input().speed + ",torque=" + data.input().torque + "}" : "null") + 
                    "output=" + (data.isOutput ? "{speed=" + data.output.speed + ",torque=" + data.output.torque + "}" : "null") + 
                    ",interia=" + data.inertia + "]" + 
                    ",graph.id=" + graphId;
    }
}