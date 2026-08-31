package technical.core;

import arc.Events;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.game.EventType.Trigger;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import technical.util.T;

public class EnvBlock extends Floor
{
    public static final Seq<EnvBuild> activeBuilds = new Seq<>();

    static /// TODO it's on game load: if player will be able to mine env blocks in future, it need to be reworked
    {
        Events.on(WorldLoadEvent.class, event -> {
            activeBuilds.clear();
            for (Tile tile : Vars.world.tiles)
            {
                if (tile.floor() instanceof EnvBlock env)
                {
                    if (env.shouldCreateBuild(tile))
                    {
                        activeBuilds.add(env.newBuild(tile));
                    }
                }
            }
        });

        Events.run(Trigger.update, () -> {
            if (!Vars.state.isPaused())
            {
                for (EnvBuild build : activeBuilds)
                {
                    build.updateTile();
                }
            }
        });

        Events.run(Trigger.draw, () -> {
            for (EnvBuild build : activeBuilds)
            {
                if (T.isInCameraBounds(build.tile.worldx(), build.tile.worldy(), Vars.tilesize * 10f))
                {
                    build.draw();
                }
            }
        });
    }

    public EnvBlock(String name)
    {
        super(name);
        destructible = false;
        targetable = false;
    }

    public boolean shouldCreateBuild(Tile tile)
    {
        return true;
    }

    public EnvBuild newBuild(Tile tile)
    {
        return new EnvBuild(tile, this);
    }

    public static class EnvBuild
    {
        public Tile tile;
        public EnvBlock block;

        public EnvBuild(Tile tile, EnvBlock block)
        {
            this.tile = tile;
            this.block = block;
        }

        public void updateTile()
        {

        }

        public void draw()
        {

        }
    }
}