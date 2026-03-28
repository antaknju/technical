package technical.expansion.trap;

import static mindustry.Vars.tilesize;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Point2;
import mindustry.entities.bullet.BulletType;
import technical.T;
import technical.content.TWeapons;

public class DartTrap extends TrapBlock {

    public TextureRegion topRegion;
    public BulletType dartType;
    public int dartAmount = 3;

    public DartTrap(String name){
        super(name);

        rotate = true;
        update = true;
        solid = true;

        rotateDraw = false;

        dartType = TWeapons.crude_dart;
    }

    @Override
    public void load()
    {
        super.load();

        topRegion = Core.atlas.find(name + "-top");
    }

    public class DartTrapBuild extends TrapBlock.TrapBlockBuild {

        @Override
        public void draw()
        {
            Draw.rect(region, x, y);

            Draw.rect(topRegion, x, y, rotdeg());
        }

        public void shoot(float x, float y)
        {
            dartType.create(this, x, y, rotdeg());
            dartType.smokeEffect.at(x, y);
        }

        @Override
        public void onTrap()
        {
            float s = (size * tilesize) * 0.8f;

            Point2 rpos = T.Rot2Pos(rotation);
            
            float off1 = s;
            float step = s / dartAmount;

            float off2 = -step * (dartAmount / 2);

            for (int i = 0; i < dartAmount; i++)
            {
                shoot(x + rpos.x * off1 + rpos.y * off2, y + rpos.y * off1 + rpos.x * off2);

                off2 += step;
            }
        }
    }
}
