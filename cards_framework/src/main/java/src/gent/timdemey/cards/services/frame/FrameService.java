package gent.timdemey.cards.services.frame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import com.alee.laf.button.WebButton;
import com.alee.managers.style.StyleId;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.contract.SnapSide;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.DataPanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelType;
import gent.timdemey.cards.services.contract.preload.IPreload;
import gent.timdemey.cards.services.contract.preload.PreloadOrder;
import gent.timdemey.cards.services.contract.preload.PreloadOrderType;
import gent.timdemey.cards.services.contract.res.FontResource;
import gent.timdemey.cards.services.contract.res.ImageResource;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;
import gent.timdemey.cards.services.interfaces.IResourceService;
import gent.timdemey.cards.services.panels.IDataPanelManager;
import gent.timdemey.cards.services.panels.IPanelManager;
import gent.timdemey.cards.services.panels.PanelBase;
import gent.timdemey.cards.services.panels.PanelButtonType;
import gent.timdemey.cards.services.panels.PanelInData;
import gent.timdemey.cards.services.panels.PanelOutData;
import gent.timdemey.cards.services.panels.dialogs.message.MessagePanelData;
import net.miginfocom.swing.MigLayout;

public class FrameService implements IFrameService, IPreload
{  
    private JFrame frame;
    private RootPanel framePanel;
    private JPanel frameTitlePanel;
    private JLayeredPane frameBodyPanel;
    private JButton title_maximize; 
    private JButton title_unmaximize;
    private Font frameTitleFont;
    private Font dialogTitleFont;
    
    private boolean drawDebug = false;

    private Rectangle prevBounds;
    private List<SnapSide> snaps = null;
    private Border frameBorder;
    private RootPanelMouseListener rpMouseListener;
    
    private static class PanelTracker
    {
        private final PanelDescriptor panelDesc;
        private final PanelBase panel;
        
        private PanelTracker(PanelDescriptor panelDesc, PanelBase panel)
        {
            this.panelDesc = panelDesc;
            this.panel = panel;
        }
    }
    
    private PanelTracker rootPanelTracker = null;
    private Stack<PanelTracker> dialogPanelTrackers = new Stack<>();
    private Stack<PanelTracker> overlayPanelTrackers  = new Stack<>();
        
    private JButton createFrameButton(ActionDescriptor desc)
    {
        IActionService actServ = Services.get(IActionService.class);     
        ActionBase action = actServ.getAction(desc);
        
        WebButton button = new WebButton(StyleId.buttonUndecorated, action);
        button.setRolloverIcon(action.icon_rollover);
        button.setText("");
        
        Dimension dim = new Dimension(24, 24);           
        button.setMinimumSize(dim);
        button.setMaximumSize(dim);
        return button;
    }
    
    @PreloadOrder(order = PreloadOrderType.DEPENDENT)
    public void preload()
    {
        IResourceService resServ = Services.get(IResourceService.class);
        IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);
        
        String frameTitleFontName = resLocServ.getAppTitleFontFilePath();
        FontResource res_frameTitleFont = resServ.getFont(frameTitleFontName);
        frameTitleFont = res_frameTitleFont.raw.deriveFont(40f);
        
