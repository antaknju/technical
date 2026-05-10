package technical.core.dialog;

import arc.Core;
import arc.Events;
import arc.math.Interp;
import arc.scene.actions.Actions;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.gen.Tex;
import mindustry.type.Sector;
import mindustry.ui.Styles;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.EventType.*;
import technical.core.TSector;
import technical.util.TCol;

public class DialogManager 
{
    private static final float boxWidth = 380f;
    private static final float dividerHeight = 1f;
    private static final float msgYPad = 7f;

    private static final float idleAlpha = 0.18f;
    private static final float activeAlpha = 1.00f;
    private static final float holdDuration = 3.00f;
    private static final float alphaLerp = 0.09f;
    private static final float hideDuration = 0.25f;

    private static final Color highlightCol = TCol.from("#5bc8f5");
    private static final Color textCol = TCol.from("#b8cdd5");
    private static final Color dividerCol = TCol.from("#1a3545");
    private static final Color backgroundCol = TCol.from("#07161d");

    private static Table root;
    private static Table msgContainer;
    private static ScrollPane scrollPane;

    public static final Seq<Message> history = new Seq<>();

    public static boolean alive = false;
    public static boolean hiding = false;
    private static float curAlpha = 0f;
    private static float targetAlpha = idleAlpha;
    private static float holdTimer = 0f;

    public static final DialogRunner runner = new DialogRunner();

    public static boolean typingMessage = false;

    public static void load()
    {
        Events.on(WorldLoadEvent.class, e -> {
            clearMessages();

            if (dialog() != null)
                runner.reset(dialog());
        });

        Events.on(StateChangeEvent.class, e -> {
            if (e.from != GameState.State.menu && e.to == GameState.State.menu) {
                hideDialog();
            }
        });

        Events.on(ClientLoadEvent.class, e -> buildUI());

        Events.run(mindustry.game.EventType.Trigger.update, () -> {
            if (!Vars.state.isGame() || dialog() == null) return;

            runner.update(dialog());
        });
    }

    private static void buildUI() 
    {
        root = new Table() 
        {
            @Override
            public void draw() 
            {
                float ea = parentAlpha * color.a;

                // Background
                Draw.color(backgroundCol, ea * 0.90f);
                Fill.rect(x + width / 2f, y + height / 2f, width, height);

                // Children
                super.draw();

                // Corner brackets
                Draw.color(highlightCol, ea);
                Lines.stroke(1.5f);
                float cl = 11f;
                
                // top-left
                Lines.line(x, y + height - cl, x, y + height);
                Lines.line(x, y + height, x + cl, y + height);
                // top-right
                Lines.line(x + width - cl, y + height, x + width, y + height);
                Lines.line(x + width, y + height, x + width, y + height - cl);
                // bot-left
                Lines.line(x, y, x + cl, y);
                Lines.line(x, y, x, y + cl);
                // bot-right
                Lines.line(x + width - cl, y, x + width, y);
                Lines.line(x + width, y, x + width, y + cl);
                
                Draw.reset();
            }
        };

        root.setFillParent(false);
        root.visible = false;
        root.margin(14f);
        root.defaults().left();
        root.top().left();

        // Header
        Table header = new Table();

        Label haderLabel = new Label("TRANSMISSION", Styles.outlineLabel);
        haderLabel.setColor(highlightCol);
        header.add(haderLabel).center().row();

        // header line
        Image highlightLine = new Image(Tex.whiteui);
        highlightLine.setColor(highlightCol);
        header.add(highlightLine).width(boxWidth).height(dividerHeight).padTop(5f).row();

        root.add(header).width(boxWidth).padBottom(9f).row();

        // Scroll Pane
        msgContainer = new Table();
        msgContainer.top().left();
        msgContainer.defaults().left();

        scrollPane = new ScrollPane(msgContainer);
        scrollPane.setScrollingDisabled(true, false); // Allow y
        scrollPane.setOverscroll(false, false);

        // Remove Scrollbar
        scrollPane.getStyle().vScroll = Styles.none;
        scrollPane.getStyle().vScrollKnob = Styles.none;
        scrollPane.setFadeScrollBars(true);
        scrollPane.setupFadeScrollBars(0f, 0f);

        root.add(scrollPane).width(boxWidth).height(220f).fillX().row();

        root.update(() -> {
            if (hiding) return;

            if (root.hasMouse())
            {
                holdTimer = holdDuration;
                targetAlpha = activeAlpha;

                Core.scene.setScrollFocus(scrollPane);
            }
            else
            {
                if (holdTimer > 0f)
                {
                    holdTimer -= Time.delta / 60f;
                    targetAlpha = activeAlpha;
                }
                else
                {
                    targetAlpha = idleAlpha;
                }

                if(Core.scene.getScrollFocus() == scrollPane){
                    Core.scene.setScrollFocus(null);
                }
            }

            curAlpha = Mathf.lerpDelta(curAlpha, targetAlpha, alphaLerp);
            root.color.a = curAlpha;
        });

        // anchor
        Core.scene.table(anchor -> {
            anchor.bottom().left();
            anchor.add(root).pad(16f).padBottom(80f).bottom().left();
        });
    }

