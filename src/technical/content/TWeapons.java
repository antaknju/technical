package technical.content;

import static mindustry.Vars.tilesize;
import static technical.debug.Debugger.print;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import arc.graphics.Color;

import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Puddles;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.entities.bullet.*;
import mindustry.entities.part.RegionPart;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Weapon;
import mindustry.type.unit.MissileUnitType;
import mindustry.type.weapons.BuildWeapon;
import mindustry.world.Tile;

import technical.utility.TCol;
import technical.expansion.BoulderBulletType;
import technical.expansion.HelperBulletType;
import technical.expansion.SpawningLaserBulletType;
import technical.utility.Fr;
import technical.utility.T;

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

    stone_boulder = new BoulderBulletType("technical-bullet-stone-boulder"){{
        damage = 100f;
        speed = 1f;

        lifetime = 90f;

        hitSize = tilesize * 2;

        hitEffect = despawnEffect = TFx.crudeExplosion;

        pierce = true;
        pierceArmor = true;
        pierceCap = 20;

        fragBullets = 6;
        fragBullet = new BoulderBulletType("technical-bullet-stone-boulder")
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

        colors = new Color[]{TLiquids.metan.color, TCol.from("#ff7a469f"), TCol.from("#ffd2c08c")};
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
        shootY = -2f;

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

            color = new Color(1, 1, 1, 1);
            colorTo = new Color(1, 1, 1, 0);

            outline = false;
            y = 2;

            moveY = -3;

            layerOffset = 1;

            mirror = false;
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