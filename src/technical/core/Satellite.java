package technical.core;

import arc.graphics.Color;
import arc.graphics.g3d.Camera3D;
import arc.graphics.g3d.PlaneBatch3D;
import arc.graphics.g3d.VertexBatch3D;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.util.Tmp;
import mindustry.graphics.g3d.GenericMesh;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.Sector;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;
import technical.util.Draw3D;

import static arc.graphics.GL20.GL_TRIANGLES;
import static mindustry.graphics.g3d.PlanetRenderer.outlineRad;

public class Satellite extends Planet
{
    private final Color claret = Color.valueOf("722f37");
    private final Color grayLight = Color.valueOf("909090");
    private final Color grayDark = Color.valueOf("505050");
    private final Color engine = Color.valueOf("303030");

    private final Color windowGlow = Color.valueOf("00ffff").a(1f);
    private final Color engineGlow = Color.valueOf("ff5500").a(1f);

    public Satellite(String name, Planet parent, float radius)
    {
        super(name, parent, radius, 1);

        generator = new SerpuloPlanetGenerator();

        orbitRadius = 6f;
        orbitTime = 60f;
        accessible = true;
        hasAtmosphere = false;
        bloom = false;
        alwaysUnlocked = true;

        meshLoader = () -> new GenericMesh() {
            private VertexBatch3D batch;
            private final Mat3D combined = new Mat3D();

            @Override
            public void render(PlanetParams params, Mat3D projection, Mat3D transform)
            {
                if (batch == null)
                {
                    batch = new VertexBatch3D(5000, false, true, 0);
                }

                Mat3D scale = new Mat3D().scl(0.1f).rotate(0, 1, 0, 90.0f);

                combined.set(projection).mul(transform).mul(scale);
                batch.proj(combined);

                // Core
                Draw3D.box(batch, new Vec3(0, 0, 0), new Vec3(0.4f, 0.15f, 1.2f), grayLight);

                // Top Stripe
                Draw3D.box(batch, new Vec3(0, 0.08f, 0), new Vec3(0.15f, 0.02f, 1.2f), claret);

                // Bridge
                Draw3D.box(batch, new Vec3(0, 0.12f, -0.2f), new Vec3(0.2f, 0.15f, 0.3f), grayDark);

                // Window
                Draw3D.box(batch, new Vec3(0, 0.14f, -0.04f), new Vec3(0.18f, 0.04f, 0.02f), windowGlow);

                // Wings
                Draw3D.box(batch, new Vec3(0, 0, -0.3f), new Vec3(0.9f, 0.05f, 0.2f), grayDark);

                // Engine Blocks
                Draw3D.cylinder(batch, new Vec3(-0.45f, 0, -0.3f), 0.12f, 0.6f, 16, engine, 0, 0, 0);
                Draw3D.cylinder(batch, new Vec3(0.45f, 0, -0.3f), 0.12f, 0.6f, 16, engine, 0, 0, 0);

                // Engine Stripes
                Draw3D.cylinder(batch, new Vec3(-0.45f, 0, -0.1f), 0.13f, 0.1f, 16, claret, 0, 0, 0);
                Draw3D.cylinder(batch, new Vec3(0.45f, 0, -0.1f), 0.13f, 0.1f, 16, claret, 0, 0, 0);

                // Engine Glow
                Draw3D.cylinder(batch, new Vec3(-0.45f, 0, -0.615f), 0.09f, 0.03f, 12, engineGlow, 0, 0, 0);
                Draw3D.cylinder(batch, new Vec3(0.45f, 0, -0.615f), 0.09f, 0.03f, 12, engineGlow, 0, 0, 0);

                // Smaller Center Engine
                Draw3D.cylinder(batch, new Vec3(0, 0, -0.5f), 0.08f, 0.4f, 12, engine, 0, 0, 0);
                Draw3D.cylinder(batch, new Vec3(0, 0, -0.715f), 0.06f, 0.03f, 12, engineGlow, 0, 0, 0);

                // Radar Holder
                Draw3D.cylinder(batch, new Vec3(0.12f, 0.22f, -0.2f), 0.015f, 0.15f, 6, grayLight, 90, 0, 0);

                // Radar Dish
                Draw3D.cylinder(batch, new Vec3(0.12f, 0.29f, -0.2f), 0.08f, 0.02f, 16, grayLight, 20, 0, 0);
                Draw3D.cylinder(batch, new Vec3(0.12f, 0.285f, -0.214f), 0.085f, 0.01f, 16, claret, 20, 0, 0);

                // Emitter & it's tip
                Draw3D.cylinder(batch, new Vec3(0.12f, 0.311f, -0.144f), 0.01f, 0.12f, 8, grayDark, 20, 0, 0);
                Draw3D.cylinder(batch, new Vec3(0.12f, 0.303f, -0.168f), 0.02f, 0.04f, 8, grayDark, 20, 0, 0);
                Draw3D.cylinder(batch, new Vec3(0.12f, 0.331f, -0.087f), 0.012f, 0.01f, 8, windowGlow, 20, 0, 0);

                // Nodes: Top, Bot Left, Bot Right
                Draw3D.cylinder(batch, new Vec3(0.12f, 0.345f, -0.16f), 0.006f, 0.1f, 4, grayDark, 50, 0, 0);
                Draw3D.cylinder(batch, new Vec3(0.08f, 0.29f, -0.14f), 0.006f, 0.1f, 4, grayDark, -5, 30, 0);
                Draw3D.cylinder(batch, new Vec3(0.16f, 0.29f, -0.14f), 0.006f, 0.1f, 4, grayDark, -5, -30, 0);

                batch.flush(GL_TRIANGLES);
            }

            @Override
            public void dispose() {
                if (batch != null) batch.dispose();
            }
        };
    }

    public boolean hasGrid()
    {
        return false;
    }

    @Override
    public void renderSectors(VertexBatch3D batch, Camera3D cam, PlanetParams params)
    {

    }

    public void setPlane(Sector sector, PlaneBatch3D projector){
        float rotation = -getRotation();
        float length = 999999f;

        projector.setPlane(
                //origin on sector position
                Tmp.v33.set(sector.tile.v).setLength((outlineRad + length) * radius).rotate(Vec3.Y, rotation).add(position),
                //face up
                sector.plane.project(Tmp.v32.set(sector.tile.v).add(Vec3.Y)).sub(sector.tile.v, radius).rotate(Vec3.Y, rotation).nor(),
                //right vector
                Tmp.v31.set(Tmp.v32).rotate(Vec3.Y, -rotation).add(sector.tile.v).rotate(sector.tile.v, 90).sub(sector.tile.v).rotate(Vec3.Y, rotation).nor()
        );
    }

    @Override
    public void drawBorders(VertexBatch3D batch, Sector sector, Color base, float alpha)
    {

    }

    @Override
    public void drawSelection(VertexBatch3D batch, Sector sector, Color color, float stroke, float length)
    {

    }

    @Override
    public void fill(VertexBatch3D batch, Sector sector, Color color, float offset)
    {

    }

    @Override
    public Vec3 lookAt(Sector sector, Vec3 out)
    {
        return null;
    }
}