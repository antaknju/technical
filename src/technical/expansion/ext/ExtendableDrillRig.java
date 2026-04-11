package technical.expansion.ext;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.graphics.Layer;
import technical.utility.TCol;
import technical.content.TFx;
import technical.expansion.tech.TechStat;

import static mindustry.Vars.tilesize;

public class ExtendableDrillRig extends ExtendableDrill
{
    public TextureRegion rigRegion;
    public TextureRegion bottomRegion;
    public float rigRange = tilesize;
    public Color rigHolderColor = TCol.metal;
    public float rigHolderThickness = 2.5f;

    public Effect rigEffect = TFx.mineRound;
    public float rigEffectChance = 0.05f;

    public float rigChangeTime = 200f;

    public ExtendableDrillRig(String name)
    {
        super(name);

        updateEffect = Fx.none;
    }

    @Override
    public void load()
    {
        super.load();

        rigRegion = Core.atlas.find(name + "-rig");
        bottomRegion = Core.atlas.find(name + "-bottom");
    }

    public class ExtendableDrillRigBuild extends ExtendableDrillBuild
    {
        public Vec2 currentRigPos = new Vec2();
        public Vec2 targetRigPos = new Vec2();

        public float rigChangeTimer = rigChangeTime;

        @Override
        public void created()
        {
            super.created();

            currentRigPos = new Vec2(x, y);
            targetRigPos = new Vec2(
                    x + Mathf.random(-rigRange, rigRange),
                    y + Mathf.random(-rigRange, rigRange)
            );
        }

        @Override
        public void updateTile()
        {
            super.updateTile();

            if (efficiency * efficiencyScale() <= 0 || !hasRequiredExtensions()) return;

            float speed = efficiency * efficiencyScale() * getTotalStat(TechStat.speed) * delta();

            rigChangeTimer += speed;

            if (rigChangeTimer >= rigChangeTime)
            {
                rigChangeTimer = 0;

                targetRigPos = new Vec2(
                        x + Mathf.random(-rigRange, rigRange),
                        y + Mathf.random(-rigRange, rigRange)
                );
            }

            currentRigPos.approach(targetRigPos, speed);

            float shakeX = Mathf.sin(totalProgress(), 3f, 0.5f) + Mathf.sin(totalProgress(), 0.8f, 0.2f);
            float shakeY = Mathf.cos(totalProgress(), 2.5f, 0.4f) + Mathf.sin(totalProgress(), 1.2f, 0.3f);

            currentRigPos.x += shakeX;
            currentRigPos.y += shakeY;

            if(wasVisible && Mathf.chanceDelta(rigEffectChance * warmup))
                rigEffect.at(currentRigPos.x, currentRigPos.y, dominantItem.color);
        }

        @Override
        public void draw()
        {
            Draw.z(Layer.block);

            Lines.stroke(rigHolderThickness, rigHolderColor);

            float offset = (float)size * tilesize / 2 - rigHolderThickness / 2;

            Lines.line(currentRigPos.x, y - offset, currentRigPos.x, currentRigPos.y);
            Lines.line(currentRigPos.x, y + offset, currentRigPos.x, currentRigPos.y);

            Lines.line(x - offset, currentRigPos.y, currentRigPos.x, currentRigPos.y);
            Lines.line(x + offset, currentRigPos.y, currentRigPos.x, currentRigPos.y);

            Draw.z(Layer.block + 1);
            Draw.reset();

            Draw.rect(rigRegion, currentRigPos.x, currentRigPos.y);

            Draw.rect(region, x, y);
        }
    }
}
