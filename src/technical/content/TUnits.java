package technical.content;

import arc.math.geom.Rect;
import mindustry.Vars;
import mindustry.ai.types.BuilderAI;
import mindustry.ai.types.CommandAI;
import mindustry.ai.types.HugAI;
import mindustry.entities.part.RegionPart;
import mindustry.gen.CrawlUnit;
import mindustry.gen.LegsUnit;
import mindustry.gen.MechUnit;
import mindustry.gen.Sounds;
import mindustry.gen.TankUnit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.ammo.ItemAmmoType;
import technical.util.TCol;
import technical.core.train.RailVehicle;
import technical.core.train.RailVehicleUnit;


public class TUnits {
    public static RailVehicle basic_train;
    public static UnitType onset, vermiphorus, cravlon, incinerator, vapor, archer;

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
            mineTier = 0;

            constructor = MechUnit::create;

            rotateSpeed = 2f;

            faceTarget = true;
            rotateToBuilding = true;

            alwaysUnlocked = true;

            accel = 0.2f;

            speed = 1.5f;
            hitSize = 8f;
            health = 150;

            fogRadius = 0f;
            itemCapacity = 100;

            buildSpeed = 0.5f;

            canBoost = true;
            boostMultiplier = 0.25f;
            boostWhenBuilding = false;
            boostWhenMining = false;

            riseSpeed = 0.01f;
            fallSpeed = 0.02f;

            buildBeamOffset = Float.POSITIVE_INFINITY;
            buildRange = Vars.buildingRange * 0.7f;

            weapons.add(TWeapons.onset_weapon);
        }};

        archer = new UnitType("archer"){{
            outlineColor = Pal.darkOutline;
            constructor = MechUnit::create;

            rotateSpeed = 1f;

            faceTarget = true;

            speed = 0.5f;

            hitSize = 8f;
            health = 300;

            weapons.add(TWeapons.archer_bow);

            fogRadius = TWeapons.archer_bow.range();

            ammoType = new ItemAmmoType(TItems.flint_arrow, 1);
            ammoCapacity = 60;
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
            hovering = false;

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