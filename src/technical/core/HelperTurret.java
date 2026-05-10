package technical.core;

import mindustry.entities.Units;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class HelperTurret extends ItemTurret 
{
    public HelperTurret(String name)
    {
        super(name);

        recoil = 5f;

        targetAir = true;
        targetGround = true;

        targetBlocks = false;
        targetHealing = true;
    }

    public class HealingTurretBuild extends ItemTurretBuild 
    {
        @Override
        public void findTarget()
        {
            target = Units.closest(team, x, y, range, u -> u.health < u.maxHealth());
        }

        @Override
        public boolean validateTarget()
        {
            return target != null && target.within(x, y, range);
        }
    }
}
