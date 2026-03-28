package technical.content;

import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.ai.types.BuilderAI;
import mindustry.ai.types.CommandAI;
import mindustry.ai.types.HugAI;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.RailBulletType;
import mindustry.entities.part.DrawPart.PartProgress;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.gen.CrawlUnit;
import mindustry.gen.LegsUnit;
import mindustry.gen.MechUnit;
import mindustry.gen.Sounds;
import mindustry.gen.TankUnit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.type.unit.TankUnitType;
import mindustry.world.meta.Env;
import technical.TCol;
import technical.expansion.train.RailVehicle;
import technical.expansion.train.RailVehicleUnit;


public class TUnits {
    public static RailVehicle basic_train;
    public static UnitType gatherdix, sporin, onset, vermiphorus, cravlon, incinerator, vapor, archer;

    public static void init()
    {
        // EntityMapping.register("basic-train", RailVehicleUnit::new);
    }

    public static void load() 
    {
        basic_train = new RailVehicle("basic-train") {{
            // aiController = SuicideAI::new;
            constructor = RailVehicleUnit::create;
            health = 100;
            isEnemy = false;
            envDisabled = 0;

            outlineColor = Pal.darkOutline;

            speed = 1f;

            totalItemCapaity = 120;
            totalPayloadCapacity = 24;

            squareShape = false;

            consumedItem = TItems.coal;
            consumedItemDuration = 10f;
            consumeEffect = TFx.coalSmelt;
        }};

        onset = new UnitType("onset"){{
            controller = u -> u.team.isAI() ? new BuilderAI(true, 400f) : new CommandAI();
            isEnemy = false;

            outlineColor = Pal.darkOutline;

            coreUnitDock = true;

            targetBuildingsMobile = false;
            mineSpeed = 3.5f;
            mineTier = 1;

            constructor = MechUnit::create;

            rotateSpeed = 2f;

            faceTarget = true;
            rotateToBuilding = true;

            alwaysUnlocked = true;

            speed = 0.75f;
            hitSize = 8f;
            health = 150;

            fogRadius = 0f;
            itemCapacity = 100;

            buildSpeed = 0.5f;

            canBoost = true;
            boostMultiplier = 0.5f;
            boostWhenBuilding = false;
            boostWhenMining = false;

            riseSpeed = 0.01f;
            fallSpeed = 0.02f;

            buildBeamOffset = Float.MAX_VALUE;
            buildRange = Vars.buildingRange * 0.7f;

            weapons.add(TWeapons.onset_weapon);
        }};

        archer = new UnitType("archer"){{
            outlineColor = Pal.darkOutline;

            coreUnitDock = true;

            targetBuildingsMobile = false;
            mineSpeed = 3.5f;
            mineTier = 1;

            constructor = MechUnit::create;

            rotateSpeed = 2f;

            faceTarget = true;
            rotateToBuilding = true;

            alwaysUnlocked = true;

            speed = 0.5f;
            hitSize = 9f;
            health = 300;

            fogRadius = 3f;
            itemCapacity = 100;

            riseSpeed = 0.01f;
            fallSpeed = 0.02f;

            buildBeamOffset = Float.MAX_VALUE;
            buildRange = Vars.buildingRange * 0.7f;

            weapons.add(TWeapons.archer_bow);
        }};

        vapor = new UnitType("vapor"){{
            constructor = TankUnit::create;

            squareShape = true;
            outlineColor = Pal.darkOutline;
            omniMovement = false;
            rotateMoveFirst = true;
            rotateSpeed = 1.3f;
            speed = 0.8f;

            tankMoveVolume *= 0.55f;
            tankMoveSound = Sounds.tankMove;

            hitSize = 18f;
            treadPullOffset = 5;
            speed = 0.7f;
            rotateSpeed = 2.6f;
            health = 2100;
            armor = 8f;
            itemCapacity = 0;
            floorMultiplier = 0.8f;
            treadRects = new Rect[]{new Rect(17 - 96f/2f, 10 - 96f/2f, 19, 76)};
            // crushFragile = true;

            ammoType = new ItemAmmoType(TItems.clay, 1);
            ammoCapacity = 300;

            weapons.add(TWeapons.vapor_turret);
        }};

        cravlon = new UnitType("cravlon") {{
            constructor = MechUnit::create;

            // outlineColor = TCol.bioOutline;

            health = 300;
            armor = 1f;

            speed = 0.6f;
            rotateSpeed = 0.5f;

            hitSize = 12f;

            ammoCapacity = 100;

            ammoType = new ItemAmmoType(TItems.stone, 1);

            parts.add(new RegionPart("-ammo"){{
                progress = PartProgress.recoil;
                moveX = 0f;
                moveY = -0.5f;
                moveRot = 0f;
            }});

            weapons.add(TWeapons.cravlon_turret);
        }};

        incinerator = new UnitType("incinerator"){{
            constructor = LegsUnit::create;

            outlineColor = Pal.darkOutline;

            speed = 0.6f;
            drag = 0.1f;
            hitSize = 14f;
            rotateSpeed = 1f;
            health = 1100;
            armor = 5f;
            stepShake = 0f;

            stepSound = Sounds.walkerStepSmall;

            legCount = 4;
            legLength = 14f;
            lockLegBase = true;
            legContinuousMove = true;
            legExtension = -3f;
            legBaseOffset = 5f;
            legMaxLength = 1.1f;
            legMinLength = 0.2f;
            legLengthScl = 0.95f;
            legForwardScl = 0.7f;

            legMoveSpace = 1f;
            hovering = true;

            shadowElevation = 0.2f;
            groundLayer = Layer.legUnit - 1f;

            ammoType = new ItemAmmoType(TItems.coal, 3);
            ammoCapacity = 100;

            weapons.add(TWeapons.incinerator_weapon);
        }};

        vermiphorus = new UnitType("vermiphorus"){{
            health = 100000;
            armor = 10000;

            constructor = CrawlUnit::create;
            aiController = HugAI::new;

            drawBody = false;
            omniMovement = false;
            rotateSpeed = 1f;
            crushDamage = 1.2f;

            hitSize = 60f;

            segments = 4;

            segmentScl = 25f;
            segmentRotSpeed = 0.10f;

            segmentRotationRange = 120f;

            segmentPhase = 10f;

            segmentMag = 5f*segments;
            speed = 0.25f;

            rotateSpeed = 0.25f;

            outlineColor = TCol.bioOutline;
            outlineRadius = 8;

            weapons.add(TWeapons.bio_missile_launcher);
            weapons.add(TWeapons.bio_sucker);
        }};
    }
}

        // gatherdix = new UnitType("gatherdix") {{
        //     controller = u -> new MinerAI();
        //     constructor = UnitEntity::create;
        //     mineItems = Seq.with(Items.copper, Items.lead);
        //     health = 200;
        //     isEnemy = false;
        //     envDisabled = 0;

        //     outlineColor = Pal.darkOutline;

        //     itemCapacity = 30;

        //     speed = 4f;
        //     rotateSpeed = 5f;
		// 	accel = 0.05f;

        //     mineWalls = false;
        //     mineFloor = true;

        //     hitSize = 5f;

        //     alwaysUnlocked = true;

        //     drag = 0.035f;
        //     armor = 1;

        //     lowAltitude = true;
        //     flying = true;

        //     fogRadius = 2f;

        //     mineTier = 1;
        //     mineSpeed = 4f;

        //     buildSpeed = 0;

        //     coreUnitDock = true;

        //     lightRadius = 150f;

        // }};

        // sporin = new UnitType("sporin"){{
        //     constructor = CrawlUnit::create;
        //     useUnitCap = false;

        //     // controller = u -> new SteamHugAI();
        //     health = 20;

        //     omniMovement = false;
        //     targetAir = false;

        //     drawBody = true;
        // }};

