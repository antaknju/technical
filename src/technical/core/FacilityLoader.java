package technical.core;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.type.Item;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.units.UnitAssembler.YeetData;
import technical.core.FacilityController.FacilityControllerBuild;

public class FacilityLoader extends TPayloadBlock
{
    public FacilityLoader(String name)
    {
        super(name);
        quickRotate = false;

        hasItems = true;
        itemCapacity = 200;

        blendsOutput = false;
    }

    public class FacilityLoaderBuild extends TPayloadBuild<Payload> implements FacilityBuild
    {
        public FacilityControllerBuild controller;

        public void updateTile()
        {
            if (moveInPayload() && controller != null)
            {
                yeetPayload(payload);
                payload = null;
            }
        }

        public void drawSelect()
        {
            drawSelectFacility(this);
        }

        @Override
        public FacilityControllerBuild controller() 
        {
            if (controller != null && !controller.isValid()) controller = null;

            return controller;
        }

        @Override
        public void controller(FacilityControllerBuild fcb) 
        {
            controller = fcb;
        }

        public void yeetPayload(Payload payload)
        {
            if (controller == null) return;

            Vec2 spawn = controller.getFacilityCenter(Tmp.v4);
            controller.addPayload(payload.content());
            float rot = payload.angleTo(spawn);
            Fx.shootPayloadDriver.at(payload.x(), payload.y(), rot);
            Fx.payloadDeposit.at(payload.x(), payload.y(), rot, new YeetData(spawn.cpy(), payload.content()));
            Sounds.shootPayload.at(x, y, 1f + Mathf.range(0.1f), 1f);
        }

        @Override
        public boolean acceptItem(Building source, Item item)
        {
            return controller() != null && items.get(item) < getMaximumAccepted(item) && controller().acceptItem(source, item);
            //return this.items.get(item) < this.getMaximumAccepted(item);
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return super.acceptPayload(source, payload) && controller() != null && controller().facilityAcceptPayload(payload.content());
        }
    }
}
