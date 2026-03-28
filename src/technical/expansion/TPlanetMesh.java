package technical.expansion;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.Log;
import arc.util.Tmp;
import arc.util.noise.Simplex;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.*;
import mindustry.type.Planet;
import technical.T;

public class TPlanetMesh extends HexMesh 
{
    // public static Color 
    //     c_snow = T.c("#ffffff"),
    //     c_rock = Color.valueOf("#505050"),
    //     c_ice_spike = Color.valueOf("#b6cad6"),
    //     c_sand = Color.valueOf("#d9a066"),
    //     c_water = Color.valueOf("#52528d"),
    //     c_lava = Color.valueOf("#e6482e"),
    //     c_basalt = Color.valueOf("#292020"),
    //     c_sulfur = Color.valueOf("#f5e76e"),
    //     c_sulfur_green = Color.valueOf("#b4d962"),
    //     c_ravine = Color.valueOf("#3d342b"),
    //     c_clay = Color.valueOf("#8c9ac9"),
    //     c_clay_dark = Color.valueOf("#697291");
    
    // Water Biomes
    final static Color 
        ocean = T.c("#3219a3"),
        deepOcean = T.c("#170957"),
        beach = T.c("#d9b566"),
        desert = T.c("#d9a066"),
        snow = T.c("#dadada"),
        mountain = T.c("#424242"),
        dirt = T.c("#4e2f1c"),
        stone = T.c("#505050"),
        clay = T.c("#8c9ac9"),
        lava = T.c("#e6482e");

    public TPlanetMesh(Planet planet, int seed, int divisions) 
    {
        this.planet = planet;
        this.shader = Shaders.planet;

        this.mesh = MeshBuilder.buildHex(new HexMesher() 
        {
            private Color getBiome(Vec3 pos) 
            {
                float temp = Simplex.noise3d(seed + 67, 2, 0.3f, 1.0f, pos.x + 67, pos.y + 67, pos.z + 67);
                float height = getHeight(pos);
                float moisture = Simplex.noise3d(seed + 37, 2, 1f, 1.0f, pos.x - 2137, pos.y + 267, pos.z - 367);

                float seaLevel = 0.45f;
                float beachLevel = 0.55f;

                Color baseColor = Color.red;
                if (height < seaLevel) 
                {
                    float t = height / seaLevel;
                    baseColor = deepOcean;
                    return baseColor.lerp(ocean, t);
                }
                
                if (height < beachLevel) 
                {
                    float t = (height - seaLevel) / (beachLevel - seaLevel);
                    baseColor = ocean;
                    return baseColor.lerp(beach, t);
                }

                if (temp > 0.5f) 
                {
                    baseColor = desert;
                    baseColor.lerp(dirt, moisture); 
                } 
                else 
                {
                    baseColor = stone;
                    baseColor.lerp(clay, moisture);
                }

                if (height > 0.8f) 
                {
                    float t = (height - 0.8f) / 0.2f;
                    baseColor = baseColor.lerp(mountain, t);
                    
                    if (height > 0.9f) {
                        float s = (height - 0.9f) / 0.1f;
                        baseColor = baseColor.lerp(snow, s);
                    }
                }

                return baseColor;
            }

            @Override
            public float getHeight(Vec3 pos) 
            {
                float baseAmp = 1.3f;
                float baseHeight = Simplex.noise3d(seed + 21, 5, 0.55f, 1.0f, pos.x + 21, pos.y - 53, pos.z + 67) * baseAmp;
                baseHeight = baseHeight - baseAmp + 1;

                float rawNoise = Simplex.noise3d(seed + 41, 4, 0.05f, 1.0f, pos.x, pos.y, pos.z - 67);

                float dist = Math.abs(rawNoise);
                float tunnelWidth = 0.15f;
                float wallThickness = 0.05f;
                float t = (dist - tunnelWidth) / wallThickness;
                t = Math.max(0.0f, Math.min(1.0f, t));
                float ravineHeight = 0.3f + (t * 0.8f);

                return Mathf.clamp((baseHeight + ravineHeight) / 2);
            }

            @Override
            public void getColor(Vec3 pos, Color out) 
            {
                out.set(getBiome(pos));
            }

        }, divisions, planet.radius, 0.2f);
    }
}
