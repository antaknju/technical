package technical.expansion;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.*;
// import arc.math.geom.Geometry;
// import arc.math.geom.Point2;
// import arc.struct.*;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.*;
import mindustry.entities.Units;
// import mindustry.entities.part.RegionPart;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.Bar;
import mindustry.ui.Fonts;
import mindustry.world.*;
import mindustry.world.blocks.UnitTetherBlock;
// import mindustry.world.blocks.storage.StorageBlock.StorageBuild;
// import mindustry.world.blocks.units.UnitCargoLoader.UnitTransportSourceBuild;
// import mindustry.world.draw.DrawBlurSpin;
// import mindustry.world.blocks.storage.CoreBlock.*;
import mindustry.world.blocks.storage.CoreBlock;

// import mindustry.world.meta.*;

import static mindustry.Vars.*;

// import technical.content.TUnits;

public class DroneCoreExpansion extends Expansion {
    public UnitType unitType = UnitTypes.mono;
    public float buildTime = 60f * 2f;

    public DroneCoreExpansion(String name)
    {
        super(name);
        coreMerge = true;
        requiredAttachment = CoreBlock.class;
    }

    // @Remote(called = Loc.server)
    public static void unitTetherBlockSpawned(Tile tile, int id){
        if(tile == null || !(tile.build instanceof UnitTetherBlock build)) return;
        build.spawned(id);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        if(!canCreateMoreUnits(player.team()))
        {
            drawPlaceText("Too Much Assistant Units", x, y, valid);
        }
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("units", (CoreExpansionBuild e) ->
            new Bar(
            () ->
            Core.bundle.format("bar.unitcap",
                Fonts.getUnicodeStr(unitType.name),
                e.team.data().countType(unitType),
                Units.getStringCap(e.team)
            ),
            () -> Pal.power,
            () -> (float)e.team.data().countType(unitType) / Units.getCap(e.team)
        ));
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

    public class CoreExpansionBuild extends StorageBuild {
        public int readUnitId = -1;
        public float buildProgress, totalProgress;
        public float warmup, readyness;
        public @Nullable Unit unit;

        @Override
        public void updateTile(){
            //unit was lost/destroyed
            if(unit != null && (unit.dead || !unit.isAdded())){
                unit = null;
                buildProgress = 0;
                totalProgress = 0;
            }

            if(readUnitId != -1){
                unit = Groups.unit.getByID(readUnitId);
                if(unit != null || !net.client()){
                    readUnitId = -1;
                }
            }

            warmup = Mathf.approachDelta(warmup, efficiency, 1f / 60f);
            readyness = Mathf.approachDelta(readyness, unit != null ? 1f : 0f, 1f / 60f);

            if(unit == null && Units.canCreate(team, unitType)){
                buildProgress += edelta() / buildTime;
                totalProgress += edelta();

                if(buildProgress >= 1f){
                    if(!net.client()){
                        unit = unitType.create(team);
                        // if(unit instanceof BuildingTetherc bt){
                        //     bt.building(this);
                        // }
                        unit.set(x, y);
                        unit.rotation = 90f;
                        unit.add();
                        // Call.unitTetherBlockSpawned(tile, unit.id);
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
            return super.shouldConsume() && unit == null;
        }

        // @Override
        // public boolean shouldActiveSound(){
        //     return shouldConsume() && warmup > 0.01f;
        // }

        @Override
        public void draw(){
            Draw.rect(block.region, x, y);
            if(unit == null && Units.canCreate(team, unitType)){
                Draw.draw(Layer.blockOver, () -> {
                    Drawf.construct(this, unitType.fullIcon, 0f, buildProgress, warmup, totalProgress);
                });
            }
            Draw.rect(Core.atlas.find(block.name + "-top"), x, y);
            if (unit != null){
                Draw.z(Layer.bullet - 0.01f);
                Draw.color(polyColor);
                Lines.stroke(polyStroke * readyness);
                Lines.poly(x, y, polySides, polyRadius, Time.time * polyRotateSpeed);
                Draw.reset();
                Draw.z(Layer.block);
            }
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
    }
}
