package gent.timdemey.cards.services.frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JSeparator;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.contract.SnapSide;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.DataPanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelType;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.contract.preload.IPreload;
import gent.timdemey.cards.services.contract.preload.PreloadOrder;
import gent.timdemey.cards.services.contract.preload.PreloadOrderType;
import gent.timdemey.cards.services.contract.res.FontResource;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceCacheService;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;
import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.swing.JSFactory;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.IDataPanelManager;
import gent.timdemey.cards.ui.panels.IPanelManager;
import gent.timdemey.cards.ui.panels.PanelInData;
import gent.timdemey.cards.ui.panels.PanelOutData;
import gent.timdemey.cards.ui.panels.dialogs.message.MessagePanelData;
import gent.timdemey.cards.ui.panels.frame.FramePanelManager;
import gent.timdemey.cards.utils.DimensionUtils;
import net.miginfocom.swing.MigLayout;

public class FrameService implements IFrameService, IPreload
{  
    private JFrame frame;
    private Font dialogTitleFont;
    
    private boolean drawDebug = false;

    private Rectangle prevBounds;
    private List<SnapSide> snaps = null;
    
    private static class PanelTracker
    {
        private final PanelDescriptor panelDesc;
        private final JSLayeredPane panel;
        
        private PanelTracker(PanelDescriptor panelDesc, JSLayeredPane panel)
        {
            this.panelDesc = panelDesc;
            this.panel = panel;
        }
    }
    
    private PanelTracker rootPanelTracker = null;
    private List<PanelTracker> rootPanelTrackers = new ArrayList<>();
    private Stack<PanelTracker> dialogPanelTrackers = new Stack<>();
    private Stack<PanelTracker> overlayPanelTrackers  = new Stack<>();
        
    
    
    @PreloadOrder(order = PreloadOrderType.DEPENDENT)
    public void preload()
    {
        IResourceCacheService resServ = Services.get(IResourceCacheService.class);
        IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);
        
