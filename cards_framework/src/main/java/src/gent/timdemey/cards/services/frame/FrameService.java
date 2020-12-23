package gent.timdemey.cards.services.frame;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.descriptors.DataPanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.contract.res.FontResource;
import gent.timdemey.cards.services.contract.res.ImageResource;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;
import gent.timdemey.cards.services.interfaces.IResourceService;
import gent.timdemey.cards.services.panels.IDataPanelManager;
import gent.timdemey.cards.services.panels.IPanelManager;
import gent.timdemey.cards.services.panels.PanelInData;
import gent.timdemey.cards.services.panels.PanelOutData;
import gent.timdemey.cards.ui.actions.ActionDescriptor;
import gent.timdemey.cards.ui.actions.ActionDescriptors;
import gent.timdemey.cards.ui.actions.IActionService;
import net.miginfocom.swing.MigLayout;

public class FrameService implements IFrameService
{  
    private JFrame frame;
    private RootPanel rootPanel;
    private CardPanel cardPanel;
    private TitlePanel titlePanel;
    private boolean drawDebug = false;
    private boolean maximized;
    private Rectangle bounds;
    private JButton title_minimize;
    private JButton title_maximize; 
    private JButton title_close;
    
    private JButton createFrameButton(ActionDescriptor desc)
    {
        IActionService actServ = Services.get(IActionService.class);     
        Action act_minimize = actServ.getAction(desc);
        
        JButton button = new JButton(act_minimize);
        Dimension dim = new Dimension(24, 24);
        
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);        
        button.setMinimumSize(dim);
        button.setMaximumSize(dim);
        return button;
    }
    
    @Override
    public JFrame getFrame()
    {
        if (frame == null)
        {
            IResourceService resServ = Services.get(IResourceService.class);
            IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);
            String fontName = resLocServ.getAppTitleFontFilePath();
            FontResource resp_font = resServ.getFont(fontName);
            Font titlefont = resp_font.raw.deriveFont(40f);
            
            frame = new JFrame();
                                    
            BufferedImage bg = getBackgroundImage();
            rootPanel = new RootPanel(bg);
            rootPanel.setLayout(new MigLayout("insets 5, gapy 0"));
            RootPanelMouseListener rpMouseListener = new RootPanelMouseListener();
            rootPanel.addMouseListener(rpMouseListener);    
            rootPanel.addMouseMotionListener(rpMouseListener);
            frame.setContentPane(rootPanel);
            
            titlePanel = new TitlePanel();
            titlePanel.setLayout(new MigLayout("insets 0"));
            titlePanel.setOpaque(false);
            JLabel title_icon = new JLabel(new ImageIcon(getFrameIcons().get(1)));
            JLabel title_text = new JLabel(getTitle());
            title_minimize = createFrameButton(ActionDescriptors.ad_minimize);
            title_maximize = createFrameButton(ActionDescriptors.ad_maximize);
            title_close = createFrameButton(ActionDescriptors.ad_quit);
            
            title_minimize.setText("");
            title_maximize.setText("");
            title_close.setText("");
            
            title_text.setFont(titlefont);
            title_text.setForeground(Color.darkGray.darker());
            titlePanel.add(title_icon, "gapx 10, left");
            titlePanel.add(title_text, "gapx 10, pushx, left");
            titlePanel.add(title_minimize, "align right, gapright 5");
            titlePanel.add(title_maximize, "align right, gapright 5");
            titlePanel.add(title_close, "align right, gapright 5");
            TitlePanelMouseListener tpMouseListener = new TitlePanelMouseListener();
            titlePanel.addMouseListener(tpMouseListener);
            titlePanel.addMouseMotionListener(tpMouseListener);
            rootPanel.add(titlePanel, "pushx, grow, wrap");
            
            cardPanel = new CardPanel();
            cardPanel.setLayout(new CardLayout());
            cardPanel.addComponentListener(new CardPanelResizeListener());
            rootPanel.add(cardPanel, "push, grow");
                        
            frame.setTitle(getTitle());
            frame.setUndecorated(true);
            frame.setSize(new Dimension(800, 600));
            frame.setMinimumSize(new Dimension(400, 200));
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setIconImages(getFrameIcons());
        }
        
        return frame;
    }

    @Override
    public void addPanel(PanelDescriptor desc)
    {        
        IPanelService panelServ = Services.get(IPanelService.class);
        IPanelManager panelMgr = panelServ.getPanelManager(desc);
        
        JComponent comp = panelMgr.create();
        if (comp.getParent() != null)
        {
            throw new IllegalStateException("The component to add already has a parent!");
        }

        // by default the panel is not visible
        comp.setVisible(false);
        
        cardPanel.add(comp);
        cardPanel.setLayer(comp, desc.layer, 0);
    }
    
    @Override
    public void removePanel(PanelDescriptor desc)
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        IPanelManager panelMgr = panelServ.getPanelManager(desc);
        
        JComponent comp = panelMgr.get();
        if (comp.getParent() != cardPanel)
        {
            throw new IllegalStateException("The component is not a child of the card panel");
        }
        
        cardPanel.remove(comp);        
    }
    
    @Override
    public void showPanel(PanelDescriptor desc)
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        IPanelManager panelMgr = panelServ.getPanelManager(desc);
        
        // grab the component    
        JComponent comp = panelMgr.get();
        
        // hide other panels is this panel is not just a (transparent) overlay
        if (!desc.overlay)
        {
            List<PanelDescriptor> panelDescs = panelServ.getPanelDescriptors();
            for (PanelDescriptor pd : panelDescs)
            {
                // don't hide the panel that needs to show
                if (pd == desc)
                {                                       
                    continue;
                }
                
                IPanelManager pMan = panelServ.getPanelManager(pd);
                
                // if the panel doesn't exist, it was never added to the card layout
                if (!pMan.isCreated())
                {
                    continue;
                }
                
                // hide the component
                JComponent cmp = pMan.get();
                if (cmp.isVisible())
                {
                    cmp.setVisible(false);
                    pMan.onHidden();
                }                
            }
        }
        
        // set the visibility of the panel if necessary
        if (!comp.isVisible())
        {
            comp.setVisible(true);            
            panelMgr.onShown();
        }
    }

    
    @Override
    public <IN, OUT> PanelOutData<OUT> showPanel(DataPanelDescriptor<IN, OUT> desc, PanelInData<IN> data)
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        IDataPanelManager<IN, OUT> panelMgr = panelServ.getPanelManager(desc);
        
        panelMgr.onCreating(data);
        
       // panelMgr.getButtonTypes()
        
      //  panelMgr.ge
        return null;
    }
    
    @Override
    public void hidePanel(PanelDescriptor desc)
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        
        // if hiding a non-overlay, we don't know what to show, so we prohibit it
        if (!desc.overlay)
        {
            throw new IllegalArgumentException("You can only hide panels that are overlays");
        }

        // the panel should exist
        IPanelManager panelMgr = panelServ.getPanelManager(desc);        
        if (!panelMgr.isCreated())
        {
            throw new IllegalArgumentException("Attempted to hide a panel which has not been created yet");
        }
        
        // hide the panel
        JComponent comp = panelMgr.get();
        comp.setVisible(false);
        panelMgr.onHidden();
    }
    
    @Override
    public void setDrawDebug(boolean on)
    {
        drawDebug = on;
        
        IPanelService panelServ = Services.get(IPanelService.class);
        List<PanelDescriptor> pDescs = panelServ.getPanelDescriptors();
        
        for (PanelDescriptor pd : pDescs)
        {
            IPanelManager pMan = panelServ.getPanelManager(pd);
            if (pMan.isCreated())
            {
                JComponent comp = pMan.get();
                comp.repaint();
            }
        }
    }

    @Override
    public boolean getDrawDebug()
    {
        return drawDebug;
    }
    
    protected String getTitle()
    {
        ICardPlugin plugin = Services.get(ICardPlugin.class);
        String title = plugin.getName();
        return title;
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

    protected BufferedImage getBackgroundImage()
    {
        IResourceService resServ = Services.get(IResourceService.class);
        IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);
        String bgpath = resLocServ.getAppBackgroundImageFilePath();
        BufferedImage background = resServ.getImage(bgpath).raw;
        return background;
    }

    @Override
    public void maximize()
    {        
        if (maximized)
        {            
            setBounds(bounds);
        }
        else
        {
            bounds = frame.getBounds();
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);            
        }
        
        setMaximized(!maximized);        
    }

    @Override
    public void unmaximize()
    {
        if (maximized)
        {            
            setBounds(bounds);
            setMaximized(false);
        }
    }
    
    private void setMaximized(boolean maximized)
    {
        this.maximized = maximized;

        IResourceService resServ = Services.get(IResourceService.class);
        IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);
        
        String filepath = maximized ? resLocServ.getAppMaximizeUndoIconFilePath() : resLocServ.getAppMaximizeIconFilePath();
        
        Image img = resServ.getImage(filepath).raw;
        title_maximize.setIcon(new ImageIcon(img));
    }
    
    @Override
    public void minimize()
    {
        frame.setExtendedState(Frame.ICONIFIED);
        setMaximized(false);
    }

    @Override
    public void setLocation(int x, int y)
    {
        frame.setLocation(x, y);
    }

    private void setBounds(Rectangle bounds)
    {
        setBounds(bounds.x, bounds.y, bounds.width, bounds.height);        
    }
    
    @Override
    public void setBounds(int x, int y, int w, int h)
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        if (x < 0)
        {
            x = 0;
        }
        if (y < 0)
        {
            y = 0;
        }
        if (x + w >= screenSize.width)
        {
            x = screenSize.width - w;
        }
        if (y + h >= screenSize.height)
        {
            y = screenSize.height - h;
        }
        
        bounds = new Rectangle(x, y, w, h);
        frame.setBounds(bounds);
    }

    @Override
    public void updatePositionService()
    {
        IPositionService posServ = Services.get(IPositionService.class);
        posServ.setMaxSize(cardPanel.getWidth(), cardPanel.getHeight());
    }
}

