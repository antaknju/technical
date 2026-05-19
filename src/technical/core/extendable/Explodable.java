package technical.core.extendable;

import arc.audio.Sound;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Puddles;
import mindustry.gen.Sounds;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import technical.content.TLiquids;
import technical.core.kinetic.KineticBlock;

import static mindustry.Vars.*;

public class Explodable extends KineticBlock
{
    public boolean canExplode = false;

    public int explosionRadius = 12;
    public int explosionDamage = 2400;
    public Effect explodeEffect = Fx.explosion;
    public Sound explodeSound = Sounds.blockExplode3;

    public int explosionPuddles = 12;
    public float explosionPuddleRange = tilesize * 8f;
    public float explosionPuddleAmount = 150f;
    public @Nullable Liquid explosionPuddleLiquid = TLiquids.toxic_waste;
    public float explosionShake = 1f, explosionShakeDuration = 6f;

    public Explodable(String name)
    {
        super(name);
    }

    public class ExplodableBuild extends KineticBuild
    {
        public boolean shouldExplode()
        {
            return false;
        }

        public void createExplosion()
        {
            if(explosionDamage > 0)
            {
                Damage.damage(x, y, explosionRadius * tilesize, explosionDamage);
            }

            explodeEffect.at(this);
            explodeSound.at(this);

            if(explosionPuddleLiquid != null)
            {
                for(int i = 0; i < explosionPuddles; i++)
                {
                    Tmp.v1.trns(Mathf.random(360f), Mathf.random(explosionPuddleRange));
                    Tile tile = world.tileWorld(x + Tmp.v1.x, y + Tmp.v1.y);
                    Puddles.deposit(tile, explosionPuddleLiquid, explosionPuddleAmount);
                }
            }

            if(explosionShake > 0)
            {
                Effect.shake(explosionShake, explosionShakeDuration, this);
            }
        }

        @Override
        public void updateTile()
        {
            super.updateTile();

            if(canExplode && shouldExplode() && state.rules.reactorExplosions)
            {
                createExplosion();
            }
        }
    }
}
