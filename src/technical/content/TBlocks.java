package technical.content;

import static mindustry.Vars.tilesize;
import static mindustry.type.ItemStack.with;
import static technical.debug.Debugger.printForced;

import arc.graphics.Color;
import arc.math.Interp;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.entities.part.DrawPart.PartProgress;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ContinuousLiquidTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.environment.Prop;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.draw.DrawArcSmelt;
import mindustry.world.draw.DrawBlurSpin;
import mindustry.world.draw.DrawBubbles;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawGlowRegion;
import mindustry.world.draw.DrawLiquidRegion;
import mindustry.world.draw.DrawLiquidTile;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import mindustry.world.draw.DrawTurret;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.BuildVisibility;
import technical.Fr;
import technical.T;
import technical.TCol;
import technical.expansion.ConveyorCrafter;
import technical.expansion.ConveyorRecipe;
import technical.expansion.DroneCoreExpansion;
import technical.expansion.FacilityController;
import technical.expansion.HelperTurret;
import technical.expansion.Inserter;
import technical.expansion.FacilityFloorTile;
import technical.expansion.FacilityInserter;
import technical.expansion.FacilityLaser;
import technical.expansion.FacilityLoader;
import technical.expansion.FacilityPlan;
import technical.expansion.FacilityPolisher;
import technical.expansion.FacilityStep;
import technical.expansion.FacilityWelder;
import technical.expansion.Recipe;
import technical.expansion.RecipeCrafter;
import technical.expansion.RollerConveyor;
import technical.expansion.RollerTunnel;
import technical.expansion.TechLab;
import technical.expansion.ThermalConduit;
import technical.expansion.ThermalConduitHeater;
import technical.expansion.ThermalPump;
import technical.expansion.ThermalRouter;
import technical.expansion.Volcano;
import technical.expansion.FacilityStep.FacilityStepType;
import technical.expansion.draw.DrawAnimatedRegion;
import technical.expansion.draw.DrawCenteredBurning;
import technical.expansion.draw.DrawCogs;
import technical.expansion.draw.DrawEffect;
import technical.expansion.draw.DrawExtensionLiquid;
import technical.expansion.draw.DrawLiquidOutputsExtendable;
import technical.expansion.draw.DrawMovingRegion;
import technical.expansion.draw.DrawPublicRegion;
import technical.expansion.draw.DrawReactorCore;
import technical.expansion.draw.DrawRingBurning;
import technical.expansion.draw.DrawSlapLiquid;
import technical.expansion.draw.DrawSteamOperator;
import technical.expansion.draw.DrawThermalHeatOverlay;
import technical.expansion.draw.DrawCogs.DrawCog;
import technical.expansion.ext.ExtendableCrafter;
import technical.expansion.ext.ExtendableDrill;
import technical.expansion.ext.ExtendableGenerator;
import technical.expansion.ext.ExtendableWallCrafter;
import technical.expansion.ext.Extension;
import technical.expansion.ext.ExtensionType;
import technical.expansion.kinetic.KineticBlock;
import technical.expansion.kinetic.KineticComponentData;
import technical.expansion.kinetic.KineticEnergy;
import technical.expansion.kinetic.Sprocket;
import technical.expansion.mycelis.MycelisCord;
import technical.expansion.mycelis.MycelisDrill;
import technical.expansion.mycelis.MycelisHeart;
import technical.expansion.mycelis.MycelisRecipe;
import technical.expansion.mycelis.MycelisRecipeCrafter;
import technical.expansion.tech.TechType;
import technical.expansion.train.RailConnector;
import technical.expansion.train.TrainFactory;
import technical.expansion.train.TrainLoader;
import technical.expansion.train.TrainPayloadLoader;
import technical.expansion.trap.BoulderTrap;
import technical.expansion.trap.DartTrap;
import technical.expansion.trap.SickleTrap;
import technical.expansion.trap.SpikeTrap;
import technical.expansion.trap.TrapHook;

public class TBlocks {
    public static Block basic_drone_core_expansion, crossbow, flamethrower,

    basic_rail_connector, copper_train_assembler, train_loader, train_unloader, train_payload_loader, train_payload_unloader, coal_pipe_heater, pipe_cooler, kinetic_energy_source,

    grill_heater, stone_chimney, basic_crusher, liquid_storage, fan, basic_control_panel,solar_heater, stone_boulder_trap, dart_trap, copper_gearbox, facility_floor, facility_loader, facility_unloader,
    drying_pad, iron_drill, iron_trap_hook, mechanical_drill,
    nuclear_reactor, iron_base_applicator, lab, facility_flame_cutter,

    low_temperature_oil_still, gear_bender, rod_roller, porcelain_router, // middle_temperature_oil_still, high_temperature_oil_still

    small_helper_turret, furnace_heater, roller_tunnel, copper_sprocket, brick_boiler, iron_ore, conducted_pipe,
    
    copper_ore, light_stone_wall, pebbles, clay, coal_ore, brick_wall, brick_wall_large, basic_core, basic_spike_trap, porcelain_pipe, zinc_ore,
    
    stone_crusher, roller_conveyor, brick_furnace, flint_extractor, iron_inserter, electric_inserter, mechanical_inserter, basic_ammo_forge, iron_chaingun, sickle_trap, flywheel, iron_gear_applicator, copper_gear_applicator, brick_crucible, basic_mold, mechanical_pump,

    porcelain_furnace, steam_engine, brass_arm, brass_welder, brass_polisher, brass_smasher, continuous_basalt_floor, jasper_floor,

    // ENVIRONMENT
    scoria_floor, spongy_scoria_floor, scoria_wall, scoria_boulder, scoria_volcano, lava_floor,
    gneiss_floor, gneiss_crater_floor, gneiss_wall,
    red_salt_floor, red_salt_boulder,

    // MYCELIS
    mycelis_heart, mycelis_cord, mycelis_cord_iron_plated, mycelis_brutal_drill, mycelis_oxidizer;

    public static void load() 
    {
        ////////////////////////////////////////
        ///            EXTENSIONS            ///
        ////////////////////////////////////////
        
        flywheel = new Extension("flywheel"){{
            requirements(Category.crafting, with());
            techType = TechType.MechanicalWorking;
            health = 300;
            size = 3;
            liquidCapacity = 30;

            type = ExtensionType.MechanicalEnergyCapacitor;
            additionalStorage = 0;
            efficiencyBoost = 30;

            warmupSpeed = 0.001f;

            consumeLiquid(TLiquids.steam, 3 * Fr.liquid);
            consumeKineticEnergy(1 * Fr.speed, 10 * Fr.torque, 30 * Fr.inertia);

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawEffect(0.8f, TFx.littleSmoke).group(3, 5).offset(tilesize), new DrawBlurSpin("-rotator", 18f){{blurThresh = 0.9f;}}, new DrawDefault());
        }};

        basic_control_panel = new Extension("basic-control-panel"){{
            requirements(Category.crafting, with());
            techType = TechType.Control;
            health = 300;
            size = 1;

            type = ExtensionType.Control;
            additionalStorage = 0;
            efficiencyBoost = 1;

            consumePower(300 * Fr.power);

            drawer = new DrawMulti(new DrawAnimatedRegion("", 20f){{turnOff=true;}});
        }};

