package gent.timdemey.cards.services.scaleman.text;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;

import gent.timdemey.cards.services.scaleman.ScalableComponent;

public class ScalableTextComponent extends ScalableComponent
{
    private final String text;
    private final Font font;

    public ScalableTextComponent(String text, Font font)
    {
        super(null);
        this.text = text;
        this.font = font;
    }

    @Override
    protected final JComponent createComponent()
    {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setSize(label.getPreferredSize());
        return label;
    }

    @Override
    protected String[] addDebugStrings()
    {
        return null;
    }
}
