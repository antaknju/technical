package technical.expansion.trap;

import static mindustry.Vars.tilesize;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import technical.content.TFx;
import technical.expansion.TBlock;
import technical.expansion.kinetic.KineticBlock;

public class SpikeTrap extends KineticBlock {
    public float damage = 5f;

    public float hitInterval = 20f;
    public float slowDuration = 25f;

    public Effect damageEffect = TFx.stringBreak;

    public SpikeTrap(String name){
        super(name);
        solid = false;
        update = true;
        size = 2;
        hasShadow = false;

        underBullets = true;
    }

    @Override
    public void load(){
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }

    public TextureRegion topRegion;

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.damage, damage / hitInterval, StatUnit.perSecond);
    }

    public class SpikeTrapBuild extends KineticBuild
    {
        float overlayTime = 0f;

        @Override
        public void updateTile()
        {
            if(timer(0, hitInterval)){
                applyTrapEffects();
            }

            if(overlayTime > 0f){
                overlayTime -= Time.delta;
            }
        }

        void applyTrapEffects()
        {
            float s = size * tilesize;

            float left   = x - s/2f;
            float bottom = y - s/2f;

            for(Unit unit : Groups.unit.intersect(left, bottom, s, s))
            {
                if(unit.team != team && unit.isGrounded()){
                    unit.damage(damage);
                    unit.apply(StatusEffects.slow, slowDuration);
                    damageEffect.at(unit.x, unit.y);

                    overlayTime = 20f;
                }
            }
        }

        @Override
        public void draw(){
            super.draw();

            if(overlayTime > 0f && ((SpikeTrap)block).topRegion != null)
            {
                Draw.alpha(Mathf.clamp(overlayTime / 12f));
                Draw.rect(((SpikeTrap)block).topRegion, x, y);
                Draw.reset();
            }
        }
    }
}