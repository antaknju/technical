package technical.expansion;

import static mindustry.Vars.content;
import static mindustry.Vars.world;

import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.util.Nullable;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import technical.expansion.ThermalLiquidBlock.ThermalLiquidBuild;
import technical.expansion.tech.Tech;
import technical.expansion.tech.TechStat;
import technical.expansion.tech.TechStatType;
import technical.expansion.tech.TechType;
import technical.utility.TBundle;
import technical.utility.TCol;

public class TBlock extends Block
{
    public TechType techType = TechType.Null;
    public ObjectMap<TechStat, Float> techStats = new ObjectMap<>();

    public float workingTemperature = 0f;

    public @Nullable Attribute RequiredAttribute = null;

    public float MinimumAttribute = 0f;

    public TBlock(String name) 
    {
        super(name);
        update = true;
        solid = true;
    }

    @Override
    public void setStats() {
        super.setStats();

        if (RequiredAttribute != null)
        {
            stats.add(Stat.tiles, RequiredAttribute, floating, 1f, true);
        }

        updateTechStats();

        StatCat techStatCat = new StatCat("tstats");

        Stat s1 = new Stat("tech-type", techStatCat);
        stats.add(s1, table -> {
            table.add(TBundle.highlight(TBundle.get_enum(techType)));
        });

        for (TechStat tstat : TechStat.values()) 
        {
            float value = totalStat(tstat);

            if (Math.abs(value - tstat.defVal()) < 0.001) continue;

            Stat stat = new Stat(TBundle.build("tech", TBundle.kebab(tstat.name())), techStatCat);
            stats.add(stat, table -> {
                table.add(TBundle.color((value >= 0 ? "+" : "-")  + String.format("%.2f", (value - tstat.defVal()) * 100f) + "%[]", TCol.highlight));
            });
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid)
    {
        super.drawPlace(x, y, rotation, valid);

        if (RequiredAttribute == null) return;

        float sum = world.tile(x, y).getLinkedTilesAs(this, tempTiles).sumf(other -> other.floor().attributes.get(RequiredAttribute));

        if (sum < 0.001f)
            drawPlaceText(TBundle.error("attribute-all"), x, y, valid);
        else if (sum < MinimumAttribute)
            drawPlaceText(TBundle.error("attribute-part"), x, y, valid);
    }

    public boolean chance(TechStat stat) 
    {
        return Mathf.chance(getTotalStat(stat));
    }

    public void updateTechStats() 
    {
        for (TechStat stat : TechStat.values()) {
            techStats.put(stat, TechStat.defVal(stat));
        }

        for (Content con : content.getBy(ContentType.effect_UNUSED)) 
        {
            if (con instanceof Tech tech && tech.type == this.techType) 
            {
                if (tech.unlockedNow())
                {
                    for (ObjectMap.Entry<TechStat, Float> entry : tech.stats.entries()) 
                    {
                        techStats.put(entry.key, sumStat(totalStat(entry.key), entry.value, entry.key));
                    }
                }
            }
        }
    }

    float sumStat(float total, float val, TechStat stat) {
        return stat.type == TechStatType.multiplier ? total * val : total + val;
    }

    float totalStat(TechStat stat)
    {
        return techStats.get(stat, stat.defVal());
    }

    public float getTotalStat(TechStat stat)
    {
        updateTechStats();

        return totalStat(stat);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation)
    {
        if (RequiredAttribute == null)
            return true;

        return tile.getLinkedTilesAs(this, tempTiles).sumf(other -> other.floor().attributes.get(RequiredAttribute)) >= MinimumAttribute;
    }

    public class TBuild extends Building
    {
        public void propagateHeat()
        {
            if (warmup() > 0 && workingTemperature() != 0)
            {
                for (var b : proximity)
                {
                    if (b != null && b.isValid() && b instanceof ThermalLiquidBuild tlb)
                    {
                        tlb.acceptHeat(warmup() * workingTemperature());
                    }
                }
            }
        }

        public void updateTile()
        {
            super.updateTile();

            propagateHeat();
        }

        public float workingTemperature()
        {
            return workingTemperature;
        }

        @Override
        public float efficiencyScale()
        {
            if (RequiredAttribute == null)
                return super.efficiencyScale();

            return super.efficiencyScale() * (sumAttribute(RequiredAttribute, tile.x, tile.y) >= MinimumAttribute ? 1f : 0f);
        }

        public TBlock tblock()
        {
            return (TBlock)block;
        }
    }
}
