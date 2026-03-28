package technical.content;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;
import static technical.debug.Debugger.print;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import arc.graphics.Color;

import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.entities.Puddles;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.DrawPart.PartProgress;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Weapon;
import mindustry.type.unit.MissileUnitType;
import mindustry.type.unit.TankUnitType;
import mindustry.type.weapons.BuildWeapon;
import mindustry.world.Tile;

import technical.TCol;
import technical.expansion.BoulderBulletType;
import technical.expansion.HelperBulletType;
import technical.expansion.SpawningLaserBulletType;
import technical.Fr;
import technical.T;

public class TWeapons 
{
    public static final HelperBulletType

    helper_package = new HelperBulletType("bullet"){{
        width = 20f;
        height = 20f;
        hitSize = 100f;

        speed = 2f;
        lifetime = 200f;
        drag = 0.01f;

        damage = 30f;
        knockback = 0.5f;
        ammoMultiplier = 5;

        frontColor = TCol.iron;
        backColor  = TCol.copper;
        trailColor = TCol.copper;

        trailLength = 8;
        trailWidth = 5f;

        smokeEffect = Fx.none;
        hitEffect = Fx.none;
        despawnEffect = Fx.none;
    }};

    public static final BoulderBulletType

    stone_boulder = new BoulderBulletType("technical-stone-boulder"){{
        damage = 100f;
        speed = 1f;

        lifetime = 90f;

        hitSize = tilesize * 2;

        hitEffect = despawnEffect = TFx.crudeExplosion;

        pierce = true;
        pierceArmor = true;
        pierceCap = 20;

        fragBullets = 6;
        fragBullet = new BoulderBulletType("technical-stone-boulder") 
        {{
            damage = 50f;
            speed = 2f;

            lifetime = 10f;

            pierce = true;
            pierceArmor = true;
            pierceCap = 20;

            hitSize = tilesize * 0.5f;

            hitEffect = despawnEffect = TFx.smallCrudeExplosion;
        }};
    }};


    public static final BulletType

    crude_dart = new BasicBulletType(){{
        width = 3f;
        height = 10f;
        hitSize = 2f;

        speed = 12f;
        lifetime = 8f;
        drag = 0.05f;

        pierce = true;
        pierceBuilding = true;

        pierceCap = 8;

        damage = 80f;
        knockback = 1f;

        frontColor = TCol.iron;
        backColor  = TCol.dense_ammo;
        trailColor = TCol.iron;

        trailLength = 6;
        trailWidth = 1.6f;

        smokeEffect = Fx.shootSmallSmoke;
        hitEffect = Fx.hitBulletSmall;
        despawnEffect = Fx.hitBulletSmall;
    }},

    iron_arrow = new BasicBulletType(){{
        width = 10f;
        height = 20f;
        hitSize = 8.5f;

        buildingDamageMultiplier = 0.5f;

        speed = 15f;
        lifetime = 30f;
        drag = 0.03f;

        pierce = true;
        pierceBuilding = true;

        pierceCap = 3;
        pierceArmor = false;

        damage = 15f;
        knockback = 0.5f;
        ammoMultiplier = 1;

        trailColor = backColor = frontColor = TCol.arrow;

        trailLength = 10;
        trailWidth = 2f;

        smokeEffect = TFx.crudeShootSmoke;
        hitEffect = despawnEffect = TFx.smallCrudeExplosion;
    }},

