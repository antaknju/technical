package technical.expansion;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.EnumSet;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Puddles;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import technical.Fr;
import technical.content.TFx;
import technical.content.TLiquids;

public class Volcano extends Floor
{
    public static final Point2[] offsets = {
        new Point2(0, 0),
        new Point2(1, 0),
        new Point2(1, 1),
        new Point2(0, 1),
        new Point2(-1, 1),
        new Point2(-1, 0),
        new Point2(-1, -1),
        new Point2(0, -1),
        new Point2(1, -1),
    };

    public Block parent = Blocks.air;

    public Effect dormantEffect = TFx.volcanoDormant;
    public Effect activeEffect = TFx.volcanoErupting;

    public Color effectColor = Pal.vent;
    public float effectSpacing = 15f;

    public float inactiveTime = Fr.time * 90;
    public float activeTime = Fr.time * 30;

    static
    {
        for(var p : offsets)
        {
            p.sub(1, 1);
        }
    }

    public Volcano(String name)
    {
        super(name);
        variants = 2;
    }

    @Override
    public void drawMain(Tile tile)
    {
        if(parent instanceof Floor floor)
        {
            floor.drawMain(tile);
        }

        if(checkAdjacent(tile))
        {
            Draw.rect(variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))], tile.worldx() - tilesize, tile.worldy() - tilesize);
        }
    }

    @Override
    public boolean updateRender(Tile tile)
    {
        return checkAdjacent(tile);
    }

    @Override
    public boolean shouldIndex(Tile tile)
    {
        return isCenterVent(tile);
    }

    public boolean isCenterVent(Tile tile)
    {
        Tile topRight = tile.nearby(1, 1);
        return topRight != null && topRight.floor() == tile.floor() && checkAdjacent(topRight);
    }

    @Override
    public void renderUpdate(UpdateRenderState state) 
    {
        float totalCycle = activeTime + inactiveTime;

        float offset = Mathf.randomSeed(state.tile.pos(), 0, totalCycle);
        
        float time = (Time.time + offset);
        float currentProgress = time % totalCycle;
        float prevProgress = (time - Time.delta) % totalCycle;

        if (Mathf.floor(prevProgress / effectSpacing) < Mathf.floor(currentProgress / effectSpacing)) 
        {
            float x = state.tile.x * tilesize - tilesize;
            float y = state.tile.y * tilesize - tilesize;

            if (state.tile.nearby(-1, -1) != null) 
            {
                if (state.tile.nearby(-1, -1).block() == Blocks.air)
                {
                    if (currentProgress < activeTime) 
                    {
                        activeEffect.at(x, y, effectColor);
                    } 
                    else 
                    {
                        dormantEffect.at(x, y, effectColor);
                    }
                }
                else
                {
                    if (currentProgress < activeTime)
                    {
                        for (var point : offsets)
                        {
                            Tile other = Vars.world.tile(state.tile.x + point.x, state.tile.y + point.y);
                            Puddles.deposit(other, other, TLiquids.lava, 0.1f, true, false);
                        }
                    }
                }
            }
        }
    }

    //note that only the top right tile works for this; render order reasons.
    public boolean checkAdjacent(Tile tile)
    {
        for(var point : offsets)
        {
            Tile other = Vars.world.tile(tile.x + point.x, tile.y + point.y);
            if(other == null || other.floor() != this)
            {
                return false;
            }
        }
        return true;
    }

    public class VolcanoBuild extends Building
    {
        
    }
}