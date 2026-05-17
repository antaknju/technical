package technical.core;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.content.Liquids;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

public class TUnitFactory extends TPayloadBlock
{
    public int[] capacities = {};

    public Seq<TUnitPlan> plans = new Seq<>(4);
    public Sound createSound = Sounds.unitCreate;
    public float createSoundVolume = 1f;

    public TUnitFactory(String name)
    {
        super(name);
        update = true;
        hasPower = true;
        hasItems = true;
        solid = true;
        configurable = true;
        clearOnDoubleTap = true;
        outputsPayload = true;
        rotate = true;
        regionRotated1 = 1;
        commandable = true;
        ambientSound = Sounds.loopUnitBuilding;
        ambientSoundVolume = 0.09f;

        config(Integer.class, (TUnitFactoryBuild build, Integer i) -> {
            if(!configurable) return;
            if(build.currentPlan == i) return;
            build.currentPlan = i < 0 || i >= plans.size ? -1 : i;
            build.progress = 0;
            build.inPayloads.clear();
            if(build.command != null && (build.unit() == null || !build.unit().commands.contains(build.command))){
                build.command = null;
            }
        });

        config(UnitType.class, (TUnitFactoryBuild build, UnitType val) -> {
            if(!configurable) return;
            int next = plans.indexOf(p -> p.unit == val);
            if(build.currentPlan == next) return;
            build.currentPlan = next;
            build.progress = 0;
            build.inPayloads.clear();
            if(build.command != null && !val.commands.contains(build.command)){
                build.command = null;
            }
        });

        config(UnitCommand.class, (TUnitFactoryBuild build, UnitCommand command) -> build.command = command);
        configClear((TUnitFactoryBuild build) -> build.command = null);

        consume(new ConsumeItemDynamic((TUnitFactoryBuild e) ->
                e.currentPlan != -1 ? plans.get(Math.min(e.currentPlan, plans.size - 1)).inputItems : ItemStack.empty));
    }

    @Override
    public void init()
    {
        initCapacities();
        super.init();
    }

    @Override
    public void afterPatch()
    {
        initCapacities();
        super.afterPatch();
    }

    public void initCapacities()
    {
        capacities = new int[Vars.content.items().size];
        itemCapacity = 10;
        for(TUnitPlan plan : plans){
            for(ItemStack stack : plan.inputItems){
                capacities[stack.item.id] = Math.max(capacities[stack.item.id], stack.amount * 2);
                itemCapacity = Math.max(itemCapacity, stack.amount * 2);
            }
        }
        consumeBuilder.each(c -> c.multiplier = b -> Vars.state.rules.unitCost(b.team));
    }

    @Override
    public void setBars()
    {
        super.setBars();
        addBar("progress", (TUnitFactoryBuild e) -> new Bar("bar.progress", Pal.ammo, e::fraction));

        addBar("units", (TUnitFactoryBuild e) ->
                new Bar(
                        () -> e.unit() == null ? "[lightgray]" + Iconc.cancel :
                                Core.bundle.format("bar.unitcap",
                                        Fonts.getUnicodeStr(e.unit().name),
                                        e.team.data().countType(e.unit()),
                                        e.unit() == null ? Units.getStringCap(e.team) : (e.unit().useUnitCap ? Units.getStringCap(e.team) : "∞")
                                ),
                        () -> Pal.power,
                        () -> e.unit() == null ? 0f : (e.unit().useUnitCap ? (float)e.team.data().countType(e.unit()) / Units.getCap(e.team) : 1f)
                ));
    }

    @Override
    public boolean outputsItems()
    {
        return false;
    }

    @Override
    public void setStats()
    {
        super.setStats();
        stats.remove(Stat.itemCapacity);

        stats.add(Stat.output, table -> {
            table.row();
            for(var plan : plans){
                table.table(Styles.grayPanel, t -> {
                    if(plan.unit.isBanned()){
                        t.image(Icon.cancel).color(Pal.remove).size(40);
                        return;
                    }
                    if(plan.unit.unlockedNow()){
                        t.image(plan.unit.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit).with(i -> StatValues.withTooltip(i, plan.unit));
                        t.table(info -> {
                            info.add(plan.unit.localizedName).left();
                            info.row();
                            info.add(Strings.autoFixed(plan.craftTime / 60f, 1) + " " + Core.bundle.get("unit.seconds")).color(Color.lightGray);
                        }).left();

                        t.table(req -> {
                            req.right();
                            for(int i = 0; i < plan.inputItems.length; i++){
                                if(i % 6 == 0) req.row();
                                ItemStack stack = plan.inputItems[i];
                                req.add(StatValues.displayItem(stack.item, stack.amount, plan.craftTime, true)).pad(5);
                            }
                        }).right().grow().pad(10f);
                    }else{
                        t.image(Icon.lock).color(Pal.darkerGray).size(40);
                    }
                }).growX().pad(5);
                table.row();
            }
        });
    }

