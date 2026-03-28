package technical.expansion;

import arc.func.Boolp;
import arc.graphics.Color;
import arc.math.Interp;
import arc.scene.event.HandCursorListener;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import technical.Fr;
import technical.content.TIcons;

public class TUI 
{
    public static Cell<Table> AddIconAmount(Table table, TextureRegionDrawable icon, float amount, String tooltip) 
    {
        Table tab = new Table();

        Image image = new Image(icon);
        image.setSize(32f);

        tab.add(image).size(32f);

        if (amount != 0) 
        {
            Label label = new Label(UI.formatAmount((long)amount), Styles.outlineLabel);
            tab.add(label).padTop(2f);
        }

        return table.add(tab).pad(4f).tooltip(tooltip);
    }

    public static Cell<Table> AddContentAmount(Table table, UnlockableContent content, float amount, String tooltip) 
    {
        Cell<Table> cell = AddIconAmount(table, new TextureRegionDrawable(content.uiIcon), amount, tooltip);

        if(!Vars.mobile)
        {
            cell.get().addListener(new HandCursorListener(() -> !content.isHidden(), true));
        }
        
        cell.get().clicked(() -> {
            if(!content.isHidden()){
                Vars.ui.content.show(content);
            }
        });

        return cell;
    }

    public static String BuildTooltip(String name, float amount, float fr, StatUnit unit)
    {
        return name + " " + UI.formatAmount((long)(amount / fr)) + " " + unit.localized();
    }

    public static void addRecipeButton(Table parent, RecipeDrawable recipe, Runnable changed, Boolp isChecked) 
    {
        // Styles.clearTogglei is standard for Mindustry toggle buttons
        ImageButton button = new ImageButton(Styles.clearTogglei);

        Table layout = new Table();

        // Generate the input and output grids
        Table inputs = buildIconGrid(recipe.inputItems, recipe.inputLiquids, recipe.inputPayloads);
        Table outputs = buildIconGrid(recipe.outputItems, recipe.outputLiquids, recipe.outputPayloads);

        // Add to layout with some padding
        layout.add(inputs).pad(4f).center();
        layout.image(mindustry.gen.Icon.right).pad(8f).color(Color.white);
        layout.add(outputs).pad(4f).center();

        button.add(layout).grow();
        button.changed(changed);
        button.update(() -> button.setChecked(isChecked.get()));

        parent.add(button).pad(4f);
    }

    public static Table buildIconGrid(ItemStack[] items, LiquidStack[] liquids, PayloadStack[] payloads) 
    {
        Table grid = new Table();
        
        // 1. Calculate total number of icons to display
        int itemLen = (items == null) ? 0 : items.length;
        int liquidLen = (liquids == null) ? 0 : liquids.length;
        int total = itemLen + liquidLen;

        // 2. Determine size and columns based on count
        float iconSize;
        int cols;

        if (total <= 1) {
            iconSize = 32f;
            cols = 1;
        } else if (total <= 4) {
            // For 2-4 items, use a 2x2 grid. 
            // 16f fits well in the same 32f total space, but 8f is used if you prefer a smaller look.
            iconSize = 16f; 
            cols = 2;
        } else {
            // For 5+ items, use 8f icons in a 3-column grid
            iconSize = 8f;
            cols = 3;
        }

        // 3. Populate the grid
        int index = 0;

        if (items != null) {
            for (ItemStack stack : items) {
                grid.add(new Image(stack.item.uiIcon)).size(iconSize).pad(1f);
                index++;
                if (index % cols == 0) grid.row();
            }
        }

        if (payloads != null) {
            for (PayloadStack stack : payloads) {
                grid.add(new Image(stack.item.uiIcon)).size(iconSize).pad(1f);
                if (++index % cols == 0) grid.row();
            }
        }

        if (liquids != null) {
            for (LiquidStack stack : liquids) {
                grid.add(new Image(stack.liquid.uiIcon)).size(iconSize).pad(1f);
                index++;
                if (index % cols == 0) grid.row();
            }
        }

        // if (total == 0) {
        //     grid.add(new Image(mindustry.gen.Icon.cancel.getRegion())).size(32f).color(Color.scarlet);
        // }

        return grid;
    }

