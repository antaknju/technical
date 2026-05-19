package technical.content;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Liquids;
import mindustry.entities.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;
import static mindustry.Vars.world;

// import technical.utility.T;
import technical.util.TCol;

public class TFx {
    public static final Rand rand = new Rand();
    public static final Vec2 v = new Vec2();

    public static final Effect

    uraniumImpact = new Effect(80f, e -> {

        color(TCol.uranium);

        stroke(e.fout() * 1.5f);

        randLenVectors(e.id, 12, 8f + e.finpow() * e.rotation, (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 6 + 1f);
        });

        e.scaled(60f, b -> {
            Lines.stroke(4f * b.fout());
            Lines.circle(e.x, e.y, b.finpow() * 60f);
        });
    }),

    slapLiquid = new Effect(34, e -> {
        randLenVectors(e.id, 4, e.finpow() * 5f, (x, y) -> {
            color(e.color, Color.gray, e.fin());
            Fill.circle(e.x + x, e.y + y, e.fout() * 1.7f);
        });
    }),

    wet = new Effect(80f, e -> {
        color(e.color);

        alpha(Mathf.clamp(e.finpow() * 1.5f));

        randLenVectors(e.id, 6, e.finpow() * 15f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.foutpow() * 2f);
        });
    }),

    mineRound = new Effect(40, e -> {
        float ringRadius = 2f;
        rand.setSeed(e.id);

        for(int i = 0; i < 6; i++)
        {
            float angle = rand.random(0, 360f);

            float len = ringRadius + (e.finpow() * 4f);

            Tmp.v1.trns(angle, len);

            color(e.color, Color.gray, e.fin());
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fout() * 0.8f);
        }
    }),

    smoke = new Effect(140f, e -> {
        color(e.color, Pal.vent2, e.fin());

        alpha(e.fslope() * 0.78f);

        float length = 3f + e.finpow() * 10f;
        rand.setSeed(e.id);
        for(int i = 0; i < rand.random(3, 5); i++){
            v.trns(rand.random(360f), rand.random(length));
            Fill.circle(e.x + v.x, e.y + v.y, rand.random(1.2f, 3.5f) + e.fslope() * 1.1f);
        }
    }).layer(Layer.flyingUnit - 1),

    littleSmoke = new Effect(60f, e -> {
        color(e.color, TCol.from("#949494ff"), e.fin());

        alpha(e.fslope() * 0.78f);

        float length = 1f + e.finpow() * 4f;
        rand.setSeed(e.id);
        for(int i = 0; i < rand.random(2, 4); i++){
            v.trns(rand.random(360f), rand.random(length));
            Fill.circle(e.x + v.x, e.y + v.y, rand.random(0.3f, 1f) + e.fslope() * 0.5f);
        }
    }).layer(Layer.flyingUnit - 1),

    steamLeak = new Effect(90f, e -> {
        rand.setSeed(e.id);
        for(int i = 0; i < 20; i++)
        {
            float a = e.rotation + rand.range(20f);
            v.trns(a, rand.random(e.finpow() * 20f));

            e.scaled(e.lifetime * rand.random(0.1f, 0.3f), b -> {
                color(e.color);
                Fill.circle(e.x + v.x, e.y + v.y, b.fout() * 1f + 0.05f);
            });
        }
    }),

    volcanoDormant = new Effect(140f, e -> {
        for(int i = 0; i < 8; i++)
        {
            rand.setSeed(e.id + i);
            
            float angle = rand.random(80f, 100f);
            float speed = rand.random(30f, 60f);

            float move = e.fin(Interp.pow3Out) * speed;
            
            float sx = e.x + Mathf.cosDeg(angle) * move;
            float sy = e.y + Mathf.sinDeg(angle) * move;
            
            Draw.color(Pal.vent, Pal.vent2, e.fin());
            Fill.circle(sx, sy, 3f * (1 - e.fin()));
        }
    }).layer(Layer.darkness - 1),

    volcanoErupting = new Effect(140f, TFx::get).layer(Layer.darkness - 1),

    hugeSmoke = new Effect(180f, e -> {
        color(e.color, Pal.vent2, e.fin());

        alpha(e.fslope() * 0.8f);

        float length = 3f + e.finpow() * 12f;
        rand.setSeed(e.id);
        for(int i = 0; i < rand.random(3, 5); i++){
            v.trns(rand.random(360f), rand.random(length));
            Fill.circle(e.x + v.x, e.y + v.y, rand.random(2f, 4.5f) + e.fslope() * 1.2f);
        }
    }).layer(Layer.flyingUnit - 1),

    drop = new Effect(9, e -> {
        color(Pal.lightishGray);
        stroke(e.fout() * 2f);
        Lines.spikes(e.x, e.y, 1f + e.fout() * 6f, e.fin() * 4f, 6);
    }),

    crush = new Effect(30, e -> {
        randLenVectors(e.id, 10, 4f + e.fin() * 5f, (x, y) -> {
            color(TCol.stone, TCol.darkStone, e.fin());
            Fill.square(e.x + x, e.y + y, 0.5f + e.fout(), 40 + e.fin() * 20f);
        });
    }),

    coalSmelt = new Effect(15, e -> {
        randLenVectors(e.id, 6, 4f + e.fin() * 5f, (x, y) -> {
            color(Color.orange.a(0.5f), Color.red.a(0.5f), e.fin());
            Fill.square(e.x + x, e.y + y, 0.5f + e.fout() * 2f, 45);
        });
    }),

    bend = new Effect(30, e -> {
        randLenVectors(e.id, 12, 1f + e.fin() * 4f, (x, y) -> {
            color(TCol.copper);
            Fill.square(e.x + x, e.y + y, 0.2f + e.fout(), 40 + e.fin() * 10f);
        });
    }),

    roll = new Effect(40, e -> {
        randLenVectors(e.id, 10, 4f + e.fin() * 5f, (x, y) -> {
            color(TCol.iron, TCol.ironDark, e.fin());
            Fill.square(e.x + x, e.y + y, 0.5f + e.fout(), 40 + e.fin() * 20f);
        });
    }),

    rivet = new Effect(40, e -> {
        randLenVectors(e.id, 10, 1f + e.fin() * 4f, (x, y) -> {
            color(TCol.brass);
            Fill.square(e.x + x, e.y + y, 0.5f + e.fout(), 40 + e.fin() * 20f);
        });
    }),

    missilePoreLaunch = new Effect(280.0F, (e) -> {
        Draw.color(TCol.bioOrange);
        Draw.alpha(0.6F);
        rand.setSeed(e.id);

        for(int i = 0; i < 12; ++i) {
            float len = rand.random(25.0F);
            float rot = rand.range(120.0F) + e.rotation;
            e.scaled(e.lifetime * rand.random(0.3F, 1.0F), (b) -> {
                v.trns(rot, len * b.finpow());
                Fill.circle(e.x + v.x, e.y + v.y, 7.4F * b.fout() + 0.2F);
            });
        }
    }),

    shortLaserBeam = new Effect(2f, e -> {
        if(!(e.data instanceof Vec2 target)) return;

        float x1 = e.x, y1 = e.y;
        float x2 = target.x, y2 = target.y;
        float angle = Angles.angle(x1, y1, x2, y2);
        
        float pulse = 1f + Mathf.sin(Time.time, 6f, 0.2f);

        Draw.blend(Blending.additive);
        
        // Outer Glow
        Draw.color(Color.red, Color.scarlet, e.fin());
        Lines.stroke(2.5f * e.fout() * pulse);
        Lines.line(x1, y1, x2, y2);

        // Inner Core
        Draw.color(Color.white);
        Lines.stroke(1f * e.fout() * pulse);
        Lines.line(x1, y1, x2, y2);
        
        // Turn off additive blending
        Draw.blend();

        Draw.color(Color.white, Color.red, e.fin());
        Angles.randLenVectors(e.id + 1, 5, 15f * e.finpow(), angle + 180f, 50f, (rx, ry) -> {
            Lines.stroke(1f * e.fout());
            Lines.lineAngle(x2 + rx, y2 + ry, angle + 180f, 3f * e.fout());
        });

        Draw.color(Color.lightGray);
        Draw.alpha(0.3f * e.fout());
        Angles.randLenVectors(e.id + 1, 2, 8f, e.rotation, 20f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 1f + e.fout());
        });

        Draw.reset();
    }).layer(Layer.effect + 1f),

    bomb = new Effect(40f, e -> {
        // 1. Core Heat Flash (The bright center)
        Draw.color(Color.white, Color.sky, e.fin());
        Lines.stroke(2f * e.fout());
        Lines.circle(e.x, e.y, 3f + e.fin() * 6f);

        // 2. Flying Metal Shards (Fast, sharp particles)
        Rand rand = new Rand();
        rand.setSeed(e.id);
        for(int i = 0; i < 6; i++){
            float ang = rand.random(360f);
            float len = rand.random(10f, 35f);
            Draw.color(Color.orange, Color.gray, e.fin());
            
            // Draws a sharp line representing a shard
            e.scaled(e.lifetime * rand.random(0.5f, 1f), b -> {
                Lines.stroke(1.5f * b.fout());
                Lines.lineAngle(e.x, e.y, ang, b.fin() * len);
            });
        }

        // 3. Smoke & Particles (The "dirty" welding look)
        Draw.color(Color.darkGray, Color.black, e.fin());
        for(int i = 0; i < 4; i++){
            float ang = rand.random(360f);
            float dst = rand.random(5f, 20f);
            Fill.circle(
                e.x + Angles.trnsx(ang, dst * e.fin()), 
                e.y + Angles.trnsy(ang, dst * e.fin()), 
                1f + e.fout() * 2.5f
            );
        }

        // 4. Spark Sprites (The "Fire" aesthetic)
        Draw.color(Color.yellow, Color.scarlet, e.fin());
        rand.setSeed(e.id + 1);
        for(int i = 0; i < 10; i++){
            float ang = rand.random(360f);
            float dst = rand.random(5f, 40f);
            Fill.poly(
                e.x + Angles.trnsx(ang, dst * e.fin()), 
                e.y + Angles.trnsy(ang, dst * e.fin()), 
                3, // Triangular sparks
                1.2f * e.fout(), 
                ang
            );
        }
    }),

    welding = new Effect(40f, e -> {
        float spread = 25f;
        
        float smokeX = e.x + Angles.trnsx(e.rotation, 3f);
        float smokeY = e.y + Angles.trnsy(e.rotation, 3f);

        Draw.color(Color.darkGray, Color.black, e.fin());
        for(int i = 0; i < 4; i++){
            float ang = e.rotation + rand.range(spread); 
            float dst = rand.random(5f, 25f);
            
            Fill.circle(
                smokeX + Angles.trnsx(ang, dst * e.fin()), 
                smokeY + Angles.trnsy(ang, dst * e.fin()), 
                1f + e.fout() * 2.5f
            );
        }

        float sparkX = e.x + Angles.trnsx(e.rotation, -2.5f);
        float sparkY = e.y + Angles.trnsy(e.rotation, -2.5f);

        Draw.color(Color.yellow, Color.scarlet, e.fin());
        rand.setSeed(e.id + 1);
        for(int i = 0; i < 10; i++){
            float ang = e.rotation + rand.range(spread * 0.7f);
            float dst = rand.random(2f, 45f); // Reduced min distance so they start at the offset
            
            Fill.poly(
                sparkX + Angles.trnsx(ang, dst * e.fin()), 
                sparkY + Angles.trnsy(ang, dst * e.fin()), 
                3, 
                1.2f * e.fout(), 
                ang
            );
        }
    }),

    notPolishing = new Effect(35f, e -> {
        Rand rand = new Rand();
        rand.setSeed(e.id);

        // 1. Centrifugal Dust Clouds (Orbits outward)
        Draw.color(Color.lightGray, Color.gray, e.fin());
        for(int i = 0; i < 8; i++){
            float angleOffset = rand.random(360f);
            // Spin the dust 120 degrees over its lifetime
            float currentAngle = angleOffset + (e.fin() * 120f); 
            float distance = 2f + (e.fin() * 18f);
            
            Fill.circle(
                e.x + Angles.trnsx(currentAngle, distance),
                e.y + Angles.trnsy(currentAngle, distance),
                0.5f + e.fout() * 2.2f
            );
        }

        // 2. High-Speed Scrape Arcs (The "Corrected" Swirl)
        Draw.color(Color.white, Color.lightGray, e.fout());
        Lines.stroke(1.2f * e.fout());
        for(int i = 0; i < 3; i++){
            float radius = 4f + rand.random(8f);
            float rotation = rand.random(360f) + (e.fin() * 180f);
            // arc(x, y, radius, fraction, rotation)
            // fraction 0.2f creates a small 72-degree segment
            Lines.arc(e.x, e.y, radius, 0.2f, rotation);
        }

        // 3. Tangential Micro-Sparks (Fine debris)
        Draw.color(Color.gray);
        for(int i = 0; i < 5; i++){
            float baseAng = rand.random(360f);
            float moveAng = baseAng + (e.fin() * 90f);
            float len = rand.random(10f, 25f);
            
            e.scaled(e.lifetime * rand.random(0.3f, 0.8f), b -> {
                float x = e.x + Angles.trnsx(moveAng, b.fin() * len);
                float y = e.y + Angles.trnsy(moveAng, b.fin() * len);
                Fill.square(x, y, b.fout() * 1.2f, moveAng);
            });
        }
    }),

    polishing = new Effect(60f, e -> {
        Rand rand = new Rand();
        rand.setSeed(e.id);

        for(int i = 0; i < 10; i++)
        {
            float particleDelay = rand.random(0.4f);
            float speedMult = rand.random(0.1f, 0.5f);
            float startAngle = rand.random(360f);
            
            float baseSize = rand.random(3f, 6f);

            e.scaled(e.lifetime * (1f + particleDelay), b -> {
                // 1. Spinning Logic (Fast circular motion)
                // b.fin() goes from 0 to 1 for this specific particle's life
                float rotation = startAngle + (b.fin() * 600f * speedMult);
                
                // 2. The "Fling" Logic (Centrifugal escape)
                // We use pow(3) so they stay in the ellipse for a bit, then fly away fast
                float escape = Mathf.pow(b.fin(), 2f) * 20f;
                
                // 3. Ellipse Projection
                float xOffset = Mathf.cosDeg(rotation) * (baseSize + escape);
                float yOffset = Mathf.sinDeg(rotation) * (baseSize + escape);

                // 4. Perspective/Depth Simulation
                // Particles at the "back" (positive Y) are slightly smaller than the "front"
                float perspectiveSize = Mathf.lerp(0.4f, 1.2f, (yOffset + 10f) / 20f);
                float size = perspectiveSize * b.fout() * rand.random(0.5f, 1.5f);

                // 5. Draw
                Draw.color(Color.lightGray, Color.darkGray, b.fin());
                
                // Use tiny squares for "grit" rather than circles
                Fill.square(e.x + xOffset, e.y + yOffset, size, rotation);
            });
        }
    }),

    cloudTrail = new Effect(40f, e -> {
        Rand rand = new Rand();
        rand.setSeed(e.id);

        for(int i = 0; i < 8; i++)
        {
            float particleDelay = rand.random(0.5f);
            float angle = rand.random(360f);
            
            float baseSize = rand.random(4f, 9f); 
            float spread = rand.random(10f, 20f);

            e.scaled(e.lifetime * (0.6f + particleDelay), b -> {
                float move = b.fin(Interp.pow3Out) * spread;
                
                float xOffset = Mathf.cosDeg(angle) * move;
                float yOffset = Mathf.sinDeg(angle) * move;

                float grow = b.fin(Interp.pow2Out) * baseSize;
                float size = grow * b.fout(Interp.pow2In);

                Draw.color(Color.white, TCol.clay, b.fin());
                
                Fill.circle(e.x + xOffset, e.y + yOffset, size);
            });
        }
    }),

    groundCrack = new Effect(60f, 64f, e -> {
        rand.setSeed(e.id);
        Draw.color(Pal.darkestGray);
        for(int j = 0; j < 5; j++) {
            Vec2 lastPos = new Vec2(e.x, e.y);
            float lastRot = rand.random(360);

            for(int i = 0; i < 8; i++) {
            v.trns(lastRot, 4).add(lastPos);
            Lines.stroke((1.5f - 1f/8f * i) * Interp.exp5Out.apply(e.fout()));
            Lines.line(lastPos.x, lastPos.y, v.x, v.y);
            lastRot += rand.range(90);
            lastPos.set(v);
            }
        }
    }).layer(Layer.block - 1),

    directCrack = new Effect(60f, e -> {
        rand.setSeed(e.id);

        for(int j = 0; j < 6; j++)
        {
            Vec2 lastPos = new Vec2(e.x, e.y);
            float lastRot = e.rotation + rand.range(45);

            for(int i = 0; i < 18; i++)
            {
                v.trns(lastRot, 10).add(lastPos);

                var tile = world.tileWorld(v.x, v.y);
                Draw.color(tile != null ? tile.floor().mapColor : Pal.darkestGray);

                Lines.stroke((3f - 1f/16f * i) * Interp.exp5Out.apply(e.fout()));
                Lines.line(lastPos.x, lastPos.y, v.x, v.y);
                lastRot += rand.range(20 + 2.5f * i);
                lastPos.set(v);
            }
        }
    }).layer(Layer.block - 1),

    smallCrack = new Effect(60f, e -> {
        rand.setSeed(e.id);

        for(int j = 0; j < 4; j++)
        {
            Vec2 lastPos = new Vec2(e.x, e.y);
            float lastRot = rand.range(360);

            for(int i = 0; i < 6; i++)
            {
                v.trns(lastRot, 6).add(lastPos);

                var tile = world.tileWorld(v.x, v.y);
                Draw.color(tile != null ? tile.floor().mapColor : Pal.darkestGray);

                Lines.stroke((3f - 1f/8f * i) * Interp.exp5Out.apply(e.fout()));
                Lines.line(lastPos.x, lastPos.y, v.x, v.y);
                lastRot += rand.range(90);
                lastPos.set(v);
            }
        }
    }).layer(Layer.block - 1),

    stringBreak = new Effect(10, e -> {
        randLenVectors(e.id, 4, 2f + e.fin() * 3f, (x, y) -> {
            color(Color.white, e.color, e.fin());
            Fill.square(e.x + x, e.y + y, 0.25f + e.fslope() * 1f, 45);
        });
    }),

    crudeExplosion = new Effect(30, e -> {
        color(TCol.stone);

        e.scaled(7, i -> {
            stroke(3f * i.fout());
            Lines.circle(e.x, e.y, 3f + i.fin() * 10f);
        });

        color(Color.gray);

        randLenVectors(e.id, 10, 4f + 24f * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 3f + 0.5f);
            Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout());
        });

        color(TCol.darkStone);
        stroke(3f * e.fout());

        randLenVectors(e.id + 1, 8, 1f + 23f * e.finpow(), (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f);
        });
    }),

    edgeCloud = new Effect(45f, e -> {
        float length = e.data instanceof Float ? (Float)e.data : 16f;
        float rot = e.rotation;

        int points = Mathf.ceil(length / 2.5f);

//        Draw.z(Layer.debris);

        rand.setSeed(e.id);

        for(int i = 0; i < points; i++)
        {
            float t = points == 1 ? 0.5f : (float)i / (points - 1);
            float offset = (t - 0.5f) * length;

            float driftAngle = rand.random(360f);
            float driftDist = rand.random(2f, 8f) * e.fin();

            float spreadX = rand.range(2f);
            float spreadY = rand.range(2f);

            float baseX = e.x + Angles.trnsx(rot, offset) + Angles.trnsx(driftAngle, driftDist) + spreadX;
            float baseY = e.y + Angles.trnsy(rot, offset) + Angles.trnsy(driftAngle, driftDist) + spreadY;

            float shade = rand.random(0.4f, 0.85f);
            float alpha = rand.random(0.5f, 1f) * e.fout();

            Draw.color(shade, shade, shade, alpha);

            float size = rand.random(1f, 2.8f) * e.fout();
            Fill.circle(baseX, baseY, size);
        }
    }).layer(Layer.groundUnit),

    ironExplosion = new Effect(30, e -> {
        color(TCol.iron);

        e.scaled(7, i -> {
            stroke(3f * i.fout());
            Lines.circle(e.x, e.y, 3f + i.fin() * 10f);
        });

        color(TCol.coal);

        randLenVectors(e.id, 10, 4f + 24f * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 3f + 0.5f);
            Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout());
        });

        color(TCol.ironDark);
        stroke(3f * e.fout());

        randLenVectors(e.id + 1, 8, 1f + 23f * e.finpow(), (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f);
        });
    }),

    smallCrudeExplosion = new Effect(30, e -> {
        color(TCol.stone);

        e.scaled(5, i -> {
            stroke(i.fout());
            Lines.circle(e.x, e.y, 2f + i.fin() * 8f);
        });

        color(Color.gray);

        randLenVectors(e.id, 5, 2f + 12f * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 1.5f + 0.25f);
            Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout());
        });

        color(TCol.darkStone);
        stroke(1.5f * e.fout());

        randLenVectors(e.id + 1, 6, 0.5f + 12f * e.finpow(), (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 1.5f);
        });
    }),

    shootLittleFlame = new Effect(22f, 40f, e -> {
        color(Pal.lightFlame, Pal.darkFlame, Color.gray, e.fin());

        randLenVectors(e.id, 15, e.finpow() * 10f, e.rotation, 10f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.40f + e.fout() * 1.2f);
        });
    }),

    burning = new Effect(22f, 40f, e -> {
        // Gradient color from light flame to dark to gray
        color(Pal.lightFlame, Pal.darkFlame, Color.gray, e.fin());

        // Make particles spread more uniformly
        randLenVectors(e.id, 30, e.finpow() * 10f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.4f + e.fout() * 1.2f);
        });
    }),


    crudeShootSmoke = new Effect(30, e -> {
        color(TCol.stone);
        e.scaled(5, i -> {
            stroke(1f * i.fout());
            Lines.circle(e.x, e.y, 2f + i.fin() * 8f);
        });

        color(Color.gray);
        randLenVectors(e.id, 6, 2f + 12f * e.finpow(), e.rotation, 25f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 1.5f + 0.25f);
            Fill.circle(e.x + x/2f, e.y + y/2f, e.fout());
        });

        color(TCol.darkStone);
        stroke(1.5f * e.fout());
        randLenVectors(e.id + 1, 8, 0.5f + 12f * e.finpow(), e.rotation, 18f, (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 1.5f);
        });
    }),


    smokeHydro = new Effect(80.0F, (e) -> {
        color(Liquids.hydrogen.color);
        Draw.alpha(0.6F);
        rand.setSeed(e.id);

        for(int i = 0; i < 24; ++i) {
            float len = rand.random(50.0F);
            float rot = rand.range(180.0F) + e.rotation;
            e.scaled(e.lifetime * rand.random(0.3F, 1.0F), (b) -> {
                v.trns(rot, len * b.finpow());
                Fill.circle(e.x + v.x, e.y + v.y, 5.3F * b.fout() + 0.2F);
            });
        }
    }).followParent(false),

    smokeColor = new Effect(280.0F, (e) -> {
        Draw.color(e.color);
        Draw.alpha(0.6F);
        rand.setSeed(e.id);

        for(int i = 0; i < 12; ++i) {
            float len = rand.random(25.0F);
            float rot = rand.range(120.0F) + e.rotation;
            e.scaled(e.lifetime * rand.random(0.3F, 1.0F), (b) -> {
                v.trns(rot, len * b.finpow());
                Fill.circle(e.x + v.x, e.y + v.y, 5.3F * b.fslope() + 0.2F);
            });
        }
    }),

    smokeTrailColor = new Effect(80.0F, (e) -> {
        Draw.color(e.color);
        Draw.alpha(0.6F);
        rand.setSeed(e.id);

        for(int i = 0; i < 2; ++i) {
            float len = rand.random(25.0F);
            float rot = rand.range(10.0F) + e.rotation;
            e.scaled(e.lifetime * rand.random(0.3F, 1.0F), (b) -> {
                v.trns(rot, len * b.finpow());
                Fill.circle(e.x + v.x, e.y + v.y, 2.3F * b.fout() + 0.2F);
            });
        }
    }),

    kalyxSmoke = new Effect(70.0F, (e) -> {
        Draw.color(TCol.bioOrange, TCol.bioOutline, e.fout());
        Draw.alpha(0.6F);
        rand.setSeed(e.id);

        for(int i = 0; i < 12; ++i) {
            float len = rand.random(50.0F);
            float rot = (rand.range(30.0F) + e.rotation);
            e.scaled(e.lifetime * rand.random(0.3F, 1.0F), (b) -> {
                v.trns(rot, len * b.finpow());
                Fill.circle(e.x + v.x, e.y + v.y, 5.3F * b.foutpow() + 0.2F);
            });
        }
    }).followParent(false),

    kalyxShoot = new Effect(9.0F, (e) -> {
        Draw.color(TCol.bioOrange, TCol.bioOutline, e.fin());
        float w = 1.2F + 12.0F * e.fout();
        Drawf.tri(e.x, e.y, w, 50.0F * e.fout(), e.rotation);
        Drawf.tri(e.x, e.y, w, 8.0F * e.fout(), e.rotation + 180.0F);
    }),

    smokeCloud = new Effect(70, e -> {
        randLenVectors(e.id, e.fin(), 30, 30f, (x, y, fin, fout) -> {
            color(e.color);
            alpha((0.5f - Math.abs(fin - 0.5f)) * 2f);
            Fill.circle(e.x + x, e.y + y, 0.5f + fout * 4f);
        });
    }),

    deathEffect = new Effect(40, e -> {
        randLenVectors(e.id, e.fin(), 15, 10f, (x, y, fin, fout) -> {
            color(e.color);
            alpha((0.5f - Math.abs(fin - 0.5f)) * 2f);
            Fill.circle(e.x + x, e.y + y, 0.5f + fout * 2f);
        });
    }),

    // solarFlareOld = new Effect(120f, e -> 
    // {
    //     blend(Blending.additive);

    //     float range = 0.5f * tilesize;
    //     float bigBaseSize = 1f;
    //     float smallBaseSize = 0.5f;

    //     color(Color.white, e.color, e.fout());
    //     alpha(e.fslope() * 3f);

    //     rand.setSeed(e.id);

    //     float bigX = e.x + rand.random(-range, range);
    //     float bigY = e.y + rand.random(-range, range);

    //     Fill.square(bigX, bigY, bigBaseSize + Mathf.absin(Time.time, 1f, 0.1f), 45f);
    //     Drawf.light(bigX, bigY, bigBaseSize * 4f, Color.white, Math.min(e.fslope() * 3f, 1f));

    //     if(rand.chance(0.5f))
    //     {
    //         float smallX = e.x + rand.random(-range, range);
    //         float smallY = e.y + rand.random(-range, range);

    //         Fill.square(smallX, smallY, smallBaseSize + Mathf.absin(Time.time, 1f, 0.1f), 45f);
    //     }

    //     blend();
    // }).layer(Layer.flyingUnit - 1),

    solarFlare = new Effect(60, e -> {

        randLenVectors(e.id, 2, 7f, (x, y) -> {
            color(Color.white, e.color, e.fin());
            Fill.square(e.x + x, e.y + y, 0.4f + e.fslope() * 0.8f, 45);
        });
    }),


    // uraniumSplat = new Effect(1000f, 500f, b -> {
    //     float intensity = 5f;

    //     color(TCol.uranium);
    //     for(int i = 0; i < 6; i++){
    //         rand.setSeed(b.id*2 + i);
    //         float lenScl = rand.random(2f, 3f);
    //         int fi = i;
    //         b.scaled(b.lifetime * lenScl, e -> {
    //             randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(5f * intensity), 32f * intensity, (x, y, in, out) -> {
    //                 float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
    //                 float rad = fout * ((2f + intensity) * 1.65f);

    //                 Fill.circle(e.x + x, e.y + y, rad);
    //                 Drawf.light(e.x + x, e.y + y, rad * 3f, b.color, 0.7f);
    //             });
    //         });
    //     }
    // }).layer(Layer.bullet - 2f),

    uraniumReactorExplosion = new Effect(30, 500f, b -> {
        float intensity = 6.8f;
        float baseLifetime = 25f + intensity * 16f;
        b.lifetime = 50f + intensity * 65f;

        color(TCol.uranium);
        alpha(0.7f);
        for(int i = 0; i < 4; i++){
            rand.setSeed(b.id*2 + i);
            float lenScl = rand.random(0.4f, 1f);
            int fi = i;
            b.scaled(b.lifetime * lenScl, e -> {
                randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(2.9f * intensity), 22f * intensity, (x, y, in, out) -> {
                    float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                    float rad = fout * ((2f + intensity) * 2.35f);

                    Fill.circle(e.x + x, e.y + y, rad);
                    Drawf.light(e.x + x, e.y + y, rad * 2.5f, TCol.uranium, 0.5f);
                });
            });
        }

        b.scaled(baseLifetime, e -> {
            Draw.color();
            e.scaled(5 + intensity * 2f, i -> {
                stroke((3.1f + intensity/5f) * i.fout());
                Lines.circle(e.x, e.y, (3f + i.fin() * 14f) * intensity);
                Drawf.light(e.x, e.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * e.fout());
            });

            color(Pal.lighterOrange, Pal.reactorPurple, e.fin());
            stroke((2f * e.fout()));

            Draw.z(Layer.effect + 0.001f);
            randLenVectors(e.id + 1, e.finpow() + 0.001f, (int)(8 * intensity), 28f * intensity, (x, y, in, out) -> {
                lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * 4 * (4f + intensity));
                Drawf.light(e.x + x, e.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
            });
        });
    });

    private static void get(Effect.EffectContainer e) {
        for (int i = 0; i < 8; i++) {
            rand.setSeed(e.id + i);

            float angle = rand.random(80f, 100f);
            float speed = rand.random(30f, 60f);

            float move = e.fin(Interp.pow3Out) * speed;

            float sx = e.x + Mathf.cosDeg(angle) * move;
            float sy = e.y + Mathf.sinDeg(angle) * move;

            color(Pal.vent, Pal.vent2, e.fin());
            Fill.circle(sx, sy, 3f * (1 - e.fin()));
        }

        for (int i = 0; i < 4; i++) {
            rand.setSeed(e.id + i + 100);

            float ang = 90 + rand.random(-40f, 40f);
            float force = rand.random(60f, 100f);
            float gravity = 120f;
            float split = 0.5f;

            if (e.fin() < split) {
                float t = e.fin() / split;

                color(TCol.lavaOrange, TCol.lavaRed, t);
                for (int j = 0; j < 3; j++) {
                    float trailT = Math.max(0, t - (j * 0.05f));
                    float tx = e.x + Mathf.cosDeg(ang) * force * trailT;
                    float ty = e.y + Mathf.sinDeg(ang) * force * trailT - (0.5f * gravity * trailT * trailT);
                    Fill.circle(tx, ty, (1f + 2f * Mathf.slope(t)) * (1f - j * 0.2f));
                }
            } else {
                float t = (e.fin() - split) / (1f - split);

                // Landing position (where t of flight was 1.0)
                float lx = e.x + Mathf.cosDeg(ang) * force;
                float ly = e.y + Mathf.sinDeg(ang) * force - (0.5f * gravity);

                color(Pal.vent, Pal.vent2, e.fin());
                for (int j = 0; j < 3; j++) {
                    // Sub-seed for the steam circles so they don't teleport
                    rand.setSeed(e.id + i + j + 500);
                    float driftX = lx + Mathf.sin(t * 8f + j) * 4f + rand.range(5f);
                    float driftY = ly + t * rand.random(10f, 30f);

                    alpha((1f - t) * 0.6f);
                    Fill.circle(driftX, driftY, 1f + (Mathf.slope(t) * 1.5f));
                }

                Puddles.deposit(world.tileWorld(lx, ly), world.tileWorld(lx, ly), TLiquids.lava, 0.3f, true, false);
            }
        }
    }
}