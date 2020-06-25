package gent.timdemey.cards.services.scaleman.text;

import java.awt.Font;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JLabel;

import gent.timdemey.cards.services.scaleman.ScalableComponent;

public class ScalableTextComponent extends ScalableComponent<Font>
{
    private final String text;

    public ScalableTextComponent(UUID id, String text, ScalableFontResource fontRes)
    {
        super(id, fontRes);
        this.text = text;
    }

    @Override
    protected final JComponent createComponent()
    {
        JLabel label = new JLabel(text);
        label.setFont(getScalableResources().get(0).getResource().raw);
        label.setSize(label.getPreferredSize());
        return label;
    }

    @Override
    protected String[] addDebugStrings()
    {
        return null;
    }
    
    public final String getText()
    {
        return text;
    }

    @Override
    public void update()
    {
        // no model to update from
    }
}
