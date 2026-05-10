package technical.core.kinetic;


public class KineticEnergy 
{
    public float speed;
    public float torque;

    public KineticEnergy()
    {
        speed = 0;
        torque = 0;
    }

    public KineticEnergy(float _speed, float _torque)
    {
        speed = _speed;
        torque = _torque;
    }

    public float power()
    {
        return speed * torque;
    }

    public float curPower(float efficiency)
    {
        return curSpeed(efficiency) * curTorque(efficiency);
    }

    public float curTorque(float efficiency)
    {
        return torque * efficiency;
    }

    public float curSpeed(float efficiency)
    {
        return speed * efficiency;
    }

    @Override
    public String toString()
    {
        return "KineticEnergy#" + System.identityHashCode(this) + "{speed=" + speed + ",torque=" + torque + "}";
    }
}

/*

    public float inertia;
    /**
     * only 0 - 1 values,
     * added with inertia speed when machine starts running.
     */
    // public float efficiency; */