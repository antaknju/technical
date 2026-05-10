package technical.util;

// Used for var scaling 
// to get X power / sec use X * Fr.power
// EXCEPTION: liquidCapacity should not be multiplied by Fr.liquid (Systematic reasons)

public class Fr 
{
    public static final float 
        time = 60f,
        health = 0.1f,

        item = 1f,
        liquid = 1/60f,
        power = 1/60f,

        speed = 0.005f,
        angularSpeed = speed,
        torque = speed,
        inertia = 1f;
}