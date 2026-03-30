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
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ContinuousLiquidTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.draw.DrawArcSmelt;
import mindustry.world.draw.DrawBlurSpin;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawGlowRegion;
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
import technical.expansion.draw.*;
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
    drying_pad, iron_drill, iron_trap_hook, mechanical_drill, iron_alloy_vessel,
    nuclear_reactor, iron_base_applicator, lab, facility_flame_cutter,

    low_temperature_oil_still, gear_bender, rod_roller, porcelain_router, // middle_temperature_oil_still, high_temperature_oil_still

    small_helper_turret, furnace_heater, roller_tunnel, copper_sprocket, brick_boiler, conducted_pipe,

    pebbles, clay, brick_wall, brick_wall_large, basic_core, basic_spike_trap, porcelain_pipe, brick_alloying_chamber,
    
    stone_crusher, roller_conveyor, brick_furnace, flint_extractor, iron_inserter, electric_inserter, mechanical_inserter, basic_ammo_forge, iron_chaingun, sickle_trap, flywheel, iron_gear_applicator, copper_gear_applicator, brick_crucible, basic_mold, mechanical_pump,

    porcelain_furnace, steam_engine, brass_arm, brass_welder, brass_polisher, brass_smasher, ingot_mold,

    // ENVIRONMENT
    scoria_floor, scoria_wall, scoria_boulder, scoria_volcano, lava_floor,
    gneiss_floor, gneiss_wall, gneiss_boulder,
    red_salt_floor, red_salt_boulder, red_salt_wall,
    jasper_floor, jasper_wall, jasper_boulder, jasper_vent,
    sulfur_floor, sulfur_wall, sulfur_boulder, sulfur_vent, sulfur_crystal, sulfur_small_crystal,
    limestone_floor, limestone_wall, limestone_boulder, limestone_large_boulder,
    zinc_ore, copper_ore, coal_ore, iron_ore,
    stone_floor, stone_wall, stone_boulder,

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


            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidCustom(false, false),
                new DrawDefault(),
                new DrawGlowRegion(){{glowIntensity = 0.8f; alpha = 0.5f; lightRadius = 5f;}}
            );
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


            drawer = new DrawMulti(new DrawDefault(), new DrawEffect(Color.gray, 8f, TFx.smoke));
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
                new DrawLiquidCustom(false, false),
                new DrawDefault(), 
                new DrawBlurSpin("-rotator", -12f){{blurThresh = 0.8f;}}, 
                new DrawRegion("-top"),
                new DrawSlapLiquid()
            );
        }};

        iron_alloy_vessel = new Extension("iron-alloy-vessel"){{
            requirements(Category.crafting, with(TItems.iron_plate, 50, TItems.iron_rod, 10));
            techType = TechType.Metallurgy;
            health = 300;
            size = 3;

            additionalStorage = 0;
            additionalLiquidStorage = 10f;
            efficiencyBoost = 30;
            type = ExtensionType.AlloyVessel;

            consumeEffect = Fx.none;

            consumeKineticEnergy(20 * Fr.speed, 5 * Fr.torque, 10 * Fr.inertia);

            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidCustom(TLiquids.lava, true, true),
                new DrawLiquidBubbles(TLiquids.lava){{
                    sides = 16;
                    recurrence = 6f;
                    spread = 8;
                    radius = 2f;
                    amount = 20;
                }},
                new DrawDefault(),
                new DrawBlurSpin("-rotator", 6f){{blurThresh = 1f;}},
                new DrawSlapLiquid(){{drawLiquid = TLiquids.solidified_brass;}}
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

            // drawer = new DrawMulti(new DrawDefault(), new DrawEffect(Color.cyan, 0.5f, TFx.solarFlare), new DrawLiquidTile(){{alpha = 0.5f;}});

            drawer = new DrawMulti(
                new DrawPublicRegion("still-bottom"),
                new DrawLiquidCustom(TLiquids.crude_oil, false, false),
                new DrawLiquidBubbles(){{
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
                new DrawLiquidCustom(false, false),
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
                ),
                new Recipe(
                    ItemStack.with(TItems.raw_zinc, 5), null,
                    null, LiquidStack.with(TLiquids.molten_zinc, 5 * Fr.liquid),
                    Fr.time * 1, 0, 400, null
                )
            );

            workingTemperature = 110;

            drawer = new DrawMulti(
                new DrawRegion("-bottom"), 
                new DrawLiquidCustom(true, false),
                new DrawLiquidBubbles(){{
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

        brick_alloying_chamber = new RecipeCrafter("brick-alloying-chamber"){{
            requirements(Category.crafting, with(TItems.brick, 300, TItems.iron_rod, 20));
            techType = TechType.Metallurgy;
            size = 4;
            health = 800;
            itemCapacity = 0;
            liquidCapacity = 20f;

            hasItems = false;
            hasPower = false;
            hasLiquids = true;

            outputsLiquid = true;

            craftEffect = TFx.coalSmelt;

            maxEfficiency = 3;

            RequiredExtensions = T.mapIntOf(
                ExtensionType.Heater, 120,
                ExtensionType.Chimney, 15,
                ExtensionType.AlloyVessel, 60
            );

            AllowedExtensions = Seq.with(
                ExtensionType.Heater,
                ExtensionType.Storage,
                ExtensionType.Chimney,
                ExtensionType.AlloyVessel
            );

            recipes = Seq.with(
                new Recipe(
                    null, LiquidStack.with(TLiquids.molten_copper, 7 * Fr.liquid, TLiquids.molten_zinc, 3 * Fr.liquid),
                    null, LiquidStack.with(TLiquids.molten_brass, 10 * Fr.liquid),
                    Fr.time * 10, 0, 300, new KineticEnergy(10 * Fr.speed, 60 * Fr.torque)
                )
            );

            workingTemperature = 110;

            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidCustom(TLiquids.lava,true, false),
                new DrawLiquidBubbles(TLiquids.lava){{
                    sides = 16;
                    recurrence = 6f;
                    spread = 8;
                    radius = 2f;
                    amount = 20;
                }},
                new DrawBlurSpin("-rotator", 6f){{blurThresh = 1f;}},
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
                new DrawLiquidCustom(false, false),
                new DrawEffect((Building build) -> ((RecipeCrafterBuild)build).recipe() != null ? ((RecipeCrafterBuild)build).recipe().outputItems[0].item.color : Color.white, 10f, TFx.smoke),
                new DrawDefault()
            );
        }};

        ingot_mold = new RecipeCrafter("ingot-mold"){{
            requirements(Category.crafting, with(TItems.iron_rod, 30, TItems.iron_plate, 100));
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
                            null, LiquidStack.with(TLiquids.molten_zinc, 3 * Fr.liquid),
                            ItemStack.with(TItems.zinc_ingot, 1), null,
                            Fr.time * 4, 0, 150, null
                    ),
                    new Recipe(
                            null, LiquidStack.with(TLiquids.molten_brass, 3 * Fr.liquid),
                            ItemStack.with(TItems.brass_ingot, 1), null,
                            Fr.time * 8, 0, 150, null
                    )
            );

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawEffect((Building build) -> ((RecipeCrafterBuild)build).recipe() != null ? ((RecipeCrafterBuild)build).recipe().outputItems[0].item.color : Color.white, 10f, TFx.smoke)
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
                new DrawLiquidCustom(false, false),
                new DrawLiquidBubbles(),
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

        stone_boulder = new Prop("stone-boulder"){{
            variants = 2;
        }};

        stone_floor = new Floor("stone-floor"){{
            variants = 3;
            decoration = stone_boulder;
        }};

        stone_wall = new StaticWall("stone-wall"){{
            variants = 2;
            attributes.set(TAttributes.stone, 0.8f);
        }};

        pebbles = new OreBlock("pebbles", TItems.stone){{
            variants = 3;
        }};

        jasper_boulder = new Prop("jasper-boulder"){{
            variants = 2;
        }};

        jasper_floor = new Floor("jasper-floor"){{
            variants = 3;
            decoration = jasper_boulder;
        }};

        jasper_wall = new StaticWall("jasper-wall"){{
            variants = 2;
            attributes.set(TAttributes.stone, 0.3f);
        }};

        jasper_vent = new SteamVent("jasper-vent"){{
            variants = 2;
            parent = blendGroup = jasper_floor;
            attributes.set(Attribute.steam, 1f);
        }};

        sulfur_boulder = new Prop("sulfur-boulder"){{
            variants = 2;
        }};

        sulfur_crystal = new TallBlock("sulfur-crystal"){{
            variants = 2;
            clipSize = 128f;
        }};

        sulfur_small_crystal = new TallBlock("sulfur-small-crystal"){{
            variants = 2;
            clipSize = 64f;
        }};

        sulfur_floor = new Floor("sulfur-floor"){{
            variants = 3;
            decoration = sulfur_boulder;
        }};

        sulfur_wall = new StaticWall("sulfur-wall"){{
            variants = 2;
            attributes.set(TAttributes.sulfur, 0.8f);
        }};

        sulfur_vent = new SteamVent("sulfur-vent"){{
            variants = 2;
            parent = blendGroup = sulfur_floor;
            attributes.set(Attribute.steam, 1f);
        }};

        limestone_boulder = new Prop("limestone-boulder"){{
            variants = 2;
        }};

        limestone_large_boulder = new TallBlock("limestone-large-boulder"){{
            variants = 2;
            clipSize = 64f;
        }};

        limestone_floor = new Floor("limestone-floor"){{
            variants = 3;
            decoration = limestone_boulder;
        }};

        limestone_wall = new StaticWall("limestone-wall"){{
            variants = 2;
            attributes.set(TAttributes.stone, 0.5f);
        }};

        gneiss_boulder = new Prop("gneiss-boulder"){{
            variants = 3;
        }};

        gneiss_floor = new Floor("gneiss-floor"){{
            variants = 3;
        }};

        gneiss_wall = new StaticWall("gneiss-wall"){{
            variants = 2;
            attributes.set(TAttributes.stone, 0.1f);
        }};

        scoria_floor = new Floor("scoria-floor"){{
            variants = 3;
        }};

//        spongy_scoria_floor = new Floor("spongy-scoria-floor"){{
//            variants = 3;
//        }};

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

        red_salt_wall = new StaticWall("red-salt-wall"){{
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
            liquidDrop = TLiquids.lava;
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