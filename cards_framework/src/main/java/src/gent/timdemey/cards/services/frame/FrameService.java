package gent.timdemey.cards.services.frame;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.services.IConfigManager;
import gent.timdemey.cards.services.IFrameService;
import gent.timdemey.cards.services.IImageService;
import gent.timdemey.cards.services.configman.ConfigKey;
import gent.timdemey.cards.ui.actions.ActionDef;
import gent.timdemey.cards.ui.actions.Actions;
import gent.timdemey.cards.ui.actions.IActionFactory;

public class FrameService implements IFrameService
{
    @Override
    public List<Image> getFrameIcons()
    {
        List<Image> images = new ArrayList<>();

        IImageService imageService = Services.get(IImageService.class);
        images.add(imageService.read("icon_spade_16x16.png"));
        images.add(imageService.read("icon_spade_24x24.png"));
        images.add(imageService.read("icon_spade_48x48.png"));
        images.add(imageService.read("icon_spade_140x140.png"));

        return images;
    }

    @Override
    public JMenuBar getMenuBar(ICardPlugin plugin)
    {
        JMenuBar menuBar = new JMenuBar();

        addMenuStart(menuBar, plugin);
        addMenuEdit(menuBar, plugin);
        addMenuDebug(menuBar, plugin);

        return menuBar;
    }
    

    protected void addMenuStart(JMenuBar menuBar, ICardPlugin plugin)
    {
        JMenu menu = createMenuStart(plugin);
        menuBar.add(menu);
    }

    protected void addMenuEdit(JMenuBar menuBar, ICardPlugin plugin)
    {
        JMenu menu = createMenuEdit(plugin);
        menuBar.add(menu);
    }

    protected void addMenuDebug(JMenuBar menuBar, ICardPlugin plugin)
    {
        if (!Services.get(IConfigManager.class).get(ConfigKey.DEBUG))
        {
            return;
        }

        JMenu menu = createMenuDebug(plugin);
        menuBar.add(menu);
    }

    protected final void addToMenu(JMenu menu, String actionId)
    {
        IActionFactory actionFactory = Services.get(IActionFactory.class);
        ActionDef actionDef = actionFactory.getActionDef(actionId);
        Action action = actionDef.action;
        KeyStroke accelerator = actionDef.accelerator;
        
        JMenuItem menu_create = new JMenuItem(action);
        if (accelerator != null)
        {
            menu_create.setAccelerator(accelerator);
        }
        menu.add(menu_create);
    }

    protected JMenu createMenuStart(ICardPlugin plugin)
    {
        JMenu menu = new JMenu(Loc.get(LocKey.Menu_game));

        if (plugin.getPlayerCount() > 1)
        {
            addToMenu(menu, Actions.ACTION_CREATE_MULTIPLAYER);
            addToMenu(menu, Actions.ACTION_JOIN);
            addToMenu(menu, Actions.ACTION_LEAVE);
        }
        else
        {
            addToMenu(menu, Actions.ACTION_START);
        }

        menu.addSeparator();
        addToMenu(menu, Actions.ACTION_QUIT);
        
        return menu;
    }

    protected JMenu createMenuEdit(ICardPlugin plugin)
    {
        JMenu menu = new JMenu(Loc.get(LocKey.Menu_edit));

        addToMenu(menu, Actions.ACTION_UNDO);
        addToMenu(menu, Actions.ACTION_REDO);    

        return menu;
    }

    protected JMenu createMenuDebug(ICardPlugin plugin)
    {
        JMenu menu = new JMenu(Loc.get(LocKey.DebugMenu_debug));

        addToMenu(menu, Actions.ACTION_DEBUG_DRAWOUTLINES);
        addToMenu(menu, Actions.ACTION_DEBUG_GC);

        return menu;
    }

    @Override
    public BufferedImage getBackground()
    {
        BufferedImage background = Services.get(IImageService.class).read("background_green.png");
        return background;
    }
}

