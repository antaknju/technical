package technical.expansion.train;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import technical.utility.T;
import technical.expansion.train.RailConnector.RailConnectorBuild;

import static mindustry.Vars.*;

public class TrainLoader extends RailAddapter
{
    public boolean isUnloader = false;

    public TrainLoader(String name) 
    {
        super(name);
        hasItems = true;
        itemCapacity = 100;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid)
    {
        super.drawPlace(x, y, rotation, valid);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("items", (TrainLoaderBuild build) ->
            new Bar(
                () -> Core.bundle.format("bar.items", build.items.total(), itemCapacity),
                () -> Pal.items,
                () -> (float)build.items.total() / (float)itemCapacity
            )
        );
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation)
    {
        return super.canPlaceOn(tile, team, rotation);
    }
    
    public class TrainLoaderBuild extends RailAddapterBuild
    {
        @Override
        public void updateTile()
        {
            Tile rcTile = getRailConnector(tile, team, rotation);
            RailConnectorBuild rc = rcTile != null ? (RailConnectorBuild) rcTile.build : null;

            if (rc == null) return;
            
            int dx = (tile.x + T.Rot2Pos(rotation).x * (size / 2 + 1)) * tilesize;
            int dy = (tile.y + T.Rot2Pos(rotation).y * (size / 2 + 1)) * tilesize;
            
            for (RailVehicleUnit train : getAllTrains(dx, dy, 2f)) {
                for(Item item : content.items()){
                    if (!isUnloader)
                    {
                        int amount1 = items.get(item);
                        int amount2 = train.acceptedItemAmount(item, amount1);

                        items.remove(item, amount2);
                        train.items.add(item, amount2);

                        if (amount2 > 0)
                            train.iconTimes.put(item, 0);
                    }
                    else
                    {
                        int amount1 = train.items.get(item);
                        int amount2 = acceptedItemAmount(item, amount1);

                        train.items.remove(item, amount2);
                        items.add(item, amount2);
                    }
                }
            }

            if (items.total() > 0 && isUnloader){
                for(Item item : content.items()){
                    if(items.has(item))
                    {
                        dump(item);
                    }
                }
            }
        }

        public int acceptedItemAmount(Item item, int amount)
        {
            int capacity = itemCapacity;
            amount = Mathf.clamp(amount, 0, capacity - items.total());
            return amount;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return items.total() < itemCapacity;
        }

        @Override
        public void handleItem(Building source, Item item){
            items.add(item, 1);
        }

        @Override
        public void draw()
        {
            Draw.rect(block.region, x, y, rotation * 90);
            Draw.rect(Core.atlas.find(block.name + "-top"), x, y, rotation * 90);
        }

        @Override
        public void write(Writes write)
        {
            super.write(write);
        }

        @Override
        public void read(Reads read, byte revision)
        {
            super.read(read, revision);
        }

        public Seq<RailVehicleUnit> getAllTrains(int x, int y, float range)
        {
            Seq<RailVehicleUnit> nearby = new Seq<>();

            Units.nearby(x - range, y - range, range * 2, range * 2, unit -> {
                if (unit instanceof RailVehicleUnit rvu)
                    nearby.add(rvu);
            });

            return nearby;
        }
    }
}
