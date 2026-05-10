package technical.util;

import arc.graphics.Color;

public class TCol 
{
    public static final Color
    sky = from("#27b7f0ff"),
    smoke = from("#3b3535ff"),
    uranium = from("#b0e117"),
    darkUranium = from("#789c0cff"),
    copper = from("#eb7425"),
    circuit = from("#8a8a8aff"),
    stone = from("#d7a349"),
    brass = from("#ffa735"),
    darkStone = from("#8a6627ff"),
    clay = from("#9aa3b3"),
    brick = from("#b75a40"),
    coal = from("#2a2a2a"),
    flint = from("#383131"),
    iron = from("#d8af93"),
    zinc = from("#b0bac0"),
    ironDark = from("#8e7454"),
    porcelain = from("#bccbd9"),
    dense_ammo = from("#b99379ff"),
    copper_dark = from("#b8520f"),
    copper_black = from("#803909"),

    bioOrange = from("#ffcd66"),
    bioOutline = from("#250f39"),
    darkBioOrange = from("#e28654"),
    bioPurple = from("#5d2e85"),

    lavaRed = from("#af3818"),
    lavaYellow = from("#f17e19"),
    lavaOrange = from("#eb6325"),

    water = from("#41aba3"),

    metal = from("#7a8185"),

    arrow = from("#926e1fff"),

    highlight = copper,
    error = Color.scarlet
    ;

    public static Color from(String hex) {
        return Color.valueOf(hex);
    }

    public static Color from(int num)
    {
        float h = (num * 0.61803398875f) % 1f;

        float r = Math.max(0, Math.min(1f, Math.abs(h * 6f - 3f) - 1f));
        float g = Math.max(0, Math.min(1f, 2f - Math.abs(h * 6f - 2f)));
        float b = Math.max(0, Math.min(1f, 2f - Math.abs(h * 6f - 4f)));

        return new Color(r, g, b);
    }

    public static String str(Color color)
    {
        return "[#" + color.toString() + "]";
    }
}
