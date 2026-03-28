package technical.expansion.train;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Nullable;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Structs;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Iconc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Tile;
import mindustry.world.blocks.UnitTetherBlock;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import technical.T;
import technical.content.TUnits;
import technical.expansion.train.RailConnector.RailConnectorBuild;

import static mindustry.Vars.*;

public class TrainFactory extends RailAddapter 
{
    public RailVehicle unitType = TUnits.basic_train;
    public float buildTime = 60f * 2f;
    public ItemStack[] unitRequirements;
    public int[] capacities = {};

    public TrainFactory(String name) {
        super(name);

        hasItems = true;
    }

    @Override
    public void init()
    {
        capacities = new int[Vars.content.items().size];
        for(ItemStack stack : unitRequirements){
            capacities[stack.item.id] = Math.max(capacities[stack.item.id], stack.amount * 2);
            itemCapacity = Math.max(itemCapacity, stack.amount * 2);
        }

        if(unitRequirements != null){
            for(ItemStack stack : unitRequirements){
                consumeItem(stack.item, stack.amount);
            }
        }

        super.init();
    }

    public static void unitTetherBlockSpawned(Tile tile, int id){
        if(tile == null || !(tile.build instanceof UnitTetherBlock build)) return;
        build.spawned(id);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        if(!canCreateMoreUnits(player.team()))
        {
            drawPlaceText("Too Much Units", x, y, valid);
        }
    }

    @Override
    public void setBars(){
        super.setBars();
        
        addBar("progress", (TrainFabricatorBuild e) -> 
            new Bar("bar.progress", Pal.ammo, e::fraction
        ));

        addBar("units", (TrainFabricatorBuild e) ->
        new Bar(
            () -> e.unit() == null ? "[lightgray]" + Iconc.cancel :
                Core.bundle.format("bar.unitcap",
                    e.unit().localizedName,
                    e.team.data().countType(e.unit()),
                    e.unit() == null ? Units.getStringCap(e.team) : (e.unit().useUnitCap ? Units.getStringCap(e.team) : "∞")
                ),
            () -> Pal.power,
            () -> e.unit() == null ? 0f : (e.unit().useUnitCap ? (float)e.team.data().countType(e.unit()) / Units.getCap(e.team) : 1f)
        ));
    }
    
    @Override
    public void setStats() {
        super.setStats();

        // Time to build one train
        // stats.add(Stat.productionTime, buildTime / 60f, StatUnit.seconds);


        stats.add(Stat.output, table -> {
            table.row();

            table.table(Styles.grayPanel, t -> {

                if(unitType.isBanned()){
                    t.image(Icon.cancel).color(Pal.remove).size(40);
                    return;
                }

                if(unitType.unlockedNow()){
                    t.image(unitType.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit).with(i -> StatValues.withTooltip(i, unitType));
                    t.table(info -> {
                        info.add(unitType.localizedName).left();
                        info.row();
                        info.add(Strings.autoFixed(buildTime / 60f, 1) + " " + Core.bundle.get("unit.seconds")).color(Color.lightGray);
                    }).left();

                    t.table(req -> {
                        req.right();
                        for(int i = 0; i < unitRequirements.length; i++){
                            if(i % 6 == 0){
                                req.row();
                            }

                            ItemStack stack = unitRequirements[i];
                            req.add(StatValues.displayItem(stack.item, stack.amount, buildTime, true)).pad(5);
                        }
                    }).right().grow().pad(10f);
                }else{
                    t.image(Icon.lock).color(Pal.darkerGray).size(40);
                }
            }).growX().pad(5);
            table.row();
        });
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation)
    {
        if (!canCreateMoreUnits(team)) return false;

        return super.canPlaceOn(tile, team, rotation);
    }

    public boolean canCreateMoreUnits(Team team)
    {
        return Units.canCreate(team, unitType);
    }
    
    public class TrainFabricatorBuild extends RailAddapterBuild
    {
        public int readUnitId = -1;
        public float buildProgress, totalProgress;
        public float warmup, readyness;
        public @Nullable RailVehicleUnit unit;

        public float fraction(){
            return buildProgress;
        }

        @Override
        public int getMaximumAccepted(Item item){
            return Mathf.round(capacities[item.id] * state.rules.unitCost(team));
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return items.get(item) < getMaximumAccepted(item) &&
                Structs.contains(unitRequirements, stack -> stack.item == item);
        }

        @Override
        public void updateTile()
        {
            Tile rcTile = getRailConnector(tile, team, rotation);
            RailConnectorBuild rc = rcTile != null ? (RailConnectorBuild) rcTile.build : null;

            if (rc == null)
            {
                buildProgress = 0;
                totalProgress = 0;
                return;
            }

            // unit was lost / destroyed or RailConnector was broken
            if(unit != null && (unit.dead || !unit.isAdded())){
                unit = null;
                buildProgress = 0;
                totalProgress = 0;
            }

            // if(readUnitId != -1){
            //     unit = (RailVehicleUnit)Groups.unit.getByID(readUnitId);

            //     if(unit != null || !net.client()){
            //         readUnitId = -1;
            //     }
            // }

            warmup = Mathf.approachDelta(warmup, efficiency, 1f / 60f);
            readyness = Mathf.approachDelta(readyness, unit != null ? 1f : 0f, 1f / 60f);

            if (unit == null && Units.canCreate(team, unitType) && rc != null)
            {
                if (efficiency < 0) return;

                buildProgress += edelta() / buildTime;
                totalProgress += edelta();

                if(buildProgress >= 1f){
                    if(!net.client()){
                        unit = (RailVehicleUnit)unitType.create(team);
                        unit.init(rc);
                        unit.set(rc.x, rc.y);
                        unit.add();

                        consume();
                    }
                }
            }
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();
            if (unit != null)
                unit.destroy();
        }

        @Override
        public void onRemoved() {
            if (unit != null)
            {
                unit.remove();

                Fx.unitDespawn.at(unit.x, unit.y, unit.rotation, unit);
            }
        }

        @Override
        public boolean shouldConsume(){
            return unit == null;
        }

        // @Override
        // public boolean shouldActiveSound(){
        //     return shouldConsume() && warmup > 0.01f;
        // }

        @Override
        public void draw()
        {
            Tile rcTile = getRailConnector(tile, team, rotation);
            RailConnectorBuild rc = rcTile != null ? (RailConnectorBuild) rcTile.build : null;

            Draw.rect(block.region, x, y, rotation * 90);
            if(unit == null && Units.canCreate(team, unitType) && rc != null)
            {
                Draw.draw(Layer.blockOver, () -> 
                {
                    Draw.scl(1f);
                    Point2 dpos = T.Rot2Pos(rotation);
                    float offset = 5f;

                    Drawf.construct(x + dpos.x * offset, y + dpos.y * offset, unitType.fullIcon, (rotation * 90 + 90) % 360, buildProgress, warmup, totalProgress);

                    Draw.scl();
                });
            }
            
            Draw.rect(Core.atlas.find(block.name + "-top"), x, y, rotation * 90);
        }

        @Override
        public float totalProgress(){
            return totalProgress;
        }

        @Override
        public float progress(){
            return buildProgress;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.i(unit == null ? -1 : unit.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            readUnitId = read.i();
        }

        public UnitType unit()
        {
            return unitType;
        }
    }
}
