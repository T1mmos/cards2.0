package gent.timdemey.cards.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.alee.laf.WebLookAndFeel;
import com.google.common.base.Preconditions;

import gent.timdemey.cards.App;
import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.IFrameService;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.dialogs.DialogService;
import gent.timdemey.cards.services.frame.FrameService;
import gent.timdemey.cards.ui.actions.ActionService;
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
            App.services.install(IDialogService.class, dialogService);
        }
        if (!Services.isInstalled(IActionService.class))
        {
            IActionService actionService = new ActionService();
            App.services.install(IActionService.class, actionService);
        }
        if (!Services.isInstalled(IFrameService.class))
        {
            IFrameService frameServ = new FrameService();
            App.services.install(IFrameService.class, frameServ);
        }
    }

    public static void StartUI(ICardPlugin plugin)
    {
        SwingUtilities.invokeLater(() -> {
            WebLookAndFeel.install();

            Services.get(IContextService.class).initialize(ContextType.UI);

            IFrameService frameServ = Services.get(IFrameService.class);
            JMenuBar menuBar = frameServ.getMenuBar(plugin);
            List<Image> images = frameServ.getFrameIcons();
            BufferedImage background = frameServ.getBackground();

            frame.setJMenuBar(menuBar);
            frame.setTitle(String.format("%s v%d.%d", plugin.getName(), plugin.getMajorVersion(), plugin.getMinorVersion()));
            frame.setSize(new Dimension(800, 600));
            frame.setMinimumSize(new Dimension(300, 200));
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(new BackgroundPanel(background));
            frame.setIconImages(images);
            frame.setVisible(true);

            Services.get(IContextService.class).getThreadContext().addStateListener(new GameBootListener(frame));
        });
    }
}
