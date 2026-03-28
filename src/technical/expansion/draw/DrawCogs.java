package technical.expansion.draw;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.geom.Point2;
import arc.struct.Seq;
import mindustry.gen.*;
import mindustry.world.Block;
import mindustry.world.draw.*;
import technical.Technical;

public class DrawCogs extends DrawBlock {

    public Seq<DrawCog> cogs = new Seq<>();

    public DrawCogs(Seq<DrawCog> cogs) 
    {
        this.cogs = cogs;
    }

    @Override
    public void load(Block block)
    {
        for (var cog : cogs)
        {
            cog.toothSprite = Core.atlas.find(Technical.name + "-" + cog.sprite);
            cog.screwSprite = Core.atlas.find(Technical.name + "-" + cog.sprite + "-screw");
        }
    }

    @Override
    public void draw(Building build) {
        float time = build.totalProgress();

        for (DrawCog cog : cogs) {
            float rotation = time * cog.speed + cog.offset;

            Draw.scl(cog.scale);

            Draw.rect(
                    cog.toothSprite,
                    build.x + cog.position.x,
                    build.y + cog.position.y,
                    rotation
            );

            Draw.rect(
                    cog.screwSprite,
                    build.x + cog.position.x,
                    build.y + cog.position.y
            );
        }

        Draw.reset();
    }

    /** Helper class to store all cog-related values */
    public static class DrawCog {
        public TextureRegion screwSprite;
        public TextureRegion toothSprite;
        public String sprite;
        public float scale = 1f;
        public float speed = 90f; // degrees per second
        public float offset = 0f; // initial rotation offset
        public Point2 position = new Point2();

        public DrawCog(String sprite, Point2 pos, float scale, float speed, float offset) {
            this.sprite = sprite;
            this.position.set(pos);
            this.scale = scale;
            this.speed = speed;
            this.offset = offset;
        }
    }
}

/*
drawer = new DrawMulti(new DrawDefault(), new DrawCogs(Seq.with(
                new DrawCogs.DrawCog("copper-cog", new Point2(4, 4), 0.5f, 1, 0),
                new DrawCogs.DrawCog("copper-cog", new Point2(0, 1), 0.3f, -2, 2f)
            )));
*/