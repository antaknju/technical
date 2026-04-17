package technical.expansion.trap;

import arc.Core;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import technical.expansion.TBlock;
import technical.expansion.kinetic.KineticBlock;
import technical.expansion.tech.TechStat;

public class TrapBlock extends KineticBlock
{
    public float cooldownTime = 2 * 60f;

    public TrapBlock(String name)
    {
        super(name);
        solid = true;

        update = true;
    }

    public float cooldownTime()
    {
        return cooldownTime * getTotalStat(TechStat.cooldown);
    }

    @Override
    public void setBars()
    {
        super.setBars();

        addBar("ready", (TrapBlockBuild build) ->
            new Bar(
                () -> Core.bundle.format(build.cooldownTimer >= cooldownTime() && build.canTrap() ? "bar.ready" : "bar.not-ready"),
                () -> Pal.ammo,
                () -> build.cooldownTimer / cooldownTime()
            )
        );
    }

    public class TrapBlockBuild extends KineticBuild
    {
        public float cooldownTimer;

        @Override
        public boolean shouldConsume()
        {
            return super.shouldConsume() && canTrap();
        }

        @Override
        public void updateTile() 
        {
            if (efficiency <= 0) return;

            if(cooldownTimer < cooldownTime())
                cooldownTimer += Time.delta;
            else
                cooldownTimer = cooldownTime();
        }

        public boolean trap()
        {
            if(cooldownTimer < cooldownTime() || !canTrap()) return false;

            onTrap();

            consume();

            cooldownTimer = 0;

            return true;
        }

        public void onTrap()
        {
            
        }

        public boolean canTrap()
        {
            return true;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            
            write.f(cooldownTimer);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            cooldownTimer = read.f();
        }
    }
}
