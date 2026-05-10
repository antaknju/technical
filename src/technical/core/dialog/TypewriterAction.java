package technical.core.dialog;

import arc.audio.Sound;
import arc.graphics.g2d.GlyphLayout;
import arc.scene.Action;
import arc.scene.event.ClickListener;
import arc.scene.event.InputEvent;
import arc.scene.ui.Label;
import arc.scene.ui.ScrollPane;
import mindustry.Vars;
import mindustry.gen.Sounds;

public class TypewriterAction extends Action
{
    private static final float charPause = 0.038f;
    private static final float punctuationPause = 0.22f;
    private static final String hideTag = "[#00000000]";

    private final String fullText;
    private final ScrollPane scroll;

    private static final Sound tickSound = Sounds.click;
    private static final float soundCooldown = 0.06f;
    private float soundTimer = 0f;

    private float timer = 0f;
    private int index = 0;
    private boolean initialized = false;

    private boolean requestingSkip = false;

    private final StringBuilder visibleText = new StringBuilder();
    private final GlyphLayout glyphLayout = new GlyphLayout();

    public TypewriterAction(String text, ScrollPane scroll, boolean requestingSkip)
    {
        this.fullText = text;
        this.scroll = scroll;
        this.requestingSkip = requestingSkip;
    }

    public void fastForward()
    {
        requestingSkip = true;
    }

    @Override
    public boolean act(float delta)
    {
        if (!Vars.state.isPlaying() || Vars.player.core() == null) return false;

        DialogManager.holdOpacity();

        Label label = (Label) actor;
        if (!initialized)
        {
            label.setText(hideTag + fullText + "[]");
            label.invalidateHierarchy();
            if (label.parent != null) label.parent.pack();

            if (label.parent != null)
            {
                label.parent.addListener(new ClickListener()
                {
                    @Override
                    public void clicked(InputEvent event, float x, float y)
                    {
                        fastForward();
                    }
                });
            }

            initialized = true;
        }

        if (index >= fullText.length()) return true;

        timer += delta;
        soundTimer += delta;

        while (index < fullText.length() && timer >= 0)
        {
            char c = fullText.charAt(index++);

            if (!requestingSkip)
                timer -= charPause + (isPunctuation(c) ? punctuationPause : 0f);

            if (c != ' ' && c != '\n' && soundTimer >= soundCooldown)
            {
                tickSound.play(1f, 1f + (float)(Math.random() * 0.1 - 0.05), 0f);
                soundTimer = 0f;
            }

            if (c == ' ')
            {
                // Find where the next word ends
                int wordEnd = index;
                while (wordEnd < fullText.length() && fullText.charAt(wordEnd) != ' ' && fullText.charAt(wordEnd) != '\n')
                {
                    wordEnd++;
                }

                // Only test if there actually is a next word
                if (wordEnd > index)
                {
                    String nextWord = fullText.substring(index, wordEnd);
                    String currentLine = getCurrentLine(visibleText);

                    glyphLayout.setText(label.getStyle().font, currentLine + " " + nextWord);

                    if (glyphLayout.width > getAvailableWidth(label))
                    {
                        visibleText.append('\n');
                        continue;
                    }
                }
            }

            visibleText.append(c);
        }

//        boolean wasAtBottom = DialogManager.isScrollAtBottom();

        String hidden = fullText.substring(index);
        label.setText(visibleText + hideTag + hidden + "[]");

        DialogManager.scrollToBottom();

        if (index >= fullText.length())
        {
            DialogManager.typingMessage = false;
            return true;
        }
        return false;
    }

    private String getCurrentLine(StringBuilder sb)
    {
        int lastNewline = -1;
        for (int i = sb.length() - 1; i >= 0; i--) {
            if (sb.charAt(i) == '\n') { lastNewline = i; break; }
        }
        return lastNewline >= 0 ? sb.substring(lastNewline + 1) : sb.toString();
    }

    private float getAvailableWidth(Label label)
    {
        return scroll.getWidth();
    }

    private boolean isPunctuation(char c)
    {
        return c == '.' || c == '?' || c == '!' || c == ',';
    }
}