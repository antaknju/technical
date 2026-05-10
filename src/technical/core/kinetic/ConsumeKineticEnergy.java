package technical.core.kinetic;

import arc.scene.ui.layout.Table;
import arc.util.Strings;
import mindustry.gen.Building;
import mindustry.ui.ReqImage;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;
import technical.util.Fr;
import technical.content.TIcons;
import technical.core.kinetic.KineticBlock.KineticBuild;

public class ConsumeKineticEnergy extends Consume
{
    public KineticEnergy energy;

    public ConsumeKineticEnergy(KineticEnergy energy)
    {
        this.energy = energy;
    }

    @Override
    public void apply(Block block)
    {
        if (block instanceof KineticBlock kblock)
        {
            kblock.kineticData.isInput = true;
            kblock.kineticData.input = (KineticBuild b) -> energy;
        }
    }

    @Override
    public void build(Building build, Table table)
    {
        table.table(t -> {
            t.add(new ReqImage(TIcons.speed, 
                () -> build instanceof KineticBuild kb && kb.kinetic != null && kb.kinetic.graph() != null && kb.kinetic.graph().currentSpeed >= energy.speed
            )).size(24f).pad(6f);
            t.label(() -> Strings.fixed(energy.speed / Fr.speed, 2)).padRight(16f);
            
            t.add(new ReqImage(TIcons.torque, 
                () -> build instanceof KineticBuild kb && kb.kinetic != null && kb.kinetic.graph() != null && kb.kinetic.graph().currentTorque >= energy.torque
            )).size(24f).pad(6f);
            t.label(() -> Strings.fixed(energy.torque / Fr.torque, 2));
        }).left();
    }

    @Override
    public void trigger(Building build)
    {
        
    }

    @Override
    public float efficiency(Building build)
    {
        return build instanceof KineticBuild kb && kb.kinetic != null && kb.kinetic.graph() != null && 
               kb.kinetic.graph().currentSpeed >= energy.speed && kb.kinetic.graph().currentTorque >= energy.torque ? 1f : 0f ;
    }

    @Override
    public void display(Stats stats)
    {
        stats.add(Stat.input, t -> {
            t.image(TIcons.speed).size(24f).pad(6f);
            t.label(() -> Strings.fixed(energy.speed / Fr.angularSpeed, 2)).padRight(16f);
            
            t.image(TIcons.torque).size(24f).pad(6f);
            t.label(() -> Strings.fixed(energy.torque / Fr.torque, 2));
        });
    }
}