        grill_heater = new Extension("grill-heater"){{
            requirements(Category.crafting, with());
            techType = TechType.TemperatureManagement;
            health = 100;
            size = 2;

            type = ExtensionType.Heater;
            additionalStorage = 0;
            efficiencyBoost = 40;

            consumeLiquid(Liquids.slag, 10 * Fr.liquid);


            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidRegion(), new DrawDefault(), new DrawGlowRegion(){{glowIntensity = 0.8f; alpha = 0.5f; lightRadius = 5f;}});
        }};

        furnace_heater = new Extension("furnace-heater"){{
            requirements(Category.crafting, with(TItems.stone, 30, TItems.iron_plate, 10));
            techType = TechType.TemperatureManagement;
            health = 100;
            size = 2;

            type = ExtensionType.Heater;
            additionalStorage = 0;
            efficiencyBoost = 30;

            consumeItem(TItems.coal, 1);

            itemDuration = 2 * Fr.time;

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawRingBurning(), new DrawDefault(), new DrawGlowRegion(){{glowIntensity = 0.8f; alpha = 0.5f; lightRadius = 5f;}});
        }};

        solar_heater = new Extension("solar-heater"){{
            requirements(Category.crafting, with(TItems.stone, 50, TItems.iron_plate, 10));
            techType = TechType.TemperatureManagement;
            size = 3;
            health = 160;

            type = ExtensionType.Heater;
            additionalStorage = 0;
            efficiencyBoost = 5;
            
            drawer = new DrawMulti(new DrawDefault(), new DrawEffect(Color.white, 0.5f, TFx.solarFlare));
        }};

        stone_chimney = new Extension("stone-chimney"){{
            requirements(Category.crafting, with(TItems.stone, 50));
            techType = TechType.PollutionManagement;
            health = 100;
            size = 2;

            additionalStorage = 0;
            efficiencyBoost = 5;
            type = ExtensionType.Chimney;


            drawer = new DrawMulti(new DrawDefault(), new DrawEffect());
        }};

        basic_crusher = new Extension("basic-crusher"){{
            requirements(Category.crafting, with(TItems.stone, 100, TItems.iron_plate, 5));
            techType = TechType.MaterialPreparation;
            health = 100;
            size = 2;

            additionalStorage = 0;
            efficiencyBoost = 20;
            type = ExtensionType.Crusher;

            consumeItem(TItems.coal, 1);

            consumeEffect = TFx.coalSmelt;
            itemDuration = 3 * Fr.time;

            drawer = new DrawMulti(new DrawDefault(), new DrawRegion("-rotator", 1f, true), new DrawRegion("-top"));
        }};

        liquid_storage = new Extension("liquid-storage"){{
            requirements(Category.crafting, with(TItems.porcelain, 30, TItems.brick, 30));
            techType = TechType.Storage;
            health = 300;
            size = 3;

            additionalStorage = 0;
            additionalLiquidStorage = 30;
            efficiencyBoost = 0;
            type = ExtensionType.Storage;

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawExtensionLiquid(), new DrawDefault());
        }};

        fan = new Extension("fan"){{
            requirements(Category.crafting, with());
            techType = TechType.TemperatureManagement;
            health = 100;
            size = 2;

            additionalStorage = 0;
            efficiencyBoost = 30;
            type = ExtensionType.Cooler;

            consumePower(60 * Fr.power);
            consumeCoolant(10 * Fr.liquid);

            consumeEffect = Fx.none;

            drawer = new DrawMulti(
                new DrawRegion("-bottom"), 
                new DrawLiquidRegion(){{alpha = 0.75f;}}, 
                new DrawDefault(), 
                new DrawBlurSpin("-rotator", -12f){{blurThresh = 0.8f;}}, 
                new DrawRegion("-top"),
                new DrawSlapLiquid()
            );
        }};

        ////////////////////////////////////////
        ///             CRAFTERS             ///
        ////////////////////////////////////////

        /// CONVEYOR CRAFTERS
        
        iron_gear_applicator = new ConveyorCrafter("iron-gear-applicator"){{
            requirements(Category.crafting, with(TItems.iron_plate, 20, TItems.iron_gear, 10));
            techType = TechType.MechanicalWorking;
            size = 1;
            health = 100;

            performedAction = new ConveyorRecipe.Action(TItems.iron_gear, ConveyorRecipe.Action.Type.Applying);

            consumeItem(TItems.iron_gear);
        }};

        copper_gear_applicator = new ConveyorCrafter("copper-gear-applicator"){{
            requirements(Category.crafting, with(TItems.iron_plate, 20, TItems.copper_gear, 10));
            techType = TechType.MechanicalWorking;
            size = 1;
            health = 100;

            performedAction = new ConveyorRecipe.Action(TItems.copper_gear, ConveyorRecipe.Action.Type.Applying);

            consumeItem(TItems.copper_gear);
        }};

        iron_base_applicator = new ConveyorCrafter("iron-base-applicator"){{
            requirements(Category.crafting, with(TItems.iron_plate, 20));
            techType = TechType.MechanicalWorking;
            size = 1;
            health = 100;

            performedAction = new ConveyorRecipe.Action(TItems.iron_plate, ConveyorRecipe.Action.Type.Applying);

            consumeItem(TItems.iron_plate);
        }};

        /// STILL
        
        low_temperature_oil_still = new ExtendableCrafter("low-temperature-oil-still"){{
            requirements(Category.crafting, with(TItems.stone, 20, TItems.copper_wire, 10));
            techType = TechType.CrudeWorking;
            size = 3;
            health = 500;

            itemCapacity = 10;
            liquidCapacity = 60f;
            hasPower = false;
            hasLiquids = true;

            dumpExtraLiquid = false;

            rotate = true;
            invertFlip = false;
            group = BlockGroup.liquids;
            itemCapacity = 0;

            craftEffect = Fx.none;

            craftTime = Fr.time * 4;

            outputLiquids = LiquidStack.with(TLiquids.wide_oil_fraction, 9 * Fr.liquid, TLiquids.metan, 1 * Fr.liquid);
            liquidOutputDirections = new int[]{1, 3};

            efficiencyCap = 100;
            maxEfficiency = 2;

            AllowedExtensions = Seq.with(
                ExtensionType.Storage,
                ExtensionType.Heater
            );

            RequiredExtensions = T.mapIntOf(
                ExtensionType.Heater, 60
            );

            consumeLiquid(TLiquids.crude_oil, 10 * Fr.liquid);

            // drawer = new DrawMulti(new DrawDefault(), new DrawEffect(Color.cyan, 0.5f, TFx.solarFlare), new DrawLiquidRegion(){{alpha = 0.5f;}});

            drawer = new DrawMulti(
                new DrawPublicRegion("still-bottom"),
                new DrawLiquidTile(TLiquids.crude_oil, 2f),
                new DrawBubbles(Color.valueOf("#484a50ff")){{
                    sides = 10;
                    recurrence = 3f;
                    spread = 6;
                    radius = 1.5f;
                    amount = 20;
                }},
                new DrawSteamOperator("still-operator", Color.valueOf("#484a50ff")){{public_bundle = true;}},
                new DrawPublicRegion("still-top"),
                new DrawLiquidOutputsExtendable()
            );
        }};

        /// OTHER
        
        gear_bender = new RecipeCrafter("gear-bender"){{
            requirements(Category.crafting, with(TItems.iron_plate, 30, TItems.brick, 30));
            techType = TechType.CrudeWorking;
            size = 2;
            health = 200;

            itemCapacity = 30;
            liquidCapacity = 0f;
            craftEffect = TFx.bend;

            maxEfficiency = 2;

            AllowedExtensions = Seq.with(
                ExtensionType.Storage,
                ExtensionType.MechanicalEnergyCapacitor
            );
            
            recipes = Seq.with(
                new Recipe(
                    ItemStack.with(TItems.copper_plate, 1), null,
                    ItemStack.with(TItems.copper_gear, 1), null,
                    2 * Fr.time, 0, 100, null
                ),
                new Recipe(
                    ItemStack.with(TItems.iron_plate, 1), null,
                    ItemStack.with(TItems.iron_gear, 1), null,
                    4 * Fr.time, 0, 150, null
                )
            );

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawAnimatedRegion("-top", 30), new DrawDefault());
        }};

        rod_roller = new RecipeCrafter("rod-roller"){{
            requirements(Category.crafting, with(TItems.precision_mechanism, 30, TItems.brick, 30));
            techType = TechType.CrudeWorking;
            size = 3;
            health = 200;

            itemCapacity = 30;
            liquidCapacity = 0f;
            updateEffect = TFx.roll;

            maxEfficiency = 2;

            AllowedExtensions = Seq.with(
                ExtensionType.Storage,
                ExtensionType.MechanicalEnergyCapacitor
            );
            
            recipes = Seq.with(
                new Recipe(
                    ItemStack.with(TItems.copper_plate, 3), null,
                    ItemStack.with(TItems.copper_rod, 1), null,
                    4 * Fr.time, 0, 100, new KineticEnergy(5 * Fr.speed, 10 * Fr.torque)
                ),
                new Recipe(
                    ItemStack.with(TItems.iron_plate, 3), null,
                    ItemStack.with(TItems.iron_rod, 1), null,
                    8 * Fr.time, 0, 150, new KineticEnergy(10 * Fr.speed, 20 * Fr.torque)
                )
            );

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawAnimatedRegion("-roller", 30), new DrawDefault());
        }};

        porcelain_furnace = new ExtendableCrafter("porcelain-furnace"){{
            requirements(Category.crafting, with(TItems.porcelain, 50, TItems.iron_plate, 20));
            techType = TechType.SoftWorking;
            size = 2;
            health = 300;

            itemCapacity = 20;
            liquidCapacity = 10f;

            hasPower = false;

            craftTime = 6 * Fr.time;
            outputItem = new ItemStack(TItems.porcelain, 1);
            craftEffect = Fx.smeltsmoke;

            efficiencyCap = 100;
            maxEfficiency = 2;

            RequiredExtensions = T.mapIntOf(
                ExtensionType.Chimney, 10,
                ExtensionType.Heater, 30
            );

            AllowedExtensions = Seq.with(
                ExtensionType.Heater,
                ExtensionType.Storage,
                ExtensionType.Chimney
            );

            consumeItems(with(TItems.clay, 3));
            consumeLiquids(LiquidStack.with(TLiquids.water, 5 * Fr.liquid));

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawArcSmelt(), new DrawDefault());
        }};

        drying_pad = new ExtendableCrafter("drying-pad"){{
            requirements(Category.crafting, with(TItems.stone, 10));
            techType = TechType.CrudeWorking;
            size = 2;
            health = 100;

            itemCapacity = 10;
            liquidCapacity = 0f;
            hasPower = false;
            hasLiquids = false;

            craftTime = 6 * Fr.time;
            outputItem = new ItemStack(TItems.brick, 1);
            craftEffect = Fx.smeltsmoke;

            efficiencyCap = 10;
            maxEfficiency = 2;

            AllowedExtensions = Seq.with(
                ExtensionType.Storage,
                ExtensionType.Heater
            );

            consumeItems(with(TItems.clay, 1));

            drawer = new DrawMulti(new DrawDefault(), new DrawEffect(Color.white, 0.5f, TFx.solarFlare));
        }};

        brick_furnace = new ExtendableCrafter("brick-furnace"){{
            requirements(Category.crafting, with(TItems.brick, 30, TItems.stone, 30));
            techType = TechType.Metallurgy;
            size = 3;
            health = 300;
            itemCapacity = 20;
            liquidCapacity = 0f;

            hasPower = false;
            hasLiquids = false;

            craftTime = 4 * Fr.time;
            outputItem = new ItemStack(TItems.iron_plate, 1);
            craftEffect = Fx.smeltsmoke;

            efficiencyCap = 50;
            maxEfficiency = 2;

            RequiredExtensions = T.mapIntOf(
                ExtensionType.Chimney, 5
            );

            AllowedExtensions = Seq.with(
                ExtensionType.Heater,
                ExtensionType.Storage,
                ExtensionType.Chimney
            );

            consumeItems(with(TItems.coal, 1, TItems.raw_iron, 2));

            drawer = new DrawMulti(new DrawDefault(), new DrawEffect(TCol.iron, 10f, TFx.hugeSmoke));
        }};

        flint_extractor = new ExtendableCrafter("flint-extractor"){{
            requirements(Category.crafting, with(TItems.porcelain, 30, TItems.precision_mechanism, 5));
            techType = TechType.SoftWorking;
            size = 3;
            health = 200;
            itemCapacity = 20;
            liquidCapacity = 30f;

            hasItems = true;
            hasPower = false;
            hasLiquids = true;

            outputsLiquid = false;

            craftEffect = Fx.smeltsmoke;

            maxEfficiency = 3;

            RequiredExtensions = T.mapIntOf();
            RequiredAttribute = TAttributes.clay;
            MinimumAttribute = 1f * size * size;

            AllowedExtensions = Seq.with(
                ExtensionType.Storage
            );

            consumeKineticEnergy(5 * Fr.speed, 10 * Fr.torque, 10 * Fr.inertia);

            consumeLiquid(TLiquids.water, 5 * Fr.liquid);
            consumeItem(TItems.clay, 10);

            outputItems = with(TItems.flint, 1);

            drawer = new DrawMulti(
                new DrawRegion("-bottom"), 
                new DrawLiquidRegion(),
                new DrawMovingRegion("-grid", Vec2.ZERO, Vec2.ZERO, 180),
                new DrawDefault()
            );
        }};

        brick_crucible = new RecipeCrafter("brick-crucible"){{
            requirements(Category.crafting, with(TItems.brick, 100, TItems.iron_plate, 100));
            techType = TechType.Metallurgy;
            size = 4;
            health = 600;
            itemCapacity = 30;
            liquidCapacity = 40f;

            hasItems = true;
            hasPower = false;
            hasLiquids = true;

            outputsLiquid = true;

            craftEffect = TFx.coalSmelt;

            maxEfficiency = 3;

            RequiredExtensions = T.mapIntOf(
                ExtensionType.Heater, 90,
                ExtensionType.Crusher, 20
            );

            AllowedExtensions = Seq.with(
                ExtensionType.Heater,
                ExtensionType.Storage,
                ExtensionType.Chimney,
                ExtensionType.Crusher
            );

            recipes = Seq.with(
                new Recipe(
                    ItemStack.with(TItems.raw_copper, 5), null,
                    null, LiquidStack.with(TLiquids.molten_copper, 5 * Fr.liquid),
                    Fr.time * 2, 0, 200, null
                ),
                new Recipe(
                    ItemStack.with(TItems.raw_iron, 5), null,
                    null, LiquidStack.with(TLiquids.molten_iron, 5 * Fr.liquid),
                    Fr.time * 4, 0, 200, null
                )
            );

            workingTemperature = 110;

            drawer = new DrawMulti(
                new DrawRegion("-bottom"), 
                new DrawLiquidRegion(), 
                new DrawBubbles(TCol.copper){{
                    sides = 10;
                    recurrence = 3f;
                    spread = 6;
                    radius = 1.5f;
                    amount = 20;
                }},
                new DrawRingBurning(){{particleLife = 60f; particleSize = 0.4f; ringRadius = 10f; clusters = 12; clusterSizeMin = 6; clusterSizeMax = 12; particleGrow = 2.4f;}}, 
                new DrawDefault()
            );
        }};

        brick_boiler = new ExtendableCrafter("brick-boiler"){{
            requirements(Category.crafting, with(TItems.brick, 10, TItems.iron_plate, 10));
            techType = TechType.SteamWorking;
            size = 2;
            health = 200;
            itemCapacity = 10;
            liquidCapacity = 0;

            hasLiquids = false;

            craftEffect = TFx.coalSmelt;

            maxEfficiency = 2.5f;

            consumeItems(with(TItems.coal, 1));

            AllowedExtensions = Seq.with(
                ExtensionType.Heater,
                ExtensionType.Storage
            );

            workingTemperature = 30;

            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawGlowRegion(),
                new DrawDefault()
            );
        }};

        basic_mold = new RecipeCrafter("basic-mold"){{
            requirements(Category.crafting, with(TItems.brick, 10, TItems.iron_plate, 20));
            techType = TechType.Metallurgy;
            size = 2;
            health = 200;
            itemCapacity = 30;
            liquidCapacity = 20f;

            hasItems = true;
            hasPower = false;
            hasLiquids = true;

            craftEffect = Fx.smeltsmoke;

            maxEfficiency = 2;

            AllowedExtensions = Seq.with(
                ExtensionType.Cooler,
                ExtensionType.Storage
            );

            recipes = Seq.with(
                new Recipe(
                    null, LiquidStack.with(TLiquids.molten_copper, 1 * Fr.liquid),
                    ItemStack.with(TItems.copper_plate, 1), null,
                    Fr.time * 4, 0, 100, null
                ),
                new Recipe(
                    null, LiquidStack.with(TLiquids.molten_iron, 1 * Fr.liquid),
                    ItemStack.with(TItems.iron_plate, 1), null,
                    Fr.time * 8, 0, 100, null
                )
            );

            // workingTemperature = 110;

            drawer = new DrawMulti(
                new DrawRegion("-bottom"), 
                new DrawLiquidRegion(), 
                new DrawEffect(TCol.copper, 10f, TFx.smoke),
                new DrawDefault()
            );
        }};

        basic_ammo_forge = new ExtendableCrafter("basic-ammo-forge"){{
            requirements(Category.crafting, with(TItems.brick, 30, TItems.iron_plate, 30));
            techType = TechType.WeaponWorking;

            size = 2;
            health = 300;
            itemCapacity = 20;
            liquidCapacity = 0f;

            hasPower = false;
            hasLiquids = false;

            craftTime = Fr.time * 4;
            outputItem = new ItemStack(TItems.dense_ammo, 1);
            craftEffect = Fx.smeltsmoke;

            efficiencyCap = 100;
            maxEfficiency = 2;

            RequiredExtensions = T.mapIntOf(
                ExtensionType.Chimney, 10,
                ExtensionType.Crusher, 20
            );

            AllowedExtensions = Seq.with(
                ExtensionType.Crusher,
                ExtensionType.Storage,
                ExtensionType.Chimney
            );

            consumeItems(with(TItems.coal, 1, TItems.iron_plate, 1));

            // drawer = new DrawMulti(new DrawDefault(), new DrawArcSmelt(){{flameRad = 0.7f; circleSpace = 1f; particleLife = 32f; particleStroke = 0.9f;}}, new DrawRegion("-flamecover"));
            drawer = new DrawMulti(new DrawDefault(), new DrawCenteredBurning(), new DrawRegion("-flamecover"));
        }};

        ////////////////////////////////////////
        ///              SCIENCE             ///
        ////////////////////////////////////////

        lab = new TechLab("lab"){{
            requirements(Category.effect, with(TItems.porcelain, 100, TItems.precision_mechanism, 10));
            techType = TechType.Research;
            size = 3;
            health = 300;

            range = 6;

            itemCapacity = 30;
            efficiencyCap = 100;

            craftTime = 6 * Fr.time;

            researchedTechType = TechType.Metallurgy;
            techItem = TItems.metallurgy_xp;

            // consumeKineticEnergy(10 * Fr.angularSpeed, 10 * Fr.torque, 30);

            kineticData = new KineticComponentData(null, 30 * Fr.inertia);
            baseInput = new KineticEnergy(5 * Fr.speed, 10 * Fr.torque);

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawDefault());
        }};

        ////////////////////////////////////////
        ///          KINETIC ENERGY          ///
        ////////////////////////////////////////
        
        kinetic_energy_source = new KineticBlock("kinetic-energy-source"){{
            requirements(Category.power, BuildVisibility.sandboxOnly, with());
            alwaysUnlocked = true;

            kineticData = new KineticComponentData(new KineticEnergy(1000, 1000), 1);
        }};

        copper_gearbox = new ExtendableCrafter("copper-gearbox"){{
            requirements(Category.crafting, with(TItems.copper_gear, 10, TItems.copper_plate, 20));
            techType = TechType.MechanicalPowerTransport;
            size = 2;
            health = 200;

            hasItems = false;
            hasPower = false;
            hasLiquids = false;

            craftEffect = TFx.bend;

            maxEfficiency = 1;

            kineticData = new KineticComponentData(new KineticEnergy(20 * Fr.speed, 10 * Fr.torque), 10 * Fr.inertia);
            consumeKineticEnergy(10 * Fr.speed, 20 * Fr.torque);

            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawCogs(Seq.with(
                    new DrawCog("copper-cog", new Point2(-3, -3), 0.4f, -2f, 0),
                    new DrawCog("copper-cog", new Point2(2, 2), 0.8f, 1f, 0.5f)
                ))
            );
        }};
        
        steam_engine = new ExtendableCrafter("steam-engine"){{
            requirements(Category.power, with(TItems.iron_plate, 30, TItems.precision_mechanism, 3, TItems.copper_gear, 30));
            techType = TechType.MechanicalPowerProduction;

            size = 2;
            health = 300;
            itemCapacity = 20;
            liquidCapacity = 10f;

            hasPower = false;
            hasLiquids = true;
            efficiencyCap = 100;
            maxEfficiency = 3;

            kineticData = new KineticComponentData(new KineticEnergy(5 * Fr.speed, 30 * Fr.torque), 10 * Fr.inertia);

            multiblockBonus = 0.5f;

            RequiredExtensions = T.mapIntOf();
            AllowedExtensions = Seq.with();

            consumeLiquid(TLiquids.steam, 1 * Fr.liquid);

            craftTime = 6 * Fr.time;

            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawRegion("-half-0"),
                new DrawMovingRegion("-rod", Vec2.ZERO, new Vec2(0, 4f), 0, Interp.fastSlow){{orthoLayering=true;}},
                new DrawRegion("-half-1"),
                new DrawEffect(Color.white, 0.5f, TFx.steamLeak){{groupMax = 3; randomRotation = true; offset = 4f;}}
            );
        }};

        copper_sprocket = new Sprocket("copper-sprocket"){{
            requirements(Category.power, with(TItems.iron_plate, 1, TItems.copper_gear, 1));
            techType = TechType.MechanicalPowerTransport;

            size = 1;
            health = 50;

            kineticData = new KineticComponentData(null, 1 * Fr.inertia);
        }};

        ////////////////////////////////////////
        ///               POWER              ///
        ////////////////////////////////////////

        nuclear_reactor = new ExtendableGenerator("nuclear-reactor"){{
            requirements(Category.power, with());
            techType = TechType.PowerProduction;
            size = 4;
            health = 300;
            itemCapacity = 100;
            liquidCapacity = 100f;

            consumeEffect = TFx.uraniumImpact;
            itemDuration = 6 * Fr.time;

            efficiencyCap = 150;
            maxEfficiency = 3;

            outputLiquid = new LiquidStack(TLiquids.toxic_waste, 10 * Fr.liquid);
            explodeOnFull = true;

            RequiredExtensions = T.mapIntOf(
                ExtensionType.Chimney, 10,
                ExtensionType.Heater, 30,
                ExtensionType.Control, 1
            );

            AllowedExtensions = Seq.with(
                ExtensionType.Chimney,
                ExtensionType.Heater,
                ExtensionType.Cooler,
                ExtensionType.Storage,
                ExtensionType.Control
            );

            isUnstable = true;

            explodeEffect = TFx.uraniumReactorExplosion;
            explodeSound = Sounds.blockExplode3;

            consumeLiquid(Liquids.water, 10 * Fr.liquid);
            consumeItem(TItems.uranium, 3);

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawReactorCore(){{lightRadius = 30f;}}, new DrawDefault());
        }};

        ////////////////////////////////////////
        ///              LIQUID              ///
        ////////////////////////////////////////
        
        porcelain_pipe = new ThermalConduit("porcelain-pipe"){{
            requirements(Category.liquid, with(TItems.iron_gear, 1, TItems.porcelain, 1));
            techType = TechType.LiquidTransportation;
            size = 1;
            health = 100;

            liquidCapacity = 5f;

            heatLeakage = 0.05f;
            heatResistance = 3;
            maxHeat = 50;
        }};

        conducted_pipe = new ThermalConduit("conducted-pipe"){{
            requirements(Category.liquid, with(TItems.copper_plate, 4, TItems.porcelain, 3));
            techType = TechType.LiquidTransportation;
            size = 1;
            health = 100;

            liquidCapacity = 1f;

            heatLeakage = 0.025f;
            heatResistance = 2;
            maxHeat = 120;

            leaks = false;

            botColor = TCol.copper_black;
        }};

        porcelain_router = new ThermalRouter("porcelain-router"){{
            requirements(Category.liquid, with(TItems.iron_gear, 3, TItems.porcelain, 10));
            techType = TechType.LiquidTransportation;
            size = 1;
            health = 100;

            squareSprite = false;

            liquidCapacity = 6f;
        }};

        coal_pipe_heater = new ThermalConduitHeater("coal-pipe-heater"){{
            requirements(Category.liquid, with());
            techType = TechType.LiquidTransportation;
            size = 1;
            health = 100;

            liquidCapacity = 3;
            itemCapacity = 10;

            heatOutput = 120;
            itemDuration = 4 * Fr.time;

            consumeItem(TItems.coal, 1);
        }};

        mechanical_pump = new ThermalPump("mechanical-pump"){{
            requirements(Category.liquid, with(TItems.iron_plate, 10, TItems.precision_mechanism, 5));
            techType = TechType.LiquidTransportation;
            size = 2;
            health = 200;

            liquidCapacity = 10f;
            pumpAmount = 1 * Fr.liquid;

            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidRegion(),
                new DrawBubbles(TLiquids.water.color),
                new DrawBlurSpin("-rotator", -10f){{blurThresh = 100;}},
                new DrawDefault(),
                new DrawRegion("-top"),
                new DrawThermalHeatOverlay()
            );
        }};

        pipe_cooler = new ThermalConduitHeater("pipe-cooler"){{
            requirements(Category.liquid, with());
            techType = TechType.LiquidTransportation;
            size = 1;
            health = 300;

            liquidCapacity = 5;
            itemCapacity = 10;

            heatOutput = -10;
            itemDuration = 4 * Fr.time;

            consumeItem(TItems.coal, 1);
        }};

        ////////////////////////////////////////
        ///            TRANSPORT             ///
        ////////////////////////////////////////
        
        ///////// NOT TRAINS ///////////

        roller_tunnel = new RollerTunnel("roller-tunnel"){{
            requirements(Category.distribution, with(TItems.iron_plate, 10, TItems.iron_gear, 10));
            techType = TechType.Transportation;
            size = 1;
            health = 300;

            speed = 0.05f;
            displayedSpeed = 5f;
        }};

        roller_conveyor = new RollerConveyor("roller-conveyor"){{
            requirements(Category.distribution, with(TItems.iron_plate, 1, TItems.stone, 1));
            techType = TechType.Transportation;
            size = 1;
            health = 100;

            tunnelReplacement = roller_tunnel;

            speed = 0.05f;
            displayedSpeed = 5f;
        }};

        iron_inserter = new Inserter("iron-inserter"){{
            requirements(Category.distribution, with(TItems.iron_plate, 5, TItems.iron_gear, 5));
            techType = TechType.Transportation;
            size = 1;
            health = 100;
            itemCapacity = 5;

            maxStackSize = 1;
            moveTime = 1 * Fr.time;

            consumeItem(TItems.coal);
            itemDuration = 12 * Fr.time;
        }};

        mechanical_inserter = new Inserter("mechanical-inserter"){{
            requirements(Category.distribution, with(TItems.copper_plate, 10, TItems.precision_mechanism, 1));
            techType = TechType.Transportation;
            size = 1;
            health = 100;
            itemCapacity = 0;

            consumeEffect = Fx.none;

            maxStackSize = 3;
            moveTime = 0.5f * Fr.time; // TODO golden rule broken

            consumeKineticEnergy(1 * Fr.speed, 0 * Fr.torque, 1 * Fr.inertia);
        }};

        electric_inserter = new Inserter("electric-inserter"){{
            requirements(Category.distribution, with());
            techType = TechType.Transportation;
            size = 1;
            health = 200;

            maxStackSize = 10;
            moveTime = 10f;

            consumePower(20 * Fr.power);
        }};

        ///////// TRAINS /////////

        basic_rail_connector = new RailConnector("basic-rail-connector"){{
            requirements(Category.distribution, with());
            techType = TechType.Transportation;
            health = 100;
            size = 1;
        }};

        copper_train_assembler = new TrainFactory("copper-train-assembler"){{
            requirements(Category.distribution, with(TItems.copper_plate, 30, TItems.precision_mechanism, 2));
            techType = TechType.Transportation;
            health = 100;
            size = 3;

            unitType = TUnits.basic_train;

            itemCapacity = 60;
            unitRequirements = with(Items.silicon, 60, Items.lead, 30);

            consumePower(90 * Fr.power);
        }};

        train_loader = new TrainLoader("train-loader"){{
            requirements(Category.distribution, with());
            techType = TechType.Transportation;
            health = 100;
            size = 3;

            itemCapacity = 1000;
        }};

        train_unloader = new TrainLoader("train-unloader"){{
            requirements(Category.distribution, with());
            techType = TechType.Transportation;
            health = 100;
            size = 3;

            itemCapacity = 1000;

            isUnloader = true;
        }};

        train_payload_loader = new TrainPayloadLoader("train-payload-loader"){{
            requirements(Category.distribution, with());
            techType = TechType.Transportation;
            health = 100;
            size = 3;

            itemCapacity = 1000;
        }};

        train_payload_unloader = new TrainPayloadLoader("train-payload-unloader"){{
            requirements(Category.distribution, with());
            techType = TechType.Transportation;
            health = 100;
            size = 3;

            itemCapacity = 1000;

            isUnloader = true;
        }};

        ////////////////////////////////////////
        ///              DRILLS              ///
        ////////////////////////////////////////

        iron_drill = new ExtendableDrill("iron-drill"){{
            requirements(Category.production, with(TItems.stone, 20, TItems.iron_plate, 10));
            techType = TechType.Mining;
            health = 200;
            size = 3;
            itemCapacity = 20;
            hasPower = false;
            hasLiquids = false;

            drillTime = 9 * Fr.time; // for full ore connection is 1
            hardnessDrillMultiplier = 400f;
            tier = 1; // ONLY FOR COMMIT TESTING

            maxEfficiency = 2;
            efficiencyCap = 50;

            itemDuration = 5 * Fr.time;
            consumeEffect = TFx.coalSmelt;

            consumeItems(with(TItems.coal, 1));

            AllowedExtensions = Seq.with(
                ExtensionType.Cooler
            );

            // RequiredExtensions = T.mapIntOf(
            //     ExtensionType.Cooler, 10
            // );

            drawer = new DrawMulti(new DrawDefault(), new DrawBlurSpin("-rotator", 1f){{blurThresh = 100f;}}, new DrawRegion("-top"));
        }};

        mechanical_drill = new ExtendableDrill("mechanical-drill"){{
            requirements(Category.production, with(TItems.iron_plate, 20, TItems.precision_mechanism, 5, TItems.copper_gear, 10));
            techType = TechType.Mining;
            health = 500;
            size = 3;
            itemCapacity = 30;
            hasPower = false;
            hasLiquids = false;

            drillTime = 9 * 0.5f * Fr.time; // for full ore connection is 0.5
            hardnessDrillMultiplier = 400f;
            tier = 2;

            maxEfficiency = 2;
            efficiencyCap = 100;

            consumeKineticEnergy(5 * Fr.speed, 10 * Fr.torque, 10 * Fr.inertia);

            AllowedExtensions = Seq.with(
                ExtensionType.Cooler
            );

            // RequiredExtensions = T.mapIntOf(
            //     ExtensionType.Cooler, 10
            // );

            drawer = new DrawMulti(new DrawDefault(), new DrawBlurSpin("-rotator", 1f){{blurThresh = 100f;}}, new DrawRegion("-top"));
        }};

        stone_crusher = new ExtendableWallCrafter("stone-crusher"){{
            requirements(Category.production, with());
            techType = TechType.Mining;
            health = 100;
            size = 2;

            attribute = TAttributes.stone;
            drillTime = 3 * Fr.time;
            output = TItems.stone;

            // RequiredExtensions = T.mapIntOf(
            //     ExtensionType.Crusher, 1
            // );

            AllowedExtensions = Seq.with(
                ExtensionType.Crusher,
                ExtensionType.Control
            );

            consumeItem(TItems.coal, 1);
        }};

        ////////////////////////////////////////
        ///             SPECIAL              ///
        ////////////////////////////////////////

        basic_drone_core_expansion = new DroneCoreExpansion("basic-drone-core-expansion"){{
            requirements(Category.effect, with());
            // techType = TechType.Core;
            health = 100;
            size = 3;

            itemCapacity = 1000;
        }};

        basic_core = new CoreBlock("basic-core"){{
            requirements(Category.effect, with(TItems.iron_plate, 1000, TItems.stone, 800));
            // techType = TechType.Core;
            health = 3000;
            itemCapacity = 2000;
            size = 3;

            buildCostMultiplier = 0.7f;

            isFirstTier = true;
            unitType = TUnits.onset;

            thrusterLength = 35/4f;
            armor = 6f;

            alwaysUnlocked = true;
            incinerateNonBuildable = true;
            requiresCoreZone = true;

            unitCapModifier = 10;

            researchCostMultiplier = 0.07f;
        }};


        ////////////////////////////////////////
        ///              UNITS               ///
        ////////////////////////////////////////

        small_helper_turret = new HelperTurret("small-helper-turret"){{
            requirements(Category.turret, with(TItems.iron_plate, 50, TItems.copper_wire, 30));
            health = 150;
            size = 2;

            shootSound = Sounds.shootCyclone;
            targetUnderBlocks = false;
            shake = 2f;

            reload = 60f;
            recoil = 5f;
            range = 100;
            shootCone = 1f;
            rotateSpeed = 0.5f;
            inaccuracy = 2f;

            drawer = new DrawTurret("t"){{
                parts.add(new RegionPart("-barrel"){{
                    progress = PartProgress.recoil;
                    under = true;
                    moveY = -1f;
                }});
            }};
            
            ammo(
                TItems.dense_ammo,
                TWeapons.helper_package
            );

            limitRange();
        }};

        ////////////////////////////////////////
        ///             DEFENSE              ///
        ////////////////////////////////////////

        brick_wall = new Wall("brick-wall"){{
            requirements(Category.defense, with(TItems.brick, 6));
            // techType = TechType.Defense;
            health = 2137 / 4;
            armor = 1;
            size = 1;
        }};

        brick_wall_large = new Wall("brick-wall-large"){{
            requirements(Category.defense, with(TItems.brick, 6*4));
            // techType = TechType.Defense;
            health = 2137;
            size = 2;
            armor = 1;
        }};

        iron_trap_hook = new TrapHook("iron-trap-hook"){{
            requirements(Category.defense, with(TItems.iron_plate, 20, TItems.iron_gear, 5, TItems.precision_mechanism, 3));
            techType = TechType.Traping;
            health = 50;
            size = 1;

            consumeKineticEnergy(1 * Fr.speed, 3 * Fr.torque, 1 * Fr.inertia);

            range = 10;
        }};

        stone_boulder_trap = new BoulderTrap("stone-boulder-trap"){{
            requirements(Category.defense, with(TItems.iron_plate, 10, TItems.iron_gear, 10, TItems.stone, 150));
            techType = TechType.Traping;
            health = 50;
            size = 2;

            consumeKineticEnergy(1 * Fr.speed, 3 * Fr.torque, 1 * Fr.inertia);

            consumeItem(TItems.stone, 10);

            boulder = TWeapons.stone_boulder;
        }};

        basic_spike_trap = new SpikeTrap("basic-spike-trap"){{
            requirements(Category.defense, with(TItems.iron_plate, 20, TItems.flint, 10, TItems.precision_mechanism, 3));
            techType = TechType.Traping;
            health = 300;
            size = 2;

            consumeKineticEnergy(5 * Fr.speed, 1 * Fr.torque, 5 * Fr.inertia);
        }};

        sickle_trap = new SickleTrap("sickle-trap"){{
            requirements(Category.defense, with(TItems.iron_plate, 60, TItems.flint, 30, TItems.precision_mechanism, 20));
            techType = TechType.Traping;
            health = 300;
            size = 2;

            consumeKineticEnergy(10 * Fr.speed, 20 * Fr.torque, 10 * Fr.inertia);
        }};

        dart_trap = new DartTrap("dart-trap"){{
            requirements(Category.defense, with(TItems.iron_plate, 10, TItems.flint, 10, TItems.precision_mechanism, 3));
            techType = TechType.Traping;
            health = 300;
            size = 2;

            consumeKineticEnergy(5 * Fr.speed, 1 * Fr.torque, 3 * Fr.inertia);
        }};

        crossbow = new ItemTurret("crossbow"){{
            requirements(Category.turret, with(TItems.iron_plate, 50, TItems.precision_mechanism, 10));
            // techType = TechType.HardAmmoTurrets;
            health = 150;
            size = 3;

            shootSound = TSounds.crossbow;
            targetUnderBlocks = false;
            shake = 1f;

            reload = 120f;
            recoil = 1f;
            range = 300;
            shootCone = 5f;
            rotateSpeed = 1f;
            inaccuracy = 2f;

            // minWarmup = 0.5f;
            // shootWarmupSpeed = 0.03f;

            drawer = new DrawTurret("t"){{
                parts.add(new RegionPart("-arrow"){{
                    progress = PartProgress.reload;

                    outline = true;
                    y = 2;

                    moveY = -3;

                    layerOffset = 1;
                }});
                parts.add(new RegionPart("-top"){{
                    layerOffset = 2;
                }});
            }};
            
            ammo(
                TItems.iron_plate,
                TWeapons.iron_arrow
            );

            // limitRange();
        }};

        flamethrower = new ContinuousLiquidTurret("flamethrower"){{
            requirements(Category.turret, with(TItems.iron_plate, 50));
            health = 150;
            size = 2;

            shootSound = Sounds.shootFlame;
            targetUnderBlocks = false;

            reload = shoot.shots * shoot.shotDelay;
            recoil = 2f;
            range = 100;
            shootCone = 5f;
            rotateSpeed = 1.5f;

            shootY = 9f;

            warmupMaintainTime = 60f;
            shootWarmupSpeed = 0.01f;
            minWarmup = 0.7f;

            drawer = new DrawTurret("t"){{
                parts.add(new RegionPart("-blade"){{
                    drawRegion = true;
                    under = true;
                    mirror = true;

                    rotate = true;
                    progress = PartProgress.warmup;
                    moveRot = -15;

                    heatLight = true;
                }});

                parts.add(new RegionPart("-glow"){{
                    drawRegion = false;

                    heatProgress = PartProgress.warmup;
                }});
            }};
            
            ammo(
                TLiquids.metan,
                TWeapons.metan_beam
            );
        }};

        brass_smasher = new ItemTurret("brass-smasher"){{
            requirements(Category.turret, with());
            // techType = TechType.HardAmmoTurrets;
            health = 150;
            size = 3;

            shootSound = Sounds.explosionDull;
            targetUnderBlocks = false;
            shake = 2.5f;

            shoot.shotDelay = shoot.firstShotDelay = 30f;

            range = 170;
            shootCone = 5f;
            rotateSpeed = 1.5f;
            inaccuracy = 5f;
            reload = 60f;

            targetAir = false;

            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawRegion("-half-0"),
                new DrawMovingRegion("-rod", new Vec2(0,6f), new Vec2(0,-7f), 0, Interp.pow3OutInverse){{orthoLayering=true;}},
                new DrawRegion("-half-1"),
                new DrawEffect(TCol.brass, 0.5f, TFx.steamLeak){{groupMin = 2; groupMax = 4; randomRotation = true; offset = 6f;}}
            );
            
            ammo(
                TItems.iron_rod,
                TWeapons.ground_crack
            );
        }};

        iron_chaingun = new ItemTurret("iron-chaingun"){{
            requirements(Category.turret, with(TItems.iron_plate, 50));
            // techType = TechType.HardAmmoTurrets;
            health = 150;
            size = 2;

            shootSound = Sounds.shootAlpha;
            targetUnderBlocks = false;
            shake = 1f;

            // shoot = new ShootPattern(){{
            //     shots = 5;
            //     shotDelay = 5f;
            // }};

            shoot = new ShootBarrel(){{
                barrels = new float[]{
                    -2, 0, 0,
                    -1, 0, 0,
                    0, 0, 0,
                    1, 0, 0,
                    2, 0, 0
                };
                shots = 5;
                shotDelay = 5f;
            }};

            reload = shoot.shots * shoot.shotDelay;
            recoil = 2f;
            range = 190;
            shootCone = 5f;
            rotateSpeed = 1.5f;
            inaccuracy = 5f;

            drawer = new DrawTurret("t"){{
                parts.add(new RegionPart("-barrel"){{
                    progress = PartProgress.recoil;
                    under = true;
                    moveY = -1f;
                }});
            }};
            
            ammo(
                TItems.dense_ammo,
                TWeapons.dense_iron_bullet
            );

            limitRange();
        }};

        ////////////////////////////////////////
        ///             MYCELIS              ///
        ////////////////////////////////////////
        
        mycelis_heart = new MycelisHeart("mycelis-heart"){{
            requirements(Category.effect, with());
            size = 3;
        }};

        mycelis_cord_iron_plated = new MycelisCord("mycelis-cord-iron-plated"){{
            requirements(Category.effect, with());
            health = 200;
            size = 1;
            regionName = "technical-mycelis-cord";
            platingRegionName = "technical-mycelis-iron-plating";
        }};

        mycelis_cord = new MycelisCord("mycelis-cord"){{
            requirements(Category.effect, with());
            health = 50;
            size = 1;
            evolution = mycelis_cord_iron_plated;
            platingRegionName = null;
            regionName = null;
        }};

        mycelis_brutal_drill = new MycelisDrill("mycelis-brutal-drill"){{
            requirements(Category.effect, with());
            health = 150;
            size = 3;
        }};

        mycelis_oxidizer = new MycelisRecipeCrafter("mycelis-oxidizer"){{
            requirements(Category.effect, with());
            health = 350;
            size = 3;

            itemCapacity = 30;
            liquidCapacity = 60f;

            recipes = Seq.with(
                new MycelisRecipe(
                    new ItemStack(TItems.raw_iron, 1), null,
                    new ItemStack(TItems.iron_plate, 1), null,
                    Fr.time * 2
                ),
                new MycelisRecipe(
                    new ItemStack(TItems.clay, 1), null,
                    new ItemStack(TItems.brick, 1), null,
                    Fr.time * 2
                )
            );
        }};

        ////////////////////////////////////////
        ///        FACILITY ASSEMBLING       ///
        ////////////////////////////////////////
        
        facility_floor = new FacilityFloorTile("facility-floor"){{
            requirements(Category.crafting, with());
            techType = TechType.Control;
            health = 100;
            size = 1;

            rotate = false;
        }};

        facility_loader = new FacilityLoader("facility-loader"){{
            requirements(Category.crafting, with());
            techType = TechType.Control;
            health = 100;
            size = 3;
        }};

        facility_unloader = new FacilityController("facility-unloader"){{
            requirements(Category.crafting, with());
            techType = TechType.Control;
            health = 100;
            size = 3;

            plans = Seq.with(
                new FacilityPlan(
                    ItemStack.with(Items.copper, 100), null, PayloadStack.with(Blocks.copperWall, 10),
                    new PayloadStack(Blocks.duo),
                    0, null,
                    Seq.with(
                        new FacilityStep(FacilityStepType.Welding, 3, 2 * Fr.time),
                        new FacilityStep(FacilityStepType.Polishing, 3, 2 * Fr.time)
                    )
                ),
                new FacilityPlan(
                    ItemStack.with(Items.copper, 200), null, PayloadStack.with(Blocks.copperWall, 20),
                    new PayloadStack(Blocks.salvo),
                    0, null,
                    Seq.with(
                        new FacilityStep(FacilityStepType.Welding, 3, 2 * Fr.time),
                        new FacilityStep(FacilityStepType.Polishing, 3, 2 * Fr.time)
                    )
                )
            );

            printForced(plans.get(0).inputPayloads[0]);
        }};

        facility_flame_cutter = new FacilityLaser("facility-flame-cutter"){{
            requirements(Category.crafting, with());
            techType = TechType.Control;
            health = 100;
            size = 2;

            stepType = FacilityStepType.Cutting;
        }};

        brass_arm = new FacilityInserter("brass-arm"){{
            requirements(Category.crafting, with());
            techType = TechType.Control;
            health = 100;
            size = 2;

            stepType = FacilityStepType.Preparing;
        }};

        brass_welder = new FacilityWelder("brass-welder"){{
            requirements(Category.crafting, with());
            techType = TechType.Control;
            health = 100;
            size = 2;

            stepType = FacilityStepType.Welding;
        }};

        brass_polisher = new FacilityPolisher("brass-polisher"){{
            requirements(Category.crafting, with());
            techType = TechType.Control;
            health = 100;
            size = 2;

            stepType = FacilityStepType.Polishing;
        }};

        ////////////////////////////////////////
        ///           ENVIRONMENT            ///
        ////////////////////////////////////////

        copper_ore = new OreBlock("copper-ore", TItems.raw_copper){{
            variants = 3;
        }};

        iron_ore = new OreBlock("iron-ore", TItems.raw_iron){{
            variants = 3;
        }};

        coal_ore = new OreBlock("coal-ore", TItems.coal){{
            variants = 3;
        }};

        zinc_ore = new OreBlock("zinc-ore", TItems.raw_zinc){{
            variants = 3;
        }};

        light_stone_wall = new StaticWall("light-stone-wall"){{
            variants = 3;
            attributes.set(TAttributes.stone, 0.8f);
        }};

        pebbles = new OreBlock("pebbles", TItems.stone){{
            variants = 3;
        }};

        continuous_basalt_floor = new Floor("continuous-basalt-floor"){{
            variants = 2;
        }};

        jasper_floor = new Floor("jasper-floor"){{
            variants = 3;
        }};

        gneiss_floor = new Floor("gneiss-floor"){{
            variants = 3;
        }};

        gneiss_crater_floor = new Floor("gneiss-crater-floor"){{
            variants = 3;
        }};

        gneiss_wall = new StaticWall("gneiss-wall"){{
            variants = 2;
            attributes.set(TAttributes.stone, 0.2f);
        }};

        scoria_floor = new Floor("scoria-floor"){{
            variants = 3;
        }};

        spongy_scoria_floor = new Floor("spongy-scoria-floor"){{
            variants = 3;
        }};

        scoria_boulder = new Prop("scoria-boulder"){{
            variants = 2;
        }};

        scoria_volcano = new Volcano("scoria-volcano"){{
            variants = 1;
            parent = blendGroup = scoria_floor;
        }};

        red_salt_floor = new Floor("red-salt-floor"){{
            variants = 3;
        }};

        red_salt_boulder = new Prop("red-salt-boulder"){{
            variants = 2;
        }};

        scoria_wall = new StaticWall("scoria-wall"){{
            variants = 2;
        }};

        lava_floor = new Floor("lava-floor"){{
            drownTime = 230f;
            status = StatusEffects.melting;
            statusDuration = 240f;
            speedMultiplier = 0.19f;
            variants = 0;
            liquidDrop = Liquids.slag;
            isLiquid = true;
            cacheLayer = TShaders.lavaLayer;
            attributes.set(Attribute.heat, 0.85f);

            emitLight = true;
            lightRadius = 40f;
            lightColor = Color.orange.cpy().a(0.38f);
            obstructsLight = true;
            forceDrawLight = true;
        }};


        clay = new Floor("clay-floor"){{
            itemDrop = TItems.clay;
            playerUnmineable = false;
            variants = 3;
            attributes.set(TAttributes.clay, 1f);
        }};
    }   
}


        
        // /// CIRCUIT
        
        // circuit_cutter = new ConveyorCrafter("circuit-cutter"){{
        //     requirements(Category.crafting, with(TItems.copper_wire, 10, TItems.iron_plate, 20));
        //     techType = TechType.CircuitCrafting;
        //     size = 1;
        //     health = 100;

        //     performedAction = new ConveyorRecipe.Action(null, ConveyorRecipe.Action.Type.Cutting);
        // }};

        // circuit_conductor_applicator = new ConveyorCrafter("circuit-conductor-applicator"){{
        //     requirements(Category.crafting, with(TItems.copper_wire, 10, TItems.iron_plate, 20));
        //     techType = TechType.CircuitCrafting;
        //     size = 1;
        //     health = 100;

        //     performedAction = new ConveyorRecipe.Action(TItems.copper_wire, ConveyorRecipe.Action.Type.Applying);

        //     consumeItem(TItems.copper_wire, 3);
        // }};


        // unit_inserter = new UnitInserter("unit-inserter"){{
        //     requirements(Category.distribution, with(TItems.iron_plate, 20));
        //     techType = TechType.Transportation;
        //     size = 1;
        //     health = 150;

        //     consumeItem(TItems.coal, 1);
        //     itemDuration = 360f;
        // }};

