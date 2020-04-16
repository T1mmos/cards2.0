package gent.timdemey.cards.services.frame;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.ui.actions.SolShowTestActions;

public class SolShowTestFrameService extends FrameService
{
    @Override
    protected void addMenuDebug(JMenuBar menuBar, ICardPlugin plugin)
    {
        // remove condition check to configuration
        JMenu menu = createMenuDebug(plugin);
        menuBar.add(menu);
    }
    
    @Override
    protected JMenu createMenuDebug(ICardPlugin plugin)
    {
        JMenu menu = super.createMenuDebug(plugin);
        
        menu.addSeparator();
        addToMenu(menu, SolShowTestActions.ACTION_FAKESOLSHOWGAME);
                
        return menu;
    }
}
