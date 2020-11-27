package gent.timdemey.cards.services.scaling.text;

import gent.timdemey.cards.services.scaling.IScalableComponentMouseListener;

class ScalableTextComponentMouseListener implements IScalableComponentMouseListener
{
    private final ScalableTextComponent component;
    
    ScalableTextComponentMouseListener (ScalableTextComponent component)
    {
        this.component = component;
    }    
    
    @Override
    public void onMouseDragged()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMouseMoved()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMouseWheelMoved()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMouseClicked()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMousePressed()
    {
        component.setMousePressed(true);
        component.repaint();
    }

    @Override
    public void onMouseReleased()
    {
        component.setMousePressed(false);
        component.repaint();
    }

    @Override
    public void onMouseEntered()
    {
        component.setMouseInside(true);
        component.repaint();
    }

    @Override
    public void onMouseExited()
    {
        component.setMouseInside(false);
        component.repaint();
    }

}