/*



        iron_catapult = new ItemCatapult("iron-catapult"){{
            requirements(Category.distribution, with(TItems.iron_plate, 10));
            techType = TechType.Transportation;
            size = 1;
            health = 100;
        }};

stone_furnace = new ExtendableCrafter("stone-furnace"){{
            requirements(Category.crafting, with());
            size = 3;
            health = 150;

            itemCapacity = 10;
            liquidCapacity = 10 / 60f;
            hasPower = true;
            hasLiquids = true;

            ambientSound = Sounds.smelter;
            ambientSoundVolume = 0.12f;

            craftTime = 60f;
            outputItem = new ItemStack(Items.silicon, 3);
            craftEffect = Fx.smeltsmoke;

            efficiencyCap = 100;
            maxEfficiency = 2;

            RequiredExtensions = T.mapIntOf(
                ExtensionType.Chimney, 10,
                ExtensionType.Crusher, 20
            );

            AllowedExtensions = Seq.with(
                ExtensionType.Chimney,
                ExtensionType.Crusher,
                ExtensionType.Heater,
                ExtensionType.Storage
            );

            consumeLiquid(Liquids.water, 1f / 60f);
            consumeItems(with(Items.sand, 1, Items.coal, 1));
            consumePower(90 / 60f);

            drawer = new DrawMulti(new DrawDefault(), new DrawFlame());
        }};



        cogwheel = new Cogwheel("cogwheel"){{
            requirements(Category.power, with());
            
            health = 100;
            size = 1;
        }};
 */