    public static void restoreHistory()
    {
        if (history.isEmpty()) return;

        root.visible = true;
        curAlpha = 0f;
        root.color.a = 0f;
        alive = true;
        hiding = false;

        for (Message msg : history)
        {
            appendRow(msg, false);
        }

        scrollToBottom();

        root.pack();
    }

    public static void showMessage(Message msg)
    {
        if (typingMessage || msg == null) return;

        typingMessage = true;

        history.add(msg);
        appendRow(msg, true);

        // First message
        if (!alive)
        {
            root.visible = true;
            curAlpha = 0f;
            root.color.a = 0f;
            alive = true;
            hiding = false;
        }

        root.pack();
    }

    public static void holdOpacity()
    {
        curAlpha = activeAlpha;
        root.color.a = activeAlpha;
        holdTimer = holdDuration;
    }

    public static boolean isScrollAtBottom()
    {
        float distanceFromBottom = scrollPane.getMaxY() - scrollPane.getScrollY();

        return distanceFromBottom <= 10f;
    }

    public static void scrollToBottom()
    {
        scrollPane.setScrollPercentY(1f);
    }

    public static void hideDialog()
    {
        if (!alive || hiding) return;
        hiding = true;
        alive = false;
        typingMessage = false;

        root.actions(
                Actions.fadeOut(hideDuration, Interp.fade),
                Actions.run(() -> {
                    hiding = false;
                    root.visible = false;
                    root.color.a = 0f;
                    curAlpha = 0f;
                    clearMessages();
                })
        );
    }

    private static void appendRow(Message msg, boolean animate)
    {
        if (!msgContainer.getChildren().isEmpty())
        {
            msgContainer.image(Tex.whiteui, dividerCol).fillX().height(dividerHeight).pad(msgYPad, 0, msgYPad, 0).row();
        }

        msgContainer.table(t -> {
            t.left().defaults().left();
            t.add(msg.sender, Styles.outlineLabel).padBottom(2f).row();

            Label body = t.add("", Styles.outlineLabel).width(boxWidth - 4f).get();
            body.setColor(textCol);

            body.addAction(new TypewriterAction(msg.message, scrollPane, !animate));
        }).fillX().padBottom(2f).row();
    }

    private static void clearMessages()
    {
        history.clear();
        if (msgContainer != null) msgContainer.clearChildren();
    }

    private static Sector sector() {
        return Vars.state.getSector();
    }

    public static Dialog dialog()
    {
        if (sector() != null && sector().preset instanceof TSector ts)
        {
            return ts.dialog;
        }
        return null;
    }
}