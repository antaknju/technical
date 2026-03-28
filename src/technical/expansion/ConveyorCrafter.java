package technical.expansion;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.entities.Effect;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import technical.Fr;
import technical.T;
import technical.content.TCustom;
import technical.content.TFx;
import technical.expansion.RollerConveyor.RollerConveyorBuild;
import technical.expansion.tech.TechStat;

public class ConveyorCrafter extends TBlock
{
    public TextureRegion roofRegion;
    public ConveyorRecipe.Action performedAction = new ConveyorRecipe.Action(null, ConveyorRecipe.Action.Type.Cutting);

    public float craftingCooldown = 1 * Fr.time;
    public Effect craftingEffect = TFx.stringBreak;

    public float craftingCooldown() {
        return craftingCooldown * getTotalStat(TechStat.cooldown);
    }

    public ConveyorCrafter(String name) {
        super(name);
        update = true;
        solid = true;
        rotate = false;
        sync = false;
        size = 1;
    }

    @Override
    public void setBars()
    {
        super.setBars();
        addBar("ready", (ConveyorCrafterBuild build) ->
            new Bar(
                () -> Core.bundle.format(build.cooldownTimer >= craftingCooldown() && build.efficiency > 0 && build.isConnectionValid() ? "bar.ready" : "bar.not-ready"),
                () -> Pal.ammo,
                () -> build.cooldownTimer / craftingCooldown()
            )
        );
    }

    @Override
    public void setStats() 
    {
        super.setStats();

        Stat cooldown = new Stat("cooldown", StatCat.crafting);
        stats.add(cooldown, table -> {
            table.add("[accent]" + craftingCooldown() / 60f + "sec[]");
        });

        Stat action = new Stat("action", StatCat.crafting);
        stats.add(action, table -> {
            table.add("[accent]" + performedAction.type.name() + "[]");
        });
    }

    @Override
    public void load() 
    {
        super.load();
        roofRegion = Core.atlas.find(name + "-roof", region);
    }

    public class ConveyorCrafterBuild extends TBuild 
    {
        public ConveyorCrafterBuild connection;
        public RollerConveyorBuild conveyor;
        public int connectionRot = -1;
        public boolean isMain = false;
        public float cooldownTimer = 0;

        @Override
        public void updateTile() 
        {
            updateConnection();

            cooldownTimer = Math.min(craftingCooldown(), cooldownTimer + delta());

            if (!isMain && isConnectionValid() && hasItems)
            {
                if (items.first() != null && connection.acceptItem(this, items.first()))
                {
                   connection.handleItem(this, items.first());
                   items.remove(items.first(), 1);
                }
            }

            if (isMain && isConnectionValid() && cooldownTimer >= craftingCooldown() && efficiency > 0)
            {
                if (conveyor.items.total() > 0)
                {
                    Item item = conveyor.ids[conveyor.mid];
                    int state = conveyor.craftingState[conveyor.mid];
                    var rec = TCustom.ConveyorRecipes.get(item);
                    if (rec != null)
                    {
                        if (rec.actions.length * rec.times > state && rec.actions[state % rec.actions.length].equals(performedAction))
                        {
                            state = conveyor.craftingState[conveyor.mid]++;
                            cooldownTimer = 0;

                            if (!chance(TechStat.materialSaveChance))
                                consume();

                            craftingEffect.at(conveyor.x, conveyor.y);

                            if (state >= rec.actions.length * rec.times - 1)
                            {
                                conveyor.items.remove(item, 1);
                                conveyor.items.add(rec.result, 1);
                                conveyor.ids[conveyor.mid] = rec.result;
                                conveyor.craftingState[conveyor.mid] = 0;
                            }
                        }
                    }
                }
            }
        }

        public boolean isConnectionValid()
        {
            if (connection == null || connectionRot == -1 || conveyor == null ||
                !connection.isValid() || !conveyor.isValid() ||
                conveyor.rotation % 2 == connectionRot % 2 || 
                connection.connection != this || connection.isMain == isMain
            ) return false;

            return true;
        }

        public void updateConnection()
        {
            if (isConnectionValid()) return;

            connection = null;
            conveyor = null;
            connectionRot = -1;

            for (int rot : T.range(0, 4))
            {
                Tile next1 = tile.nearby(rot);
                if (next1 == null || !(next1.build instanceof RollerConveyorBuild rcb) || !next1.build.isValid()) continue;

                if (next1.build.rotation % 2 != (rot + 1) % 2) continue;

                Tile next2 = next1.nearby(rot);
                if (next2 == null || !(next2.build instanceof ConveyorCrafterBuild ccb) || next2.block() != block || !next2.build.isValid()) continue;


                if (ccb.connection == null || !ccb.connection.isValid())
                {
                    conveyor = rcb;
                    connection = ccb;
                    connectionRot = rot;
                    isMain = true;

                    ccb.conveyor = rcb;
                    ccb.connection = this;
                    ccb.connectionRot = rot;
                    ccb.isMain = false;
                }
            }
        }

        @Override
        public void drawSelect()
        {
            if (!isConnectionValid()) return;

            T.outline(this);
            T.outline(connection, Pal.place);
        }

        @Override
        public void draw() 
        {
            super.draw();

            if (isConnectionValid())
            {
                Draw.rect(roofRegion, conveyor.x, conveyor.y, connectionRot * 90f);
            }
        }
    }
}