// public static Block glass_core, water_extractor, torch, magnet_core, core_expansion,

    // // units
    // sulphur_unit_factory, missile_fabricator, electrical_unit_factory,

    // // envirnoment
    // meltium_ore, salt_vent, carbon_sand,
    
    // // sulphur
    // uranium_ore, sulphur_floor, sulphur_wall, sulphur_fluid_floor,
    
    // // magnetium
    // ore_magnetium, magnetium_floor, dense_magnetium_floor, magnetium_tree, magnetium_cluster_tree, magnetium_wall, magnetium_vent,

    // // heat
    // slag_heater,
    
    // // power
    // sulphur_burner, electrical_beam_node, sulphur_fluid_generator,
    
    // // crafters
    // silicon_smelter, hydrogenator, glass_furnace, electricicator, scrap_melter, thermogen_infuser,
    
    // // conduits
    // glass_conduit, glass_conduit_bridge, glass_conduit_router, glass_conduit_junction, glass_tank,

    // // ducts
    // lead_duct, lead_duct_bridge, lead_duct_router, glass_sorter, glass_cargo_loader, glass_cargo_unloader,

    // // turrets
    // water_turret, alphus, coiler, bolt_shotgun, backstabber, sandstorm, feltarion, allien,

    // // defense
    // silicon_wall, large_silicon_wall, protective_glass_wall, silicon_door,
    
    // //drills
    // burst_drill, sulphur_extractor, plasma_beam_drill, wall_crusher
    // ;

    // public static void load() {

    //     // DEFENSE

    //     int wallHealthMultiplier = 6;

    //     silicon_door = new AutoDoor("silicon-door"){{
    //         requirements(Category.defense, with(Items.silicon, 4 * 4 * 2, AourusItems.glass, 4));
    //         scaledHealth = 80 * wallHealthMultiplier;
    //         size = 2;

    //         buildCostMultiplier = 16;

    //         researchCostMultiplier = 8/30f; // 1/30 build price
    //     }};

    //     silicon_wall = new Wall("silicon-wall"){{
    //         requirements(Category.defense, with(Items.silicon, 4));
    //         scaledHealth = 80 * wallHealthMultiplier;

    //         buildCostMultiplier = 16;

    //         // researchCost = ItemStack.with(Items.silicon, 20);
    //         researchCostMultiplier = 3/30f; // 1/30 build price
    //     }};

    //     large_silicon_wall = new Wall("large-silicon-wall"){{
    //         requirements(Category.defense, with(Items.silicon, 4 * 4));
    //         size = 2;
    //         scaledHealth = 80 * wallHealthMultiplier;

    //         buildCostMultiplier = 16;

    //         // researchCost = ItemStack.with(Items.silicon, 200);
    //         researchCostMultiplier = 8/30f; // 1/30 build price
    //     }};

    //     protective_glass_wall = new Wall("protective-glass-wall"){{
    //         requirements(Category.defense, with(AourusItems.glass, 8, Items.silicon, 16));
    //         health = 120 * wallHealthMultiplier * 4;
    //         size = 2;
    //         chanceDeflect = 8;
    //         insulated = true;
    //         absorbLasers = true;
    //         schematicPriority = 10;
    //         researchCostMultiplier = 8/30f; // 1/30 build price
    //     }};
        
    //     // PRODUCTION

    //     wall_crusher = new WallCrafter("wall-crusher"){{
    //         size = 3;
    //         health = 600;

    //         drillTime = 60f;

    //         output = Items.sand;

    //         ambientSound = Sounds.drill;

    //         consumePower(120 / 60f);

    //         attribute = Attribute.get("sand");

    //         consumeLiquid(Liquids.hydrogen, 10f / 60f).boost();
            
    //         itemCapacity = 20;
    //         // boostItemUseTime = 60f / 0.75f;


    //         // researchCost = ItemStack.with(Items.lead, 300, AourusItems.meltium, 100);

    //         researchCostMultiplier = 20/30f; // 1/30 build price

    //         requirements(Category.production, ItemStack.with(Items.silicon, 100, AourusItems.electric_alloy, 50));
    //     }};

    //     plasma_beam_drill = new BeamDrill("plasma-beam-drill") {{
    //         size = 3;
    //         health = 500;
    //         tier = 3;
    //         range = 4;
    //         drillTime = 60f * 12;

    //         consumePower(160f / 60f);

    //         itemCapacity = 100;

    //         tier = 2;
    //         optionalBoostIntensity = 1.8f;

    //         // boostHeatColor = Color.sky.cpy().mul(0.87f);
    //         // heatColor = new Color(1f, 0.35f, 0.35f, 0.9f);

    //         heatColor = TCol.sulphur;
    //         boostHeatColor = Liquids.hydrogen.color;

    //         sparkColor = AourusItems.sulphur_fluid.color;

    //         // heatPulse = 0.3f;
    //         // heatPulseScl = 7f;

    //         // fogRadius = 1;

    //         // color = Color.valueOf("ff6000");

    //         consumeLiquid(AourusItems.sulphur_fluid, 2f / 60f);
    //         consumeLiquid(Liquids.hydrogen, 30f / 60f).boost();

    //         // drillMultipliers.put(AourusItems.meltium, 0.5f);
    //         researchCostMultiplier = 5/30f; // 1/30 build price

    //         // researchCost = ItemStack.with(Items.silicon, 1000, AourusItems.glass, 1000);

    //         requirements(Category.production, ItemStack.with(Items.silicon, 200, AourusItems.glass, 100));
    //     }};

    //     water_extractor = new AttributeCrafter("vent-water-extractor") {{
    //         attribute = Attribute.steam;
    //         // group = BlockGroup.liquids;
    //         minEfficiency = 9f - 0.0001f;
    //         baseEfficiency = 0;
    //         displayEfficiency = false;
    //         craftEffect = Fx.turbinegenerate;
    //         drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawBlurSpin("-rotator", 6f));
    //         craftTime = 120f;
    //         size = 3;
    //         ambientSound = Sounds.hum;
    //         ambientSoundVolume = 0.06f;
    //         hasLiquids = true;
    //         boostScale = 1f / 9f;
    //         itemCapacity = 0;
    //         outputLiquid = new LiquidStack(Liquids.water, 30f / 60f);
    //         liquidCapacity = 60f;

    //         health = 150;

    //         consumePower(30f/60);

    //         // researchCost = ItemStack.with(Items.lead, 20, AourusItems.meltium, 20);
    //         researchCostMultiplier = 0.5f/30f; // 1/30 build price

    //         requirements(Category.production, ItemStack.with(AourusItems.meltium, 30, Items.lead, 60));
    //     }};

    //     burst_drill = new BurstDrill("burst-drill"){{
    //         size = 4;
    //         drillTime = 60f * 4.5f;
    //         tier = 1;
    //         drillEffect = new MultiEffect(Fx.mineImpact, Fx.drillSteam, Fx.mineImpactWave.wrap(Color.valueOf("#fe80f5"), 40f));
    //         shake = 0f;
    //         itemCapacity = 40;
    //         health = 150;

    //         // AourusItems

    //         researchCostMultiplier = 0.5f/30f; // 1/30 build price

    //         arrows = 2;
    //         arrowColor = Color.valueOf("#fe80f5");
    //         fogRadius = 1;

    //         shake = 3f;

    //         health = 80;

    //         liquidBoostIntensity = 1.5f;

    //         drillMultipliers.put(AourusItems.meltium, 0.5f);
    //         drillMultipliers.put(Items.sand, 0.5f);

    //         consumePower(10/60f);

    //         consumeLiquid(Liquids.hydrogen, 3f / 60f).boost();

    //         requirements(Category.production, ItemStack.with(Items.lead, 20, AourusItems.meltium, 20));
    //     }};
    
    //     sulphur_extractor = new WallCrafter("sulphur-extractor") {{
    //         size = 2;
    //         health = 100;

    //         drillTime = 90f;

    //         output = AourusItems.sulphur;

    //         ambientSound = Sounds.drill;

    //         consumePower(20 / 60f);

    //         attribute = Attribute.get("sulphur");

    //         // researchCost = ItemStack.with(Items.lead, 300, AourusItems.meltium, 100);

    //         researchCostMultiplier = 6/30f; // 1/30 build price

    //         requirements(Category.production, ItemStack.with(Items.lead, 50, AourusItems.meltium, 50));
    //     }};

    //     // TURRETS

    //     feltarion = new PayloadAmmoTurret("feltarion"){{
    //         requirements(Category.turret, with(AourusItems.meltium, 500, Items.silicon, 500));
    //         size = 4;

    //         rotateDraw = true;
            
    //         minWarmup = 0.94f;
    //         targetInterval = 40f;
    //         unitSort = UnitSorts.strongest;
    //         shootWarmupSpeed = 0.03f;
    //         targetAir = false;
    //         targetUnderBlocks = false;

    //         shake = 3f;
    //         ammoPerShot = 1;
    //         maxAmmo = 3;
    //         shootY = -1;
    //         outlineColor = Pal.darkOutline;

    //         shootSound = Sounds.missileLaunch;
            
    //         reload = 500f;
    //         range = 800;
    //         shootCone = 1f;
    //         health = 400;
    //         rotateSpeed = 0.75f;

    //         ammo(AourusMisc.sulphur_missile, AourusWeapons.feltarion_missile);

    //         coolant = consumeCoolant(0.1f);
            
    //         researchCostMultiplier = 4/30f; // 1/30 build price

    //         drawer = new DrawTurret("reinforced-")
    //         {{
    //             parts.add(
    //             new RegionPart("-mid"){{
    //                 progress = PartProgress.recoil;
    //                 heatProgress = PartProgress.warmup.add(-0.2f).add(p -> Mathf.sin(9f, 0.2f) * p.warmup);
    //                 heatColor = TCol.sulphur;
    //                 mirror = false;
    //                 under = true;
    //                 y = 2;
    //                 moveY = -5f;
    //             }}, 
    //             new RegionPart("-blade"){{
    //                 progress = PartProgress.warmup;
    //                 heatProgress = PartProgress.warmup;
    //                 heatColor = TCol.sulphur;
    //                 moveRot = -16f;
    //                 layerOffset = 2;
    //                 heatLayerOffset = 10;
    //                 // moveX = 0f;
    //                 moveY = -4f;
    //                 x = 8;
    //                 y = 0;
    //                 mirror = true;
    //             }},
    //             new RegionPart("-missile"){{
    //                 progress = PartProgress.reload.curve(Interp.pow2In);

    //                 colorTo = new Color(0.5f, 0.5f, 0.5f, 0f);
    //                 color = Color.white;
    //                 mixColorTo = Pal.darkOutline;
    //                 mixColor = new Color(1f, 1f, 1f, 0f);
    //                 outline = false;
    //                 y = 2;

    //                 layerOffset = 1;

    //                 moves.add(new PartMove(PartProgress.warmup.inv(), 0f, -3f, 0f));
    //             }});
    //         }};
    //     }};

    //     allien = new ContinuousLiquidTurret("allien"){{
    //         requirements(Category.turret, with(AourusItems.meltium, 500, Items.silicon, 500));
    //         size = 4;

    //         ammo(
    //             Liquids.hydrogen, new PointLaserBulletType(){{
    //                 sprite = "laser-white";
    //                 damage = 210f;
    //                 buildingDamageMultiplier = 0.3f;
    //                 hitColor = Color.valueOf("718eff");
    //                 color = Color.valueOf("718eff");
    //                 oscMag = 0.5f;
    //                 oscScl = 2;
    //                 beamEffect = Fx.hitFlameBeam;
    //             }}
    //         );

    //         loopSoundVolume = 1f;
    //         loopSound = Sounds.laserbeam;

    //         liquidConsumed = 90f / 60f;

    //         shootY = 6f;

    //         rotateSpeed = 0.35f;

    //         range = 200f;
    //         var haloProgress = PartProgress.warmup;
    //         Color haloColor = Color.valueOf("718eff");
    //         float haloY = 8f, haloRotSpeed = 1.5f, haloX = 4f;

    //         aimChangeSpeed = 0.9f;
    //         rotateSpeed = 0.9f;

    //         drawer = new DrawTurret("reinforced-")
    //         {{
    //             parts.addAll(
                
    //             new RegionPart("-mid"){{
    //                 progress = PartProgress.warmup;
    //                 heatProgress = PartProgress.warmup;
    //                 // heatColor = ACol.sulphur;
    //                 mirror = false;
    //                 under = false;
    //                 moveY = 7f;
    //             }},
    //             new RegionPart("-mag"){{
    //                 progress = PartProgress.warmup;
    //                 heatProgress = PartProgress.warmup;
    //                 // heatColor = ACol.sulphur;
    //                 under = false;
    //                 moveRot = -26f;
    //                 // moveX = 0f;
    //                 moveY = -3f;
    //                 mirror = true;

    //                 layerOffset = 1;
    //                 heatLayerOffset = 2;

    //                 children.addAll(
    //                     new ShapePart(){{
    //                     progress = haloProgress;
    //                     color = haloColor;
    //                     circle = true;
    //                     hollow = true;
    //                     stroke = 0f;
    //                     strokeTo = 1.5f;
    //                     radius = 5f;
    //                     layer = Layer.effect;
    //                     y = haloY;
    //                     x = haloX;
    //                 }},

    //                     new ShapePart(){{
    //                         progress = haloProgress;
    //                         color = haloColor;
    //                         sides = 3;
    //                         rotation = 90f;
    //                         hollow = true;
    //                         stroke = 0f;
    //                         strokeTo = 1.5f;
    //                         radius = 2f;
    //                         layer = Layer.effect;
    //                         y = haloY;
    //                         x = haloX;
    //                     }},

    //                     new HaloPart(){{
    //                         progress = haloProgress;
    //                         color = haloColor;
    //                         sides = 3;
    //                         tri = true;
    //                         shapes = 3;
    //                         triLength = 0f;
    //                         triLengthTo = 6f;
    //                         shapeRotation = 180f;
    //                         radius = 3f;
    //                         haloRadius = 8f;
    //                         haloRotateSpeed = -haloRotSpeed;
    //                         haloRotation = 180f / 3f;
    //                         layer = Layer.effect;
    //                         y = haloY;
    //                         x = haloX;
    //                     }},

    //                     new HaloPart(){{
    //                         progress = haloProgress;
    //                         color = haloColor;
    //                         sides = 3;
    //                         tri = true;
    //                         shapes = 3;
    //                         triLength = 0f;
    //                         triLengthTo = 3f;
    //                         radius = 3f;
    //                         haloRadius = 8f;
    //                         haloRotateSpeed = -haloRotSpeed;
    //                         haloRotation = 180f / 3f;
    //                         layer = Layer.effect;
    //                         y = haloY;
    //                         x = haloX;
    //                     }}
    //                 );
    //             }},
    //             new RegionPart("-side"){{
    //                 progress = PartProgress.warmup;
    //                 heatProgress = PartProgress.warmup;
    //                 // heatColor = ACol.sulphur;
    //                 moveRot = -18f;
    //                 under = true;
    //                 // moveX = 0f;
    //                 moveY = -2f;
    //                 mirror = true;
    //             }}
    //             );
    //         }};

    //     }};

    //     sandstorm = new ItemTurret("sandstorm"){{
    //         requirements(Category.turret, with(Items.silicon, 50, Items.lead, 50));
    //         ammo(
    //             Items.sand, new BasicBulletType(){{
    //                 damage = 60;
    //                 speed = 8.5f;
    //                 width = height = 8;
    //                 shrinkY = 0.3f;
    //                 backSprite = "large-bomb-back";
    //                 sprite = "mine-bullet";
    //                 velocityRnd = 0.11f;
    //                 collidesGround = false;
    //                 collidesTiles = false;
    //                 shootEffect = Fx.shootSmall;
    //                 smokeEffect = Fx.shootSmallSmoke;
    //                 frontColor = Color.white;
    //                 backColor = trailColor = hitColor = TCol.sand;
    //                 trailChance = 0.44f;
    //                 ammoMultiplier = 6f;

    //                 lifetime = 30f;
    //                 rotationOffset = 90f;
    //                 trailRotation = true;
    //                 trailEffect = AourusEffects.sandstorm_trail;

    //                 hitEffect = despawnEffect = Fx.hitBulletColor;
    //             }}
    //         );

    //         drawer = new DrawTurret("reinforced-"){{
    //             for(int i = 3; i > 0; i--){
    //                 int f = i;
    //                 parts.add(new RegionPart("-barrel-" + i){{
    //                     progress = PartProgress.recoil;
    //                     recoilIndex = f - 1;
    //                     under = true;
    //                     moveY = -2f;
    //                 }});
    //             }
    //         }};

    //         reload = 5f;
    //         // shootY = 15f;
    //         rotateSpeed = 5f;
    //         shootCone = 30f;
    //         consumeAmmoOnce = true;

    //         range = 250;

    //         size = 3;
    //         targetGround = false;

    //         recoils = 3;
    //         shoot = new ShootBarrel(){{
    //             barrels = new float[]{
    //             0f, 1f, 0f,
    //             3f, 0f, 0f,
    //             -3f, 0f, 0f,
    //             };
    //         }};

    //         ammoPerShot = 3;
    //         itemCapacity = ammoPerShot * 10;

    //         recoil = 0.5f;
    //         rotateSpeed = 5f;
    //         inaccuracy = 18f;
    //         shootCone = 35f;

    //         scaledHealth = 200;
    //         shootSound = Sounds.shootSnap;
    //         coolant = consumeCoolant(0.2f);
            
    //         researchCostMultiplier = 5/30f; // 1/30 build price
    //     }};

    //     bolt_shotgun = new PowerTurret("bolt-shotgun"){{
    //         drawer = new DrawTurret("reinforced-");
    //         size = 3;
    //         health = 1200;
    //         reload = 60f;
    //         range = 250f;

    //         heatColor = Color.valueOf("ffa500");

    //         shootCone = 120f;
    //         inaccuracy = 12f;
    //         recoil = 2f;

    //         targetAir = true;
    //         targetGround = false;

    //         rotateSpeed = 1f;

    //         shootSound = Sounds.shotgun;

    //         ammoPerShot = 3;

    //         shoot = new ShootPattern(){{
    //             shots = 3;
    //             shotDelay = 2.5f;
    //         }};

    //         shootEffect = Fx.shootSmall;

    //         consumePower(90 / 60f);

    //         coolant = consumeCoolant(10f);
    //         coolantMultiplier = 0.02f;

    //         shootType = AourusWeapons.bolts;

    //         researchCostMultiplier = 20/30f; // 1/30 build price

    //         requirements(Category.turret, with(AourusItems.glass, 20, Items.silicon, 80));
    //     }};

    //     water_turret = new LiquidTurret("water-turret"){{
    //         drawer = new DrawTurret("reinforced-");
    //         ammo(
    //             Liquids.water, new LiquidBulletType(Liquids.water){{
    //                 lifetime = 35f;
    //                 speed = 4f;
    //                 knockback = 0.3f;
    //                 puddleSize = 8f;
    //                 orbSize = 4f;
    //                 drag = 0.001f;
    //                 ammoMultiplier = 0.4f;
    //                 statusDuration = 60f * 4f;
    //                 damage = 5f;
    //                 layer = Layer.bullet - 2f;
    //             }}
    //         );
    //         size = 2;
    //         reload = 3f;
    //         shoot.shots = 2;
    //         velocityRnd = 0.1f;
    //         inaccuracy = 4f;
    //         recoil = 1f;
    //         shootCone = 45f;
    //         liquidCapacity = 20f;
    //         shootEffect = Fx.shootLiquid;
    //         range = 150f;
    //         health = 100;
    //         flags = EnumSet.of(BlockFlag.turret, BlockFlag.extinguisher);

    //         // researchCost = ItemStack.with(Items.lead, 50, AourusItems.meltium, 50);
    //         researchCostMultiplier = 0.5f/30f; // 1/30 build price

    //         requirements(Category.turret, with(Items.lead, 100, AourusItems.meltium, 50));
    //     }};
        
    //     alphus = new ItemTurret("alphus"){{
    //         drawer = new DrawTurret("reinforced-");
    //         size = 3;
    //         reload = 30f;
    //         range = 200f;
            
    //         shootCone = 10f;
    //         inaccuracy = 5f;
    //         recoil = 1.5f;

    //         ammoPerShot = 3;

    //         itemCapacity = ammoPerShot * 6;

    //         ammo(
    //             AourusItems.sulphur, AourusWeapons.sulphur_missile
    //         );

    //         shootEffect = Fx.shootSmall;

    //         health = 250;

    //         coolant = consumeCoolant(10f);
    //         coolantMultiplier = 0.1f;

    //         // researchCost = ItemStack.with(AourusItems.meltium, 100, Items.silicon, 100);
    //         researchCostMultiplier = 6/30f; // 1/30 build price

    //         requirements(Category.turret, with(AourusItems.meltium, 100, Items.silicon, 80));
    //     }};
        
    //     coiler = new ContinuousLiquidTurret("coiler"){{
    //         drawer = new DrawTurret("reinforced-");
    //         requirements(Category.turret, with(Items.silicon, 200, AourusItems.glass, 50));

    //         liquidCapacity = 30f;
    //         liquidConsumed = 20f / 60f;
    //         targetInterval = 5f;
            
    //         targetUnderBlocks = false;

    //         range = 100f;

    //         loopSound = Sounds.torch;
    //         shootSound = Sounds.none;
    //         loopSoundVolume = 1f;

    //         consumePower(30f / 60f);

    //         ammo(
    //             Liquids.hydrogen, AourusWeapons.hydrogen_beam
    //         );

    //         health = 500;
    //         shootY = 7f;
    //         size = 3;

    //         researchCostMultiplier = 4/30f; // 1/30 build price
    //     }};

    //     backstabber = new ItemTurret("backstabber"){{
    //         size = 4;
    //         health = 1200;
    //         reload = 100f;

    //         range = 250f;

    //         shootCone = 10f;
    //         inaccuracy = 6f;
    //         recoil = 2f;

    //         targetAir = true;
    //         targetGround = true;

    //         rotateSpeed = 1f;

    //         ammoPerShot = 2;

    //         consumePower(90 / 60f);

    //         coolant = consumeCoolant(30f / 60);
    //         coolantMultiplier = 0.02f;

    //         researchCostMultiplier = 90/30f; // 1/30 build price

    //         requirements(Category.turret, with(AourusItems.glass, 60, Items.silicon, 200, AourusItems.electric_alloy, 60));

    //         ammo(
    //             AourusItems.electric_alloy, new BasicBulletType(){{
    //                 shootEffect = new MultiEffect(Fx.shootTitan, new WaveEffect(){{
    //                     colorTo = Pal.surge;
    //                     sizeTo = 26f;
    //                     lifetime = 14f;
    //                     strokeFrom = 4f;
    //                 }});
    //                 // smokeEffect = Fx.shootSmokeTitan;
    //                 hitColor = Pal.surge;

    //                 sprite = "large-orb";
    //                 trailEffect = Fx.missileTrail;
    //                 trailInterval = 6f;
    //                 trailParam = 5f;
    //                 pierceCap = 3;
    //                 buildingDamageMultiplier = 0.25f;
    //                 fragOnHit = false;
    //                 speed = 4f;
    //                 damage = 100f;
    //                 lifetime = 40f;
    //                 width = height = 18f;
    //                 backColor = Pal.surge;
    //                 frontColor = Color.white;
    //                 shrinkX = shrinkY = 1;
    //                 trailColor = Pal.surge;
    //                 trailLength = 20;
    //                 trailWidth = 3f;

    //                 despawnSound = Sounds.dullExplosion;

    //                 shootSound = Sounds.cannon;

    //                 intervalBullet = new BasicBulletType(3f, 30){{
    //                     width = 9f;
    //                     hitSize = 5f;
    //                     height = 15f;
    //                     pierceCap = 3;
    //                     lifetime = 20f;
    //                     pierceBuilding = true;
    //                     hitColor = backColor = trailColor = Pal.surge;
    //                     frontColor = Color.white;
    //                     trailWidth = 2.1f;
    //                     trailLength = 5;

    //                     buildingDamageMultiplier = 0.25f;
    //                     homingPower = 0.2f;

    //                     fragBullet  = new LightningBulletType()
    //                     {{
    //                         lightningColor = hitColor = TCol.electricity;
    //                         damage = 10f;
    //                         lightningLength = 8;
    //                         lightningLengthRand = 1;
    //                         shootEffect = Fx.none;
    //                         despawnEffect = AourusEffects.hit_electric;
    //                         pierceArmor = true;
    //                     }};

    //                     fragBullets = 1;
    //                 }};

    //                 bulletInterval = 2.5f;
    //                 intervalRandomSpread = 4f;
    //                 intervalBullets = 4;
    //                 intervalAngle = 180f;
    //                 intervalSpread = 120f;
    //             }}
    //         );


    //         drawer = new DrawTurret("reinforced-"){{
    //             parts.add(new RegionPart("-riffle"){{
    //                 progress = PartProgress.recoil;
    //                 heatColor = Color.valueOf("ff6214");
    //                 // mirror = true;
    //                 under = false;
    //                 moveX = 0;
    //                 moveY = -3f;
    //                 // moveRot = -7f;
    //             }});
    //         }};
    //     }};

    //     // HEAT

    //     slag_heater = new HeatProducer("slag-heater"){{
    //         requirements(Category.crafting, with(Items.silicon, 50, AourusItems.meltium, 80));

    //         researchCostMultiplier = 5/60f;

    //         drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.slag), new DrawDefault(), new DrawHeatOutput());
    //         size = 2;
    //         itemCapacity = 0;
    //         liquidCapacity = 20f;
    //         rotateDraw = false;
    //         regionRotated1 = 1;
    //         ambientSound = Sounds.hum;
    //         consumeLiquid(Liquids.slag, 8f / 60f);
    //         heatOutput = 3f;
    //     }};

    //     // slag_heater = new Generice

    //     // POWER

    //     sulphur_fluid_generator = new ConsumeGenerator("sulphur-fluid-generator") {{
    //         size = 3;
    //         health = 300;
    //         itemCapacity = 10;

    //         // itemDuration = 60f;
    //         powerProduction = 900 / 60f;

    //         explodeOnFull = true;

    //         liquidCapacity = 120f/ 60;

    //         hasLiquids = true;

    //         drawer = new DrawMulti(
    //             new DrawRegion("-bottom"), 
    //             new DrawLiquidRegion(AourusItems.sulphur_fluid),
    //             new DrawParticles(){{
    //                 color = TCol.sulphur_light;
    //                 alpha = 0.7f;
    //                 particleSize = 10f;
    //                 particles = 20;
    //                 particleRad = 10f;
    //                 particleLife = 250f;
    //                 reverse = true;
    //                 particleSizeInterp = Interp.pow2In;
    //             }},
    //             new DrawDefault());

    //         explosionRadius = 9;
    //         explosionDamage = 500;
    //         explodeEffect = new MultiEffect(Fx.bigShockwave, new WrapEffect(Fx.titanSmoke, AourusItems.sulphur_fluid.color), AourusEffects.sulphurSplat);
    //         explodeSound = Sounds.largeExplosion;

    //         explosionPuddles = 50;
    //         explosionPuddleRange = 8 * 7f;
    //         explosionPuddleLiquid = AourusItems.sulphur_fluid;
    //         explosionPuddleAmount = 200f;
    //         explosionMinWarmup = 0.5f;

    //         ambientSound = Sounds.hum;
    //         ambientSoundVolume = 0.24f;
    //         size = 3;
    //         // health = 700;
    //         // itemDuration = 360f;

    //         researchCostMultiplier = 20/30f; // 1/30 build price

    //         outputLiquid = new LiquidStack(AourusItems.sulphur_fluid, 20f / 60f);

    //         consumeItems(with(AourusItems.sulphur, 3));
    //         consumeLiquid(Liquids.hydrogen, 10f / 60f);


    //         consumeLiquid(Liquids.water, 30f / 60f);

    //         requirements(Category.power, ItemStack.with(AourusItems.glass, 100, Items.silicon, 200));
    //     }};

    //     sulphur_burner = new ThermalGenerator("sulphur-burner"){{

    //         attribute = Attribute.get("sulphur");
    //         size = 3;
    //         health = 160;
    //         // itemCapacity = 10;

    //         powerProduction = 60f / 60f;

    //         ambientSound = Sounds.smelter;
    //         ambientSoundVolume = 0.6f;

    //         drawer = new DrawMulti(new DrawDefault(), new DrawArcSmelt(), new DrawRegion("-top"));

    //         // researchCost = ItemStack.with(Items.lead, 10, AourusItems.meltium, 10);
    //         researchCostMultiplier = 0.2f/30f; // 1/30 build price

    //         requirements(Category.power, ItemStack.with(Items.lead, 50, AourusItems.meltium, 30));
    //     }};
    
    //     electrical_beam_node = new BeamNode("electrical-beam-node"){{
    //         requirements(Category.power, with(AourusItems.meltium, 8));
    //         consumesPower = outputsPower = true;
    //         health = 50;
    //         range = 10;
    //         fogRadius = 1;
    //         researchCost = with(AourusItems.meltium, 10);
    //         buildCostMultiplier = 1f;

    //         researchCostMultiplier = 1/30f; // 1/30 build price

    //         laserColor1 = Color.valueOf("ff6000");
    //         laserColor2 = Color.valueOf("a93f00");

    //         consumePowerBuffered(1000f);
    //     }};
        
    //     // CRAFTERS

    //     hydrogenator = new GenericCrafter("hydrogenator") {{
    //         size = 3;
    //         health = 160;
    //         itemCapacity = 10;

    //         craftTime = 10f;

    //         group = BlockGroup.liquids;

    //         craftTime = 60f;

    //         consumeLiquid(Liquids.water, 30f / 60f);
    //         consumePower(60 / 60f);

    //         drawer = new DrawMulti(
    //             new DrawRegion("-bottom"), 
    //             new DrawLiquidTile(Liquids.water, 2f),
    //             new DrawBubbles(Color.valueOf("7693e3")){{
    //                 sides = 10;
    //                 recurrence = 3f;
    //                 spread = 6;
    //                 radius = 1.5f;
    //                 amount = 20;
    //             }},
    //             new DrawDefault()
    //         );

    //         // outputItem = new ItemStack(Liquids.hydrogen, 4);

    //         // craftEffect = Fx.smeltsmoke;

    //         hasPower = true;
    //         hasLiquids = true;

    //         ambientSound = Sounds.electricHum;
    //         ambientSoundVolume = 0.08f;

    //         outputLiquids = LiquidStack.with(Liquids.hydrogen, 10f / 60);

    //         researchCostMultiplier = 4/30f; // 1/30 build price

    //         requirements(Category.crafting, with(Items.silicon, 50, AourusItems.meltium, 100, AourusItems.glass, 50));
    //     }};

    //     thermogen_infuser = new HeatCrafter("thermogen-infuser") {{
    //         size = 3;
    //         health = 160;
    //         itemCapacity = 10;

    //         craftTime = 10f;

    //         group = BlockGroup.liquids;

    //         craftTime = 100f;

    //         heatRequirement = 6;
    //         maxEfficiency = 2.5F;

    //         consumeLiquid(Liquids.hydrogen, 20f / 60f);
    //         consumePower(600 / 60f);

    //         drawer = new DrawMulti(
    //             new DrawRegion("-bottom"), 
    //             new DrawLiquidTile(Liquids.hydrogen),
    //             new DrawGlowRegion(),
    //             new DrawDefault(),
    //             new DrawHeatInput()
    //         );

    //         // outputItem = new ItemStack(Liquids.hydrogen, 4);

    //         // craftEffect = Fx.smeltsmoke;

    //         hasPower = true;
    //         hasLiquids = true;

    //         ambientSound = Sounds.techloop;
    //         ambientSoundVolume = 0.08f;

    //         outputLiquids = LiquidStack.with(AourusItems.thermogen, 1f / 60);

    //         researchCostMultiplier = 4/30f; // 1/30 build price

    //         requirements(Category.crafting, with(Items.silicon, 50, AourusItems.meltium, 100, AourusItems.glass, 50));
    //     }};

    //     electricicator = new HeatCrafter("electricicator") {{
    //         size = 3;
    //         health = 150;
    //         itemCapacity = 10;

    //         craftTime = 90f;
            

    //         outputItem = new ItemStack(AourusItems.electric_alloy, 1);

    //         hasPower = true;
    //         // hasLiquids = true;

    //         ambientSound = Sounds.smelter;
    //         ambientSoundVolume = 0.12f;

    //         heatRequirement = 6;
    //         maxEfficiency = 3;


    //         // consumeLiquid(Liquids.slag, 5f / 60f);
    //         consumeItems(with(Items.silicon, 2, AourusItems.meltium, 3));
    //         consumePower(90 / 60f);

    //         drawer = new DrawMulti(new DrawRegion("-bottom"), 
    //         new DrawArcSmelt(),
    //         new DrawDefault());

    //         // researchCost = ItemStack.with(Items.lead, 300, AourusItems.meltium, 300);
    //         researchCostMultiplier = 7/30f; // 1/30 build price

    //         requirements(Category.crafting, ItemStack.with(Items.silicon, 600, AourusItems.meltium, 200));
    //     }};

    //     scrap_melter = new EfficiencyCrafter("scrap-melter")
    //     {{
    //         size = 1;
    //         health = 350;
    //         itemCapacity = 10;

    //         craftEffect = Fx.incinerateSlag;
    //         craftTime = 25f;
    //         // effectChance = 0.03f;

    //         hasLiquids = true;
    //         liquidCapacity = 10f/ 60;

    //         consumePower(45 / 60f);
    //         consume(new ConsumeItemMeltable());

    //         outputLiquids = LiquidStack.with(Liquids.slag, 10f/60);
    //         drawer = new DrawMulti(
    //             new DrawDefault(),
    //             new DrawLiquidRegion(Liquids.slag),
    //             new DrawRegion("-top")
    //         );

    //         researchCostMultiplier = 7/30f; // 1/30 build price

    //         requirements(Category.crafting, ItemStack.with(Items.silicon, 200, AourusItems.meltium, 100));
    //     }};

    //     silicon_smelter = new GenericCrafter("silicon-smelter") {{
    //         size = 3;
    //         health = 150;
    //         itemCapacity = 10;

    //         craftTime = 60f;
            

    //         outputItem = new ItemStack(Items.silicon, 3);

    //         craftEffect = Fx.smeltsmoke;

    //         hasPower = true;
    //         hasLiquids = true;

    //         ambientSound = Sounds.smelter;
    //         ambientSoundVolume = 0.12f;


    //         consumeLiquid(Liquids.water, 30f / 60f);
    //         consumeItems(with(AourusItems.sulphur, 2, AourusItems.meltium, 2));
    //         consumePower(90 / 60f);

            
    //         // inputLiquid = new LiquidStack(Liquids.water, 0.5f);

    //         var flame = new DrawCrucibleFlame();
    //         flame.flameRad = 4f;
    //         flame.particleRad = 12f;
    //         flame.particleSize = 4f;
    //         flame.particles = 50;

    //         craftEffect = Fx.smeltsmoke;
    //         drawer = new DrawMulti(new DrawRegion("-bottom"), flame, new DrawDefault());

    //         // researchCost = ItemStack.with(Items.lead, 300, AourusItems.meltium, 300);
    //         researchCostMultiplier = 2/30f; // 1/30 build price

    //         requirements(Category.crafting, ItemStack.with(Items.lead, 150, AourusItems.meltium, 100));
    //     }};
        
    //     glass_furnace = new GenericCrafter("glass-furnace") {{
    //         size = 3;
    //         health = 200;
    //         itemCapacity = 10;

    //         craftTime = 90f;

    //         outputItem = new ItemStack(AourusItems.glass, 1);

    //         craftEffect = Fx.smeltsmoke;

    //         hasPower = true;
    //         hasLiquids = true;

    //         ambientSound = Sounds.smelter;
    //         ambientSoundVolume = 0.12f;

    //         consumeLiquid(Liquids.water, 60f / 60f);
    //         consumeItems(with(AourusItems.sulphur, 3, Items.sand, 3));
    //         consumePower(90 / 60f);

    //         craftEffect = Fx.smeltsmoke;
    //         drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawGlowRegion("-glow"), new DrawLiquidRegion(), new DrawDefault());

    //         // researchCost = ItemStack.with(Items.lead, 1000, AourusItems.meltium, 1000, Items.silicon, 300);
    //         researchCostMultiplier = 4/30f; // 1/30 build price

    //         requirements(Category.crafting, ItemStack.with(Items.lead, 200, AourusItems.meltium, 200, Items.silicon, 100));
    //     }};

    //     // UNITS

    //     electrical_unit_factory = new UnitFactory("electrical-unit-factory"){{
    //         size = 3;
    //         health = 300;
    //         itemCapacity = 1000;
    //         buildCostMultiplier = 2f;

    //         // regionSuffix = "-dark";

    //         plans = Seq.with(
    //             new UnitPlan(TUnits.shrine, 60f * 25, ItemStack.with(Items.silicon, 30, AourusItems.electric_alloy, 10)),
    //             new UnitPlan(TUnits.millenium, 60f * 30, ItemStack.with(Items.silicon, 80, AourusItems.electric_alloy, 30))
    //         );

    //         // researchCost = ItemStack.with(Items.silicon, 100, AourusItems.meltium, 500);
    //         researchCostMultiplier = 15/30f; // 1/30 build price

    //         consumePower(60 / 60f);
    //         consumeLiquid(Liquids.slag, 15 / 60f);

    //         requirements(Category.units, ItemStack.with(Items.silicon, 300, AourusItems.electric_alloy, 100, AourusItems.glass, 100));
    //     }};

    //     sulphur_unit_factory = new UnitFactory("sulphur-unit-factory"){{
    //         size = 3;
    //         health = 300;
    //         itemCapacity = 1000;
    //         buildCostMultiplier = 2f;

    //         regionSuffix = "-dark";

    //         plans = Seq.with(
    //             new UnitPlan(TUnits.boomer, 60f * 5, ItemStack.with(Items.silicon, 10, AourusItems.sulphur, 30)),
    //             new UnitPlan(TUnits.sulphury, 60f * 10, ItemStack.with(Items.silicon, 50, AourusItems.sulphur, 50))
    //         );

    //         researchCost = ItemStack.with(Items.silicon, 100, AourusItems.meltium, 500);
    //         // researchCostMultiplier = 5/30f; // 1/30 build price

    //         consumePower(60 / 60f);

    //         requirements(Category.units, ItemStack.with(AourusItems.meltium, 100, Items.silicon, 100));
    //     }};

    //     missile_fabricator = new UnitFactory("missile-fabricator"){{
    //         size = 3;
    //         health = 300;
    //         itemCapacity = 1000;
    //         buildCostMultiplier = 2f;

    //         regionSuffix = "-dark";

    //         plans = Seq.with(
    //             new UnitPlan(AourusMisc.sulphur_missile, 60f * 10, ItemStack.with(Items.silicon, 60, AourusItems.sulphur, 60))
    //         );

    //         // researchCost = ItemStack.with(Items.silicon, 100, AourusItems.meltium, 500);
    //         researchCostMultiplier = 90/30f; // 1/30 build price

    //         consumePower(60 / 60f);

    //         requirements(Category.units, ItemStack.with(AourusItems.meltium, 100, Items.silicon, 100));
    //     }};
        
    //     // TRANSPORT

    //     glass_tank = new LiquidRouter("glass-tank"){{
    //         requirements(Category.liquid, with(AourusItems.glass, 10));
    //         liquidCapacity = 1000f;
    //         size = 2;
    //         liquidPadding = 6f/4f;
    //         researchCostMultiplier = 4;
    //         solid = true;
    //         health = 400;

    //         researchCostMultiplier = 5/30f; // 1/30 build price
    //     }};

    //     glass_conduit = new ArmoredConduit("glass-conduit"){{
    //         requirements(Category.liquid, ItemStack.with(AourusItems.glass, 1));
    //         botColor = Pal.darkestMetal;
    //         leaks = true;
    //         liquidCapacity = 50f;
    //         liquidPressure = 1.03f;
    //         health = 50;
    //         researchCostMultiplier = 3;
    //         underBullets = true;
    //         // explosivenessScale = flammabilityScale = 20f/50f;

    //         researchCostMultiplier = 10/30f; // 1/30 build price
    //         // researchCost = ItemStack.with(AourusItems.glass, 10);
    //     }};

    //     // glass_conduit_junction = new LiquidJunction("glass-conduit-junction"){{
    //     //     requirements(Category.liquid, ItemStack.with(AourusItems.glass, 5, Items.lead, 10));
    //     //     buildCostMultiplier = 3f;
    //     //     health = 250;
    //     //     ((Conduit)glass_conduit).junctionReplacement = this;
    //     //     researchCostMultiplier = 1;
    //     //     solid = false;
    //     //     underBullets = true;
    //     // }};

    //     glass_conduit_bridge = new DirectionLiquidBridge("glass-bridge-conduit"){{
    //         requirements(Category.liquid, ItemStack.with(AourusItems.glass, 2, AourusItems.meltium, 10));
    //         range = 4;
    //         hasPower = false;
    //         liquidCapacity = 120f;
    //         researchCostMultiplier = 1;
    //         underBullets = true;
    //         health = 50;

    //         ((Conduit)glass_conduit).rotBridgeReplacement = this;

    //         researchCostMultiplier = 25/30f; // 1/30 build price
    //     }};

    //     glass_conduit_router = new LiquidRouter("glass-conduit-router"){{
    //         requirements(Category.liquid, ItemStack.with(AourusItems.meltium, 10, AourusItems.glass, 2));
    //         liquidCapacity = 150f;
    //         liquidPadding = 3f/4f;
    //         researchCostMultiplier = 3;
    //         underBullets = true;
    //         solid = false;
    //         health = 50;

    //         researchCostMultiplier = 25/30f; // 1/30 build price
    //     }};
    
    //     lead_duct = new Duct("lead-duct"){{
    //         requirements(Category.distribution, ItemStack.with(Items.lead, 1));
    //         health = 50;
    //         speed = 4f;
    //         researchCostMultiplier = 5/30f; // 1/30 build price
    //     }};

    //     lead_duct_router = new DuctRouter("lead-duct-router"){{
    //         health = 50;
    //         speed = 4f;
    //         regionRotated1 = 1;
    //         solid = false;
    //         researchCostMultiplier = 3/30f; // 1/30 build price

    //         requirements(Category.distribution, ItemStack.with(Items.lead, 10));
    //     }};

    //     lead_duct_bridge = new DuctBridge("lead-duct-bridge"){{
    //         health = 50;
    //         speed = 4f;
    //         buildCostMultiplier = 0.5f;
    //         researchCostMultiplier = 5/30f; // 1/30 build price

    //         ((Duct)lead_duct).bridgeReplacement = this;

    //         requirements(Category.distribution, ItemStack.with(Items.lead, 20, AourusItems.meltium, 10));
    //     }};
        
    //     glass_sorter = new Sorter("glass-sorter"){{
    //         requirements(Category.distribution, ItemStack.with(AourusItems.glass, 5, AourusItems.meltium, 10));
    //         health = 50;
    //         researchCost = ItemStack.with(AourusItems.glass, 50, AourusItems.meltium, 1000);
    //     }};

    //     glass_cargo_loader = new UnitCargoLoader("glass-cargo-loader"){{
    //         requirements(Category.distribution, ItemStack.with(AourusItems.glass, 50, Items.silicon, 100));
    //         health = 500;
    //         itemCapacity = 200;
    //         size = 2;
    //         unitType = TUnits.cargo;
    //         // researchCost = ItemStack.with(AourusItems.glass, 500, Items.silicon, 1000);
    //         researchCostMultiplier = 10/30f; // 1/30 build price

    //         polyStroke = 1.5f;
    //         polySides = 5;
    //         polyRotateSpeed = 2f;
    //         polyColor = Liquids.hydrogen.color;
    //         polyRadius = 5.5f;

    //         consumePower(180f / 60f);
    //         consumeLiquid(Liquids.hydrogen, 10f / 60);

    //     }};

    //     glass_cargo_unloader = new UnitCargoUnloadPoint("glass-cargo-unloader"){{
    //         requirements(Category.distribution, ItemStack.with(AourusItems.glass, 50, Items.silicon, 100));
    //         size = 2;
    //         itemCapacity = 200;
    //         health = 500;
    //         // researchCost = ItemStack.with(AourusItems.glass, 500, Items.silicon, 1000);
    //         researchCostMultiplier = 10/30f; // 1/30 build price

    //         consumePower(60f / 60f);
    //     }};

    //     // CORE

    //     core_expansion = new CoreExpansion("core-expansion"){{
    //         requirements(Category.effect, with(Items.silicon, 300, AourusItems.glass, 100));
    //         size = 3;
    //         coreMerge = true;
    //         itemCapacity = 500;
    //         scaledHealth = 55;
    //     }};

    //     torch = new LightBlock("torch") {{
    //         size = 1;
    //         health = 250;
    //         configurable = false;
    //         brightness = 0.6f;
    //         radius = 80f;
    //         fogRadius = 6;
    //         consumePower(30f/60);
    //         researchCostMultiplier = 1/30f; // 1/30 build price

    //         requirements(Category.effect, ItemStack.with(Items.lead, 10, AourusItems.meltium, 10));
    //     }};

    //     glass_core = new CoreBlock("glass-core") {{
    //         size = 3;
    //         health = 2000;
    //         itemCapacity = 1000;

            

    //         isFirstTier = true;
    //         incinerateNonBuildable = true;

    //         alwaysUnlocked = true;
            
    //         unitType = TUnits.aqarus;


    //         requirements(Category.effect, ItemStack.with(Items.lead, 800, AourusItems.meltium, 800));
    //     }};

    //     magnet_core = new CoreBlock("magnet-core") {{
    //         size = 4;
    //         health = 4500;
    //         itemCapacity = 2500;

    //         // isFirstTier = true;
    //         incinerateNonBuildable = true;

    //         // alwaysUnlocked = true;

    //         unitType = TUnits.aquarion;

    //         requirements(Category.effect, ItemStack.with(AourusItems.magnetium, 1000, Items.silicon, 1000, AourusItems.glass, 200));
    //     }};
        
    //     /////////////////
    //     // ENVIRONMENT //
    //     /////////////////


    //     // Magnetium

    //     ore_magnetium = new OreBlock("ore-magnetium") {{
    //         variants = 3;

    //         itemDrop = AourusItems.magnetium;

    //         emitLight = true;
    //         lightColor = Color.valueOf("9d9fd5").a(0.40f);

    //         wallOre = true;
    //     }};


    //     magnetium_wall = new StaticWall("magnetium-wall");

    //     magnetium_floor = new Floor("magnetium-floor") {{
    //         variants = 3;
    //         wall = magnetium_wall;
    //     }};

    //     dense_magnetium_floor = new Floor("dense-magnetium-floor") {{
    //         variants = 4;
    //         wall = magnetium_wall;
    //     }};

    //     magnetium_tree = new TreeBlock("magnetium-tree"){{
    //         emitLight = true;
    //         lightColor = Color.valueOf("8896c3").a(0.35f);
    //     }};

    //     magnetium_cluster_tree = new TreeBlock("magnetium-cluster-tree"){{
    //         emitLight = true;
    //         lightColor = Color.valueOf("8896c3").a(0.35f);
    //     }};

    //     magnetium_vent = new SteamVent("magnetium-vent"){{
    //         parent = blendGroup = AourusBlocks.dense_magnetium_floor;
    //         effect = AourusEffects.ultra_vent_steam;
    //         effectColor = Color.valueOf("273358");
    //         attributes.set(Attribute.steam, 1f);
    //     }};

    //     // Sulphur

    //     uranium_ore = new OreBlock("uranium-ore") {{
    //         variants = 3;

    //         itemDrop = AourusItems.sulphur;

    //         emitLight = true;
    //         lightColor = Color.valueOf("35ca61").a(0.10f);
    //     }};

    //     sulphur_floor = new Floor("sulphur-floor") {{
    //         playerUnmineable = true;
    //         variants = 3;

    //         wall = Blocks.arkyicWall;

    //         attributes.set(Attribute.get("sulphur"), 1 / 9f);

    //         emitLight = true;
    //         lightColor = Color.valueOf("d2ff7f").a(0.38f);
    //     }};

    //     sulphur_wall = new StaticWall("sulphur-wall") {{
    //         playerUnmineable = true;
    //         attributes.set(Attribute.get("sulphur"), .5f);
    //         variants = 3;
    //     }};
    
    //     sulphur_fluid_floor = new Floor("sulphur-fluid-floor") {{
    //         drownTime = 160f;
    //         status = AourusStatuses.sulphured;
    //         statusDuration = 120f;
    //         speedMultiplier = 0.19f;
    //         variants = 0;
    //         liquidDrop = AourusItems.sulphur_fluid;
    //         isLiquid = true;
    //         cacheLayer = CacheLayer.water;
    //         attributes.set(Attribute.get("sulphur"), 2 / 9f);

    //         wall = Blocks.arkyicWall;

    //         emitLight = true;
    //         lightRadius = 40f;
    //         lightColor = Color.valueOf("d2ff7f").a(0.38f);
    //     }};

    //     // Others

    //     carbon_sand = new Floor("carbon-sand") {{
    //         variants = 3;
    //         wall = Blocks.carbonWall;

    //         attributes.set(Attribute.get("sand"), 1);
    //         itemDrop = Items.sand;
    //         playerUnmineable = true;
    //     }};

    //     meltium_ore = new OreBlock("meltium-ore") {{
    //         variants = 3;

    //         itemDrop = AourusItems.meltium;
    //     }};

    //     salt_vent = new SteamVent("salt-vent"){{
    //         parent = blendGroup = Blocks.salt;
    //         attributes.set(Attribute.steam, 1f);
    //     }};
    // }