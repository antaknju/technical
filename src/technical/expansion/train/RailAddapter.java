package technical.expansion.train;

import static mindustry.Vars.player;
import static mindustry.Vars.world;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Tile;
import mindustry.world.meta.BlockGroup;
import technical.T;
import technical.expansion.TBlock;

public class RailAddapter extends TBlock
{
    public RailAddapter(String name) 
    {
        super(name);
        update = true;
        rotate = true;
        drawArrow = true;

        group = BlockGroup.transportation;
    }
    
    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid)
    {
        if (!inWorldBounds(x, y)) return;

        if(getRailConnector(world.tile(x, y), player.team(), rotation) == null)
        {
            drawPlaceText("Isn't Rotated Towards Rail Connector", x, y + 1, valid);
        }
    }

    public boolean inWorldBounds(float x, float y) {
        return x >= 0 && y >= 0 && x < world.width() && y < world.height();
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, Core.atlas.find(name + "-top")};
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation)
    {
        return getRailConnector(tile, team, rotation) != null && super.canPlaceOn(tile, team, rotation);
    }

    public Tile getRailConnector(Tile tile, Team team, int rotation)
    {
        int half = size / 2;

        int nx = tile.x + T.Rot2Pos(rotation).x * (half + 1);
        int ny = tile.y + T.Rot2Pos(rotation).y * (half + 1);

        if (!inWorldBounds(nx, ny)) return null;

        Tile neighbor = world.tile(nx, ny);
        if ((neighbor.block() instanceof RailConnector)) {
            return neighbor;
        }

        return null;
    }

    public class RailAddapterBuild extends TBuild
    {
        @Override
        public void draw(){
            Draw.rect(block.region, x, y, rotation * 90);
            Draw.rect(Core.atlas.find(block.name + "-top"), x, y);
        }
    }
}