    crude_arrow = new BulletType(){{
        // The initial firing effects from the turret/weapon
        smokeEffect = TFx.crudeShootSmoke;
        speed = 0f;
        keepVelocity = false;
        ammoMultiplier = 1;

        // Spawns the arrow as a unit
        spawnUnit = new MissileUnitType("crude-arrow"){{
            // Map original bullet stats to the unit
            speed = 10f;
            lifetime = 30f;
            drag = 0.03f;

            rotateSpeed = 0;

            drawSoftShadow = false;

            pierce = true;
            pierceCap = 3;

            health = 15; // Missiles need health so they can be shot down (or survive long enough to hit)
            lowAltitude = true;
            outlineColor = Pal.darkOutline;

            trailColor = engineColor = TCol.arrow;
            engineSize = 1;

            deathExplosionEffect = hitEffect = despawnEffect = Fx.none;

            weapons.add(new Weapon(){{
                shootSound = Sounds.explosion;
                shootCone = 360f;
                mirror = false;
                shootOnDeath = true;

                shootEffect = Fx.none;
                smokeEffect = Fx.none;

                deathExplosionEffect = hitEffect = despawnEffect = Fx.none;

                bullet = new ExplosionBulletType(15f, 12f){{ // 15 damage, 12f splash radius (approximate to original hitSize)
                    buildingDamageMultiplier = 0.5f;
                    knockback = 0.5f;

                    shootEffect = Fx.none;
                    smokeEffect = Fx.none;

                    deathExplosionEffect = hitEffect = despawnEffect = TFx.crudeExplosion;
                }};
            }});
        }};
    }},

    metan_beam = new SpawningLaserBulletType(){{
        length = 100f;
        range = length;
        damageInterval = 10f;
        shake = 0;
        
        damage = 10f;

        colors = new Color[]{TLiquids.metan.color, T.c("#ff7a469f"), T.c("#ffd2c08c")};
        width = 3f;

        spawnType = new BulletType(2f, 37f){{
            hitSize = 5f;
            lifetime = 10f;
            pierce = true;
            pierceBuilding = true;
            pierceCap = 2;
            statusDuration = 60f * 3;
            shootEffect = TFx.shootLittleFlame;
            hitEffect = Fx.hitFlameSmall;
            despawnEffect = Fx.none;
            status = StatusEffects.burning;
            keepVelocity = false;
            hittable = false;
        }};
    }},

    vapor_bullet = new BasicBulletType(5f, 20f) {{
        width = 14f;
        height = 14f;
        lifetime = 40f;
        
        drag = 0.03f;

        damage = 60f;

        frontColor = Color.white;
        backColor = TCol.clay;
        lightColor = Color.white;
        lightOpacity = 0.4f;

        trailEffect = TFx.cloudTrail;
        
        trailChance = 0.7f;

        shootEffect = Fx.shootBigSmoke;
        smokeEffect = Fx.smoke;
        hitEffect = Fx.steam;
        despawnEffect = Fx.steam;

        status = StatusEffects.wet;
        statusDuration = Fr.time * 2f;

        pierce = true;
        pierceCap = 3;

        pierceBuilding = true;

        hitSize = 6f;
    }},

    ground_crack = new BulletType(0f, 40f){{
        speed = 0f;
        collides = false;
        collidesTiles = false;
        collidesAir = false;
        hitEffect = Fx.none;
        despawnEffect = Fx.none;
        shootEffect = Fx.none;
        smokeEffect = Fx.none;

        instantDisappear = true;
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        float cone = 40f;
        float length = 170f;

        // damage units
        Units.nearbyEnemies(b.team, b.x, b.y, length, unit -> {
            float angleTo = Angles.angle(b.x, b.y, unit.x, unit.y);
            if(Angles.within(b.rotation(), angleTo, cone) && !unit.type.flying){
                unit.damage(b.damage);
                TFx.smallCrack.at(unit.x, unit.y);
            }
        });

        // damage builds
        Vars.indexer.allBuildings(b.x, b.y, length, build -> {
            if(build.team != b.team){
                float angleTo = Angles.angle(b.x, b.y, build.x, build.y);
                if(Angles.within(b.rotation(), angleTo, cone)){
                    build.damage(b.damage);
                    TFx.smallCrack.at(build.x, build.y);
                }
            }
        });

        TFx.directCrack.at(b.x, b.y, b.rotation());

        b.remove();
    }},

