package technical.content;

import static mindustry.Vars.headless;
import static mindustry.Vars.renderer;
import static mindustry.Vars.tree;

import arc.Core;
import arc.graphics.Texture;
import arc.graphics.gl.Shader;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.graphics.CacheLayer;

/* Thanks to BetaMindy mod for example use of mindustry shaders */
public class TShaders 
{
    public static @Nullable ModSurfaceShader lava;
    public static CacheLayer.ShaderLayer lavaLayer;
    protected static boolean loaded;

    public static void load()
    {
        if(!headless)
        {
            lava = new ModSurfaceShader("lava");
            loaded = true;
        }
        lavaLayer = new CacheLayer.ShaderLayer(lava);
        CacheLayer.add(lavaLayer);
    }

    public static class ModSurfaceShader extends Shader{
        Texture noiseTex;

        public ModSurfaceShader(String frag){
            super(Core.files.internal("shaders/screenspace.vert"),
                    tree.get("shaders/" + frag + ".frag"));
            loadNoise();
        }

        public ModSurfaceShader(String vertRaw, String fragRaw){
            super(vertRaw, fragRaw);
            loadNoise();
        }

        public String textureName(){
            return "noise";
        }

        public void loadNoise(){
            Core.assets.load("sprites/" + textureName() + ".png", Texture.class).loaded = t -> {
                t.setFilter(Texture.TextureFilter.linear);
                t.setWrap(Texture.TextureWrap.repeat);
            };
        }

        @Override
        public void apply(){
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);
            setUniformf("u_time", Time.time);

            if(hasUniform("u_noise")){
                if(noiseTex == null){
                    noiseTex = Core.assets.get("sprites/" + textureName() + ".png", Texture.class);
                }

                noiseTex.bind(1);
                renderer.effectBuffer.getTexture().bind(0);

                setUniformi("u_noise", 1);
            }
        }
    }
}
