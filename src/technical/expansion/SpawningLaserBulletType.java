package technical.expansion;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.entities.Damage;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;

public class SpawningLaserBulletType extends ContinuousLaserBulletType {

    public BulletType spawnType;
    public float spawnChance = 0.1f;
    public int spawnTries = 3;
    public float spread = 90f;

    public SpawningLaserBulletType(){
        super();

        // intervalBullets = spawnTries;
    }

    @Override
    public void update(Bullet b)
    {
        super.update(b);

        float fout = Mathf.clamp(
            b.time > b.lifetime - fadeTime ?
                1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f
        );

        float realLength = Damage.findLength(b, length * fout, laserAbsorb, pierceCap);
        float rot = b.rotation();

        for(int i = 0; i < spawnTries; i++)
        {
            if(Mathf.chanceDelta(spawnChance))
            {
                float dst = Mathf.random(realLength);

                Vec2 vec = Tmp.v2.trns(rot, dst);

                Bullet spawned = spawnType.create(
                    b,
                    b.team,
                    b.x + vec.x,
                    b.y + vec.y,
                    rot + Mathf.range(spread),
                    Mathf.random(0.7f, 1.3f)
                );

                if(spawned != null && spawnType.shootEffect != null){
                    spawnType.shootEffect.at(spawned.x, spawned.y, spawned.rotation());
                }
            }
        }
    }
}