        String dialogTitleFontName = resLocServ.getDialogTitleFontFilePath();
        FontResource res_dialogTitleFont = resServ.getFont(dialogTitleFontName);
        dialogTitleFont = res_dialogTitleFont.raw.deriveFont(20f);
    }
    
    @Override
    public JFrame getFrame()
    {
        if (frame == null)
        {
            frame = new JFrame();
                                    
            BufferedImage bg = getBackgroundImage();
            framePanel = new RootPanel(bg);
            frameBorder = BorderFactory.createLineBorder(Color.gray, 1, false);
            framePanel.setBorder(frameBorder);
            framePanel.setLayout(new MigLayout("insets 5, gapy 0"));
            rpMouseListener = new RootPanelMouseListener();
            framePanel.addMouseListener(rpMouseListener);    
            framePanel.addMouseMotionListener(rpMouseListener);
            frame.setContentPane(framePanel);
            
            frameTitlePanel = new JPanel();
            frameTitlePanel.setLayout(new MigLayout("insets 0, hidemode 3"));
            frameTitlePanel.setOpaque(false);
            JLabel title_icon = new JLabel(new ImageIcon(getFrameIcons().get(1)));
            JLabel title_text = new JLabel(getTitle());
            JButton title_minimize = createFrameButton(ActionDescriptors.ad_minimize);         
            title_unmaximize = createFrameButton(ActionDescriptors.ad_unmaximize);
            title_maximize = createFrameButton(ActionDescriptors.ad_maximize);
            JButton title_close = createFrameButton(ActionDescriptors.ad_quit);
                        
            title_text.setFont(frameTitleFont);
            title_text.setForeground(Color.white.darker());
            frameTitlePanel.add(title_icon, "gapx 10, left");
            frameTitlePanel.add(title_text, "gapx 10, pushx, left");
            frameTitlePanel.add(title_minimize, "align center top, gapright 5");
            frameTitlePanel.add(title_maximize, "align center top, gapright 5");
            frameTitlePanel.add(title_unmaximize, "align center top, gapright 5");
            frameTitlePanel.add(title_close, "align center top");
            TitlePanelMouseListener tpMouseListener = new TitlePanelMouseListener();
            frameTitlePanel.addMouseListener(tpMouseListener);
            frameTitlePanel.addMouseMotionListener(tpMouseListener);
            framePanel.add(frameTitlePanel, "pushx, grow, wrap");
            
            title_unmaximize.setVisible(false);
            
            frameBodyPanel = new JLayeredPane();
            frameBodyPanel.setLayout(new MigLayout());
            frameBodyPanel.addComponentListener(new FrameBodyPanelResizeListener());
            framePanel.add(frameBodyPanel, "push, grow");
                        
            frame.setTitle(getTitle());
            frame.setUndecorated(true);
            frame.setSize(new Dimension(800, 600));
            frame.setMinimumSize(new Dimension(400, 200));
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            frame.setIconImages(getFrameIcons());
            
            // add global actions
            IActionService actServ = Services.get(IActionService.class);
            for (ActionDescriptor ad : getFrameActions())
            {
                ActionBase action = actServ.getAction(ad);
                KeyStroke keyStroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
                if (keyStroke != null)
                {
                    framePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, ad.id);
                    framePanel.getActionMap().put(ad.id, action);
                }
            }
        }
        
        return frame;
    }

    
    private PanelBase get(PanelDescriptor desc)
    {
        for (Component comp : frameBodyPanel.getComponents())
        {
            if (comp instanceof PanelBase)
            {
                PanelBase pb = (PanelBase) comp;
                if (pb.panelDesc == desc)
                {
                    return pb;
                }
            }
        }
        
        return null;
    }
    
    @Override
    public void removePanel(PanelDescriptor desc)
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        IPanelManager panelMgr = panelServ.getPanelManager(desc);
        
        // check that the component is actually part of the frame body panel
        JComponent contentComp = panelMgr.getPanel();
        JComponent directChild;
        if (desc.panelType == PanelType.Root)
        {
            directChild = contentComp;
        }
        else 
        {
            // dialog content panels are multiwrapped in other panels
            directChild = (JComponent) contentComp.getParent().getParent();
        }
        if (directChild.getParent() != frameBodyPanel)
        {
            throw new IllegalStateException("The component is not part of the hierarchy of the frame body panel");
        }        
        
        directChild.setVisible(false);
        panelMgr.onHidden();
        
        frameBodyPanel.remove(directChild);        
        //frameBodyPanel.repaint();
    }
    
    @Override
    public boolean isShown(PanelDescriptor desc)
    {
        PanelBase pb = get(desc);
        return pb != null;
    }
    
    @Override
    public void showPanel(PanelDescriptor desc)
    {        
        if (desc.panelType != PanelType.Root)
        {
            throw new IllegalArgumentException("This method is intended for Root panels only");
        }
        
        IPanelService panelServ = Services.get(IPanelService.class);
        IPanelManager panelMgr = panelServ.getPanelManager(desc);
                  
        // check that the given panel descriptor is a root panel
        PanelBase pb = get(desc);        
        
        boolean add = false;
        if (pb == null)
        {
            add = true;
            pb = panelMgr.createPanel();
            if (pb.getParent() != null)
            {
                throw new IllegalStateException("The component to add already has a parent!");
            }
        }      
        
        showInternal(desc, pb, add);
        
        refresh();
    }
    
    @Override
    public <IN, OUT> void showPanel(DataPanelDescriptor<IN, OUT> desc, IN data, Consumer<PanelOutData<OUT>> onClose)
    {                
        if (desc.panelType != PanelType.Dialog && desc.panelType != PanelType.DialogOverlay)
        {
            throw new IllegalArgumentException("This method is intended for Dialog / DialogOverlay panels only");
        }
        
        PanelBase pb = createDialogPanel(desc, data, onClose);
        
        showInternal(desc, pb, true);
        
        refresh();
    }
        
    private void showInternal(PanelDescriptor desc, PanelBase pb, boolean add)
    {        
        IPanelService panelServ = Services.get(IPanelService.class);
        IPanelManager panelMgr = panelServ.getPanelManager(desc);
        
        // add it to the miglayout
        if (add)
        {
            int layer = 0;
            if (desc.panelType == PanelType.Root)
            {
                layer = 0;
            }
            else if (desc.panelType == PanelType.Dialog)
            {
                layer = 100 + dialogPanelTrackers.size();
            }            
            else if (desc.panelType == PanelType.Overlay)
            {
                layer = 300 + overlayPanelTrackers.size();
            }
            else if (desc.panelType == PanelType.DialogOverlay)
            {
                layer = 400 + dialogPanelTrackers.size();
            }
            else
            {
                throw new IllegalArgumentException("Unsupported PanelType: " + desc.panelType);
            }
            
            frameBodyPanel.add(pb, "pos 0 0 100% 100%");
            frameBodyPanel.setLayer(pb, layer);
            frameBodyPanel.invalidate();
            frameBodyPanel.validate();
        }
                
        // in case of a root panel, all open dialogPanelTrackers, overlayPanelTrackers, and current panel are closed.
        // do not close persistent dialogPanelTrackers
        if (desc.panelType == PanelType.Root)
        {
            while (!dialogPanelTrackers.empty())
            {
                closePanelInternal(PanelType.Dialog);
            }
            while (!overlayPanelTrackers.empty())
            {
                closePanelInternal(PanelType.Overlay);
            }
            if (rootPanelTracker != null)
            {
                closePanelInternal(PanelType.Root);
            }
            rootPanelTracker = new PanelTracker(desc, pb);
        }
        else if (desc.panelType == PanelType.Dialog || desc.panelType == PanelType.DialogOverlay)
        {
            // hide the root panel or dialog below this one
            if (desc.panelType == PanelType.Dialog)
            {
                if (dialogPanelTrackers.size() == 0)
                {
                    rootPanelTracker.panel.setVisible(false);
                }
                else
                {
                    PanelTracker below = dialogPanelTrackers.peek();
                    below.panel.setVisible(false);
                    panelServ.getPanelManager(below.panelDesc).onHidden();
                }
            }            
            
            dialogPanelTrackers.push(new PanelTracker(desc, pb));
        }
        else if (desc.panelType == PanelType.Overlay)
        {
            overlayPanelTrackers.push(new PanelTracker(desc, pb));
        }
        
        // show the panel and notify its manager
        panelMgr.getPanel().setVisible(true);
        panelMgr.onShown();
    }    
    
    public void closePanel(PanelType panelType)
    {
        if (panelType == PanelType.Root)
        {
            throw new IllegalArgumentException("Not allowed to close a panel of type " + panelType + ", as there always need to be exactly one visible");
        }
        
        closePanelInternal(panelType);
        refresh();
    }
    
    private void refresh()
    {
        frameBodyPanel.invalidate();
        frameBodyPanel.validate();
        frameBodyPanel.repaint();
    }
    
    private void closePanelInternal(PanelType panelType)
    {
        IPanelService panelServ = Services.get(IPanelService.class);

        PanelTracker pt_toClose;
        if ((panelType == PanelType.Dialog || panelType == PanelType.DialogOverlay) && !dialogPanelTrackers.isEmpty())
        {
            pt_toClose = dialogPanelTrackers.pop();
        }
        else if (panelType == PanelType.Overlay && !overlayPanelTrackers.isEmpty())
        {
            pt_toClose = overlayPanelTrackers.pop();
        }
        else if (panelType == PanelType.Root)
        {
            pt_toClose = rootPanelTracker;
        }
        else
        {
            Logger.warn("Attempted to close panel of type %s, but no such panels are open; ignoring the request", panelType);
            return;
        }        
                
        // the panel should exist
        IPanelManager panelMgr = panelServ.getPanelManager(pt_toClose.panelDesc);
        if (!panelMgr.isPanelCreated())
        {
            throw new IllegalArgumentException("Attempted to hide a panel which has not been created yet: " + panelType);
        }
        
        PanelBase pb_toClose = pt_toClose.panel;
        if (!pb_toClose.isVisible())
        {
            throw new IllegalStateException("Attempted to hide a panel which is not visible");
        }
        
        // hide it, remove if panel is of temporary nature
        if (panelType == PanelType.Root)
        {
            pb_toClose.setVisible(false);
            rootPanelTracker = null;
        }
        else 
        {
            frameBodyPanel.remove(pb_toClose);
            
            // make topmost dialog visible again if it was previously hidden which is the
            // case a dialog was on top
            if (panelType == PanelType.Dialog)
            {
                if (!dialogPanelTrackers.isEmpty())
                {
                    PanelTracker topmost = dialogPanelTrackers.peek();
                    topmost.panel.setVisible(true);
                    panelServ.getPanelManager(topmost.panelDesc).onShown();
                }
                else if (rootPanelTracker != null)
                {
                    rootPanelTracker.panel.setVisible(true);
                    panelServ.getPanelManager(rootPanelTracker.panelDesc).onShown();                    
                }
            }
        }
        
        // notify the panel manager
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
            if (pMan.isPanelCreated())
            {
                JComponent comp = pMan.getPanel();
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

    protected List<ActionDescriptor> getFrameActions()
    {
        return Arrays.asList(
            ActionDescriptors.ad_debugdraw,
            ActionDescriptors.ad_togglemenump,
            ActionDescriptors.ad_quit);
    }
    
    protected BufferedImage getBackgroundImage()
    {
        IResourceService resServ = Services.get(IResourceService.class);
        IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);
        String bgpath = resLocServ.getAppBackgroundImageFilePath();
        BufferedImage background = resServ.getImage(bgpath).raw;
        return background;
    }
    
    protected BufferedImage getDialogBackgroundImage()
    {
        IResourceService resServ = Services.get(IResourceService.class);
        IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);
        String bgpath = resLocServ.getDialogBackgroundImageFilePath();
        BufferedImage background = resServ.getImage(bgpath).raw;
        return background;
    }
    
    @Override
    public boolean isSnapped()
    {
        return snaps != null;
    }
    
    @Override
    public boolean isSnapped(SnapSide ... snapsides)
    {
        if (snapsides == null || snapsides.length == 0)
        {
            throw new IllegalArgumentException("snapsides");
        }
        
        List<SnapSide> snapsides_list = Arrays.asList(snapsides);
        
        if (snaps == null)
        {
            return false;
        }
        if (snaps.size() != snapsides.length || !snaps.stream().allMatch(snapsides_list::contains))
        {
            return false;            
        }
        
        return true;
    }
    
    @Override
    public void snap(GraphicsDevice device, SnapSide... snapsides)
    {
        if (device == null)
        {
            throw new IllegalArgumentException("device");
        }
        if (snapsides == null || snapsides.length == 0)
        {
            throw new IllegalArgumentException("snapsides");
        }
        
        List<SnapSide> snapsides_list = Arrays.asList(snapsides);
        
        boolean snapTop = snapsides_list.contains(SnapSide.TOP);
        boolean snapBottom = snapsides_list.contains(SnapSide.BOTTOM);
        boolean snapLeft = snapsides_list.contains(SnapSide.LEFT);
        boolean snapRight = snapsides_list.contains(SnapSide.RIGHT);
        if (snapTop && snapBottom)
        {
            throw new IllegalArgumentException("cannot snap to both TOP and BOTTOM");
        }
        if (snapLeft && snapRight)
        {
            throw new IllegalArgumentException("cannot snap to both LEFT and RIGHT");
        }

        saveBounds();
        Rectangle maxBounds = device.getDefaultConfiguration().getBounds();
        
        if (snapTop && !snapLeft && !snapRight)
        {           
            saveBounds();  
            frame.setBounds(maxBounds);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            setMaximized(true);
            
        }
        else if (snapLeft || snapRight)
        {
            int x, y, w, h;
            if (snapTop) 
            {
                y = maxBounds.y;
                h = maxBounds.height / 2;
            }
            else if (snapBottom)
            {
                y = maxBounds.y + maxBounds.height / 2;
                h = maxBounds.height / 2;
            }
            else
            {
                y = maxBounds.y;
                h = maxBounds.height;
            }
            
            if (snapLeft)
            {
                x = maxBounds.x;
                w = maxBounds.width / 2;
            }
            else // snapRight
            {
                x = maxBounds.x + maxBounds.width / 2;
                w = maxBounds.width / 2;
            }

            setBounds(x, y, w, h);
        }
        
        snaps = snapsides_list;
    }
    
    @Override
    public void snap(SnapSide ... snapsides)
    {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        snap(device, snapsides);
    }

    @Override
    public void unsnap()
    {
        if (snaps == null)
        {            
            throw new IllegalStateException("The frame isn't currently snapped");
        }
        
        snaps = null;
        restoreBounds();
        setMaximized(false);
    }

    private void restoreBounds()
    {
        setBounds(prevBounds);
        prevBounds = null;
    }
    
    private void saveBounds()
    {
        prevBounds = frame.getBounds();
    }
    
    @Override
    public void maximize()
    {        
        if (isSnapped(SnapSide.TOP))
        {
            return;
        }
       
        snap(SnapSide.TOP);           
    }
    
    private void setMaximized(boolean maximized)
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
        frame.setBounds(x, y, w, h);
    }

    @Override
    public void updatePositionService()
    {
        IPositionService posServ = Services.get(IPositionService.class);
        posServ.setMaxSize(frameBodyPanel.getWidth(), frameBodyPanel.getHeight());
    }
    
    @Override
    public void showMessage(String title, String message)
    {
        MessagePanelData payload = new MessagePanelData(title, message);
        showPanel(PanelDescriptors.MESSAGE, payload, c -> {});
    }
    
    @Override
    public void showInternalError()
    {
        String title = Loc.get(LocKey.DialogTitle_generalerror);
        String msg = Loc.get(LocKey.DialogMessage_generalerror);
        MessagePanelData payload = new MessagePanelData(title, msg);
        
        showPanel(PanelDescriptors.MESSAGE, payload, c -> {});
    }    
    
    private <IN, OUT> PanelBase createDialogPanel(DataPanelDescriptor<IN, OUT> desc, IN inData, Consumer<PanelOutData<OUT>> callback)
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        IDataPanelManager<IN, OUT> dpMan = panelServ.getPanelManager(desc);
               
        // create the buttons that need to be shown according to the content creator
        EnumSet<PanelButtonType> dbTypes = dpMan.getButtonTypes();
        Map<PanelButtonType, JButton> buttons = new HashMap<>();   
        
        // function to generate a runnable which is run after the dialog was closed somehow
        Function<PanelButtonType, Runnable> closeFuncSupplier = (btnType) -> () ->
        {
            PanelOutData<OUT> outData = new PanelOutData<>();
            OUT out = dpMan.onClose(btnType);
            outData.closeType = btnType;
            outData.data_out = out;
            closePanel(PanelType.Dialog);
            if (callback != null)
            {
                callback.accept(outData);
            }
        };
        
        // prepare the buttons
        for (PanelButtonType dbType : dbTypes)
        {
            ActionListener action_close = (e) -> closeFuncSupplier.apply(dbType).run();
            
            String loctext = Loc.get(dbType.lockey);
            JButton button = new JButton(loctext); 
            
            button.addActionListener(action_close);            
            buttons.put(dbType, button);            
        }
        
        // prepare the incoming data
        PanelInData<IN> panelInData = new PanelInData<>();
        panelInData.payload = inData;
        panelInData.verifyButtonFunc = (btnType) ->
        {
            JButton button = buttons.get(btnType);
            if (button == null)
            {
                return;
            }
            
            boolean enabled = dpMan.isButtonEnabled(btnType);
            button.setEnabled(enabled);
        }; 
        panelInData.closeFunc = closeFuncSupplier.apply(PanelButtonType.Forced);
        
        // let the dialog panel manager load given the input data
        dpMan.load(panelInData);

        // create the entire panel, add the custom content and the buttons
        PanelBase dialogPanel = new PanelBase(desc);
        dialogPanel.setLayout(new MigLayout("insets 0"));  
        dialogPanel.addMouseListener(new MouseAdapter(){}); // block mouse input to panels below
        JPanel marginPanel = new RootPanel(getDialogBackgroundImage());        
        marginPanel.setLayout(new MigLayout("insets 20"));
        dialogPanel.add(marginPanel, "hmax 400, align center, push, alignx center, aligny center");
        {
            // dialog title
            String title = dpMan.createTitle();
            JLabel titleLabel = new JLabel(" - " + title + " - ");
            titleLabel.setFont(dialogTitleFont);
            marginPanel.add(titleLabel, "gapy 0 20, pushx, alignx center, wrap");
            
            // content panel provided by the manager
            PanelBase contentPanel = dpMan.createPanel();
            marginPanel.add(contentPanel, "grow, push, wrap");
            
            // standard buttons at the bottom as required
            String mig_first = "span, wmin 60, split " + dbTypes.size() + ", pushx, align center, sg buts";
            String mig_sg = "sg buts";
            int cnt = 0;
            for (PanelButtonType dbType : dbTypes)
            {           
                JButton button = buttons.get(dbType);
                marginPanel.add(button, cnt++ == 0 ? mig_first : mig_sg);
                
                // enabled/disable the button
                panelInData.verifyButtonFunc.accept(dbType);
            }
            
            // more dialog customization
            marginPanel.setOpaque(true);
            marginPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        }
        
        dialogPanel.setOpaque(false);
        return dialogPanel;
    }
}

