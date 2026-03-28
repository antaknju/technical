package technical.expansion;

import static mindustry.Vars.content;
import static mindustry.Vars.itemSize;
import static mindustry.Vars.renderer;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Fonts;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import technical.Fr;
import technical.T;
import technical.content.TFx;
import technical.expansion.kinetic.KineticBlock;
import technical.expansion.tech.TechStat;

public class Inserter extends KineticBlock 
{
    public float moveTime = 1 * Fr.time;
    public Effect pickupEffect = Fx.pickup;
    public Effect dropEffect = TFx.drop;
    public int maxStackSize = 5;

    public TextureRegion armRegion;
    public TextureRegion itemRegion;

    public float itemDuration = 120f;
    public Effect consumeEffect = TFx.coalSmelt;

    public Inserter(String name)
    {
        super(name);
        
        update = true;
        hasItems = true;
        itemCapacity = 10;
        rotate = true;
        solid = true;
        configurable = true;

        config(Item.class, (InserterBuild tile, Item item) -> tile.filterItem = item);
        configClear((InserterBuild tile) -> tile.filterItem = null);
    }

    @Override
    public void load()
    {
        super.load();
        armRegion = Core.atlas.find(name + "-arm");
        itemRegion = Core.atlas.find(name + "-item");
    }

    @Override
    public void setStats() 
    {
        super.setStats();

        stats.add(Stat.speed, moveTime / 60f, StatUnit.seconds);
        stats.add(Stat.itemsMoved, maxStackSize, StatUnit.items);
        stats.add(Stat.productionTime, itemDuration / 60f, StatUnit.seconds);
    }


    public class InserterBuild extends KineticBuild 
    {
        public float progress = 0;
        public ItemStack carriedStack = new ItemStack();
        public int armDirection = -1;
        public float itemTime = 0;
        public int itemTimeTendency = 0;
        public float consumeTime;

        public @Nullable Item filterItem;

        public void trySourceAction()
        {
            if (carriedStack.amount < maxStackSize) 
            {
                Building other = nearby(Mathf.mod(rotation + 2, 4));
                if (other != null && other.isValid() && other.block.hasItems && other.items.total() > 0 && (filterItem == null || other.items.get(filterItem) > 0)) 
                {
                    if(carriedStack.amount <= 0) 
                    {
                        if (filterItem == null)
                            carriedStack = new ItemStack(other.items.first(), 0);
                        else
                            carriedStack = new ItemStack(filterItem, 0);
                    }

                    boolean consumesCarried = false;
                    for(var cons : consumers){
                        if(cons instanceof ConsumeItems ci){
                            for(var stack : ci.items){
                                if(stack.item == carriedStack.item){
                                    consumesCarried = true;
                                    break;
                                }
                            }
                        }
                    }

                    int removed = other.removeStack(carriedStack.item, maxStackSize - carriedStack.amount + (consumesCarried ? itemCapacity - items.get(carriedStack.item) : 0));

                    int addedConsumed = 0;
                    if (consumesCarried)
                    {
                        addedConsumed = Mathf.clamp(removed, 0, itemCapacity - items.get(carriedStack.item));
                        items.add(carriedStack.item, addedConsumed);
                    }

                    carriedStack.amount += removed - addedConsumed;

                    if (carriedStack.amount > 0)
                    {
                        pickupEffect.at(other.x, other.y);
                        itemTimeTendency = 1;
                    }
                }
            }
        }

        public void tryTargetAction()
        {
            Building other = nearby(rotation);
            if (other != null && other.isValid() && other.block.hasItems)
            {
                int am = 0;
                for (int i = 0; i < carriedStack.amount; i++)
                {
                    if(other.acceptItem(this, carriedStack.item))
                    {
                        other.handleItem(this, carriedStack.item);
                        am++;
                    }
                }
                carriedStack.amount -= am;

                if (am > 0)
                {
                    dropEffect.at(other.x, other.y);
                }

                if (carriedStack.amount <= 0)
                {
                    itemTimeTendency = -1;
                }
            }
        }

        @Override
        public void updateTile()
        {
            super.updateTile();

            consumeTime = Math.max(0f, consumeTime - delta());
            
            float itemSpeed = delta() / 4;
            itemTime += itemSpeed * itemTimeTendency;
            itemTime = Mathf.clamp(itemTime);

            if(efficiency <= 0) return;

            if (consumeTime <= 0f) 
            {
                consumeTime = itemDuration;
                consumeEffect.at(x, y);
                consume();
            }

            progress += edelta() * armDirection * getTotalStat(TechStat.speed);

            if(progress <= 0)
            {
                progress = 0;
                armDirection = 1;

                trySourceAction();
            } 
            else if (progress >= moveTime)
            {
                progress = moveTime;
                armDirection = -1;

                if(carriedStack.amount > 0)
                {
                    tryTargetAction();
                }
            }
        }

        @Override
        public void buildConfiguration(Table table)
        {
            ItemSelection.buildTable(Inserter.this, table, content.items(), () -> filterItem, this::configure, selectionRows, selectionColumns);
        }

        @Override
        public Item config(){
            return filterItem;
        }

        @Override
        public void drawSelect() 
        {
            super.drawSelect();

            drawItemSelection(filterItem);

            Lines.stroke(1f, Pal.accent);

            Building b = nearby(rotation);
            if (b != null && b.isValid())
            {
                T.outline(b, Pal.accent);
            }

            b = nearby(Mathf.mod(rotation + 2, 4));
            if (b != null && b.isValid())
            {
                T.outline(b, Pal.place);
            }
        }

        @Override
        public void draw()
        {
            Draw.rect(region, x, y, rotation * 90);

            if (filterItem != null) 
            {
                Draw.color(filterItem.color);
                Draw.rect(itemRegion, x, y, rotation * 90);
                Draw.color();
            }

            float angle = Mathf.lerp(0f, 180f, progress / moveTime) + rotation * 90;

            float halfWidth = 32f / 8f;

            float pivotOffsetX = -halfWidth;
            float pivotOffsetY = 0f;

            float drawX = x + Angles.trnsx(angle, pivotOffsetX, pivotOffsetY);
            float drawY = y + Angles.trnsy(angle, pivotOffsetX, pivotOffsetY);

            Draw.z(Layer.blockOver);
            Draw.rect(armRegion, drawX, drawY, angle);

            Draw.z(Layer.blockOver+1);

            float itemX = x + Angles.trnsx(angle, -32f / 4, 0);
            float itemY = y + Angles.trnsy(angle, -32f / 4, 0);

            T.drawItemStack(carriedStack, itemX, itemY, itemTime);
        }



        @Override
        public void write(Writes write)
        {
            super.write(write);
            write.s(filterItem == null ? -1 : filterItem.id);

            write.s(carriedStack == null || carriedStack.item == null ? -1 : carriedStack.item.id);
            write.i(carriedStack.amount);

            write.f(progress);
            write.i(armDirection);

            write.f(itemTime);
            write.i(itemTimeTendency);

            write.f(consumeTime);
        }

        @Override
        public void read(Reads read, byte revision)
        {
            super.read(read, revision);

            filterItem = content.item(read.s());

            carriedStack = new ItemStack(
                content.item(read.s()),
                read.i()
            );

            progress = read.f();
            armDirection = read.i();

            itemTime = read.f();
            itemTimeTendency = read.i();

            consumeTime = read.f();
        }
    }
}
