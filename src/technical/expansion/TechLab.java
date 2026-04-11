package technical.expansion;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;
import static technical.debug.Debugger.print;
import static technical.debug.Debugger.printForced;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.ObjectSet;
import arc.util.Strings;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.draw.DrawBlock;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import technical.utility.Fr;
import technical.content.TIcons;
import technical.expansion.kinetic.ConsumeKineticDynamic;
import technical.expansion.kinetic.KineticBlock;
import technical.expansion.kinetic.KineticEnergy;
import technical.expansion.tech.TechStat;
import technical.expansion.tech.TechType;
import technical.utility.TDraw;

public class TechLab extends KineticBlock
{    
    public DrawBlock drawer;

    public TechType researchedTechType;
    public Item techItem;

    public int range = 8;
    // public float itemDuration = 60f;
    // public int itemAmount = 1;
    public float efficiencyCap = 100;
    public float craftTime = 30f;
    public int maxProductivity = 20;
    public int minProductivity = 4;

    public KineticEnergy baseInput;

    public TechLab(String name) 
    {
        super(name);
        solid = true;
        update = true;
        hasItems = true;
        techType = TechType.Research;

        consume(new ConsumeKineticDynamic(b -> new KineticEnergy(baseInput.speed * ((TechLabBuild)b).productivity(), baseInput.torque * ((TechLabBuild)b).productivity())));
    }

    @Override
    public void init()
    {
        super.init();

        printForced(baseInput);
    }

    @Override
    public void setStats()
    {
        super.setStats();

        stats.add(Stat.input, t -> {
            t.image(TIcons.speed).size(24f).pad(6f);
            t.label(() -> Strings.fixed(baseInput.speed / Fr.angularSpeed, 2)).padRight(16f);
            
            t.image(TIcons.torque).size(24f).pad(6f);
            t.label(() -> Strings.fixed(baseInput.torque / Fr.torque, 2));
        });

        stats.add(Stat.output, table -> {
            table.image(techItem.uiIcon).size(24f).padRight(4f).tooltip(Core.bundle.format("technical.tip.productivity-range", minProductivity, maxProductivity, range));
            table.add(minProductivity + "~" + maxProductivity).right().tooltip(Core.bundle.format("technical.tip.productivity-range", minProductivity, maxProductivity, range));
        });

        stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
        stats.add(Stat.range, range, StatUnit.blocks);
    }

    @Override
    public void setBars()
    {
        addBar("productivity", (TechLabBuild b) ->
            new Bar(
                () -> Core.bundle.format("bar.productivity", b.productivity()),
                () -> Pal.lightOrange,
                () -> (float)b.productivity() / maxProductivity
            )
        );

        addBar("progress", (TechLabBuild b) -> new Bar(
                "bar.research-progress",
                Pal.place,
                b::progress
        ));
    }

    @Override
    public void load(){
        super.load();
        drawer.load(this);
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid)
    {
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashSquare(Pal.accent, x * tilesize + offset, y * tilesize + offset, range * tilesize * 2);
    }

    public class TechLabBuild extends KineticBuild
    {
        public float progress = 0f;

        public ObjectSet<TBuild> getResearchedBuilds()
        {
            ObjectSet<TBuild> visited = new ObjectSet<>();
            for(int dx = -range; dx <= range; dx++)
            {
                for(int dy = -range; dy <= range; dy++)
                {
                    var b = world.build(tile.x + dx, tile.y + dy);

                    if(b == null || b == this || !(b instanceof TBuild tb) || tb.tblock().techType != researchedTechType) continue;
                    if(!visited.add(tb)) continue;
                }
            }

            return visited;
        }

        public int productivity()
        {
            float productivity = 0;
            for (var tb : getResearchedBuilds())
            {
                productivity += tb.efficiency * tb.efficiencyScale() * tb.block.size * tb.block.size * 100 / 9; // TODO for efficiency capping
            }
            productivity /= efficiencyCap;

            if (productivity < minProductivity) 
                return 0;

            return Math.round(productivity);
        }

        @Override
        public float progress()
        {
            return progress;
        }

        @Override
        public void updateTile()
        {
            super.updateTile();

            if (timer(timerDump, dumpTime / timeScale))
                dump(techItem);

            if(efficiency <= 0 || items.get(techItem) >= getMaximumAccepted(techItem)) return;

            var productivity = productivity();

            if(productivity > 0f)
            {
                progress += getProgressIncrease(craftTime);

                if(progress >= 1)
                {
                    consume();
                    progress = 0;

                    for (int i = 0; i < productivity; i++)
                    {
                        if (items.get(techItem) >= getMaximumAccepted(techItem)) break;
                        offload(techItem);
                    }
                }
            }
            else
            {
                progress -= getProgressIncrease(craftTime);
            }
            progress = Mathf.clamp(progress);
        }

        @Override
        public float getProgressIncrease(float baseTime)
        {
            return super.getProgressIncrease(baseTime) * getTotalStat(TechStat.speed);
        }

        @Override
        public void draw()
        {
            drawer.draw(this);
        }

        @Override
        public void drawSelect()
        {
            Drawf.dashSquare(Pal.accent, x, y, range * tilesize * 2);

            for (var tb : getResearchedBuilds())
            {
                TDraw.highlight(tb, Pal.accent);
            }
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }
    }
}
