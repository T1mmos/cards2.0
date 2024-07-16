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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import gent.timdemey.cards.services.context.Context;
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
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceCacheService;
import gent.timdemey.cards.services.interfaces.IResourceNameService;
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
import gent.timdemey.cards.utils.StreamUtils;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import net.miginfocom.swing.MigLayout;

public class FrameService implements IFrameService, IPreload
{
    private JFrame frame;
    private Font dialogTitleFont;

    private boolean drawDebug = false;

    private Rectangle prevBounds;
    private List<SnapSide> snaps = null;

    private Stack<PanelDescriptor> panelStack = new Stack<>();

    @PreloadOrder(order = PreloadOrderType.DEPENDENT)
    public void preload()
    {
        IResourceCacheService resServ = Services.get(IResourceCacheService.class);
        IResourceNameService resLocServ = Services.get(IResourceNameService.class);

        String dialogTitleFontName = resLocServ.getFilePath(ResourceDescriptors.DialogTitleFont);
        FontResource res_dialogTitleFont = resServ.getFont(dialogTitleFontName);
        dialogTitleFont = res_dialogTitleFont.raw.deriveFont(20f);
    }

    @Override
    public JFrame getFrame()
    {
        FramePanelManager fpm = getFramePanelManager();
       
        if (frame == null)
        {
            String title = fpm.getFrameTitle();
            List<Image> icons = fpm.getFrameIcons();
        
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

    private static IPanelManager get(PanelDescriptor desc)
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        return panelServ.getPanelManager(desc);
    }

    @Override
    public void hidePanel(PanelDescriptor pd)
    {      
        hidePanelInternal(pd);
        
        // if a dialog was closed and there are lower dialogs, make topmost dialog visible again 
        if (pd.panelType == PanelType.Dialog && !panelStack.isEmpty())
        {
            // now make the topmost dialog visible again, or the root panel if no more dialog are stacked
            showPanel(panelStack.peek());   
        }
    }

    @Override
    public boolean isShown(PanelDescriptor desc)
    {
        IPanelManager pm = get(desc);
        if (!pm.isPanelCreated())
        {
            return false;
        }
        if (!pm.getPanel().isVisible())
        {
            return false;
        } 
        
        return true;
    }

    @Override
    public void showPanel(PanelDescriptor desc) 
    {
        if (desc.panelType != PanelType.Root && desc.panelType != PanelType.Overlay)
        {
            throw new IllegalArgumentException("This method is intended for Root and Overlay panels only");
        }

        IPanelManager pm = get(desc);

        boolean add;
        JSLayeredPane pb;
        if (!pm.isPanelCreated())
        {
            add = true;
            pb = pm.createPanel();
            if (pb.getParent() != null)
            {
                throw new IllegalStateException("The component to add already has a parent!");
            }
        }
        else
        {
            add = false;
            pb = pm.getPanel();
        }

        showPanelInternal(desc, pb, add);
    }

    @Override
    public void removePanel(PanelDescriptor desc)
    {
        IPanelService panelServ = Services.get(IPanelService.class);

        IPanelManager panelMgr = panelServ.getPanelManager(desc);
        if (!panelMgr.isPanelCreated())
        {
            throw new IllegalArgumentException("Attempted to remove a panel which has not been created yet: " + desc);
        }
        if (panelMgr.getPanel().isVisible())
        {
            hidePanel(desc);
        }
        
        panelMgr.destroyPanel();
    }

    @Override
    public <IN, OUT> void showPanel(DataPanelDescriptor<IN, OUT> desc, IN data, Consumer<PanelOutData<OUT>> onClose)
    {
        if (desc.panelType != PanelType.Dialog && desc.panelType != PanelType.DialogOverlay)
        {
            throw new IllegalArgumentException("This method is intended for Dialog / DialogOverlay panels only");
        }

        JSLayeredPane pb = createDialogPanel(desc, data, onClose);
        showPanelInternal(desc, pb, true);
    }
    
    
    private static Stream<PanelDescriptor> filterVisible(Stream<PanelDescriptor> stream)
    {
        return stream.filter(pd -> 
        {
            IPanelManager pm = get(pd);
            return pm.isPanelCreated() && pm.getPanel().isShowing();  
        });
    }
    
    private static Stream<PanelDescriptor> filterType(Stream<PanelDescriptor> stream, PanelType pt)
    {
        return stream.filter(pd -> pd.panelType == pt);
    }
    
    private static Stream<IPanelManager> toPanelManager(Stream<PanelDescriptor> stream)
    {
        return stream.map(pd -> get(pd));
    }
    
    private static Stream<PanelDescriptor> getPanelDescriptorsAsStream()
    {
        IPanelService pServ = Services.get(IPanelService.class);
        return pServ.getPanelDescriptors().stream();
    }
        
    private int getBaseLayer(PanelType pt)
    {
        if (pt== PanelType.Root)
        {
            return 0;
        }
        else if (pt == PanelType.Dialog)
        {
            return 100;
        }
        else if (pt == PanelType.Overlay)
        {
            return 300;
        }
        else if (pt == PanelType.DialogOverlay)
        {
            return 400;
        }
        else
        {
            throw new IllegalArgumentException("Unsupported PanelType: " + pt);
        }
    }
    
    private void showPanelInternal(PanelDescriptor pd, JSLayeredPane comp, boolean add)
    {        
        // first hide all dialogs if a root panel is requested; overlay dialogs/panels are left open    
        if (pd.panelType == PanelType.Root)
        {
            // hide dialogs
            while (panelStack.size() > 1)
            {
                hidePanelInternal(panelStack.peek());
            }            
        }
        
        // hide topmost root panel or dialog when showing another dialog
        if (pd.panelType == PanelType.Dialog && panelStack.size() > 1)
        {
            hidePanelInternal(panelStack.peek());
        }

        // hide root panel if it's different from the root panel to be shown
        boolean isRoot = pd.panelType == PanelType.Root;
        boolean isDialog = pd.panelType == PanelType.Dialog;
        boolean diffRootPD = panelStack.size() == 1 && panelStack.get(0) != pd;
        boolean noRoot = panelStack.size() == 0;
        boolean hideRoot = isRoot && diffRootPD;
        boolean push = (isRoot && (diffRootPD || noRoot)) || isDialog;
        if (hideRoot)
        {
            hidePanelInternal(panelStack.peek());    
        }
        
        // root panel / dialogs are traced as a stack, add it to the trace
        if (isPanelStackable(pd.panelType))
        {
            if (pd.panelType == PanelType.Dialog && panelStack.isEmpty())
            {
                throw new IllegalStateException("First panel on the stack must be a root panel");
            }
            if (push)
            {
                panelStack.push(pd);   
            }            
        }
        
        if (push)
        {
            // determine how many panels of this PanelType are already visible,
            // use this number to assign the correct layer
            List<IPanelManager> visPms = StreamUtils.combine(
                    getPanelDescriptorsAsStream(), 
                    s -> filterVisible(s),
                    s -> filterType(s, pd.panelType),
                    s -> toPanelManager(s))
                .collect(Collectors.toList());
            int visCnt = visPms.size();   
            int layer = getBaseLayer(pd.panelType) + visCnt;
        
            getFrameBodyPanel().add(comp, "pos 0 0 100% 100%");
            getFrameBodyPanel().setLayer(comp, layer);
            
        }
        
        getFrameBodyPanel().invalidate();
        getFrameBodyPanel().validate();
        setVisible(pd);
    }
    
    private boolean isPanelStackable(PanelType pd)
    {
        return pd == PanelType.Root || pd == PanelType.Dialog;
    }
    
    private void hidePanelInternal(PanelDescriptor pd)
    {
        // the panel should have been created
        IPanelManager pm = get(pd);
        if (!pm.isPanelCreated())
        {
            throw new IllegalArgumentException("Attempted to hide a panel which has not been created yet: " + pd);
        }
        
        // the panel must be visible
        JComponent jcomp = pm.getPanel();
        if (!jcomp.isVisible())
        {
            throw new IllegalStateException("Attempted to hide a panel which is not visible: " + pd);
        }

        if (isPanelStackable(pd.panelType) && panelStack.isEmpty())
        {
            throw new IllegalStateException("Attempted to hide a stackable panel which is not the topmost one: " + pd);
        }
        
        // can only hide a dialog if it is on top of the panel stack
        if (pd.panelType == PanelType.Dialog && panelStack.peek() != pd)
        {
            throw new IllegalStateException("Attempted to hide a dialog which is not the topmost one: " + pd);
        }
        
        // hide it
        setHidden(pd);        

        // remove from the stack the root panel or dialog that was just hidden
        if (isPanelStackable(pd.panelType))
        {
            panelStack.pop();
        }
    }

    private void setVisible(PanelDescriptor pd)
    {
        // IPanelService panelServ = Services.get(IPanelService.class);
        IPanelManager pm = get(pd);
        
        if (pd.panelType == PanelType.Overlay)
        {
            IDrawer drawer = pm.getPanel().getDrawer();
            drawer.setForegroundAlpha(0.4f);
            pm.getPanel().setVisible(true);
            final Timer timer = new Timer(150, new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    float alpha = drawer.getBackgroundAlpha();
                    alpha += 0.05f;
                    if (alpha >= 1.0f)
                    {
                        alpha = 1.0f;
                        ((Timer) e.getSource()).stop();

                        pm.onShown();
                    }
                    drawer.setForegroundAlpha(alpha);
                }
            });
            timer.setRepeats(true);
            timer.setCoalesce(false);
            timer.start();
        }
        else
        {
            pm.onShown();
            pm.getPanel().setVisible(true);
        }
    }

    private void setHidden(PanelDescriptor pd)
    {
        IPanelManager pm = get(pd);    
        
        // check that the component is actually part of the frame body panel
        JComponent contentComp = pm.getPanel();
        JComponent directChild;
        if (pd.panelType == PanelType.Root || pd.panelType == PanelType.Overlay)
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
            throw new IllegalStateException("The component '" + pd.id + "' (" + pd.panelType
                    + ") is not part of the hierarchy of the frame body panel");
        }
            
        if (pd.panelType == PanelType.Overlay)
        {
            IDrawer drawer = pm.getPanel().getDrawer();

            final Timer timer = new Timer(15, new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    float alpha = drawer.getForegroundAlpha();
                    alpha -= 0.05f;
                    if (alpha <= 0f)
                    {
                        alpha = 0f;
                        ((Timer) e.getSource()).stop();

                        pm.onHidden();
                        pm.getPanel().setVisible(false);
                        getFrameBodyPanel().remove(directChild);
                    }
                    drawer.setForegroundAlpha(alpha);
                }
            });
            timer.setRepeats(true);
            timer.setCoalesce(false);
            timer.start();
        }
        else
        {
            pm.onHidden();
            pm.getPanel().setVisible(false);
            getFrameBodyPanel().remove(directChild);
            getFrameBodyPanel().revalidate();
            getFrameBodyPanel().repaint();
        }
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
        IResourceNameService resLocServ = Services.get(IResourceNameService.class);
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
    public boolean isSnapped(SnapSide... snapsides)
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
    public void snap(SnapSide... snapsides)
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
        showPanel(PanelDescriptors.Message, payload, c ->
        {
        });
    }

    @Override
    public void showInternalError()
    {
        String title = Loc.get(LocKey.DialogTitle_generalerror);
        String msg = Loc.get(LocKey.DialogMessage_generalerror);
        MessagePanelData payload = new MessagePanelData(title, msg);

        showPanel(PanelDescriptors.Message, payload, c ->
        {
        });
    }

    @Override
    public void installStateListeners()
    {
        IContextService ctxtServ = Services.get(IContextService.class);
        Context ctxt = ctxtServ.getThreadContext();
        
        ctxt.addStateListener(new FrameGameListener());
        ctxt.addStateListener(new FrameSoundStateListener());
    }
    
    private <IN, OUT> JSLayeredPane createDialogPanel(DataPanelDescriptor<IN, OUT> desc, IN inData,
            Consumer<PanelOutData<OUT>> callback)
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        IDataPanelManager<IN, OUT> dpMan = panelServ.getPanelManager(desc);

        // function to generate a runnable which is run after the dialog was closed
        // somehow
        Function<PanelButtonDescriptor, Runnable> closeFuncSupplier = (btnType) -> () ->
        {
            PanelOutData<OUT> outData = new PanelOutData<>();
            OUT out = dpMan.onClose(btnType);
            outData.closeType = btnType;
            outData.data_out = out;
            hidePanelInternal(desc);
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
            dialogPanel.addMouseListener(new MouseAdapter()
            {
            }); // block mouse input to panels below
            JSLayeredPane marginPanel = JSFactory.createLayeredPane(ComponentTypes.MARGINPANEL);
            marginPanel.setLayout(new MigLayout("insets 20 20 5 20"));
            marginPanel.setBackground(new Color(50, 50, 50));
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