        String dialogTitleFontName = resLocServ.getFilePath(ResourceDescriptors.DialogTitleFont);
        FontResource res_dialogTitleFont = resServ.getFont(dialogTitleFontName);
        dialogTitleFont = res_dialogTitleFont.raw.deriveFont(20f);
    }
    
    @Override
    public JFrame getFrame()
    {
        FramePanelManager fpm = getFramePanelManager();
        String title = fpm.getFrameTitle();
        List<Image> icons = fpm.getFrameIcons();
        
        if (frame == null)
        {
            frame = new JFrame();
            frame.setTitle(title);
            frame.setUndecorated(true);
            frame.pack();
            frame.setMinimumSize(DimensionUtils.getMinimum(frame.getSize(), new Dimension(450, 200)));
            frame.setSize(new Dimension(800, 600));
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            frame.setIconImages(icons);

            IPanelService panelServ = Services.get(IPanelService.class);
            IPanelManager pm = panelServ.getPanelManager(PanelDescriptors.Frame);
            JSLayeredPane content = pm.createPanel();
            frame.setContentPane(content);           
        }
        
        return frame;
    }

    
    private PanelTracker get(PanelDescriptor desc)
    {
       /* for (Component comp : frameBodyPanel.getComponents())
        {
            if (comp instanceof JSLayeredPane)
            {
                JSLayeredPane pb = (JSLayeredPane) comp;
                if (pb.panelDesc == desc)
                {
                    return pb;
                }
            }
        }
        */
        
        List<PanelTracker> list;
        if (desc.panelType == PanelType.Root)
        {
            list = rootPanelTrackers;
        }
        else if (desc.panelType == PanelType.Dialog || desc.panelType == PanelType.DialogOverlay)
        {
            list = dialogPanelTrackers;
        }
        else if (desc.panelType == PanelType.Overlay)
        {
            list = overlayPanelTrackers;
        }
        else
        {
            throw new IllegalArgumentException("Unknown PanelDescriptor: " + desc);
        }
        
        for (PanelTracker pt : list)
        {
            if (pt.panelDesc == desc)
            {
                return pt;
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
        if (desc.panelType == PanelType.Root || desc.panelType == PanelType.Overlay)
        {
            directChild = contentComp;
        }
        else 
        {
            // dialog content panels are multiwrapped in other panels
            directChild = (JComponent) contentComp.getParent().getParent();
        }
        if (directChild.getParent() != getFrameBodyPanel())
        {
            throw new IllegalStateException("The component '"+desc.id+"' ("+desc.panelType+") is not part of the hierarchy of the frame body panel");
        }        
        
        closePanelInternal(desc.panelType);
        
        panelMgr.onHidden();
    }
    
    @Override
    public boolean isShown(PanelDescriptor desc)
    {
        boolean visible = get(desc).panel.isVisible();
        return visible;
    }
    
    @Override
    public void showPanel(PanelDescriptor desc)
    {        
        showPanel(desc, null);
    }
    
    @Override
    public void showPanel(PanelDescriptor desc, Runnable onShown)
    { 
        if (desc.panelType != PanelType.Root && desc.panelType != PanelType.Overlay)
        {
            throw new IllegalArgumentException("This method is intended for Root and Overlay panels only");
        }
        
        IPanelService panelServ = Services.get(IPanelService.class);
        IPanelManager panelMgr = panelServ.getPanelManager(desc);
                  
        PanelTracker pt = get(desc);        
        
        boolean add;
        JSLayeredPane pb;
        if (pt == null)
        {
            add = true;
            pb = panelMgr.createPanel();
            if (pb.getParent() != null)
            {
                throw new IllegalStateException("The component to add already has a parent!");
            }
        }     
        else
        {
            add = false;
            pb = pt.panel;
        }
        
        showInternal(desc, pb, add, onShown);
    }
    
    @Override
    public <IN, OUT> void showPanel(DataPanelDescriptor<IN, OUT> desc, IN data, Consumer<PanelOutData<OUT>> onClose)
    {                
        if (desc.panelType != PanelType.Dialog && desc.panelType != PanelType.DialogOverlay)
        {
            throw new IllegalArgumentException("This method is intended for Dialog / DialogOverlay panels only");
        }
        
        JSLayeredPane pb = createDialogPanel(desc, data, onClose);
        
        showInternal(desc, pb, true, null);
    }
        
    private void showInternal(PanelDescriptor desc, JSLayeredPane comp, boolean add, Runnable onShown)
    {                
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
            
            getFrameBodyPanel().add(comp, "pos 0 0 100% 100%");
            getFrameBodyPanel().setLayer(comp, layer);
            getFrameBodyPanel().invalidate();
            getFrameBodyPanel().validate();
        }
                
        // in case of a root panel, all open dialogPanelTrackers, overlayPanelTrackers, and current panel are closed.
        // do not close persistent dialogPanelTrackers
        PanelTracker pt = null;
        if (desc.panelType == PanelType.Root)
        {
            while (!dialogPanelTrackers.empty())
            {
                closePanelInternal(PanelType.Dialog);
            }
         /*   while (!overlayPanelTrackers.empty())
            {
                closePanelInternal(PanelType.Overlay);
            }*/
            if (rootPanelTracker != null)
            {
                closePanelInternal(PanelType.Root);
            }
            
            
            if (add)
            {
                pt = rootPanelTracker = new PanelTracker(desc, comp);
                rootPanelTrackers.add(rootPanelTracker);
            }
            else
            {
                pt = rootPanelTracker = get(desc);                
            }
        }
        else if (desc.panelType == PanelType.Dialog || desc.panelType == PanelType.DialogOverlay)
        {
            // hide the root panel or dialog below this one
            if (desc.panelType == PanelType.Dialog)
            {
                if (dialogPanelTrackers.size() == 0)
                {
                    setHidden(rootPanelTracker);
                }
                else
                {
                    PanelTracker below = dialogPanelTrackers.peek();
                    setHidden(below);
                }
            }            
            
            pt = new PanelTracker(desc, comp);
            dialogPanelTrackers.push(pt);
        }
        else if (desc.panelType == PanelType.Overlay)
        {
            pt = new PanelTracker(desc, comp);
            overlayPanelTrackers.push(pt);
        }
        
        setVisible(pt, onShown);
    }    
    
    public void closePanel(PanelType panelType)
    {
        if (panelType == PanelType.Root)
        {
            throw new IllegalArgumentException("Not allowed to close a panel of type " + panelType + ", as there always need to be exactly one visible");
        }
        
        closePanelInternal(panelType);
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
        
        JComponent pb_toClose = pt_toClose.panel;
        if (!pb_toClose.isVisible())
        {
            throw new IllegalStateException("Attempted to hide a panel which is not visible");
        }
        
        // hide it, remove if panel is of temporary nature
        setHidden(pt_toClose);
        if (panelType == PanelType.Root)
        {
            rootPanelTracker = null;
        }
        else 
        {
            getFrameBodyPanel().remove(pb_toClose);
            
            // make topmost dialog visible again if it was previously hidden which is the
            // case a dialog was on top
            if (panelType == PanelType.Dialog)
            {
                if (!dialogPanelTrackers.isEmpty())
                {
                    PanelTracker topmost = dialogPanelTrackers.peek();
                    setVisible(topmost, null);   
                }
                else if (rootPanelTracker != null)
                {
                    setVisible(rootPanelTracker, null);              
                }
            }
        }
        
    }
    
    private void setVisible(PanelTracker pt, Runnable onShown)
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        IDrawer drawer = pt.panel.getDrawer();
        drawer.setForegroundAlpha(0.2f);
        
        pt.panel.setVisible(true);
        
        final Timer timer = new Timer(15, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                float alpha = drawer.getForegroundAlpha();
                alpha += 0.05;
                if (alpha >= 1) {
                    alpha = 0.999f; // prevents text suddenly changing size
                    ((Timer) e.getSource()).stop();
                    
                    panelServ.getPanelManager(pt.panelDesc).onShown();      
                    if (onShown != null)
                    {
                        onShown.run();
                    }
                }
                drawer.setForegroundAlpha(alpha);
            }
        });
        timer.setRepeats(true);
        timer.setCoalesce(true);
        timer.start();
        
    }

    private void setHidden(PanelTracker pt)
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        panelServ.getPanelManager(pt.panelDesc).onHidden();
        pt.panel.setVisible(false);
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
    
    protected BufferedImage getDialogBackgroundImage()
    {
        IResourceCacheService resServ = Services.get(IResourceCacheService.class);
        IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);
        String bgpath = resLocServ.getFilePath(ResourceDescriptors.DialogBackground);
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
            getFramePanelManager().setMaximized(true);
            
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
        getFramePanelManager().setMaximized(false);
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

    @Override
    public void minimize()
    {
        frame.setExtendedState(Frame.ICONIFIED);
        getFramePanelManager().setMaximized(false);
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
        posServ.setMaxSize(getFrameBodyPanel().getWidth(), getFrameBodyPanel().getHeight());
    }
    
    @Override
    public void showMessage(String title, String message)
    {
        MessagePanelData payload = new MessagePanelData(title, message);
        showPanel(PanelDescriptors.Message, payload, c -> {});
    }
    
    @Override
    public void showInternalError()
    {
        String title = Loc.get(LocKey.DialogTitle_generalerror);
        String msg = Loc.get(LocKey.DialogMessage_generalerror);
        MessagePanelData payload = new MessagePanelData(title, msg);
        
        showPanel(PanelDescriptors.Message, payload, c -> {});
    }    
    
    private <IN, OUT> JSLayeredPane createDialogPanel(DataPanelDescriptor<IN, OUT> desc, IN inData, Consumer<PanelOutData<OUT>> callback)
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        IDataPanelManager<IN, OUT> dpMan = panelServ.getPanelManager(desc);
               
        // function to generate a runnable which is run after the dialog was closed somehow
        Function<PanelButtonDescriptor, Runnable> closeFuncSupplier = (btnType) -> () ->
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
        
        // store the buttons for each PanelButtonDescriptor
        Map<PanelButtonDescriptor, JButton> buttons = new HashMap<>(); 
        
        // prepare the incoming data
        PanelInData<IN> panelInData = new PanelInData<>();
        panelInData.payload = inData;
        panelInData.verifyButtonFunc = (btnDesc) ->
        {
            JButton button = buttons.get(btnDesc);
            if (button == null)
            {
                return;
            }
            
            if (btnDesc.action != null)
            {
                btnDesc.action.checkEnabled();
            }
            else
            {
                boolean enabled = dpMan.isButtonEnabled(btnDesc);
                button.setEnabled(enabled);
            }            
        }; 
        panelInData.closeFunc = closeFuncSupplier.apply(PanelButtonDescriptors.Forced);
                
        // let the dialog panel manager load given the input data
        dpMan.load(panelInData);
        
        // create the entire panel, add the custom content and the buttons
        JSLayeredPane dialogPanel = JSFactory.createLayeredPane(ComponentTypes.DIALOG);
        {
            dialogPanel.setLayout(new MigLayout("insets 0"));  
            dialogPanel.addMouseListener(new MouseAdapter(){}); // block mouse input to panels below
            JSLayeredPane marginPanel = JSFactory.createLayeredPane(ComponentTypes.MARGINPANEL);
            marginPanel.setLayout(new MigLayout("insets 20 20 5 20"));
            marginPanel.setBackground(new Color(50,50,50));
           // marginPanel.getDrawer().setBackgroundAlpha(0.5f);
            marginPanel.setOpaque(false);
            marginPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
            dialogPanel.add(marginPanel, "hmax 400, push, alignx center, aligny center");
            {
                // dialog title
                String title = dpMan.createTitle();
                JLabel titleLabel = new JLabel(title + "   ");
                titleLabel.setFont(dialogTitleFont);
                marginPanel.add(titleLabel, "gapy 0 20, pushx, growx, alignx left, wrap");
                
                // content panel provided by the manager
                JComponent contentPanel = dpMan.createPanel();
                marginPanel.add(contentPanel, "grow, push, wrap");
                
                // create the buttons
                List<PanelButtonDescriptor> dbTypes = dpMan.getButtonTypes();
                if (dbTypes != null)
                {
                    for (PanelButtonDescriptor dbType : dbTypes)
                    {
                        ActionListener action_close = (e) -> closeFuncSupplier.apply(dbType).run();
                                        
                        JButton button;
                        if (dbType.lockey != null)
                        {
                            button = new JButton(Loc.get(dbType.lockey));
                        }
                        else 
                        {
                            button = new JButton(dbType.action);
                        }
                        
                        button.addActionListener(action_close);            
                        buttons.put(dbType, button);            
                    }
                }        
                
                // buttons at the bottom as required
                if (dbTypes != null && !dbTypes.isEmpty())
                {
                    // add separator
                    marginPanel.add(new JSeparator(), "span, push, growx");
                    
                    String mig_first = "span, wmin 60, split " + dbTypes.size() + ", pushx, align center, sg buts";
                    String mig_sg = "sg buts";
                    int cnt = 0;
                    for (PanelButtonDescriptor dbType : dbTypes)
                    {           
                        JButton button = buttons.get(dbType);
                        marginPanel.add(button, cnt++ == 0 ? mig_first : mig_sg);
                        
                        // enabled/disable the button
                        panelInData.verifyButtonFunc.accept(dbType);
                    }
                }            
            }
            dialogPanel.setOpaque(false); 
        }        
        
        return dialogPanel;
    }
    
    protected FramePanelManager getFramePanelManager()
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        IPanelManager pm = panelServ.getPanelManager(PanelDescriptors.Frame);
        FramePanelManager fpm = (FramePanelManager) pm;
        return fpm;
    }
    
    protected final JLayeredPane getFrameBodyPanel()
    {
        return getFramePanelManager().getFrameBodyPanel();
    }
}

