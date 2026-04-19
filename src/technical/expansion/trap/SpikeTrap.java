package technical.expansion.trap;

import static technical.debug.Debugger.print;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import technical.content.TFx;
import technical.expansion.kinetic.KineticBlock;

public class SpikeTrap extends KineticBlock
{
    public float damage = 5f;

    public float hitInterval = 20f;
//    public float slowDuration = 25f;

    public float overlayTime = 20f;

    public Effect damageEffect = TFx.stringBreak;

    public SpikeTrap(String name)
    {
        super(name);
        solid = false;
        update = true;
        size = 2;
        hasShadow = false;

        underBullets = true;
    }

    @Override
    public void load()
    {
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }

    public TextureRegion topRegion;

    @Override
    public void setStats()
    {
        super.setStats();
        stats.add(Stat.damage, damage / hitInterval, StatUnit.perSecond);
    }

    public class SpikeTrapBuild extends KineticBuild
    {
        float overlayTimer = 0f;

        @Override
        public void updateTile()
        {
            if(overlayTimer > 0f)
            {
                overlayTimer -= delta();
            }
        }

        public void unitOnAny(Unit unit)
        {
            if (efficiency >= 0 && unit.team != team && unit.isGrounded())
            {
                unit.apply(StatusEffects.slow, 4f);

                if (timer(0, hitInterval))
                {
                    unit.damagePierce(damage);
                    damageEffect.at(unit.x, unit.y);

                    consume();

                    overlayTimer = overlayTime;
                }
            }
        }

        @Override
        public void draw()
        {
            super.draw();

            if(overlayTimer > 0f && ((SpikeTrap)block).topRegion != null)
            {
                Draw.alpha(Mathf.clamp(overlayTimer / 12f));
                Draw.rect(((SpikeTrap)block).topRegion, x, y);
                Draw.reset();
            }
        }
    }
}