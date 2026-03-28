package technical.expansion;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.util.*;
import mindustry.game.Team;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.*;
import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.blocks.storage.StorageBlock;
// import mindustry.world.blocks.storage.StorageBlock.StorageBuild;
// import mindustry.world.blocks.units.UnitCargoLoader.UnitTransportSourceBuild;
// import mindustry.world.draw.DrawBlurSpin;
// import mindustry.world.blocks.storage.CoreBlock.*;

// import mindustry.world.meta.*;

import static mindustry.Vars.*;

// import technical.content.TUnits;

public class Expansion extends StorageBlock {
    public float polyStroke = 1.8f, polyRadius = 8f;
    public int polySides = 6;
    public float polyRotateSpeed = 1f;
    public Color polyColor = Pal.accent;

    public Class<? extends Block> requiredAttachment = PowerBlock.class;

    public Expansion(String name)
    {
        super(name);
        update = true;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid)
    {
        if(getFullyAttachedNeighbor(world.tile(x, y), player.team()) == null)
        {
            drawPlaceText("Isn't Attached To One " + requiredAttachment.getSimpleName(), x, y, valid);
        }
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation)
    {
        return getFullyAttachedNeighbor(tile, team) != null && super.canPlaceOn(tile, team, rotation);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, Core.atlas.find(name + "-top")};
    }

    public Tile getFullyAttachedNeighbor(Tile tile, Team team)
    {
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        int half = size / 2;

        for (int[] dir : directions) {
            boolean fullyAttached = true;
            Tile lastNeighbor = null;

            for (int offset = -half; offset <= half; offset++) {
                int nx = tile.x + dir[0] * (half + 1) + (dir[1] != 0 ? offset : 0);
                int ny = tile.y + dir[1] * (half + 1) + (dir[0] != 0 ? offset : 0);


                Tile neighbor = world.tile(nx, ny);
                if (neighbor == null || neighbor.build == null || neighbor.build.team != team || !requiredAttachment.isInstance(neighbor.block()) || (lastNeighbor != null && neighbor.build != lastNeighbor.build)) {
                    fullyAttached = false;
                    break;
                }

                lastNeighbor = neighbor;
            }

            if (fullyAttached) {
                int nx = tile.x + dir[0] * (half + 1);
                int ny = tile.y + dir[1] * (half + 1);
                return world.tile(nx, ny);
            }
        }

        return null;
    }

    public class ExpansionBuild extends StorageBuild {
        
        // @Override
        // public boolean shouldActiveSound(){
        //     return true;
        // }

        @Override
        public void draw(){
            Draw.rect(block.region, x, y);
            Draw.rect(Core.atlas.find(block.name + "-top"), x, y);
            
            Draw.z(Layer.bullet - 0.01f);
            Draw.color(polyColor);
            Lines.stroke(polyStroke);
            Lines.poly(x, y, polySides, polyRadius, Time.time * polyRotateSpeed);
            Draw.reset();
            Draw.z(Layer.block);
        }
    }
}



    // public boolean isEnoughCore(Tile tile, Team team, int rotation)
    // {
    //     if(tile == null) return false;
    //     int coresNearby = 0;
    //     int range = size/2 + 1;

    //     for (int dx = -range; dx <= range; dx++) {
    //         for (int dy = -range; dy <= range; dy++) {
    //             if (Math.abs(dx) == Math.abs(dy)) continue;

    //             Tile nearby = world.tile(tile.x + dx, tile.y + dy);
    //             if (nearby != null && nearby.block() instanceof CoreBlock && nearby.build != null && nearby.build.team == team) {
    //                 coresNearby++;
    //             }
    //         }
    //         if(coresNearby >= size) return true;
    //     }
    //     return false;
    // // }

    // public Tile getFullyAttachedNeighbor(Tile tile, Team team) {
    //     if (tile == null || tile.build == null) return null;

    //     int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};
    //     int half = size / 2;

    //     StringBuilder debugLine = new StringBuilder();
    //     debugLine.append("\n");

    //     for (int[] dir : directions) {
    //         boolean fullyAttached = true;
    //         debugLine.append("Direction (" + dir[0] + "," + dir[1] + "): ");

    //         for (int offset = -half; offset <= half; offset++) {
    //             int nx = tile.x + dir[0] * (half + 1) + (dir[1] != 0 ? offset : 0);
    //             int ny = tile.y + dir[1] * (half + 1) + (dir[0] != 0 ? offset : 0);

    //             debugLine.append("(").append(nx).append(",").append(ny).append(")");

    //             Tile neighbor = world.tile(nx, ny);
    //             if (neighbor == null || neighbor.build == null || neighbor.build.team != team
    //                     || !requiredAttachment.isInstance(neighbor.block())) {
    //                 fullyAttached = false;
    //                 debugLine.append("[X]"); // mark failure
    //                 // break;
    //             } else {
    //                 debugLine.append("[O]"); // mark success
    //             }

    //             debugLine.append(" "); // separator between positions
    //         }

    //         debugLine.append("\n");

    //         if (fullyAttached) {
    //             int nx = tile.x + dir[0] * (half + 1);
    //             int ny = tile.y + dir[1] * (half + 1);
    //             return world.tile(nx, ny);
    //         }
    //     }

    //     System.out.println(debugLine.toString());

    //     return null;
    // }