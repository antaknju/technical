package technical.expansion.mycelis;

import static mindustry.Vars.tilesize;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.type.Item;
import mindustry.world.Block;
import technical.T;
import technical.TCol;
import technical.Technical;
import technical.content.TFx;
import technical.content.TLiquids;
import technical.expansion.mycelis.MycelisHeart.MycelisHeartBuild;

public class MycelisBlock extends Block {

    public Liquid bloodLiquid = TLiquids.bio_fluid;
    public float shortageDamage = 30f;
    public float beatShortageInterval = 300f;

    public enum TraitType
    {
        small_scar,
        small_blob,
        big_blob,
        medium_vein,
        medium_lobe,
    }

    public static class TraitDef
    {
        public String name;
        public float size = tilesize;
        public float anim_speed = 0.2f;

        public float max_scale = 1f;
        public float scale_radius = 0.25f; // max is 1

        public float default_rotation = 0f;
        public float rotation_radius = 45f; // default is 0


        public TextureRegion[] regions;
    }

    public static class TraitInstance
    {
        public TraitType type;
        public float rotation;
        public float x, y;
        public float sx, sy;
        public float offset = 0;
    }

    public ObjectMap<TraitType, TraitDef> traitDefs = T.mapOf(
        TraitType.small_scar, new TraitDef()
        {
            {
                name = "small-scar";
                size = tilesize;
                scale_radius = 0.1f;
                rotation_radius = 20f;
            }
        },
        TraitType.small_blob, new TraitDef()
        {
            {
                name = "small-blob";
                size = tilesize;
                scale_radius = 0.1f;
                rotation_radius = 10f;
            }
        },
        TraitType.big_blob, new TraitDef()
        {
            {
                name = "big-blob";
                size = tilesize * 3;
                scale_radius = 0.1f;
                rotation_radius = 10f;
            }
        },
        TraitType.medium_vein, new TraitDef()
        {
            {
                name = "medium-vein";
                size = tilesize * 2;
                scale_radius = 0.15f;
                rotation_radius = 30f;
            }
        },
        TraitType.medium_lobe, new TraitDef()
        {
            {
                name = "medium-lobe";
                size = tilesize * 2;
                scale_radius = 0.15f;
                rotation_radius = 10f;
            }
        }
    );

    // public Color beatColor = T.c("#993c7ac0");

    public MycelisType type;

    public MycelisBlock(String name) {
        super(name);
        update = true;
        
        hasLiquids = true;
        hasItems = true;
        
        sync = true;
        rotate = false;

        health = 500;
        liquidCapacity = 100f;
        itemCapacity = 30;

        createRubble = false;
        destroyEffect = TFx.smokeCloud.wrap(TCol.bioOrange);
    }

    @Override
    public void load()
    {
        super.load();
        
        for (TraitType type : traitDefs.keys()) 
        {
            TraitDef def = traitDefs.get(type);
            def.regions = T.loadMultipleRegions(Technical.name + "-" + def.name);
        }
    }

    public class MycelisBuild extends Building 
    {
        final float beatDuration = 10f;

        public float beatTimer = 0f;
        public float beatScale = 0;
        public boolean beating = false;

        public float beatShortageTimer = 0f;
        public int beatCount = 0;

        public Seq<TraitInstance> traits = new Seq<>();

        @Override
        public void placed()
        {
            super.placed();
            generateTraits();
        }

        void generateTraits(){
            traits.clear();

            Seq<TraitType> keys = traitDefs.keys().toSeq();

            int chunksPerAxis = Math.max(1, size / 2);
            float blockSize = tilesize * size;
            float chunkSize = blockSize / chunksPerAxis;

            float halfBlock = blockSize / 2f;

            for(int cx = 0; cx < chunksPerAxis; cx++)
            {
                for(int cy = 0; cy < chunksPerAxis; cy++)
                {
                    if(Mathf.chance(0.20f)) continue;

                    TraitInstance ins = new TraitInstance();
                    ins.type = keys.random();
                    TraitDef def = traitDefs.get(ins.type);

                    // if (def.size > Mathf.ceil(size / 2) * tilesize) return;

                    ins.sx = def.max_scale + Mathf.random(-def.scale_radius, def.scale_radius) - def.scale_radius;
                    ins.sy = def.max_scale + Mathf.random(-def.scale_radius, def.scale_radius) - def.scale_radius;

                    // chunk-local bounds
                    float chunkMinX = -halfBlock + cx * chunkSize;
                    float chunkMinY = -halfBlock + cy * chunkSize;
                    float chunkMaxX = chunkMinX + chunkSize;
                    float chunkMaxY = chunkMinY + chunkSize;

                    // shrink bounds to account for trait size
                    float padX = ins.sx * def.size * 0.5f;
                    float padY = ins.sy * def.size * 0.5f;

                    ins.x = Mathf.random(chunkMinX + padX, chunkMaxX - padX);
                    ins.y = Mathf.random(chunkMinY + padY, chunkMaxY - padY);

                    ins.rotation = def.default_rotation + Mathf.random(def.rotation_radius);
                    ins.offset = Mathf.random(0, (def.regions.length - 1) * (60f / def.anim_speed));

                    traits.add(ins);
                }
            }
        }