    dense_iron_bullet = new BasicBulletType(){{
        width = 10f;
        height = 10f;
        hitSize = 5f;

        speed = 6f;
        lifetime = 20f;
        drag = 0.01f;

        pierce = false;
        pierceArmor = false;

        damage = 3f;
        knockback = 0.5f;
        ammoMultiplier = 5;

        frontColor = TCol.iron;
        backColor  = TCol.dense_ammo;
        trailColor = TCol.iron;

        trailLength = 6;
        trailWidth = 1.6f;

        smokeEffect = Fx.shootSmallSmoke;
        hitEffect = Fx.hitBulletSmall;
        despawnEffect = Fx.hitBulletSmall;
    }};

    public static final Weapon

    archer_bow = new Weapon("technical-archer-bow"){{
        x = 0f;
        y = 0f;
        shootY = 2f;

        layerOffset = 0.01f;

        cooldownTime = 20f;

        rotationLimit = 180f;
        rotateSpeed = 1f;
        inaccuracy = 5f;
        rotate = true;

        mirror = false;

        recoil = 2f;

        reload = 90f;

        bullet = crude_arrow;
        bullet.damage = 10f;

        parts.add(new RegionPart("-arrow"){{
            progress = PartProgress.reload;

            color = TCol.from("#ffffff");

            outline = true;
            y = 2;

            moveY = -3;

            layerOffset = 1;
        }});
    }},

    cravlon_turret = new Weapon("technical-cravlon-turret"){{
        x = 4f;
        y = 4f;
        shootY = 2f;

        showStatSprite = true;
        layerOffset = 0.01f;

        cooldownTime = 20f;

        rotationLimit = 120f;
        rotateSpeed = 1f;
        inaccuracy = 5f;
        rotate = true;

        mirror = false;

        recoil = 2f;

        reload = 40f;

        bullet = new BoulderBulletType("technical-stone-boulder") 
        {{
            damage = splashDamage = 10f;
            speed = 2.5f;

            lifetime = 100f;

            pierce = true;
            pierceArmor = true;
            pierceCap = 3;

            hitSize = tilesize;

            smokeEffect = TFx.crudeShootSmoke;

            hitEffect = despawnEffect = TFx.smallCrudeExplosion;
        }};
    }},

    vapor_turret = new Weapon("technical-vapor-turret"){{
        layerOffset = 0.0001f;
        shootY = 10f;
        recoil = 1f;
        rotate = true;
        rotateSpeed = 1.4f;
        mirror = false;
        shootCone = 2f;
        x = 0f;
        y = 0f;
        
        reload = 30f;

        parts.add(new RegionPart("-riffle"){{
            y = 0;
            moveY = -3f;
            progress = PartProgress.recoil;
            outlineLayerOffset = 0;
            under = true;
            top = false;
            mirror = false;
        }});

        bullet = vapor_bullet;
    }},

    incinerator_weapon = new Weapon("technical-incinerator-weapon"){{
        shootSound = Sounds.shootFlame;
        shootY = 2f;
        reload = 13f;
        recoil = 1f;
        y = -3;

        rotate = true;

        rotateSpeed = 1f;
        rotationLimit = 120f;

        ejectEffect = Fx.none;
        bullet = new BulletType(4.2f, 37f*2f){{
            hitSize = 7f;
            lifetime = 13f;
            pierce = true;
            pierceBuilding = true;
            pierceCap = 2;
            statusDuration = 60f * 5;
            shootEffect = Fx.shootSmallFlame;
            hitEffect = Fx.hitFlameSmall;
            despawnEffect = Fx.none;
            status = StatusEffects.burning;
            keepVelocity = false;
            hittable = false;
        }};
    }},

    onset_weapon = new BuildWeapon("technical-onset-weapon"){{
        display = false;

        top = false;

        rotate = true;

        rotateSpeed = 2f;
        rotationLimit = 60f;

        x = 5;
        y = 1;
    }},

