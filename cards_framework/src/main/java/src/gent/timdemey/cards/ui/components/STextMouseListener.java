package gent.timdemey.cards.ui.components;

import gent.timdemey.cards.ui.components.ext.IHasMouse;

class STextMouseListener implements IHasMouse
{
    private final SText component;
    
    STextMouseListener (SText component)
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
