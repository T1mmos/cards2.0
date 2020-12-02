package gent.timdemey.cards.services.frame;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.contract.preload.IPreload;
import gent.timdemey.cards.services.contract.preload.PreloadOrder;
import gent.timdemey.cards.services.contract.preload.PreloadOrderType;
import gent.timdemey.cards.services.contract.res.FontResource;
import gent.timdemey.cards.services.contract.res.ImageResource;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;
import gent.timdemey.cards.services.interfaces.IResourceService;
import gent.timdemey.cards.ui.actions.ActionDescriptor;
import gent.timdemey.cards.ui.actions.ActionDescriptors;
import gent.timdemey.cards.ui.actions.IActionService;
import net.miginfocom.swing.MigLayout;

public class FrameService implements IFrameService, IPreload
{  
    private JFrame frame;
    private RootPanel rootPanel;
    private CardPanel cardPanel;
    private TitlePanel titlePanel;
    private Map<PanelDescriptor, JComponent> pDesc2Comps;
    private boolean drawDebug = false;
    private Font titlefont;
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
    public void addPanel(PanelDescriptor pDesc, JComponent comp)
    {        
        if (pDesc2Comps == null)
        {
            pDesc2Comps = new HashMap<>();
        }
        
        cardPanel.add(comp);
        cardPanel.setLayer(comp, pDesc.layer, 0);
        
        comp.setVisible(false);
        pDesc2Comps.put(pDesc, comp);
    }
    
    @Override
    public void showPanel(PanelDescriptor desc)
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        
        // if this panel is not an overlay, hide all the others
        if (!desc.overlay)
        {
            for (PanelDescriptor pd : pDesc2Comps.keySet())
            {
                if (pd == desc)
                {
                    continue;
                }
                
                JComponent cmp = pDesc2Comps.get(pd);
                if (cmp.isVisible())
                {
                    cmp.setVisible(false);
                    panelServ.onPanelHidden(pd);
                }
            }           
        }
        
        JComponent comp = pDesc2Comps.get(desc);
        
        if (!comp.isVisible())
        {
            comp.setVisible(true);
            panelServ.onPanelShown(desc);
        }        
    }
    
    @Override
    public void hidePanel(PanelDescriptor desc)
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        
        if (desc.overlay)
        {
            JComponent currComp = pDesc2Comps.get(desc);
            currComp.setVisible(false);
            panelServ.onPanelHidden(desc);
        }
    }
    
    @Override
    public void setDrawDebug(boolean on)
    {
        drawDebug = on;

        for (JComponent comp : pDesc2Comps.values())
        {
            comp.repaint();
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
        BufferedImage background = resServ.getImage("background.png").raw;
        return background;
    }

    @PreloadOrder(order = PreloadOrderType.DEPENDENT)
    @Override
    public void preload()
    {
        IResourceService resServ = Services.get(IResourceService.class);
        IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);
        String fontName = resLocServ.getAppTitleFontFilePath();
        FontResource resp_font = resServ.getFont(fontName);
        titlefont = resp_font.raw.deriveFont(40f);
    }

    @Override
    public void maximize()
    {        
        if (maximized)
        {            
            frame.setBounds(bounds);
        }
        else
        {
            bounds = frame.getBounds();
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);            
        }
        
        maximized = !maximized;
        
        updateTitleMaximizeIcon();
    }
    
    private void updateTitleMaximizeIcon()
    {
        IResourceService resServ = Services.get(IResourceService.class);
        IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);
        
        String filepath = maximized ? resLocServ.getAppMaximizeUndoIconFilePath() : resLocServ.getAppMaximizeIconFilePath();
        
        Image img = resServ.getImage(filepath).raw;
        title_maximize.setIcon(new ImageIcon(img));
    }

    @Override
    public void minimize()
    {
        maximized = false;
        frame.setExtendedState(Frame.ICONIFIED);
        updateTitleMaximizeIcon();
    }
}

