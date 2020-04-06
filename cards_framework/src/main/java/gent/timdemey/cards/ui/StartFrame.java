package gent.timdemey.cards.ui;

import java.awt.Dimension;
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
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.alee.laf.WebLookAndFeel;
import com.google.common.base.Preconditions;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.services.IConfigManager;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.IImageService;
import gent.timdemey.cards.services.configman.ConfigKey;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.dialogs.DialogService;
import gent.timdemey.cards.ui.actions.ActionCreateGame;
import gent.timdemey.cards.ui.actions.ActionDebugDrawDebugLines;
import gent.timdemey.cards.ui.actions.ActionDebugGC;
import gent.timdemey.cards.ui.actions.ActionJoinGame;
import gent.timdemey.cards.ui.actions.ActionLeaveGame;
import gent.timdemey.cards.ui.actions.ActionQuitGame;
import gent.timdemey.cards.ui.actions.ActionRedo;
import gent.timdemey.cards.ui.actions.ActionService;
import gent.timdemey.cards.ui.actions.ActionStartGame;
import gent.timdemey.cards.ui.actions.ActionStopGame;
import gent.timdemey.cards.ui.actions.ActionUndo;
import gent.timdemey.cards.ui.actions.IActionService;

public class StartFrame
{
    private static JFrame frame;

    private StartFrame()
    {
    }

    public static void installUiServices()
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());

        frame = new JFrame();
        if (!Services.isInstalled(IDialogService.class))
        {
            IDialogService dialogService = new DialogService(frame);
            Services.install(IDialogService.class, dialogService);
        }
        if (!Services.isInstalled(IActionService.class))
        {
            IActionService actionService = new ActionService();
            Services.install(IActionService.class, actionService);
        }
    }

    public static void StartUI(ICardPlugin plugin)
    {
        SwingUtilities.invokeLater(() -> {
            WebLookAndFeel.install();

            Services.get(IContextService.class).initialize(ContextType.UI);

            buildMenu(frame, plugin);

            BufferedImage background = Services.get(IImageService.class).read("background_green.png");
            frame.setTitle(
                    String.format("%s v%d.%d", plugin.getName(), plugin.getMajorVersion(), plugin.getMinorVersion()));
            frame.setSize(new Dimension(800, 600));
            frame.setMinimumSize(new Dimension(300, 200));
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(new BackgroundPanel(background));
            frame.setIconImages(getFrameIcons());
            frame.setVisible(true);

            Services.get(IContextService.class).getThreadContext().addStateListener(new GameBootListener(frame));
        });
    }

    private static List<Image> getFrameIcons()
    {
        List<Image> images = new ArrayList<>();

        IImageService imageService = Services.get(IImageService.class);
        images.add(imageService.read("icon_spade_16x16.png"));
        images.add(imageService.read("icon_spade_24x24.png"));
        images.add(imageService.read("icon_spade_48x48.png"));
        images.add(imageService.read("icon_spade_140x140.png"));

        return images;
    }

    private static void buildMenu(JFrame frame, ICardPlugin plugin)
    {
        JMenuBar menuBar = new JMenuBar();

        // start menu
        {
            JMenu menu = new JMenu(Loc.get("menu_game"));

            if (plugin.getPlayerCount() > 1)
            {
                JMenuItem menu_create = new JMenuItem(new ActionCreateGame());
                menu.add(menu_create);

                JMenuItem menu_join = new JMenuItem(new ActionJoinGame());
                menu.add(menu_join);

                JMenuItem menu_leave = new JMenuItem(new ActionLeaveGame());
                menu.add(menu_leave);
            }
            else
            {
                JMenuItem menu_start = new JMenuItem(new ActionStartGame());
                menu.add(menu_start);

                JMenuItem menu_stop = new JMenuItem(new ActionStopGame());
                menu.add(menu_stop);
            }

            menu.addSeparator();

            JMenuItem menu_quit = new JMenuItem(new ActionQuitGame());
            menu.add(menu_quit);

            menuBar.add(menu);
        }

        // Edit menu
        {
            JMenu menu = new JMenu(Loc.get("menu_edit"));

            Action actionUndo = new ActionUndo();
            JMenuItem menu_undo = new JMenuItem(actionUndo);
            menu_undo.setAccelerator(KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            menu.add(menu_undo);

            Action actionRedo = new ActionRedo();
            JMenuItem menu_redo = new JMenuItem(actionRedo);
            menu_redo.setAccelerator(KeyStroke.getKeyStroke('Y', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            menu.add(menu_redo);

            menuBar.add(menu);
        }

        // Debug menu
        if (Services.get(IConfigManager.class).get(ConfigKey.DEBUG))
        {
            JMenu menu = new JMenu(Loc.get("debug_menu_debug"));

            JMenuItem menu_drawdebug = new JCheckBoxMenuItem(new ActionDebugDrawDebugLines());
            menu.add(menu_drawdebug);

            JMenuItem menu_gc = new JMenuItem(new ActionDebugGC());
            menu.add(menu_gc);

            // debug start game, for mocking server connection.
            JMenuItem menu_start = new JMenuItem(new ActionStartGame());
            menu.add(menu_start);

            menuBar.add(menu);
        }

        frame.setJMenuBar(menuBar);
    }

}
