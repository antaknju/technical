package technical.core.kinetic;

import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import technical.core.TBlock;
import technical.util.TDraw;

public class KineticBlock extends TBlock 
{
    public KineticComponentData kineticData = null;

    public KineticBlock(String name)
    {
        super(name);
    }

    private ConsumeKineticEnergy consumeKineticEnergy(KineticEnergy energy){
        return consume(new ConsumeKineticEnergy(energy));
    }

    public ConsumeKineticEnergy consumeKineticEnergy(float speed, float torque)
    {
        return consumeKineticEnergy(new KineticEnergy(speed, torque));
    }

    public ConsumeKineticEnergy consumeKineticEnergy(float speed, float torque, float inertia)
    {
        if (kineticData == null)
            kineticData = new KineticComponentData(null, inertia);
        return consumeKineticEnergy(new KineticEnergy(speed, torque));
    }

    public class KineticBuild extends TBuild 
    {
        public KineticComponent kinetic = null;

        @Override
        public Building create(Block block, Team team) 
        {
            var build = super.create(block, team);
            if (kineticData != null && block instanceof KineticBlock kblock && build instanceof KineticBuild kbuild) 
            {
                kbuild.kinetic = new KineticComponent(kblock.kineticData);
                return kbuild;
            }
            return build;
        }

        public Seq<Building> proximity()
        {
            return proximity;
        }

        @Override
        public void placed() 
        {
            super.placed();

            if (Vars.net.client() || kinetic == null) return;

            KineticGraph targetGraph = null;
            for (var other : proximity()) 
            {
                if (other.isValid() && other instanceof KineticBuild kb && kb.kinetic != null && kb.kinetic.graph() != null) 
                {
                    if (targetGraph == null) 
                    {
                        targetGraph = kb.kinetic.graph();
                    } 
                    else if (targetGraph != kb.kinetic.graph()) 
                    {
                        targetGraph = targetGraph.mergeWith(kb.kinetic.graph());
                    }
                }
            }

            if (targetGraph == null) 
            {
                targetGraph = new KineticGraph(0, 0);
            }
            
            targetGraph.add(this);
        }

        @Override
        public void drawSelect()
        {
            if (kinetic != null && kinetic.graph() != null)
            {
                for (Building other : kinetic.graph().builds) 
                {
                    TDraw.highlight(other, (other != this ? Pal.place : Pal.accent));
                }
            }
        }

        @Override
        public void onRemoved() 
        {
            super.onRemoved();

            if (kinetic != null)
            {
                var graph = kinetic.graph();

                if (!Vars.net.client() && graph != null) 
                {
                    graph.remove(this);

                    graph.splitCheck(this); 
                }
            }
        }

        @Override
        public void updateTile() 
        {
            super.updateTile();
            
            if (kinetic != null) 
                kinetic.update();
        }

        public float systemSpeedBalance()
        {
            return kinetic != null && kinetic.graph() != null ? kinetic.graph().targetSpeed() : 0;
        }

        public float systemTorqueBalance()
        {
            return kinetic != null && kinetic.graph() != null ? kinetic.graph().targetTorque() : 0;
        }

        public float systemEfficiency()
        {
            return kinetic != null && kinetic.graph() != null ? kinetic.graph().currentEfficiency() : 0;
        }

        public float systemInertia()
        {
            return kinetic != null && kinetic.graph() != null ? kinetic.graph().totalInertia : 0;
        }

        @Override
        public void write(Writes write)
        {
            super.write(write);

            if (kinetic == null)
            {
                write.i(-1);
                return;
            }

            var g = kinetic.graph();
            write.i(g != null ? g.id : -1);

            if (g != null)
            {
                write.f(g.currentSpeed());
                write.f(g.currentTorque());
            }
        }

        @Override
        public void read(Reads read, byte revision)
        {
            super.read(read, revision);

            int gid = read.i();

            if (gid != -1)
            {
                var g = KineticGraph.revive(gid, read.f(), read.f());
                g.add(this);
            }
        }
    }
}