    bio_missile_launcher = new Weapon("bio-missile-launcher"){{
        shootSound = Sounds.none;
        x = 45f;
        y = -35f;
        shootY = 0f;
        showStatSprite = false;
        layerOffset = 0.01f;
        cooldownTime = 60f;
        smoothReloadSpeed = 0.15f;
        shootWarmupSpeed = 0.05f;
        minWarmup = 0.9f;
        rotationLimit = 60f;
        rotateSpeed = 180f;
        inaccuracy = 20f;
        rotate = true;

        reload = 60f;

        bullet = new BulletType(){{
            smokeEffect = TFx.missilePoreLaunch; // smoke at launch
            shake = 2f;
            speed = 1f;            // ensure the spawn bullet moves
            keepVelocity = false;
            inaccuracy = 2f;

            spawnUnit = new MissileUnitType("bio-missile"){{
                outlineColor = TCol.bioOutline;
                trailColor = TCol.bioOrange;

                engineSize = 0f;
                engineLayer = Layer.effect;
                lowAltitude = true;

                abilities.add(new Ability() {
                    @Override
                    public void update(Unit unit) {
                        if (unit.isFlying() && Mathf.chanceDelta(0.15)) {
                            TFx.smokeCloud.at(unit.x + Mathf.range(2f), unit.y + Mathf.range(2f), TCol.bioOrange);

                            Puddles.deposit(Vars.world.tileWorld(x, y), TLiquids.bio_fluid, 10f);
                        }
                    }
                });

                // flight
                speed = 1f;
                maxRange = 120f;
                lifetime = 290f;
                health = 150f;

                // predictTarget = true;

                weapons.add(new Weapon(){{
                    shootSound = TSounds.splat;

                    shootCone = 360f;
                    mirror = false;
                    reload = 1f;
                    shootOnDeath = true;

                    bullet = new ExplosionBulletType(){
                    {
                        shootEffect = Fx.titanSmokeLarge.wrap(TCol.bioOrange);

                        pierceArmor = true;
                        splashDamage = 500;
                        splashDamageRadius = 50f;

                        smokeEffect = Fx.none;
                        shake = 4f;
                    }

                    @Override
                    public void removed(Bullet b) {
                        super.removed(b);

                        for(int i = 0; i < 30; i++){
                            float angle = Mathf.random(360f);
                            float dst = Mathf.random(10f, 40f);
                            float px = b.x + Angles.trnsx(angle, dst);
                            float py = b.y + Angles.trnsy(angle, dst);

                            Tile tile = Vars.world.tileWorld(px, py);
                            
                            Puddles.deposit(tile, TLiquids.bio_fluid, Mathf.random(30f, 50f));
                        }
                    }};
                }});
            }};
        }};
    }},

