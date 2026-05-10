package technical.core;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;

import static mindustry.Vars.tilesize;

import arc.Core;
import arc.struct.Seq;

public class BoulderBulletType extends BulletType
{
    public TextureRegion[] frames;
    public float animSpeed = 2.5f;

    public String spriteName;

    public BoulderBulletType(String spriteName)
    {
        splashDamage = damage;
        splashDamageRadius = hitSize * 2f;

        collidesTiles = true;
        collidesGround = true;
        collidesAir = false;
        collideTerrain = true;
        // collidesTeam = true;

        keepVelocity = true;

        hitUnder = false;

        this.spriteName = spriteName;
    }

    @Override
    public void load()
    {
        Seq<TextureRegion> temp = new Seq<>();

        int i = 0;
        while(true)
        {
            TextureRegion region = Core.atlas.find(spriteName + i);
            if(region.found()) 
            {
                temp.add(region);
                i++;
            } 
            else
                break;
        }

        if(i > 0)
        {
            frames = new TextureRegion[i];
            frames = temp.toArray(TextureRegion.class);
        }
    }

    @Override
    public void update(Bullet b) {
        super.update(b);

        Building build = b.tileOn() != null ? b.tileOn().build : null;

        if (build != null) 
        {
            if (!build.block.underBullets && build.collide(b)) 
            {
                hit(b);
                b.remove();
            }
        }
    }


    @Override
    public void draw(Bullet b)
    {
        if (frames == null || frames.length <= 0) return;

        Draw.z(Layer.block + 1f);

        int frameIndex = frames.length > 1 ? (int)((b.time / animSpeed) % frames.length) : 0;

        Draw.scl(hitSize / tilesize / 2f);
        Draw.rect(frames[frameIndex], b.x, b.y, b.rotation());
        Draw.reset();
    }

    @Override
    public void removed(Bullet b)
    {
        super.removed(b);

        if (b.hit) hit(b);
    }
}
