package technical.expansion.kinetic;

import arc.func.Func;
import technical.expansion.kinetic.KineticBlock.KineticBuild;

public class KineticComponentData 
{
    public Func<KineticBuild, KineticEnergy> input;
    public KineticEnergy output;

    public float inertia;

    public boolean isInput = false;
    public boolean isOutput = false;

    public KineticComponentData(KineticEnergy output, float inertia)
    {
        this.output = output;
        this.inertia = inertia;

        isOutput = output != null;
    }

    public KineticEnergy input(KineticBuild kbuild)
    {
        return input.get(kbuild);
    }
}