        @Override
        public void draw()
        {
            if (tile == null || inFogTo(Vars.player.team())) return;

            Draw.scl(1f + beatScale * 0.3f);

            drawBase();

            drawTraits();

            drawTop();

            Draw.scl();
        }

        public void drawBase()
        {
            Draw.rect(block.region, x, y);
        }

        public void drawTop()
        {
            
        }

        public void drawTraits()
        {
            for (TraitInstance t : traits) 
            {
                TraitDef def = traitDefs.get(t.type);
                if (def == null || def.regions == null || def.regions.length == 0) continue;

                int frame = (int)((Time.time + t.offset) * def.anim_speed) % def.regions.length;
                TextureRegion region = def.regions[frame];

                Draw.rect(region, x + t.x, y + t.y, region.width / 4 * t.sx * Draw.xscl, region.height / 4 * t.sy * Draw.yscl, t.rotation);
            }
        }

        @Override
        public void updateTile() {
            super.updateTile();

            beatTimer -= delta();

            if (beatTimer <= 0f && beating) 
            {
                beating = false;
                beatShortageTimer = 0f;
            }

            if (beating)
            {
                float t = beatTimer / beatDuration;
                beatScale = Interp.sine.apply(0f, 1f, t);
            }
            else
            {
                beatScale = 0;
            }

            if (beatTimer < 0f) 
            {
                beatShortageTimer += edelta();

                if (beatShortageTimer >= beatShortageInterval) {
                    beatShortageTimer = 0f;

                    TFx.deathEffect.at(x, y, 2f, TCol.bioOrange);
                    damage(shortageDamage);
                }
            } 
            else 
            {
                beatShortageTimer = 0f;
            }
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount) {
            if (liquid == bloodLiquid) {
                super.handleLiquid(source, liquid, amount);
            }
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return liquid == bloodLiquid;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return items.total() < itemCapacity;
        }

        public void onBeat()
        {
            beating = true;
            beatTimer = beatDuration;
            beatCount++;

            if (liquids.get(bloodLiquid) >= liquidCapacity)
                heal(shortageDamage);

            for (Building b : proximity)
            {
                if (b instanceof MycelisBuild mb && !(b instanceof MycelisHeartBuild))
                {
                    if (mb.beatCount < beatCount && beatCount > 0 && !mb.beating)
                    {
                        if (b != null && b.acceptLiquid(this, bloodLiquid)) 
                        {
                            float flow = b.block.liquidCapacity - b.liquids.get(bloodLiquid);
                            b.handleLiquid(this, bloodLiquid, flow);
                        }

                        mb.onBeat();
                    }
                }
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);
            
            write.i(beatCount);
            write.f(beatShortageTimer);
            
            write.i(traits.size);
            for(TraitInstance ins : traits){
                write.i(ins.type.ordinal());
                write.f(ins.x);
                write.f(ins.y);
                write.f(ins.sx);
                write.f(ins.sy);
                write.f(ins.rotation);
                write.f(ins.offset);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            
            beatCount = read.i();
            beatShortageTimer = read.f();
            
            // Load traits
            int traitCount = read.i();
            traits.clear();
            for(int i = 0; i < traitCount; i++){
                TraitInstance ins = new TraitInstance();
                int typeOrdinal = read.i();
                ins.type = TraitType.values()[typeOrdinal];
                ins.x = read.f();
                ins.y = read.f();
                ins.sx = read.f();
                ins.sy = read.f();
                ins.rotation = read.f();
                ins.offset = read.f();
                traits.add(ins);
            }
        }
    }
}
