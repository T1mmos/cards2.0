package gent.timdemey.cards.ui.panels.frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.contract.res.FontResource;
import gent.timdemey.cards.services.contract.res.ImageResource;
import gent.timdemey.cards.services.frame.RootPanelMouseListener;
import gent.timdemey.cards.services.frame.TitlePanelMouseListener;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.IResourceCacheService;
import gent.timdemey.cards.services.interfaces.IResourceNameService;
import gent.timdemey.cards.ui.components.swing.JSButton;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.PanelManagerBase;
import net.miginfocom.swing.MigLayout;

public class FramePanelManager extends PanelManagerBase
{
    protected JSLayeredPane framePanel = null;
    private JSLayeredPane frameTitlePanel;
    private JSLayeredPane frameBodyPanel;
    private Font frameTitleFont;
    private JButton title_maximize; 
    private JButton title_unmaximize;
    private Border frameBorder;
    private RootPanelMouseListener rpMouseListener;
    
    private IResourceCacheService _ResourceCacheService;
    private IResourceNameService _ResourceNameService;
    private IActionService _ActionService;
    private FrameBodyPanelResizeListener _ResizeListener;
    private ICardPlugin _CardPlugin;
    
    public FramePanelManager( 
        Container container,
        ICardPlugin cardPlugin,
        IResourceNameService resourceNameService,
        IResourceCacheService resourceCacheService,
        IActionService actionService,
        FrameBodyPanelResizeListener resizeListener)
    {
        super(container);
        
        this._CardPlugin = cardPlugin;
        this._ResourceNameService = resourceNameService;
        this._ResourceCacheService = resourceCacheService;
        this._ActionService = actionService;
        this._ResizeListener = resizeListener;
    }
    
    @Override
    public void preload()
    {        
        String frameTitleFontName = _ResourceNameService.getFilePath(ResourceDescriptors.AppTitleFont);
        FontResource res_frameTitleFont = _ResourceCacheService.getFont(frameTitleFontName);
        frameTitleFont = res_frameTitleFont.raw.deriveFont(40f);
    }
    
    private JButton createFrameButton(ActionDescriptor desc)
    { 
        ActionBase action = _ActionService.getAction(desc);
        
        JSButton button = _JSFactory.createButton(action);
      
        button.setBorder(null);
        button.setRolloverIcon(action.icon_rollover);
        button.setText("");
        button.setFocusable(false);
        
        Dimension dim = new Dimension(24, 24);           
        button.setMinimumSize(dim);
        button.setMaximumSize(dim);
        return button;
    }

    @Override
    public JSLayeredPane createPanel()
    {
        BufferedImage bg = getBackgroundImage();
        framePanel = _JSFactory.createLayeredPane(ComponentTypes.FRAME);
        framePanel.setOpaque(true);
        framePanel.setLayout(new MigLayout("insets 5, gapy 0"));
        framePanel.getDrawer().setBackgroundImage(bg);
        frameBorder = BorderFactory.createLineBorder(Color.gray, 1, false);
        framePanel.setBorder(frameBorder);
        rpMouseListener = _Container.Get(RootPanelMouseListener.class);
        framePanel.addMouseListener(rpMouseListener);    
        framePanel.addMouseMotionListener(rpMouseListener);
        
        frameTitlePanel = _JSFactory.createLayeredPane(ComponentTypes.FRAMETITLE);
        frameTitlePanel.setLayout(new MigLayout("insets 0, hidemode 3"));
        JLabel title_icon = new JLabel(new ImageIcon(getFrameIcons().get(1)));
        JLabel title_text = new JLabel(getFrameTitle());
        JButton title_minimize = createFrameButton(ActionDescriptors.MINIMIZE);         
        title_unmaximize = createFrameButton(ActionDescriptors.UNMAXIMIZE);
        title_maximize = createFrameButton(ActionDescriptors.MAXIMIZE);
        JButton title_close = createFrameButton(ActionDescriptors.QUIT);
                    
        title_text.setFont(frameTitleFont);
        title_text.setForeground(Color.darkGray.darker());
        frameTitlePanel.add(title_icon, "gapx 10, left");
        frameTitlePanel.add(title_text, "gapx 10, pushx, left");
        frameTitlePanel.add(title_minimize, "align center top, gapright 5");
        frameTitlePanel.add(title_maximize, "align center top, gapright 5");
        frameTitlePanel.add(title_unmaximize, "align center top, gapright 5");
        frameTitlePanel.add(title_close, "align center top");
        TitlePanelMouseListener tpMouseListener = _Container.Get(TitlePanelMouseListener.class);
        frameTitlePanel.addMouseListener(tpMouseListener);
        frameTitlePanel.addMouseMotionListener(tpMouseListener);
        framePanel.add(frameTitlePanel, "pushx, grow, wrap");
        
        title_unmaximize.setVisible(false);
        
        frameBodyPanel = _JSFactory.createLayeredPane(ComponentTypes.FRAMEBODY);
        frameBodyPanel.addComponentListener(_ResizeListener);
        framePanel.add(frameBodyPanel, "push, grow");
        
        // add global actions
        for (ActionDescriptor ad : getFrameActions())
        {
            ActionBase action = _ActionService.getAction(ad);
            KeyStroke keyStroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
            if (keyStroke != null)
            {
                framePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, ad.id);
                framePanel.getActionMap().put(ad.id, action);
            }
        }
        
        return framePanel;
    }

    @Override
    public JSLayeredPane getPanel()
    {
        return framePanel;
    }

    @Override
    public void destroyPanel()
    {
        // TODO Auto-generated method stub
        
    }

    protected BufferedImage getBackgroundImage()
    {
        String bgpath = _ResourceNameService.getFilePath(ResourceDescriptors.AppBackground);
        BufferedImage background = _ResourceCacheService.getImage(bgpath).raw;
        return background;
    }

    protected List<ActionDescriptor> getFrameActions()
    {
        return Arrays.asList(
            ActionDescriptors.DEBUGDRAW,
            ActionDescriptors.TOGGLEMENUMP,
            ActionDescriptors.QUIT);
    }

    public List<Image> getFrameIcons()
    {
        List<Image> images = new ArrayList<>();

        for (int dim : new int []{16,24,48,140})
        {            
            ImageResource resp = _ResourceCacheService.getImage(_ResourceNameService.getFilePath(ResourceDescriptors.AppIcon, dim, dim));
            images.add(resp.raw);
        }

        return images;
    }

    public String getFrameTitle()
    {
        String title = _CardPlugin.getName();
        return title;
    }    
    
    public void setMaximized(boolean maximized)
    {
        if (maximized)
        {
            framePanel.removeMouseListener(rpMouseListener);    
            framePanel.removeMouseMotionListener(rpMouseListener);
            framePanel.setBorder(null);
        }
        else
        {
            framePanel.addMouseListener(rpMouseListener);    
            framePanel.addMouseMotionListener(rpMouseListener);
            framePanel.setBorder(frameBorder);
        }
                
        title_maximize.setVisible(!maximized);
        title_unmaximize.setVisible(maximized);
    }
    
    public JLayeredPane getFrameBodyPanel()
    {
        return frameBodyPanel;
    }
}
