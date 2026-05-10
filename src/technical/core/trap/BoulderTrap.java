package technical.core.trap;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;
import technical.core.BoulderBulletType;

public class BoulderTrap extends TrapBlock 
{
    public float throwTime = 10f;

    public BoulderBulletType boulder;

    public TextureRegion topRegion;
    public TextureRegion boulderRegion;

    public BoulderTrap(String name)
    {
        super(name);
        
        rotate = true;
        update = true;
    }

    @Override
    public void load()
    {
        super.load();

        topRegion = Core.atlas.find(name + "-top");
        boulderRegion = Core.atlas.find(boulder.spriteName + "0");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, boulderRegion, topRegion};
    }

    public class BoulderTrapBuild extends TrapBlockBuild 
    {
        @Override
        public void onTrap() 
        {
            boulder.create(this, x, y, rotdeg());
        }

        @Override
        public void draw()
        {
            Draw.rect(region, x, y, rotdeg());

            Draw.z(Layer.block);

            Draw.color(Color.white, cooldownTimer / cooldownTime);
            Draw.rect(boulderRegion, x, y, rotdeg());
            Draw.reset();

            Draw.z(Layer.block + 2f);

            Draw.rect(topRegion, x, y, rotdeg());

            Draw.reset();
        }

        @Override
        public boolean collide(Bullet other) 
        {
            if (other.type instanceof BoulderBulletType)
            {
                if (other.time <= throwTime)
                {
                    return false;
                }
            }

            return true;
        }
    }
}
 