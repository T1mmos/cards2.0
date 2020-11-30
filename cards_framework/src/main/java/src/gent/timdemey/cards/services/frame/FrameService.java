package gent.timdemey.cards.services.frame;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.services.configman.ConfigKey;
import gent.timdemey.cards.services.contract.ImageResource;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.interfaces.IConfigManager;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;
import gent.timdemey.cards.services.interfaces.IResourceService;
import gent.timdemey.cards.ui.RootPanel;
import gent.timdemey.cards.ui.actions.ActionDef;
import gent.timdemey.cards.ui.actions.Actions;
import gent.timdemey.cards.ui.actions.IActionFactory;
import net.miginfocom.swing.MigLayout;

public class FrameService implements IFrameService
{  
    private JFrame frame;
    private RootPanel rootPanel;
    private Map<PanelDescriptor, JComponent> panels;
    private PanelDescriptor currPanelDesc;
    private boolean drawDebug = false;
    
    @Override
    public JFrame getFrame()
    {
        if (frame == null)
        {
            ICardPlugin plugin = Services.get(ICardPlugin.class);
            
            frame = new JFrame();
            
            BufferedImage bg = getBackgroundImage();
            rootPanel = new RootPanel(bg);
            rootPanel.setLayout(new MigLayout("insets 0"));
            frame.setContentPane(rootPanel);
            
            JMenuBar menuBar = getMenuBar(plugin);
            frame.setJMenuBar(menuBar);
            
            frame.setTitle(String.format("%s v%d.%d", plugin.getName(), plugin.getMajorVersion(), plugin.getMinorVersion()));
            frame.setSize(new Dimension(800, 600));
            frame.setMinimumSize(new Dimension(300, 200));
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setIconImages(getFrameIcons());
        }
        
        return frame;
    }

    @Override
    public void addPanel(PanelDescriptor pDesc, JComponent comp)
    {        
        if (panels == null)
        {
            panels = new HashMap<>();
        }
        
        rootPanel.add(comp, "push, grow, hidemode 3");
        rootPanel.setLayer(comp, pDesc.layer, 0);
        
        comp.setVisible(false);
        panels.put(pDesc, comp);
    }
    
    @Override
    public void setPanel(PanelDescriptor desc)
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        
        if (currPanelDesc != null)
        {
            JComponent currComp = panels.get(currPanelDesc);
            currComp.setVisible(false);
            panelServ.onPanelHidden(currPanelDesc);
        }
        
        JComponent comp = panels.get(desc);
        comp.setVisible(true);
        currPanelDesc = desc;
        
        panelServ.onPanelShown(desc);
    }

    @Override
    public PanelDescriptor getCurrentPanel()
    {
        return currPanelDesc;
    }
    
    @Override
    public void setDrawDebug(boolean on)
    {
        drawDebug = on;

        JComponent comp = panels.get(currPanelDesc);        
        comp.repaint();        
    }

    @Override
    public boolean getDrawDebug()
    {
        return drawDebug;
    }
    
    protected List<Image> getFrameIcons()
    {
        List<Image> images = new ArrayList<>();

        IResourceService resServ = Services.get(IResourceService.class);
        IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);
        for (int dim : new int []{16,24,48,140})
        {            
            ImageResource resp = resServ.getImage(resLocServ.getAppIconFilePath(dim));
            images.add(resp.raw);
        }

        return images;
    }

    protected JMenuBar getMenuBar(ICardPlugin plugin)
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

    protected BufferedImage getBackgroundImage()
    {
        IResourceService resServ = Services.get(IResourceService.class);
        BufferedImage background = resServ.getImage("background.png").raw;
        return background;
    }
}