    @Override
    public void getPlanConfigs(Seq<UnlockableContent> options)
    {
        for(var plan : plans){
            if(!plan.unit.isBanned()){
                options.add(plan.unit);
            }
        }
    }

    public static class TUnitPlan
    {
        public UnitType unit;
        public ItemStack[] inputItems;
        public PayloadStack[] inputPayloads;
        public float craftTime;

        public TUnitPlan(UnitType unit, float craftTime, ItemStack[] inputItems, PayloadStack[] inputPayloads)
        {
            this.unit = unit;
            this.craftTime = craftTime;
            this.inputItems = inputItems;
            this.inputPayloads = inputPayloads != null ? inputPayloads : new PayloadStack[0];
        }
    }

    public class TUnitFactoryBuild extends TPayloadBuild<UnitPayload>
    {
        public @Nullable Vec2 commandPos;
        public @Nullable UnitCommand command;
        public int currentPlan = -1;
        public float progress, time, speedScl;

        public Seq<Payload> inPayloads = new Seq<>();

        @Override
        public boolean acceptPayload(Building source, Payload payload)
        {
            if(currentPlan == -1 || this.payload != null) return false;

            PayloadStack[] required = plans.get(currentPlan).inputPayloads;
            if(required == null) return false;

            for(PayloadStack stack : required){
                if(stack.item == payload.content()){
                    int have = 0;
                    for(Payload p : inPayloads){
                        if(p.content() == payload.content()) have++;
                    }
                    return have < stack.amount;
                }
            }
            return false;
        }

        @Override
        public void handlePayload(Building source, Payload payload)
        {
            inPayloads.add(payload);
        }
        public boolean hasRequiredPayloads()
        {
            if(currentPlan == -1) return false;
            PayloadStack[] required = plans.get(currentPlan).inputPayloads;
            if(required == null) return true;

            for(PayloadStack stack : required){
                int have = 0;
                for(Payload p : inPayloads){
                    if(p.content() == stack.item) have++;
                }
                if(have < stack.amount) return false;
            }
            return true;
        }

        @Override
        public void updateTile()
        {
            if(!configurable) currentPlan = 0;
            if(currentPlan < 0 || currentPlan >= plans.size) currentPlan = -1;

            if(payload != null){
                moveOutPayload();
                return;
            }

            if(currentPlan != -1 && !hasRequiredPayloads()){
                moveInPayload();
            }

            boolean canBuild = efficiency > 0 && currentPlan != -1 && hasRequiredPayloads();

            if(canBuild){
                time += edelta() * speedScl * Vars.state.rules.unitBuildSpeed(team);
                progress += edelta() * Vars.state.rules.unitBuildSpeed(team);
                speedScl = Mathf.lerpDelta(speedScl, 1f, 0.05f);
            }else{
                speedScl = Mathf.lerpDelta(speedScl, 0f, 0.05f);
            }

            if(currentPlan != -1 && hasRequiredPayloads()){
                TUnitPlan plan = plans.get(currentPlan);

                if(plan.unit.isBanned()){
                    currentPlan = -1;
                    inPayloads.clear();
                    return;
                }

                if(progress >= plan.craftTime){
                    progress %= 1f;

                    Unit unit = plan.unit.create(team);
                    if(unit.isCommandable()){
                        if(commandPos != null) unit.command().commandPosition(commandPos);
                        unit.command().command(
                                command == null && unit.type.defaultCommand != null
                                        ? unit.type.defaultCommand
                                        : command);
                    }

                    createSound.at(this, 1f + Mathf.range(0.06f), createSoundVolume);
                    payload = new UnitPayload(unit);
                    payVector.setZero();

                    inPayloads.clear();
                    consume();
                    Events.fire(new UnitCreateEvent(payload.unit, this));
                }

                progress = Mathf.clamp(progress, 0, plan.craftTime);
            }else{
                progress = 0f;
            }
        }

        @Override
        public boolean shouldConsume()
        {
            if(currentPlan == -1) return false;
            return enabled && payload == null
//                    && team.activateUnitFactories()
                    && hasRequiredPayloads();
        }

        @Override
        public BlockStatus status()
        {
            if(!team.activateUnitFactories()) return BlockStatus.inactive;
            return super.status();
        }

        @Override
        public void dumpPayload()
        {
            if(payload.dump()){
                Call.unitBlockSpawn(tile);
            }
        }

        public float fraction()
        {
            return currentPlan == -1 ? 0 : progress / plans.get(currentPlan).craftTime;
        }

        public boolean canSetCommand()
        {
            var output = unit();
            return output != null && output.commands.size > 1 && output.allowChangeCommands &&
                    !(output.commands.size == 2 && output.commands.get(1) == UnitCommand.enterPayloadCommand);
        }

        @Override
        public void created()
        {
            if(currentPlan == -1){
                currentPlan = plans.indexOf(u -> u.unit.unlockedNow());
            }
        }

        @Override
        public void drawSelect()
        {
            super.drawSelect();
            if(plans.size > 1 && currentPlan != -1 && currentPlan < plans.size){
                drawItemSelection(plans.get(currentPlan).unit);
            }
        }

