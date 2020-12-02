package gent.timdemey.cards.ui.actions;

import javax.swing.Icon;

import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.services.context.ContextType;

public class A_Minimize extends ActionBase
{

    protected A_Minimize(ActionDescriptor desc, String title, Icon icon)
    {
        super(desc, title, icon);
    }

    @Override
    public void onContextInitialized(ContextType type)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onContextDropped(ContextType type)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onChange(ReadOnlyChange roChange)
    {
        // TODO Auto-generated method stub
        
    }

}
