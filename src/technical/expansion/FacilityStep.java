package technical.expansion;

public class FacilityStep
{
    public enum FacilityStepType
    {
        Preparing,

        WarmingUp,

        Welding,
        Polishing,
        Injecting,
        Cooling,
        Lubrication,
        Cutting,

        Exporting,
    }

    public FacilityStepType type;
    public int workers;
    public float time;

    public FacilityStep(FacilityStepType type, int workers, float time)
    {
        this.type = type;
        this.workers = workers;
        this.time = time;
    }   
}