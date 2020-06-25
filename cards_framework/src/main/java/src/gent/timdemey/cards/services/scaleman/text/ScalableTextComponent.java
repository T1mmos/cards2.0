package gent.timdemey.cards.services.scaleman.text;

import java.awt.Font;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JLabel;

import gent.timdemey.cards.services.contract.FontResource;
import gent.timdemey.cards.services.scaleman.IScalableResource;
import gent.timdemey.cards.services.scaleman.ScalableComponent;

public class ScalableTextComponent extends ScalableComponent
{
    private final String text;
    private final FontResource fontRes;

    public ScalableTextComponent(UUID id, String text, FontResource fontRes)
    {
        super(id);
        this.text = text;
        this.fontRes = fontRes;
    }

    @Override
    protected final JComponent createComponent()
    {
        JLabel label = new JLabel(text);
        label.setFont(fontRes.font);
        label.setSize(label.getPreferredSize());
        return label;
    }

    @Override
    protected String[] addDebugStrings()
    {
        return null;
    }

    @Override
    public List<? extends IScalableResource> getResources()
    {
        return Arrays.asList(fontRes);
    }

    @Override
    public void update()
    {
        // no model to update from
    }
}
