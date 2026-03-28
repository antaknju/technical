package technical.debug;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.Lines;
import arc.input.InputProcessor;
import arc.input.KeyCode;
import arc.math.Mat;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.ui.Fonts;
import mindustry.world.Tile;
import technical.T;
import technical.TCol;

import static mindustry.Vars.tilesize;

import java.lang.reflect.Field;

public class Debugger
{
    private static boolean enabled = false;

    public static final Object[] empty_args = new Object[0];

    private static final Seq<DebugLine> lineBuffer = new Seq<>();

    private static final Font text = Fonts.outline;

    public static class DebugLine 
    {
        public Vec2 start = new Vec2();
        public Vec2 end = new Vec2();
        public float time = 1f;
        public Color color = Color.white;
        public boolean valid = false;
        public float stroke = 1f;

        public DebugLine() {}
        public DebugLine(Vec2 _start, Vec2 _end)
        {
            start = _start.cpy();
            end = _end.cpy();
            valid = true;
        }

        public static DebugLine from(Vec2 start, Vec2 end)
        {
            if(start == null || end == null)
            {
                return new DebugLine();
            }

            return new DebugLine(start, end);
        }

        public static DebugLine point(Tile tile)
        {
            return point(new Vec2(tile.x * tilesize, tile.y * tilesize));
        }

        public static DebugLine point(Vec2 pos)
        {
            return DebugLine.point(pos.x, pos.y);
        }

        public static DebugLine point(float x, float y)
        {
            return new DebugLine(new Vec2(x - 0.1f, y - 0.1f), new Vec2(x + 0.1f, y + 0.1f)).stroke(4f);
        }

        public static DebugLine dir(Vec2 start, Vec2 direction, float length)
        {
            if(start == null || direction == null || direction.isZero())
            {
                return new DebugLine();
            }

            Vec2 end = start.cpy().add(direction.cpy().nor().scl(length));
            return from(start, end);
        }

        public DebugLine time(float time)
        {
            this.time = time;
            return this;
        }

        public DebugLine color(Color color)
        {
            this.color = color;
            return this;
        }

        public DebugLine color(int index)
        {
            this.color = TCol.from(index);
            return this;
        }

        public DebugLine stroke(float stroke)
        {
            this.stroke = stroke;
            return this;
        }

        public void draw(){
            if(!valid || !enabled) return;

            lineBuffer.add(this);
        }
    }

    public static void load()
    {
        Core.input.addProcessor(new InputProcessor(){
            @Override
            public boolean keyDown(KeyCode key){
                if(!T.isSandbox()) return false;

                if(key == KeyCode.f2)
                {
                    enabled = !enabled;
                    printForced("Changed Debugger State: @", enabled);
                    return true;
                }
                return false;
            }
        });

        Core.input.addProcessor(new InputProcessor(){
            @Override
            public boolean keyDown(KeyCode key)
            {
                if(key == KeyCode.f3 && enabled){
                    print("Changed Team: @", enabled);
                    Vars.player.team(Vars.player.team() == Team.sharded ? Team.crux : Team.sharded);
                    return true;
                }
                return false;
            }
        });

        Events.run(EventType.Trigger.uiDrawBegin, Debugger::draw);
    }

    public static final void printForced(String text, Object... args)
    {
        System.out.println(Log.format("&p&fb[T]&fr " + text + "&fr", args));
    }

    public static final void printForced(Object object)
    {
        printForced(String.valueOf(object), empty_args);
    }

    public static void print(String text, Object... args)
    {
        if (enabled)
            printForced(text, args);
    }

    public static void print(Object object)
    {
        if (enabled)
            printForced(object);
    }

    private static void draw()
    {
        if(!enabled || Vars.player == null) return;

        for(int i = lineBuffer.size - 1; i >= 0; i--){
            DebugLine line = lineBuffer.get(i);

            Lines.stroke(line.stroke);
            Draw.color(line.color);
            Lines.line(
                line.start.x,
                line.start.y,
                line.end.x,
                line.end.y
            );

            line.time -= Time.delta;
            if(line.time <= 0f){
                lineBuffer.remove(i);
            }
        }

        Draw.reset();

        //////////////////////////////
        ////// SCREEN PROJECTION /////
        //////////////////////////////
        Mat oldProj = Draw.proj();

        float width = Core.graphics.getWidth();
        float height = Core.graphics.getHeight();
        Draw.proj(0f, 0f, width, height);

        ////// TITLE DRAWING /////
        text.draw(
            "[]DEBUG MODE: [accent]ON[]",
            width / 2,
            30f,
            Align.center
        );

        /// LEFT PANEL DRAWING /////
        Tile tile = Vars.world.tileWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y);
        if(tile == null || tile.build == null || tile.block() == Blocks.air) return;
        Building build = tile.build;

        float left_panel_x = 30f;
        float left_panel_y = Core.graphics.getHeight() - 90f;

        text.draw("[accent]" + build.block.name + "[]", left_panel_x, left_panel_y, Align.left);

        int text_line = 1;
        for(Field f : build.getClass().getFields()){
            try{
                Object value = f.get(build);
                text.draw(
                    "[lightgray]" + f.getName() + ": []" + String.valueOf(value),
                    left_panel_x,
                    left_panel_y - text_line * 17f,
                    Align.left
                );
                text_line++;
            }catch(Throwable ignored){}
        }

        Draw.proj(oldProj);
    }
}

