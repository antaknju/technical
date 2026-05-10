package technical.core.tech;

import arc.Core;
import arc.struct.ObjectMap;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.type.ItemStack;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import technical.util.TBundle;

public class Tech extends UnlockableContent
{
    public ObjectMap<TechStat, Float> stats = new ObjectMap<>();

    public TechType type;

    public ItemStack[] researchCost;

    public Tech(String name)
    {
        super(name);

        this.localizedName = Core.bundle.get("tech." + this.name + ".name", this.name);
        this.description = Core.bundle.getOrNull("tech." + this.name + ".description");
        this.details = Core.bundle.getOrNull("tech." + this.name + ".details");
    }

    @Override
    public void setStats()
    {
        StatCat techStatCat = new StatCat("tstats");

        Stat s1 = new Stat("tech-type", techStatCat);
        super.stats.add(s1, table -> {
            table.add("[blue]" + TBundle.get_enum(type) + "[]");
        });

        for (TechStat tstat : TechStat.values()) {
            float value = getStat(tstat);

            if (value <= tstat.defVal()) continue;

            Stat stat = new Stat(TBundle.kebab("tstat-" + tstat.name()), techStatCat);
            super.stats.add(stat, table -> {
                table.add("[blue]" + (value >= 0 ? "+" : "-")  + String.format("%.2f", (value - tstat.defVal()) * 100f) + "%[]");
            });
        }
    }

    public void mapStats(ObjectMap<TechStat, Float> map)
    {
        stats = map;
    }

    public void addStat(TechStat stat, float val)
    {
        stats.put(stat, val);
    }

    public float getStat(TechStat stat)
    {
        return stats.get(stat, 1f);
    }

    @Override
    public ContentType getContentType() 
    {
        return ContentType.effect_UNUSED;
    }

    public void researchCost(ItemStack... stacks)
    {
        this.researchCost = stacks;
    }

    @Override
    public ItemStack[] researchRequirements()
    {
        return researchCost;
    }
}