    bio_sucker = new Weapon("bio-sucker"){{
        x = 0f;
        y = 60f;
        mirror = false;
        recoil = 0f;
        shootCone = 40f;
        reload = 1f;
        continuous = true;
        showStatSprite = false;

        rotate = false;

        bullet = new ContinuousBulletType(){{
            length = 120f;
            range = length;
            damageInterval = 5f;
            shake = 0f;
            hittable = false;
            collides = false;
            keepVelocity = false;
            absorbable = false;
            damage = 1f;
            smokeEffect = Fx.none;
            continuous = true;
        }

            @Override
            public void applyDamage(Bullet b)
            {
                final float rot;
                if(b.owner instanceof Unit u){
                    rot = u.rotation();
                }else{
                    rot = b.rotation();
                }

                Log.info(rot);

                float suckStrength = 200f;
                float maxPullSpeed = 3f; // max speed toward center

                // --- PULL & DAMAGE UNITS ---
                Units.nearbyEnemies(b.team, b.x - length, b.y - length, length*2, length*2, u -> {
                    if(u.isValid()){
                        float delta = Angles.angleDist(rot, Angles.angle(b.x, b.y, u.x, u.y));
                        float dst = Mathf.dst(b.x, b.y, u.x, u.y);

                        if(delta <= shootCone || dst < 5f){

                            float offsetDistance = 10f;
                            float offsetX = b.x + Angles.trnsx(rot, offsetDistance);
                            float offsetY = b.y + Angles.trnsy(rot, offsetDistance);

                            // pull vector points toward this offset
                            Tmp.v1.set(offsetX - u.x, offsetY - u.y);

                            if(dst > 1f){
                                Tmp.v1.nor().scl(suckStrength * Time.delta / u.hitSize);
                                u.vel.lerp(Tmp.v1, 0.1f); // smooth velocity toward center
                            } else {
                                u.vel.scl(dst); // slow down very close
                            }

                            // Cap max velocity
                            if(u.vel.len() > maxPullSpeed){
                                u.vel.nor().scl(maxPullSpeed);
                            }

                            u.damage(damage / damageInterval * 60f * length / (dst + 0.001f) * length * 0.1F);
                        }
                    }
                });

                // --- DAMAGE BUILDINGS & ADD PUDDLES ---
                int tileX = Mathf.floor(b.x / Vars.tilesize);
                int tileY = Mathf.floor(b.y / Vars.tilesize);
                int radiusTiles = Mathf.ceil(length / Vars.tilesize);

                for(int x = tileX - radiusTiles; x <= tileX + radiusTiles; x++){
                    for(int y = tileY - radiusTiles; y <= tileY + radiusTiles; y++){
                        if(!Vars.world.tiles.in(x, y)) continue;
                        Tile t = Vars.world.tiles.get(x, y);

                        float dx = t.worldx() - b.x;
                        float dy = t.worldy() - b.y;
                        float dst = Mathf.dst(dx, dy);

                        if(dst <= length){
                            float angleToTile = Angles.angle(b.x, b.y, t.worldx(), t.worldy());
                            float delta = ((angleToTile - rot + 180f) % 360f) - 180f;

                            if(Math.abs(delta) <= shootCone){
                                // Damage buildings
                                if(t.build != null && t.build.team != b.team){
                                    t.build.damage(damage / damageInterval * 60f);
                                }

                                // Deposit puddles
                                if(Mathf.chance(0.05f))
                                    Puddles.deposit(t, TLiquids.bio_fluid, 15f);
                            }
                        }
                    }
                }

                // --- PARTICLES ---
                drawParticles(b);
            }

            public void drawParticles(Bullet b){
                int outerCount = 10*(int)damageInterval;
                int innerCount = 5*(int)damageInterval;

                final float rot;
                if(b.owner instanceof Unit u){
                    rot = u.rotation();
                }else{
                    rot = b.rotation();
                }

                for(int i = 0; i < outerCount; i++){
                    float angle = rot - shootCone + Mathf.random(0f, shootCone*2);
                    float radius = Mathf.random(50f, length);

                    float startX = b.x + Angles.trnsx(angle, radius);
                    float startY = b.y + Angles.trnsy(angle, radius);

                    final float centerX = b.x;
                    final float centerY = b.y;

                    Effect e = new Effect(20f, eff -> {
                        Draw.color(TCol.bioOrange, TCol.darkBioOrange, eff.fin());
                        float progress = eff.fin();
                        Fill.circle(startX + (centerX - startX) * progress, startY + (centerY - startY) * progress, 2f);
                    });
                    e.at(b.x, b.y);
                }

                for(int i = 0; i < innerCount; i++){
                    float angle = Mathf.random(0f, 360f);
                    float radius = Mathf.random(0f, 10f);

                    float startX = b.x + Angles.trnsx(angle, radius);
                    float startY = b.y + Angles.trnsy(angle, radius);

                    final float centerX = b.x;
                    final float centerY = b.y;

                    Effect e = new Effect(15f, eff -> {
                        Draw.color(TCol.bioOrange, TCol.darkBioOrange, eff.fin());
                        float progress = eff.fin();
                        Fill.circle(startX + (centerX - startX) * progress, startY + (centerY - startY) * progress, 2f + 1f*eff.fout());
                    });
                    e.at(b.x, b.y);
                }
            }
        };
    }}
    ;
}
// public static final BulletType 

    // feltarion_missile = new BulletType(0f, 0f)
    // {{
    //     shootEffect = AourusEffects.massive_shoot;
    //     smokeEffect = Fx.none;
    //     hitColor = Pal.redLight;
    //     ammoMultiplier = 1f;

    //     spawnUnit = new MissileUnitType("feltarion-missile"){{
    //         speed = 3.5f;
    //         maxRange = 6f;
    //         lifetime = 60f * 3f;
    //         outlineColor = Pal.darkOutline;
    //         engineColor = trailColor = TCol.sulphur_light;
    //         engineLayer = Layer.effect;
    //         engineSize = 3.1f;
    //         engineOffset = 10f;
    //         rotateSpeed = 0.25f;
    //         trailLength = 18;
    //         missileAccelTime = 50f;
    //         lowAltitude = true;
    //         loopSound = Sounds.missileTrail;
    //         loopSoundVolume = 0.6f;
    //         deathSound = Sounds.largeExplosion;
    //         targetAir = false;
            
    //         // targetUnderBlocks = false;

    //         fogRadius = 6f;

    //         health = 160;

    //         weapons.add(new Weapon(){{
    //             shootCone = 360f;
    //             mirror = false;
    //             reload = 1f;
    //             deathExplosionEffect = Fx.massiveExplosion;
    //             shootOnDeath = true;
    //             shake = 10f;
    //             bullet = new ExplosionBulletType(1000f, 65f){{
    //                 hitColor = Pal.redLight;
    //                 shootEffect = AourusEffects.massive_missile_explosion;

    //                 collidesAir = false;
    //                 buildingDamageMultiplier = 0.1f;

    //                 ammoMultiplier = 1f;
    //             }};
    //         }});

    //         abilities.add(new MoveEffectAbility(){{
    //             effect = Fx.missileTrailSmoke;
    //             rotation = 180f;
    //             y = -9f;
    //             color = Color.grays(0.6f).lerp(TCol.sulphur_light, 0.5f).a(0.4f);
    //             interval = 7f;
    //         }});
    //     }};
    // }},

    // bolts = new LightningBulletType()
    // {{
    //     layer = Layer.max;
    //     lightningColor = hitColor = TCol.electricity;
    //     damage = 40f;
    //     lightningLength = 40;
    //     lightningLengthRand = 8;
    //     shootEffect = Fx.lightning;

    //     fragBullets = 3;
    //     fragBullet  = new LightningBulletType()
    //     {{
    //         lightningColor = hitColor = TCol.electricity;
    //         damage = 30f;
    //         lightningLength = 8;
    //         lightningLengthRand = 1;
    //         shootEffect = Fx.none;
    //     }};
    // }},

    // cry_laser = new LaserBulletType(50f){{
    //     colors = new Color[]{TCol.electricity.cpy().a(0.4f), TCol.electricity, Color.white};
    //     // buildingDamageMultiplier = 0.25f;
    //     layer = Layer.max;

    //     width = 20f;
    //     hitEffect = Fx.hitLancer;
    //     sideAngle = 120f;
    //     sideWidth = 2f;
    //     sideLength = 20f;
    //     lifetime = 30f;
    //     drawSize = 400f;
    //     length = 120f;
    //     pierceCap = 2;

    //     fragBullets = 2;
    //     fragBullet  = new LightningBulletType()
    //     {{
    //         lightningColor = hitColor = TCol.electricity;
    //         damage = 15f;
    //         lightningLength = 6;
    //         lightningLengthRand = 2;
    //         shootEffect = Fx.lightning;
    //     }};
    // }},
    
    // sulphur_missile = new MissileBulletType(){{
    //     damage = 40f;

    //     speed = 3f;
    //     homingPower = 0.1f;
    //     homingDelay = 5f;

    //     width = 12f;
    //     height = 12f;

    //     hitEffect = AourusEffects.fireExplosion;
    //     despawnEffect = AourusEffects.fireExplosion;

    //     shootEffect = Fx.shootSmall;

    //     status = StatusEffects.burning;

    //     frontColor = trailColor = Color.valueOf("f3fae5");
    //     backColor = Color.valueOf("b6ec4e");
    //     trailLength = 10;
        
    //     hitSize = 6f;
    //     lifetime = 60f;
    //     pierce = true;
    // }},

    // hydrogen_beam = new ContinuousFlameBulletType(){{

    //     flareWidth = 3f;
    //     flareLength = 15f;

    //     damage = 30f;
    //     length = 80f;
    //     knockback = 1f;
    //     pierceCap = 2;
    //     buildingDamageMultiplier = 0.3f;

    //     flareColor = Color.valueOf("718eff");

    //     lengthWidthPans = new float[] {
    //         1.12f, 2f, 0.88f,
    //         1.1f, 1.5f, 0.88f,
    //         1.08f, 1f, 0.88f,
    //         1.06f, 0.5f, 0.88f
    //     };
    //     colors = new Color[]{Color.valueOf("91a4ff").a(0.8f), Color.valueOf("718eff"), Color.valueOf("3a8dff").a(0.8f), Color.white};
    // }},

    // ultra_hydrogen_beam = new ContinuousFlameBulletType(){{

    //     flareWidth = 5f;
    //     flareLength = 30f;

    //     damage = 160f;
    //     length = 120f;
    //     knockback = 2f;
    //     pierceCap = 4;
    //     buildingDamageMultiplier = 0.3f;

    //     flareColor = Color.valueOf("718eff");

    //     lengthWidthPans = new float[] {
    //         2f, 4f, 0.5f,
    //         1.6f, 3f, 0.45f,
    //         1.4f, 3.5f, 0.4f,
    //         1.2f, 3f, 0.35f,
    //         1f, 2f, 0.3f,
    //     };
    //     colors = new Color[]{Color.valueOf("91a4ff").a(0.8f), Color.valueOf("718eff").a(0.8f), Color.valueOf("3a8dff").a(0.8f), Color.white, Color.white};
    // }},

    // laser = new RailBulletType(){{
    //     length = 160f;
    //     damage = 40f;
    //     hitColor = TCol.electricity;
    //     hitEffect = endEffect = Fx.hitBulletColor;
    //     pierceDamageFactor = 0.8f;

    //     smokeEffect = Fx.colorSpark;

    //     endEffect = new Effect(14f, e -> {
    //         color(e.color);
    //         Drawf.tri(e.x, e.y, e.fout() * 1.5f, 5f, e.rotation);
    //     });

    //     shootEffect = new Effect(10, e -> {
    //         color(e.color);
    //         float w = 1.2f + 7 * e.fout();

    //         Drawf.tri(e.x, e.y, w, 30f * e.fout(), e.rotation);
    //         color(e.color);

    //         for(int i : Mathf.signs){
    //             Drawf.tri(e.x, e.y, w * 0.9f, 18f * e.fout(), e.rotation + i * 90f);
    //         }

    //         Drawf.tri(e.x, e.y, w, 4f * e.fout(), e.rotation + 180f);
    //     });

    //     lineEffect = new Effect(20f, e -> {
    //         if(!(e.data instanceof Vec2 v)) return;

    //         color(e.color);
    //         stroke(e.fout() * 0.9f + 0.6f);

    //         Fx.rand.setSeed(e.id);
    //         for(int i = 0; i < 7; i++){
    //             Fx.v.trns(e.rotation, Fx.rand.random(8f, v.dst(e.x, e.y) - 8f));
    //             Lines.lineAngleCenter(e.x + Fx.v.x, e.y + Fx.v.y, e.rotation + e.finpow(), e.foutpowdown() * 20f * Fx.rand.random(0.5f, 1f) + 0.3f);
    //         }

    //         e.scaled(14f, b -> {
    //             stroke(b.fout() * 1.5f);
    //             color(e.color);
    //             Lines.line(e.x, e.y, v.x, v.y);
    //         });
    //     });
    // }};

    // public static final Weapon
    // bomb_turret = new Weapon("aourus-bomb-turret"){{
    //     reload = 60f;
    //     recoil = 1.5f;
    //     inaccuracy = 15;
    //     shootSound = Sounds.missileSmall;
    //     top = true;
    //     mirror = alternate = true;
    //     rotate = true;
    //     // heatColor = ACol.electricity;
    //     shootCone = 30f;

    //     x = 15f;
    //     y = -1f;

    //     shoot = new ShootPattern(){{
    //         shots = 3;
    //         shotDelay = 3f;
    //     }};

    //     bullet = new MissileBulletType(0f, 0f, "shell"){{
    //         width = 8f;
    //         height = 10f;
    //         hitEffect = Fx.flakExplosion;
    //         shootEffect = Fx.none;
    //         smokeEffect = Fx.none;

    //         speed = 5f;
    //         lifetime = 60f;
    //         drag = 0.025f;

    //         status = StatusEffects.blasted;
    //         statusDuration = 60f;
    //         damage = splashDamage = 40f;
    //     }};
    // }},

    // shocking_turrets = new Weapon("aourus-maltorion-back"){{
    //     reload = 70;
    //     recoil = 1.5f;
    //     inaccuracy = 5;
    //     shootSound = Sounds.bolt;
    //     top = true;
    //     mirror = alternate = true;
    //     rotate = false;
    //     heatColor = TCol.electricity;
    //     shootCone = 30f;

    //     inaccuracy = 12f;

    //     x = 7f;
    //     y = -8f;

    //     shootY = 0f;
    //     shootX = 3.5f;

    //     parts.add(new RegionPart("-riffle"){{
    //         rotation = -50f;
    //         under = true;
    //         moves.add(new PartMove(PartProgress.reload, 0f, 0f, 0f));

    //         heatColor = Color.red;
    //         cooldownTime = 60f;
    //     }});

    //     shoot = new ShootPattern(){{
    //         shots = 5;
    //         shotDelay = 5f;
    //     }};

    //     bullet = new BasicBulletType(4.5f, 20f){{
    //         ejectEffect = Fx.none;
    //         trailWidth = 1.5f;
    //         trailLength = 15;
    //         drawSize = 200f;

    //         status = StatusEffects.shocked;
    //         statusDuration = 30f;
    //         lifetime = 40f;
    //         homingPower = 0.05f;
    //         homingRange = 120f;
    //         width = 10f;
    //         height = 25f;
    //         keepVelocity = true;
    //         knockback = 0.75f;
    //         trailColor = backColor = lightColor = lightningColor = hitColor = TCol.electricity;
    //         frontColor = backColor.cpy().lerp(Color.white, 0.45f);
    //         trailChance = 0.1f;
    //         trailParam = 1f;
    //         // trailEffect = NHFx.trailToGray;
    //         // despawnEffect = NHFx.square(backColor, 18f, 2, 12f, 2);
    //         // hitEffect = NHFx.lightningHitSmall(backColor);
    //         shootEffect = Fx.none;
    //         smokeEffect = Fx.colorSpark;

    //         // buildingDamageMultiplier = 0.4f;
    //     }};
    // }},
    // bolter = new Weapon("aourus-maltorion-side"){{
    //     reload = 30f;
    //     recoil = 1.5f;
    //     inaccuracy = 5;
    //     shootSound = Sounds.shockBlast;
    //     top = true;
    //     mirror = alternate = true;
    //     rotate = false;
    //     rotateSpeed = 2.55f;
    //     heatColor = TCol.electricity;
    //     shootCone = 30f;


    //     parts.add(new RegionPart("-riffle"){{
    //         moveRot = -25f;
    //         under = true;
    //         moves.add(new PartMove(PartProgress.reload, 0f, 0f, 0f));

    //         heatColor = Color.red;
    //         cooldownTime = 60f;
    //     }});


    //     x = 8f;
    //     y = -3f;

    //     shoot = new ShootPattern(){{
    //         shots = 4;
    //         shotDelay = 3f;
    //     }};
    //     bullet = new LightningBulletType()
    //     {{
    //         layer = Layer.max;
    //         lightningColor = hitColor = TCol.electricity;
    //         damage = 30f;
    //         lightningLength = 12;
    //         lightningLengthRand = 12;
    //         shootEffect = Fx.lightning;
    //     }};
    // }};