    public static void buildRecipeEntry(Table table, RecipeDrawable recipe, boolean isInput) 
    {
        Table t = new Table();
        if (isInput) t.left();
        else t.right();

        Table mat = new Table();
        mat.left();

        int i = 0;

        // Items
        ItemStack[] items = isInput ? recipe.inputItems : recipe.outputItems;
        if (items != null) 
        {
            for (ItemStack stack : items)
            {
                Cell<Table> cell = AddContentAmount(mat, stack.item, stack.amount, BuildTooltip(stack.item.localizedName, stack.amount, Fr.item, StatUnit.items));
                if (isInput) cell.left(); else cell.right();
                
                if (++i % 2 == 0) mat.row();
            }
        }

        // Liquids
        LiquidStack[] liquids = isInput ? recipe.inputLiquids : recipe.outputLiquids;
        if (liquids != null) 
        {
            for (LiquidStack stack : liquids) 
            {
                Cell<Table> cell = AddContentAmount(mat, stack.liquid, stack.amount / Fr.liquid, BuildTooltip(stack.liquid.localizedName, stack.amount, Fr.liquid, StatUnit.liquidSecond));
                if (isInput) cell.left(); else cell.right();

                if (++i % 2 == 0) mat.row();
            }
        }

        // Payloads
        PayloadStack[] payloads = isInput ? recipe.inputPayloads : recipe.outputPayloads;
        if (payloads != null)
        {
            for (PayloadStack stack : payloads)
            {
                Cell<Table> cell = AddContentAmount(mat, stack.item, stack.amount, stack.item.localizedName + " x" + stack.amount);
                if (isInput) cell.left(); else cell.right();
                if (++i % 2 == 0) mat.row();
            }
        }

        // Power (only input)
        if (isInput && recipe.inputPower > 0f) 
        {
            AddIconAmount(mat, mindustry.gen.Icon.powerOld, i, recipe.inputPower + " " + StatUnit.powerSecond.localized()).left();

            if (++i % 2 == 0) mat.row();
        }

        // Kinetic Energy (only input)
        if (isInput && recipe.inputKinetic != null) 
        {
            if (++i % 2 == 0) mat.row();

            AddIconAmount(mat, new TextureRegionDrawable(TIcons.torque), recipe.inputKinetic.torque / Fr.torque, recipe.inputKinetic.torque / Fr.torque + " " + StatUnit.perSecond.localized()).left();
            AddIconAmount(mat, new TextureRegionDrawable(TIcons.speed), recipe.inputKinetic.speed / Fr.speed, recipe.inputKinetic.speed / Fr.speed + " " + StatUnit.perSecond.localized()).left();
        }

        table.add(mat).pad(12f).fill();
    }

    public static void buildRecipesStats(Table stat, Seq<RecipeDrawable> recipes) 
    {
        stat.clear();
        stat.row();

        for (RecipeDrawable recipe : recipes) {
            Table t = new Table();
            t.background(Tex.whiteui);
            t.setColor(Pal.darkestGray);

            // INPUT
            buildRecipeEntry(t, recipe, true);

            // TIME
            AddTimeBar(t, recipe.craftTime);

            // OUTPUT
            buildRecipeEntry(t, recipe, false);

            stat.add(t).pad(10f).grow();
            stat.row();
        }

        stat.row();
        stat.defaults().grow();
    }

    public static Table AddTimeBar(Table table, float duration)
    {
        Table time = new Table();

        final float[] currentTime = {0f};

        time.update(() -> {
            currentTime[0] += Time.delta;
            if (currentTime[0] > duration) currentTime[0] = 0f;
        });

        String durationFormated = duration == 0 ? "0" : String.format("%.2f", duration / 60f);

        Cell<Bar> barCell = time.add(new Bar(
            () -> durationFormated,
            () -> Pal.accent,
            () -> 0.15f + Interp.smooth.apply(currentTime[0] / duration) * 0.85f)
        );

        barCell.width(Vars.mobile ? 220f : 250f).height(45f);
        Cell<Table> timeCell = table.add(time).pad(12f);
        timeCell.tooltip(Stat.productionTime.localized() + ": " + durationFormated + " " + StatUnit.seconds.localized());

        return table;
    }
}
