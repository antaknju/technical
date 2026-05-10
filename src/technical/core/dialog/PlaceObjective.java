package technical.core.dialog;

import arc.math.geom.Point2;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.game.GameStats;
import mindustry.world.Block;

public class PlaceObjective extends DialogObjective
{
    public Block block;

    public int startCount;

    public PlaceObjective(Block block)
    {
        this.block = block;
    }

    @Override
    public void onStart()
    {
        startCount = Vars.state.stats.getPlaced(block);
    }

    @Override
    public boolean onComplete()
    {
        return Vars.state.stats.getPlaced(block) > startCount;
    }

    @Override
    public DialogObjective clone()
    {
        var copy = (PlaceObjective) super.clone();
        copy.block = block;

        return copy;
    }

    @Override
    public boolean onNegativeComplete()
    {
        return !onComplete();
    }
}