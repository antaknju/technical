package technical.core;

import mindustry.entities.Units;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.type.ammo.ItemAmmoType;
import technical.util.T;

public class UnitInserter extends Inserter 
{
    public UnitInserter(String name) 
    {
        super(name);
    }

    public class UnitInserterBuild extends InserterBuild 
    {
        @Override
        public void tryTargetAction() 
        {
            Unit other = Units.closest(team, x + T.Rot2Pos(rotation).x, y + T.Rot2Pos(rotation).y, 16, u -> u.isValid() && u.ammo() < u.type.ammoCapacity);

            if (other != null && other.isValid())
            {
                Item item = ((ItemAmmoType)other.type.ammoType).item;
                if (item == carriedStack.item)
                {
                    int am = 0;
                    for (int i = 0; i < carriedStack.amount; i++)
                    {
                        if(other.ammo() < other.type.ammoCapacity)
                        {
                            other.ammo += 1;
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
        }
    }
}
