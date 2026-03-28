#define HIGHP

// Pure Glow Palette
#define LAVA_RED    vec3(180, 40, 20) / 255.0
#define LAVA_ORANGE vec3(255, 110, 30) / 255.0
#define LAVA_YELLOW vec3(255, 230, 100) / 255.0

uniform sampler2D u_texture;
uniform sampler2D u_noise;
uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;

void main() {
    vec2 uv = v_texCoords;
    float t = u_time * 0.0004;

    // 1. SCALE ADJUSTMENT
    // Lowering 0.015 to 0.005 makes the "blobs" much larger.
    vec2 p = (uv * u_resolution + u_campos) * 0.005;
    
    // 2. FRACTAL NOISE (Anti-Chunking)
    // We sample the noise at two different scales and blend them.
    // This fills in the gaps and smooths out the "square" look of a single texture.
    vec2 move1 = vec2(t * 0.5, t * 0.3);
    vec2 move2 = vec2(t * -0.2, t * 0.4);
    
    float noise1 = texture2D(u_noise, p + move1).r;
    float noise2 = texture2D(u_noise, p * 0.5 - move2).g; // Larger secondary scale
    
    // Combine them for a "Domain Warp"
    float combinedNoise = (noise1 + noise2) * 0.5;
    
    // Distort the coordinates for the final "Flow"
    vec2 warpedP = p + (combinedNoise * 0.4) - (t * 1.2);
    float flow = texture2D(u_noise, warpedP).r;
    
    // Soften the flow by mixing it with the combined noise
    flow = smoothstep(0.0, 1.0, (flow + combinedNoise) * 0.5);

    // 3. GLOW LOGIC
    // Use a smoother power curve to avoid harsh pixel edges
    float baseHeat = smoothstep(0.1, 0.8, flow);
    float brightHot = pow(flow, 3.0); 
    
    // 4. COLOR MIXING
    vec3 color = mix(LAVA_RED, LAVA_ORANGE, baseHeat);
    color = mix(color, LAVA_YELLOW, brightHot);
    
    // Add a soft glow based on the base heat to fill out the "size"
    color += LAVA_ORANGE * (baseHeat * 0.2);
    color += brightHot * 0.5;

    // 5. FINAL MASK
    float alpha = texture2D(u_texture, uv).a;
    gl_FragColor = vec4(color * alpha, alpha);
}