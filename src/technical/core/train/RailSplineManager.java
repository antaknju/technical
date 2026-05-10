package technical.core.train;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.EventType.ResetEvent;
import mindustry.game.EventType.Trigger;
import mindustry.graphics.Layer;
import technical.core.train.RailConnector.RailConnectorBuild;

import java.util.*;

public class RailSplineManager 
{
    private static final int segments = 30;
    private static final float railOffset = 4f;
    private static final Color color = Color.valueOf("#b0bac0");

    // public static boolean clearedRenderer = false;
    public static Collection<RailConnectorBuild> nodes = new ArrayList<>();

    public static void load()
    {
        Events.on(ClientLoadEvent.class, e -> {
            Events.run(Trigger.draw, RailSplineManager::drawSplines);
        });

        Events.on(ResetEvent.class, e -> {
            nodes.clear();
        });
    }

    public static void drawSplines() 
    {
        Set<RailConnectorBuild> visited = new HashSet<>();
        for (RailConnectorBuild node : nodes) 
        {
            if (!visited.contains(node)) 
            {
                List<RailConnectorBuild> path = new ArrayList<>();
                buildPath(node, visited, path);
                if (path.size() < 2) continue;

                boolean closed = isClosedLoop(path);
                Draw.z(Layer.floor + 1);
                drawDoubleSpline(path, closed);
            }
        }

        Draw.reset();
    }

    public static void buildPath(RailConnectorBuild start, Set<RailConnectorBuild> visited, List<RailConnectorBuild> path)
    {
        RailConnectorBuild current = findPathStart(start, visited);
        RailConnectorBuild previous = null;

        while (current != null && !visited.contains(current)) {
            visited.add(current);
            path.add(current);

            RailConnectorBuild next = null;
            for (RailConnectorBuild link : current.links) {
                if (link != previous) {
                    next = link;
                    break;
                }
            }

            previous = current;
            current = next;
        }
    }

    public static RailConnectorBuild findPathStart(RailConnectorBuild start, Set<RailConnectorBuild> visited)
    {
        Set<RailConnectorBuild> connected = new HashSet<>();
        collectConnected(start, connected);

        for (RailConnectorBuild n : connected) {
            if (n.links.size == 1) return n;
        }

        return start;
    }

    private static void collectConnected(RailConnectorBuild node, Set<RailConnectorBuild> connected) {
        if (connected.contains(node)) return;
        connected.add(node);
        for (RailConnectorBuild link : node.links) {
            collectConnected(link, connected);
        }
    }

    public static boolean isClosedLoop(List<RailConnectorBuild> path) {
        if (path.size() < 3) return false;

        RailConnectorBuild first = path.get(0);
        RailConnectorBuild last = path.get(path.size() - 1);

        boolean directlyLinked = first.links.contains(last) && last.links.contains(first);

        boolean firstIsEnd = first.links.size == 1;
        boolean lastIsEnd = last.links.size == 1;

        return directlyLinked && !firstIsEnd && !lastIsEnd;
    }

    private static void drawDoubleSpline(List<RailConnectorBuild> path, boolean closed) {
        int n = path.size();

        for (int i = 0; i < n - 1 + (closed ? 1 : 0); i++) {
            int i0 = i - 1;
            int i1 = i;
            int i2 = i + 1;
            int i3 = i + 2;

            if (!closed) {
                if (i0 < 0) i0 = i1;
                if (i3 >= n) i3 = i2;
            } else {
                i0 = (i - 1 + n) % n;
                i1 = i % n;
                i2 = (i + 1) % n;
                i3 = (i + 2) % n;
            }

            float x0 = path.get(i0).x, y0 = path.get(i0).y;
            float x1 = path.get(i1).x, y1 = path.get(i1).y;
            float x2 = path.get(i2).x, y2 = path.get(i2).y;
            float x3 = path.get(i3).x, y3 = path.get(i3).y;

            for (int s = 0; s < segments; s++) {
                float t1 = s / (float)segments;
                float t2 = (s + 1) / (float)segments;

                float px1 = catmullRom(x0, x1, x2, x3, t1);
                float py1 = catmullRom(y0, y1, y2, y3, t1);
                float px2 = catmullRom(x0, x1, x2, x3, t2);
                float py2 = catmullRom(y0, y1, y2, y3, t2);

                float dx = px2 - px1;
                float dy = py2 - py1;
                float len = (float)Math.sqrt(dx*dx + dy*dy);
                if(len == 0) continue;
                float offsetX = -dy / len * railOffset;
                float offsetY = dx / len * railOffset;

                Draw.color(color);
                Lines.line(px1 + offsetX, py1 + offsetY, px2 + offsetX, py2 + offsetY);

                Draw.color(color);
                Lines.line(px1 - offsetX, py1 - offsetY, px2 - offsetX, py2 - offsetY);
            }
        }
    }

    public static float catmullRom(float p0, float p1, float p2, float p3, float t) {
        float t2 = t * t;
        float t3 = t2 * t;
        return 0.5f * ((2*p1) + (-p0 + p2)*t + (2*p0 - 5*p1 + 4*p2 - p3)*t2 + (-p0 + 3*p1 - 3*p2 + p3)*t3);
    }

    // public static void clear() {
    //     nodes.clear();
    //     clearedRenderer = true;
    // }

    // public static void makeReadyForNextClear() {
    //     clearedRenderer = false;
    // }
}
