package technical.core;

import arc.math.Mathf;
import mindustry.type.ItemStack;

public class FlyingItem {
    public ItemStack stack;
    public float x, y;
    public float startx, starty;
    public float tx, ty;
    public float progress;
    public float speed;
    public float time;

    public FlyingItem(ItemStack stack, float x, float y, float tx, float ty, float speed)
    {
        this.stack = stack;
        this.startx = x;
        this.starty = y;
        this.x = x;
        this.y = y;
        this.tx = tx;
        this.ty = ty;
        this.speed = speed / 60f;
        this.progress = 0f;
    }

    public void update(float delta)
    {
        progress += speed * delta;

        float ispeed = delta / 30f;
        if (progress < 0.9f)
            time = Mathf.clamp(time + ispeed);
        else
            time = Mathf.clamp(time - ispeed);

        if(progress > 1f) progress = 1f;

        x = Mathf.lerp(startx, tx, progress);
        y = Mathf.lerp(starty, ty, progress);
    }

    public boolean arrived(){
        return progress >= 1f;
    }
}
