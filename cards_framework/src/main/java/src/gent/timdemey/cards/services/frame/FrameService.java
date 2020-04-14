package gent.timdemey.cards.services.frame;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
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
import gent.timdemey.cards.ui.actions.A_CreateGame;
import gent.timdemey.cards.ui.actions.A_DebugDrawDebugLines;
import gent.timdemey.cards.ui.actions.A_DebugGC;
import gent.timdemey.cards.ui.actions.A_JoinGame;
import gent.timdemey.cards.ui.actions.A_LeaveGame;
import gent.timdemey.cards.ui.actions.A_QuitGame;
import gent.timdemey.cards.ui.actions.A_Redo;
import gent.timdemey.cards.ui.actions.A_StartGame;
import gent.timdemey.cards.ui.actions.A_StopGame;
import gent.timdemey.cards.ui.actions.A_Undo;

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

    protected JMenu createMenuStart(ICardPlugin plugin)
    {
        JMenu menu = new JMenu(Loc.get(LocKey.Menu_game));

        if (plugin.getPlayerCount() > 1)
        {
            JMenuItem menu_create = new JMenuItem(new A_CreateGame());
            menu.add(menu_create);

            JMenuItem menu_join = new JMenuItem(new A_JoinGame());
            menu.add(menu_join);

            JMenuItem menu_leave = new JMenuItem(new A_LeaveGame());
            menu.add(menu_leave);
        } else
        {
            JMenuItem menu_start = new JMenuItem(new A_StartGame());
            menu.add(menu_start);

            JMenuItem menu_stop = new JMenuItem(new A_StopGame());
            menu.add(menu_stop);
        }

        menu.addSeparator();

        JMenuItem menu_quit = new JMenuItem(new A_QuitGame());
        menu.add(menu_quit);
        return menu;
    }

    protected JMenu createMenuEdit(ICardPlugin plugin)
    {
        JMenu menu = new JMenu(Loc.get(LocKey.Menu_edit));

        Action actionUndo = new A_Undo();
        JMenuItem menu_undo = new JMenuItem(actionUndo);
        menu_undo.setAccelerator(KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        menu.add(menu_undo);

        Action actionRedo = new A_Redo();
        JMenuItem menu_redo = new JMenuItem(actionRedo);
        menu_redo.setAccelerator(KeyStroke.getKeyStroke('Y', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        menu.add(menu_redo);

        return menu;
    }

    protected JMenu createMenuDebug(ICardPlugin plugin)
    {
        JMenu menu = new JMenu(Loc.get(LocKey.DebugMenu_debug));

        JMenuItem menu_drawdebug = new JCheckBoxMenuItem(new A_DebugDrawDebugLines());
        menu.add(menu_drawdebug);

        JMenuItem menu_gc = new JMenuItem(new A_DebugGC());
        menu.add(menu_gc);

        // debug start game, for mocking server connection.
        JMenuItem menu_start = new JMenuItem(new A_StartGame());
        menu.add(menu_start);

        return menu;
    }

    @Override
    public BufferedImage getBackground()
    {
        BufferedImage background = Services.get(IImageService.class).read("background_green.png");
        return background;
    }
}