        @Override
        public Vec2 getCommandPosition() { return commandPos; }

        @Override
        public void onCommand(Vec2 target) { commandPos = target; }

        @Override
        public Object senseObject(LAccess sensor)
        {
            if(sensor == LAccess.config) return currentPlan == -1 ? null : plans.get(currentPlan).unit;
            return super.senseObject(sensor);
        }

        @Override
        public double sense(LAccess sensor)
        {
            if(sensor == LAccess.progress) return Mathf.clamp(fraction());
            if(sensor == LAccess.itemCapacity) return Mathf.round(itemCapacity * Vars.state.rules.unitCost(team));
            return super.sense(sensor);
        }

        @Override
        public void buildConfiguration(Table table)
        {
            Seq<UnitType> units = Seq.with(plans).map(u -> u.unit).retainAll(u -> u.unlockedNow() && !u.isBanned());

            if(units.any()){
                ItemSelection.buildTable(TUnitFactory.this, table, units,
                        () -> currentPlan == -1 ? null : plans.get(currentPlan).unit,
                        unit -> configure(plans.indexOf(u -> u.unit == unit)),
                        selectionRows, selectionColumns);

                table.row();
                Table commands = new Table();
                commands.top().left();

                Runnable rebuildCommands = () -> {
                    commands.clear();
                    commands.background(null);
                    var unit = unit();
                    if(unit != null && canSetCommand()){
                        commands.background(Styles.black6);
                        var group = new ButtonGroup<ImageButton>();
                        group.setMinCheckCount(0);
                        int i = 0, columns = Mathf.clamp(units.size, 2, selectionColumns);
                        var list = unit.commands;

                        commands.image(Tex.whiteui, Pal.gray).height(4f).growX().colspan(columns).row();

                        for(var item : list){
                            ImageButton button = commands.button(item.getIcon(), Styles.clearNoneTogglei, 40f, () -> {
                                configure(item);
                            }).tooltip(item.localized()).group(group).get();

                            button.update(() -> button.setChecked(
                                    command == item || (command == null && unit.defaultCommand == item)));

                            if(++i % columns == 0) commands.row();
                        }

                        if(list.size < columns){
                            for(int j = 0; j < (columns - list.size); j++) commands.add().size(40f);
                        }
                    }
                };
                rebuildCommands.run();
                table.row();
                table.add(commands).fillX().left();
            }else{
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }
        }

        @Override
        public void display(Table table)
        {
            super.display(table);
            TextureRegionDrawable reg = new TextureRegionDrawable();
            table.row();
            table.table(t -> {
                t.left();
                t.image().update(i -> {
                    i.setDrawable(currentPlan == -1 ? Icon.cancel : reg.set(plans.get(currentPlan).unit.uiIcon));
                    i.setScaling(Scaling.fit);
                    i.setColor(currentPlan == -1 ? Color.lightGray : Color.white);
                }).size(32).padBottom(-4).padRight(2);
                t.label(() -> currentPlan == -1 ? "@none" : plans.get(currentPlan).unit.localizedName)
                        .wrap().width(230f).color(Color.lightGray);
            }).left();
        }

        @Override
        public Object config() { return currentPlan; }

        @Override
        public void draw()
        {
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());

            if(currentPlan != -1){
                TUnitPlan plan = plans.get(currentPlan);
                Draw.draw(Layer.blockOver, () ->
                        Drawf.construct(this, plan.unit, rotdeg() - 90f,
                                progress / plan.craftTime, speedScl, time));
            }

            Draw.z(Layer.blockOver);
            payRotation = rotdeg();
            drawPayload();

            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(topRegion, x, y);
        }

        @Override
        public int getMaximumAccepted(Item item)
        {
            return Mathf.round(capacities[item.id] * Vars.state.rules.unitCost(team));
        }

        @Override
        public boolean acceptItem(Building source, Item item)
        {
            return currentPlan != -1
                    && items.get(item) < getMaximumAccepted(item)
                    && Structs.contains(plans.get(currentPlan).inputItems, stack -> stack.item == item);
        }

        public @Nullable UnitType unit()
        {
            return currentPlan == -1 ? null : plans.get(currentPlan).unit;
        }

        @Override
        public void write(Writes write)
        {
            super.write(write);
            write.f(progress);
            write.s(currentPlan);
            TypeIO.writeVecNullable(write, commandPos);
            TypeIO.writeCommand(write, command);

            write.s((short) inPayloads.size);
            for(Payload p : inPayloads){
                Payload.write(p, write);
            }
        }

        @Override
        public void read(Reads read, byte revision)
        {
            super.read(read, revision);
            progress = read.f();
            currentPlan = read.s();
            commandPos = TypeIO.readVecNullable(read);
            command = TypeIO.readCommand(read);

            int count = read.s();
            inPayloads.clear();
            for(int i = 0; i < count; i++){
                Payload p = Payload.read(read);
                if(p != null) inPayloads.add(p);
            }
        }
    }
}