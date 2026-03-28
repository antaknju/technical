package technical.expansion;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Puddles;
import mindustry.gen.Puddle;
import mindustry.graphics.Layer;
import mindustry.type.Liquid;
import mindustry.world.Tile;

public class CellTLiquid extends TLiquid{
    public Color colorFrom = Color.white.cpy(), colorTo = Color.white.cpy();
    public int cells = 6;

    public @Nullable TLiquid spreadTarget;
    public float maxSpread = 0.75f, spreadConversion = 1.2f, spreadDamage = 0.11f, removeScaling = 0.25f;

    public CellTLiquid(String name)
    {
        super(name);
    }

    @Override
    public void update(Puddle puddle)
    {
        if(spreadTarget != null)
        {
            float scaling = Mathf.pow(Mathf.clamp(puddle.amount / Puddles.maxLiquid), 2f);
            boolean reacted = false;

            for(var point : Geometry.d4c)
            {
                Tile tile = puddle.tile.nearby(point);
                if(tile != null && tile.build != null && tile.build.liquids != null && tile.build.liquids.get(spreadTarget) > 0.0001f)
                {
                    float amount = Math.min(tile.build.liquids.get(spreadTarget), maxSpread * Time.delta * scaling);
                    tile.build.liquids.remove(spreadTarget, amount * removeScaling);
                    Puddles.deposit(tile, this, amount * spreadConversion);
                    reacted = true;
                }
            }

            //damage thing it is on
            if(spreadDamage > 0 && puddle.tile.build != null && puddle.tile.build.liquids != null && puddle.tile.build.liquids.get(spreadTarget) > 0.0001f)
            {
                reacted = true;

                //spread in 4 adjacent directions around thing it is on
                float amountSpread = Math.min(puddle.tile.build.liquids.get(spreadTarget) * spreadConversion, maxSpread * Time.delta) / 2f;
                for(var dir : Geometry.d4)
                {
                    Tile other = puddle.tile.nearby(dir);
                    if(other != null)
                    {
                        Puddles.deposit(puddle.tile, other, puddle.liquid, amountSpread);
                    }
                }

                puddle.tile.build.damage(spreadDamage * Time.delta * scaling);
            }

            //spread to nearby puddles
            for(var point : Geometry.d4){
                Tile tile = puddle.tile.nearby(point);
                if(tile != null){
                    var other = Puddles.get(tile);
                    if(other != null && other.liquid == spreadTarget){
                        //TODO looks somewhat buggy when outputs are occurring
                        float amount = Math.min(other.amount, Math.max(maxSpread * Time.delta * scaling, other.amount * 0.25f * scaling));
                        other.amount -= amount;
                        puddle.amount += amount;
                        reacted = true;
                        if(other.amount <= Puddles.maxLiquid / 3f){
                            other.remove();
                            Puddles.deposit(tile, puddle.tile, this, Math.max(amount, Puddles.maxLiquid / 3f));
                        }
                    }
                }
            }
        }
    }

    @Override
    public float react(Liquid other, float amount, Tile tile, float x, float y){
        if(other == spreadTarget){
            return amount;
        }
        return 0f;
    }

    @Override
    public void drawPuddle(Puddle puddle){
        super.drawPuddle(puddle);

        float baseLayer = puddle.tile != null && puddle.tile.block().solid || puddle.tile.build != null ? Layer.blockOver : Layer.debris - 0.5f;

        int id = puddle.id;
        float amount = puddle.amount, x = puddle.x, y = puddle.y;
        float f = Mathf.clamp(amount / (Puddles.maxLiquid / 1.5f));
        float smag = puddle.tile.floor().isLiquid ? 0.8f : 0f, sscl = 25f;
        float length = Math.max(f, 0.3f) * 9f;

        rand.setSeed(id);
        for(int i = 0; i < cells; i++){
            Draw.z(baseLayer + i/1000f + (id % 100) / 10000f);
            Tmp.v1.trns(rand.random(360f), rand.random(length));
            float vx = x + Tmp.v1.x, vy = y + Tmp.v1.y;

            Draw.color(colorFrom, colorTo, rand.random(1f));

            Fill.circle(
            vx + Mathf.sin(Time.time + i * 532, sscl, smag),
            vy + Mathf.sin(Time.time + i * 53, sscl, smag),
            f * 3.8f * rand.random(0.35f, 1f) * Mathf.absin(Time.time + ((i + id) % 60) * 54, 75f * rand.random(1f, 2f), 1f));
        }

        Draw.color();
    }
}