// aqarus = new UnitType("aqarus") {{
        //     controller = u -> new BuilderAI(false, 500f);
        //     health = 100;
        //     isEnemy = false;
        //     envDisabled = 0;

        //     outlineColor = Pal.darkOutline;

        //     itemCapacity = 20;

        //     speed = 4f;
        //     rotateSpeed = 3f;
		// 	accel = 0.05f;

        //     mineWalls = false;
        //     mineFloor = true;

        //     hitSize = 5f;

        //     alwaysUnlocked = true;

        //     drag = 0.035f;
        //     // hitSize = 8f;
        //     armor = 0.5f;

        //     lowAltitude = true;
        //     flying = true;

        //     fogRadius = 0f;
        //     targetable = false;
        //     hittable = false;
        //     targetPriority = -2;

        //     payloadCapacity = 2 * 2 * tilesize * tilesize;
        //     pickupUnits = false;
        //     vulnerableWithPayloads = true;

		// 	// engineColor = Color.valueOf("#4eabda");

        //     mineTier = 1;
        //     mineSpeed = 6f;

        //     buildSpeed = 1f;

        //     coreUnitDock = true;

        //     lightRadius = 150f;

        //     constructor = PayloadUnit::create;

        //     weapons.add(new Weapon() {{
        //         reload = 1f;
        //         x = 0f;
        //         y = 0f;

        //         top = false;

        //         autoTarget = false;

        //         noAttack = true;
                
        //         shootSound = Sounds.none;

        //         rotate = false;
        //         // flags = EnumSet.of(BlockFlag.turret, BlockFlag.extinguisher);

        //         shootCone = 5f;
        //         inaccuracy = 5f;

        //         bullet = new LiquidBulletType()
        //         {{
        //             lifetime = 25f;
        //             speed = 4f;
        //             knockback = 0;
        //             puddleSize = 8f;
        //             orbSize = 2f;
        //             drag = 0.001f;
        //             ammoMultiplier = 0.4f;
        //             statusDuration = 60f;
        //             damage = 0;
        //             layer = Layer.bullet - 2f;
        //             liquid = Liquids.water;
        //         }};
        //     }});
        // }};

        // gatherdix = new UnitType("gatherdix") {{
        //     controller = u -> new MinerAI();
        //     mineItems = Seq.with(AourusItems.meltium, Items.lead);
        //     health = 200;
        //     isEnemy = false;
        //     envDisabled = 0;

        //     outlineColor = Pal.darkOutline;

        //     itemCapacity = 30;

        //     speed = 4f;
        //     rotateSpeed = 5f;
		// 	accel = 0.05f;

        //     mineWalls = false;
        //     mineFloor = true;

        //     hitSize = 5f;

        //     alwaysUnlocked = true;

        //     drag = 0.035f;
        //     // hitSize = 8f;
        //     armor = 1;

        //     lowAltitude = true;
        //     flying = true;

        //     fogRadius = 2f;

        //     // payloadCapacity = 2 * 2 * tilesize * tilesize;
        //     // pickupUnits = true;
        //     // vulnerableWithPayloads = true;

		// 	// engineColor = Color.valueOf("#4eabda");

        //     mineTier = 1;
        //     mineSpeed = 4f;

        //     buildSpeed = 0;

        //     coreUnitDock = true;

        //     lightRadius = 150f;

        //     constructor = UnitEntity::create;
        // }};

        // aquarion = new UnitType("aquarion") {{
        //     controller = u -> new BuilderAI(true, 500f);
        //     health = 800;
        //     isEnemy = false;
        //     envDisabled = 0;

        //     outlineColor = Pal.darkOutline;

        //     itemCapacity = 60;

        //     speed = 4f;
        //     rotateSpeed = 2f;
		// 	accel = 0.1f;

        //     hitSize = 8f;

        //     drag = 0.035f;
        //     armor = 3;

        //     lowAltitude = true;
        //     flying = true;

        //     mineTier = 1;
        //     mineSpeed = 8f;

        //     buildSpeed = 1.5f;

        //     coreUnitDock = true;

        //     lightRadius = 175f;

        //     constructor = UnitEntity::create;

        //     weapons.add(new Weapon("aourus-aquarion-riffle") {{
        //         reload = 20f;
        //         x = 0f;
        //         y = 0f;

        //         engineOffset = 7f;

        //         recoil = 0.5f;

        //         mirror = false;

        //         top = false;
                
        //         shootSound = Sounds.none;

        //         rotate = false;

        //         shootCone = 5f;
        //         inaccuracy = 5f;

        //         shoot = new ShootPattern() {{
        //             shots = 20;
        //             shotDelay = 0.5f;
        //         }};

        //         bullet = new LiquidBulletType() {{
        //             lifetime = 35f;
        //             speed = 4f;
        //             knockback = 0.3f;
        //             puddleSize = 10f;
        //             orbSize = 2f;
        //             drag = 0.002f;
        //             ammoMultiplier = 1f;
        //             statusDuration = 60f * 5f;
        //             damage = 5f;
        //             layer = Layer.bullet - 2f;
        //             liquid = Liquids.water;

        //             buildingDamageMultiplier = 0;
        //         }};
        //     }});
        // }};

        // millenium = new UnitType("millenium") {{
        //     outlineColor = Pal.darkOutline;
        //     constructor = CrawlUnit::create;
        //     aiController = HugAI::new;
        //     drawBody = false;
        //     omniMovement = false;
        //     rotateSpeed = 1.7f;
        //     crushDamage = 0.6f;
        //     health = 800;
        //     armor = 3;
        //     hitSize = 9f;
        //     segments = 6;
        //     segmentScl = 3f;
        //     segmentPhase = 1f;
        //     segmentMag = 0.3f*segments;
        //     speed = 0.8f;

        //     healColor = Pal.accent;

        //     abilities.add(new ShieldArcAbility(){{
        //         region = "aourus-none";
        //         // drawArc = false;
        //         radius = 8f;
        //         angle = 140f;
        //         regen = 0.3f;
        //         cooldown = 60f * 15f;
        //         max = 1000f;
        //         width = 7f;
        //         whenShooting = false;
        //     }});

        //     weapons.add(new Weapon() {{
        //         shootSound = Sounds.none;
        //         shootY = 0f;
        //         reload = 40f;
        //         shootCone = 40f;
        //         shootSound = Sounds.bolt;
        //         // ejectEffect = Fx.none;

        //         shoot = new ShootPattern() {{
        //             shots = 3;
        //             shotDelay = 5f;
        //         }};

        //         x = 3f;
        //         mirror = true;

        //         bullet = new LightningBulletType()
        //         {{
        //             lightningColor = hitColor = TCol.electricity;
        //             damage = 10f;
        //             lightningLength = 12;
        //             lightningLengthRand = 3;
        //             shootEffect = Fx.lightning;
        //         }};
        //     }});
        // }};

        // boomer = new UnitType("boomer") {{

        //     aiController = SuicideAI::new;

        //     outlineColor = Pal.darkOutline;


        //     constructor = TankUnit::create;

        //     squareShape = true;
        //     omniMovement = false;
        //     rotateMoveFirst = true;
        //     rotateSpeed = 1.3f;
        //     speed = 0.8f;
        //     outlineColor = Pal.darkOutline;
           
        //     hitSize = 10f;
        //     treadPullOffset = 3;
        //     speed = 0.75f;
        //     rotateSpeed = 3.5f;
        //     health = 200;
        //     armor = 2f;
        //     itemCapacity = 0;
        //     treadRects = new Rect[]{new Rect(12 - 32f, 7 - 32f, 14, 51)};

        //     ammoType = new ItemAmmoType(AourusItems.sulphur);

        //     cachedRequirements = ItemStack.with(Items.silicon, 100);

        //     // researchRequirements(ItemStack.with(Items.silicon, 100));

        //     weapons.add(new Weapon(){{
        //         shootOnDeath = true;
        //         reload = 24f;
        //         shootCone = 180f;
        //         ejectEffect = Fx.none;
                
        //         shootSound = Sounds.flame;
        //         x = shootY = 0f;
        //         mirror = false;
        //         bullet = new BulletType(){{
        //             hitEffect = new MultiEffect(AourusEffects.small_impact_wave.wrap(TCol.sulphur_light, 40f));
        //             hitSound = Sounds.explosion;
        //             collidesTiles = false;
        //             collides = false;
        //             hitSound = Sounds.explosion;
        //             status = StatusEffects.burning;
        //             statusDuration = 60f * 4;

        //             rangeOverride = 35f;
        //             speed = 0f;
        //             splashDamageRadius = 64f;
        //             instantDisappear = true;
        //             splashDamage = 80f;
        //             killShooter = true;
        //             hittable = false;
        //             collidesAir = true;
        //         }};
        //     }});
        // }};

        // tyrant = new UnitType("tyrant") {{
        //     constructor = TankUnit::create;
        //     aiController = HugAI::new;

        //     outlineColor = Pal.darkOutline;

        //     alwaysShootWhenMoving = true;

        //     health = 1000;
        //     armor = 10f;

        //     squareShape = true;
        //     omniMovement = false;
        //     rotateMoveFirst = true;
        //     rotateSpeed = 1.3f;
        //     speed = 0.8f;
        //     outlineColor = Pal.darkOutline;

        //     hitSize = 24f;
        //     treadPullOffset = 3;
        //     speed = 0.75f;
        //     rotateSpeed = 3.5f;
        //     itemCapacity = 0;
        //     treadRects = new Rect[]{
        //             new Rect(
        //                     13 - 45,
        //                     3 - 47f,
        //                     14,
        //                     24
        //             ),
        //             new Rect(
        //                     13 - 45,
        //                     67 - 47f,
        //                     14,
        //                     24
        //             )
        //     };

        //     ammoType = new ItemAmmoType(AourusItems.sulphur);

            
        //     weapons.add(new Weapon(){{

        //         // minShootVelocity = 0.2f;
                
        //         // shootOnDeath = true;
        //         reload = 40f;
        //         shootCone = 180f;
        //         ejectEffect = Fx.none;

        //         ignoreRotation = true;
        //         top = false;

        //         shootSound = Sounds.none;
        //         x = shootY = 0f;
        //         mirror = false;
        //         bullet = new BulletType(){{
        //             collidesTiles = false;
        //             collides = false;
        //             hitSound = Sounds.explosionbig;

        //             buildingDamageMultiplier = 1.5f;
        //             shake = 1f;

        //             rangeOverride = 50f;
        //             hitEffect = new MultiEffect(AourusEffects.impact_wave.wrap(TCol.sulphur_light, 40f));
        //             speed = 0f;
        //             splashDamageRadius = 50f;
        //             instantDisappear = true;
        //             splashDamage = 60f;
        //             // killShooter = true;
        //             hittable = false;
        //             collidesAir = true;

        //             statusDuration = 60 * 4f; // sulphured
        //             status = AourusStatuses.sulphured; 
        //         }};
        //     }});
        // }};

        // sulphury = new UnitType("sulphury") {{
        //     constructor = LegsUnit::create;

        //     outlineColor = Pal.darkOutline;

        //     health = 350;
        //     speed = 0.72f;
        //     drag = 0.11f;
        //     armor = 2f;
        //     hitSize = 10f;

        //     legStraightness = 0.3f;
        //     stepShake = 0f;

        //     legCount = 4;
        //     legLength = 8f;
        //     lockLegBase = true;
        //     legContinuousMove = true;
        //     legExtension = -2f;
        //     legBaseOffset = 3f;
        //     legMaxLength = 1.1f;
        //     legMinLength = 0.2f;
        //     legLengthScl = 0.96f;
        //     legForwardScl = 1.1f;
        //     legGroupSize = 2;
        //     rippleScale = 0.2f;

        //     legMoveSpace = 1f;
        //     allowLegStep = true;
        //     hovering = true;
        //     legPhysicsLayer = false;

        //     shadowElevation = 0.1f;
        //     groundLayer = Layer.legUnit - 1f;

        //     ammoType = new ItemAmmoType(AourusItems.sulphur);

        //     weapons.add(new Weapon("aourus-sulphury-flamethrower"){{
        //         top = false;
        //         shootSound = Sounds.flame;
        //         shootY = 2f;
        //         reload = 11f;
        //         recoil = 1f;
        //         ejectEffect = Fx.none;

        //         y = 0;
        //         x = 3f;

        //         bullet = new BulletType(4.2f, 20f){{
        //             ammoMultiplier = 3f;
        //             hitSize = 7f;
        //             lifetime = 13f;
        //             pierce = true;
        //             pierceBuilding = true;
        //             pierceCap = 2;
        //             statusDuration = 60f * 4;
        //             shootEffect = Fx.shootSmallFlame;
        //             hitEffect = Fx.hitFlameSmall;
        //             despawnEffect = Fx.none;
        //             status = StatusEffects.burning;
        //             keepVelocity = false;
        //             hittable = false;
        //             buildingDamageMultiplier = 1.75f;
        //         }};
        //     }});
        // }};

        // shrine = new UnitType("shrine") {{
        //     constructor = UnitEntity::create;

        //     outlineColor = Pal.darkOutline;

        //     flying = true;
        //     // lowAltitude = true;

        //     health = 200;
        //     speed = 2f;
        //     drag = 0.02f;
        //     accel = 0.05f;

        //     targetAir = false;

        //     range = 140f;
        //     faceTarget = false;
        //     autoFindTarget = true;
        //     circleTarget = true;

        //     armor = 1f;
        //     hitSize = 10f;

        //     ammoType = new ItemAmmoType(Items.graphite);

        //     weapons.add(new Weapon(){{
        //         minShootVelocity = 0.75f;
        //         x = 0;
        //         mirror = false;
        //         shootY = 0f;
        //         reload = 12f;
        //         shootCone = 180f;
        //         ejectEffect = Fx.none;
        //         inaccuracy = 15f;
        //         ignoreRotation = true;
        //         top = false;
        //         shootSound = Sounds.none;
        //         bullet = new BombBulletType(27f, 25f){{
        //             width = 8f;
        //             height = 10f;
        //             hitEffect = Fx.flakExplosion;
        //             shootEffect = Fx.none;
        //             smokeEffect = Fx.none;

        //             status = StatusEffects.blasted;
        //             statusDuration = 60f;
        //             damage = splashDamage * 0.5f;
        //         }};
        //     }});
        // }};

        // flex = new UnitType("flex") {{
        //     constructor = UnitEntity::create;

        //     outlineColor = Pal.darkOutline;

        //     flying = true;

        //     lowAltitude = true;

        //     // circleTarget = true;

        //     health = 650;
        //     speed = 3f;
        //     drag = 0.03f;
        //     accel = 0.05f;

        //     armor = 4f;
        //     hitSize = 30f;

        //     ammoType = new ItemAmmoType(AourusItems.sulphur);

        //     Weapon weapon = new Weapon("aourus-flex-turret"){{
        //         mirror = false;
        //         x = 0f;
        //         y = 6f;

        //         lowAltitude = true;

        //         top = true;
        //         shootSound = Sounds.missile;
        //         shootY = 4f;
        //         reload = 20f;
        //         recoil = 5f;

        //         rotate = true;

        //         bullet = AourusWeapons.sulphur_missile;
        //             bullet.damage = 30f;
        //     }};

        //     Weapon weapon_mirrored = new Weapon("aourus-flex-turret"){{
        //         mirror = true;
        //         x = 7f;
        //         y = -3f;

        //         lowAltitude = true;

        //         top = true;
                
        //         shootSound = Sounds.missile;
        //         shootY = 4f;
        //         reload = 20f;
        //         recoil = 5f;

        //         rotate = true;

        //         bullet = AourusWeapons.sulphur_missile;
        //             bullet.damage = 30f;
        //     }};

        //     weapons.add(weapon, weapon_mirrored);
        // }};
    
        // tantaros = new UnitType("tantaros") {{
        //     trailLength = 35;
		// 	waveTrailX = 9f;
		// 	waveTrailY = -15f;
		// 	trailScl = 2f;

        //     outlineColor = Pal.darkOutline;

		// 	constructor = UnitWaterMove::create;

        //     targetAir = true;
        //     targetGround = true;

        //     faceTarget = false;
            
        //     naval = true;
        //     flying = false;
        //     isEnemy = false;
        //     lowAltitude = true;

        //     rotateSpeed = 1f;

        //     health = 1500;
        //     speed = 2f;
        //     drag = 0.25f;
        //     accel = 0.03f;

        //     armor = 10f;
        //     hitSize = 30f;

        //     immunities.add(AourusStatuses.sulphured);

        //     // payloadCapacity = (4 * 4) * tilePayload;

        //     // ammoType = new Liq

        //     weapons.add(new Weapon("aourus-flex-turret"){{
        //         mirror = true;
        //         x = 7f;
        //         y = -3f;

        //         lowAltitude = true;

        //         top = true;
                
        //         shootSound = Sounds.missile;
        //         shootY = 4f;
        //         reload = 20f;
        //         recoil = 5f;
                
        //         rotateSpeed = 2.5f;


        //         // rotationLimit = 120f;

        //         rotate = true;

        //         bullet = AourusWeapons.sulphur_missile;
        //         bullet.damage = 40f;
        //     }});

        //     weapons.add(new Weapon("aourus-tantaros-riffle"){{
        //         mirror = false;

        //         shootSound = Sounds.bolt;

        //         x = 0f;
        //         y = 4f;

        //         top = true;
        //         // shootSound = Sounds.none;
        //         shootY = 4f;
        //         reload = 30f;
        //         recoil = 3f;

        //         // rotationLimit = 90f;

        //         recoilTime = 5f;

        //         shootCone = 2f;

        //         rotateSpeed = 1f;

        //         rotate = true;

        //         reload = 45f;

        //         bullet = new ShrapnelBulletType(){{
        //             lifetime = 20f;
        //             length = 150f;
        //             damage = 50f;
        //             status = AourusStatuses.sulphured;
        //             statusDuration = 60f;
        //             fromColor = TCol.sulphur_light;
        //             toColor = TCol.sulphur_light;
        //             serrationSpaceOffset = 40f;
        //             width = 6f;
        //             shootEffect = AourusEffects.sulphur_front_spark;
        //             smokeEffect = new MultiEffect(AourusEffects.sulphur_front_spark, new Effect(lifetime + 10f, b -> {
        //                 Draw.color(fromColor, toColor, b.fin());
        //                 Fill.circle(b.x, b.y, (width / 1.75f) * b.fout());
        //             }));
        //         }};
        //     }});
            
        //     // weapons.add(AourusWeapons.tantaroser);
        // }};
        
        // maltorion = new UnitType("maltorion") {{
        //     constructor = LegsUnit::create;

        //     outlineColor = Pal.darkOutline;

        //     legCount = 6;
        //     legLength = 18f;
        //     legGroupSize = 3;
        //     lockLegBase = true;
        //     legContinuousMove = true;
        //     legExtension = -3f;
        //     legBaseOffset = 7f;
        //     legMaxLength = 1.1f;
        //     legMinLength = 0.2f;
        //     legLengthScl = 0.95f;
        //     legForwardScl = 0.9f;

        //     legMoveSpace = 1f;
        //     hovering = false;

        //     flying = false;

        //     health = 2000;
        //     speed = 1.5f;
        //     drag = 0.03f;
        //     accel = 0.05f;

        //     armor = 7f;
        //     hitSize = 16f;

        //     // weapons.add(AourusWeapons.shocking_turrets);
        //     weapons.add(AourusWeapons.bolter);
        //     weapons.add(AourusWeapons.shocking_turrets);
        // }};
    
        // anihilator = new UnitType("anihilator") {{
        //     constructor = UnitEntity::create;

        //     outlineColor = Pal.darkOutline;

        //     // range = 200f;

        //     flying = true;
        //     lowAltitude = true;

        //     health = 5000;
        //     armor = 6f;
        //     speed = 1.5f;
        //     drag = 0.04f;
        //     accel = 0.03f;

        //     hitSize = 30f;

        //     weapons.add(new Weapon(){{

        //         top = false;

        //         mirror = false;

        //         outlineRadius = 0;

        //         parts.add(new RegionPart("aourus-anihilator-bomb-launcher-side"){{
        //             under = false;

        //             mirror = true;

        //             moves.add(new PartMove(PartProgress.reload, 2f, 2f, 20f));

        //             heatColor = Color.red;
        //             cooldownTime = 60f;
        //         }});
        //         parts.add(new RegionPart("aourus-anihilator-bomb-launcher"){{
        //             under = false;

        //             mirror = false;

        //             moves.add(new PartMove(PartProgress.reload, 0f, -5f, 0f));

        //             heatColor = Color.red;
        //             cooldownTime = 60f;
        //         }});

                

        //         shootSound = Sounds.titanExplosion;
        //         x = y = shootY = 0f;

        //         reload = 750f;

        //         recoil = 5f;
                
        //         bullet = new BulletType(){{

        //             shootEffect = new MultiEffect(new Effect(9, e -> {
        //                 color(Color.white, e.color, e.fin());
        //                 stroke(0.7f + e.fout());
        //                 Lines.square(e.x, e.y, e.fin() * 5f, e.rotation + 45f);

        //                 Drawf.light(e.x, e.y, 23f, e.color, e.fout() * 0.7f);
        //             }), new WaveEffect(){{
        //                 colorFrom = colorTo = Pal.missileYellow;
        //                 sizeTo = 15f;
        //                 lifetime = 12f;
        //                 strokeFrom = 3f;
        //             }});

        //             spawnUnit = new MissileUnitType("anihilator-bomb"){{
        //                 trailColor = engineColor = Pal.redSpark;
        //                 engineSize = 2.25f;
        //                 engineLayer = Layer.effect;
        //                 speed = 1.5f;
        //                 maxRange = 6f;
        //                 lifetime = 60f * 3f;
        //                 // outlineColor = Pal.darkOutline;
        //                 // killable = false;
        //                 lowAltitude = true;
        //                 targetable = false;
        //                 hittable = false;
        //                 top = false;
        //                 outlineRadius = 0;

        //                 // hitEffect = new MultiEffect(Fx.titanExplosion, Fx.titanSmoke);

        //                 weapons.add(new Weapon(){{
        //                     shootSound = Sounds.none;
        //                     shootCone = 360f;
        //                     mirror = false;
        //                     reload = 1f;
        //                     shootOnDeath = true;
        //                     bullet = new ExplosionBulletType(200f, 25f){{
        //                         shootEffect = new MultiEffect(
        //                             new WrapEffect(Fx.titanExplosion, TCol.red, 24f),
        //                             new WrapEffect(Fx.titanSmoke, TCol.red, 24f)
        //                         );
        //                         damage = 150f;
        //                         range = 30f;
        //                     }};
        //                 }});
        //             }};
        //         }};
        //     }});
        
        //     weapons.add(AourusWeapons.bomb_turret);
        // }};
    
        // cargo = new UnitType("cargo") {{
        //     constructor = BuildingTetherPayloadUnit::create;
        //     controller = u -> new CargoAI();
        //     isEnemy = false;
        //     allowedInPayloads = false;
        //     logicControllable = false;
        //     playerControllable = false;
        //     envDisabled = 0;
        //     payloadCapacity = 0f;

        //     outlineColor = Pal.darkOutline;

        //     lowAltitude = false;
        //     flying = true;
        //     drag = 0.06f;
        //     speed = 3.5f;
        //     rotateSpeed = 9f;
        //     accel = 0.1f;
        //     itemCapacity = 100;
        //     health = 200f;
        //     hitSize = 9f;
        //     // engineSize = 2.3f;
        //     // engineOffset = 6.5f;
        //     hidden = true;
        //     engineSize = 0;

        //     // setEnginesMirror(
        //     // new UnitEngine(21 / 4f, 19 / 4f, 2.2f, 45f),
        //     // new UnitEngine(23 / 4f, -22 / 4f, 2.2f, 315f)
        //     // );
        // }};