package technical.utility;

import arc.Core;
import arc.graphics.Color;
import technical.Technical;

public class TBundle
{
    /**
     * @param elements "technical", "brick-wall"
     * @return "technical.brick-wall"
     */
    public static String build(String... elements)
    {
        return String.join(".", elements);
    }

    /**
     * @param text "block.technical.brick-wall"
     * @param args 67
     * @return "Brick Wall Magic Number: 67"
     */
    public static String get(String text, Object... args)
    {
        return Core.bundle.format(text, args);
    }

    /**
     *
     * @param text "BrickWall"
     * @return "[highlight]BrickWall[]"
     */
    public static String highlight(String text)
    {
        return color(text, TCol.highlight);
    }

    /**
     *
     * @param text "BrickWall"
     * @param color Color.white
     * @return "[#ffffff]BrickWall[]"
     */
    public static String color(String text, Color color)
    {
        return TCol.str(color) + text + "[]";
    }

    /**
     *
     * @param color Color.white
     * @return "[#ffffff]"
     */
    public static String color(Color color)
    {
        return TCol.str(color);
    }

//    public static String tstat(String text)
//    {
//        return get(build("tstat.", text));
//    }

    public static String error(String text)
    {
        return get(build("err", text));
    }

    public static String technical(String text)
    {
        return get(build(Technical.name, text));
    }

    public static String get_enum(Enum<?> en)
    {
        return get(build("enum", kebab(en.getClass().getSimpleName()), kebab(en.name())));
    }

    /**
     * @param text "BrickWall"
     * @return "brick-wall"
     */
    public static String kebab(String text)
    {
        StringBuilder bun = new StringBuilder();

        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);

            if (i > 0 && Character.isUpperCase(c))
                bun.append('-');

            bun.append(Character.toLowerCase(c));
        }

        return bun.toString();
    }
}
