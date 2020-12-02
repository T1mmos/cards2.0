package gent.timdemey.cards.ui.actions;

import javax.swing.Icon;

import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.services.context.ContextType;

public class A_QuitGame extends ActionBase
{
    protected A_QuitGame(ActionDescriptor desc, String title, Icon icon)
    {
        super(desc, title, icon);
    }

    @Override
    public void onContextInitialized(ContextType type)
    {
    }

    @Override
    public void onContextDropped(ContextType type)
    {
    }

    @Override
    public void onChange(ReadOnlyChange roChange)
    {
    }
}
