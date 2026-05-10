package technical.core;

import arc.util.Log;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;

public class HelperBulletType extends BasicBulletType 
{
    public HelperBulletType(String sprite)
    {
        this.sprite = sprite;

        collidesTeam = true;
        collidesTiles = false;

        collides = true;

        collidesGround = true;
        collidesAir = true;

        keepVelocity = true;

        healPercent = 10f;

        reflectable = false;

        weaveMag = 5;
        weaveScale = 2;
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health) 
    {
        super.hitEntity(b, entity, health);
        Log.info("Healing");
        // ((UnitEntity)entity).heal(healAmount);
    }
}
