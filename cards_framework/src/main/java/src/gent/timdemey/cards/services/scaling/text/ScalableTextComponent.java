package gent.timdemey.cards.services.scaling.text;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gent.timdemey.cards.services.scaling.IScalableResource;
import gent.timdemey.cards.services.scaling.ScalableComponent;

public class ScalableTextComponent extends ScalableComponent<Font>
{
    private String text;

    public ScalableTextComponent(UUID id, String text, ScalableFontResource fontRes)
    {
        super(id, fontRes);
        this.text = text;
    }

    @Override
    protected final JComponent createComponent()
    {
        // center the label by wrapping it in a panel and packing the label's width
        // according to its content / text in update()
        JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        container.setOpaque(false);
        
        JScalableLabelComponent label = new JScalableLabelComponent(text);        
        container.add(label);
        return container;
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
    
    protected final void setText(String text)
    {
        this.text = text;
    }

    @Override
    public void update()
    {
        Dimension dim = getBounds().getSize();
        IScalableResource<Font> res = getScalableResources().get(0);
        Dimension fontDim = new Dimension(dim.width, dim.height);
        Font font = res.get(fontDim);
        
        JLabel label = getLabel();        
        label.setFont(font);
        label.setText(text);
        label.setPreferredSize(label.getPreferredSize());
        label.getParent().validate();
    }
    
    @Override
    public void setForeground(Color color)
    {
        JLabel label = getLabel();   
        label.setForeground(color);
    }
    
    private JLabel getLabel()
    {
        JLabel label = (JLabel) ((JPanel) getComponent()).getComponent(0);
        return label;
    }
}
