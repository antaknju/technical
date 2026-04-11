package technical.expansion.kinetic;

import arc.func.Func;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import mindustry.gen.Building;
import mindustry.ui.ReqImage;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import technical.utility.Fr;
import technical.content.TIcons;
import technical.expansion.kinetic.KineticBlock.KineticBuild;

public class ConsumeKineticDynamic extends Consume
{
    public final Func<KineticBuild, KineticEnergy> energy;

    @SuppressWarnings("unchecked")
    public <T extends KineticBuild> ConsumeKineticDynamic(Func<T, KineticEnergy> energy)
    {
        this.energy = (Func<KineticBuild, KineticEnergy>)energy;
    }

    @Override
    public void apply(Block block)
    {
        if (block instanceof KineticBlock kblock)
        {
            if (kblock.kineticData == null)
            {
                kblock.kineticData = new KineticComponentData(null, 30 * Fr.inertia);
            }

            kblock.kineticData.isInput = true;
            kblock.kineticData.input = energy;
        }
    }

    @Override
    public void build(Building build, Table table)
    {
        if (!(build instanceof KineticBuild kbuild)) return;

        var energy = this.energy.get(kbuild);
        if (energy == null) return;

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
        if (!(build instanceof KineticBuild kbuild)) return 1;

        var energy = this.energy.get(kbuild);
        if (energy == null) return 1;

        return build instanceof KineticBuild kb && kb.kinetic != null && kb.kinetic.graph() != null &&
               kb.kinetic.graph().currentSpeed >= energy.speed && kb.kinetic.graph().currentTorque >= energy.torque ? 1f : 0f ;
    }
}
