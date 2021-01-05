package gent.timdemey.cards.services.frame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import com.alee.laf.button.WebButton;
import com.alee.managers.style.StyleId;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.contract.SnapSide;
import gent.timdemey.cards.services.contract.descriptors.DataPanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelType;
import gent.timdemey.cards.services.contract.res.FontResource;
import gent.timdemey.cards.services.contract.res.ImageResource;
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
import gent.timdemey.cards.ui.actions.ActionDescriptor;
import gent.timdemey.cards.ui.actions.ActionDescriptors;
import gent.timdemey.cards.ui.actions.IActionService;
import net.miginfocom.swing.MigLayout;

public class FrameService implements IFrameService
{  
    private JFrame frame;
    private RootPanel rootPanel;
    private JLayeredPane cardPanel;
    private JPanel titlePanel;
    private boolean drawDebug = false;
    private JButton title_minimize;
    private JButton title_maximize; 
    private JButton title_close;

    private Rectangle prevBounds;
    private EnumSet<SnapSide> snaps = null;
    private Border frameBorder;
    private RootPanelMouseListener rpMouseListener;
    
    private static class OpenPanel
    {
        private final PanelDescriptor panelDesc;
        private final PanelBase panel;
        
        private OpenPanel(PanelDescriptor panelDesc, PanelBase panel)
        {
            this.panelDesc = panelDesc;
            this.panel = panel;
        }
    }
    
    private OpenPanel currPanel = null;
    private Stack<OpenPanel> dialogs = new Stack<>();
    private Stack<OpenPanel> overlays  = new Stack<>();
        
    private JButton createFrameButton(ActionDescriptor desc)
    {
        IActionService actServ = Services.get(IActionService.class);     
        Action act_minimize = actServ.getAction(desc);
        
        WebButton button = new WebButton(StyleId.buttonUndecorated, act_minimize);
        Dimension dim = new Dimension(24, 24);           
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
            frameBorder = BorderFactory.createLineBorder(Color.gray, 1, false);
            rootPanel.setBorder(frameBorder);
            rootPanel.setLayout(new MigLayout("insets 5, gapy 0"));
            rpMouseListener = new RootPanelMouseListener();
            rootPanel.addMouseListener(rpMouseListener);    
            rootPanel.addMouseMotionListener(rpMouseListener);
            frame.setContentPane(rootPanel);
            
            titlePanel = new JPanel();
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
            title_text.setForeground(Color.white.darker());
            titlePanel.add(title_icon, "gapx 10, left");
            titlePanel.add(title_text, "gapx 10, pushx, left");
            titlePanel.add(title_minimize, "align center top, gapright 5");
            titlePanel.add(title_maximize, "align center top, gapright 5");
            titlePanel.add(title_close, "align center top");
            TitlePanelMouseListener tpMouseListener = new TitlePanelMouseListener();
            titlePanel.addMouseListener(tpMouseListener);
            titlePanel.addMouseMotionListener(tpMouseListener);
            rootPanel.add(titlePanel, "pushx, grow, wrap");
            
            cardPanel = new JLayeredPane();
            cardPanel.setLayout(new MigLayout());
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

    
    private PanelBase get(PanelDescriptor desc)
    {
        for (Component comp : cardPanel.getComponents())
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
        if (desc instanceof DataPanelDescriptor<?, ?>)
        {
            throw new IllegalArgumentException("This method is not intended for data panels");
        }
        
        IPanelService panelServ = Services.get(IPanelService.class);
        IPanelManager panelMgr = panelServ.getPanelManager(desc);
                        
        PanelBase pb = get(desc);
        boolean add = false;
        if (pb == null)
        {
            add = true;
            pb = panelMgr.create();
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
        PanelBase pb = createDialog(desc, data, onClose);
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
            if (desc.panelType == PanelType.Overlay)
            {
                layer = 0;
            }
            if (desc.panelType == PanelType.Dialog)
            {
                layer = 100 + dialogs.size();
            }
            else if (desc.panelType == PanelType.Overlay)
            {
                layer = 200 + overlays.size();
            }
            
            cardPanel.add(pb, "pos 0 0 100% 100%");
            cardPanel.setLayer(pb, layer);
            cardPanel.invalidate();
            cardPanel.validate();
        }
                
        // in case of a root panel, all open dialogs, overlays, and current panel are closed
        if (desc.panelType == PanelType.Root)
        {
            while (!dialogs.empty())
            {
                closePanelInternal(PanelType.Dialog);
            }
            while (!overlays.empty())
            {
                closePanelInternal(PanelType.Overlay);
            }
            if (currPanel != null)
            {
                closePanelInternal(PanelType.Root);
            }
            currPanel = new OpenPanel(desc, pb);
        }
        else if (desc.panelType == PanelType.Dialog)
        {
            // hide the root panel or dialog below this one
            if (dialogs.size() == 0)
            {
                currPanel.panel.setVisible(false);
            }
            else
            {
                OpenPanel below = dialogs.peek();
                below.panel.setVisible(false);
                panelServ.getPanelManager(below.panelDesc).onHidden();
            }
            
            dialogs.push(new OpenPanel(desc, pb));
        }
        else if (desc.panelType == PanelType.Overlay)
        {
            overlays.push(new OpenPanel(desc, pb));
        }
        
        // show the panel and notify its manager
        panelMgr.get().setVisible(true);
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
        cardPanel.invalidate();
        cardPanel.validate();
        cardPanel.repaint();
    }
    
    private void closePanelInternal(PanelType panelType)
    {
        IPanelService panelServ = Services.get(IPanelService.class);

        OpenPanel op;
        if (panelType == PanelType.Dialog && !dialogs.isEmpty())
        {
            op = dialogs.pop();
        }
        else if (panelType == PanelType.Overlay && !overlays.isEmpty())
        {
            op = overlays.pop();
        }
        else if (panelType == PanelType.Root)
        {
            op = currPanel;
        }
        else
        {
            Logger.warn("Attempted to close panel of type %s, but no such panels are open; ignoring the request", panelType);
            return;
        }        
                
        // the panel should exist
        IPanelManager panelMgr = panelServ.getPanelManager(op.panelDesc);
        if (!panelMgr.isCreated())
        {
            throw new IllegalArgumentException("Attempted to hide a panel which has not been created yet");
        }
        
        PanelBase pb = op.panel;
        if (!pb.isVisible())
        {
            throw new IllegalStateException("Attempted to hide a panel which is not visible");
        }
        
        // hide it, remove if panel is of temporary nature
        if (panelType == PanelType.Root)
        {
            pb.setVisible(false);
            currPanel = null;
        }
        else 
        {
            cardPanel.remove(pb);
            
            // make topmost dialog visible again
            if (!dialogs.isEmpty())
            {
                OpenPanel topmost = dialogs.peek();
                topmost.panel.setVisible(true);
                panelServ.getPanelManager(topmost.panelDesc).onShown();
            }
            else
            {
                currPanel.panel.setVisible(true);
                panelServ.getPanelManager(currPanel.panelDesc).onShown();
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
    public boolean isSnapped()
    {
        return snaps != null;
    }
    
    @Override
    public void snap(EnumSet<SnapSide> snapsides)
    {
        if (snapsides == null || snapsides.size() == 0)
        {
            throw new IllegalArgumentException("snapsides");
        }
        
        boolean snapTop = snapsides.contains(SnapSide.TOP);
        boolean snapBottom = snapsides.contains(SnapSide.BOTTOM);
        boolean snapLeft = snapsides.contains(SnapSide.LEFT);
        boolean snapRight = snapsides.contains(SnapSide.RIGHT);
        if (snapTop && snapBottom)
        {
            throw new IllegalArgumentException("cannot snap to both TOP and BOTTOM");
        }
        if (snapLeft && snapRight)
        {
            throw new IllegalArgumentException("cannot snap to both LEFT and RIGHT");
        }

        saveBounds();
        Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        
        if (snapTop && !snapLeft && !snapRight)
        {           
            saveBounds();  
            frame.setBounds(maxBounds);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            setMaximized(true);  
        }
        else if (snapLeft || snapRight)
        {
            int x, y, w, h;
            if (snapTop) 
            {
                y = 0;
                h = maxBounds.height / 2;
            }
            else if (snapBottom)
            {
                y = maxBounds.height / 2;
                h = maxBounds.height / 2;
            }
            else
            {
                y = 0;
                h = maxBounds.height;
            }
            
            if (snapLeft)
            {
                x = 0;
                w = maxBounds.width / 2;
            }
            else // snapRight
            {
                x = maxBounds.width / 2;
                w = maxBounds.width / 2;
            }

            setBounds(x, y, w, h);
        }
        
        snaps = snapsides;
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
        boolean maximized = snaps != null && snaps.size() == 1 && snaps.contains(SnapSide.TOP);
        if (maximized)
        {
            unsnap();
        }
        else
        {
            snap(EnumSet.of(SnapSide.TOP));
        }      
    }
    
    private void setMaximized(boolean maximized)
    {
        if (maximized)
        {
            rootPanel.removeMouseListener(rpMouseListener);    
            rootPanel.removeMouseMotionListener(rpMouseListener);
            rootPanel.setBorder(null);
        }
        else
        {
            rootPanel.addMouseListener(rpMouseListener);    
            rootPanel.addMouseMotionListener(rpMouseListener);
            rootPanel.setBorder(frameBorder);
        }
        
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
        
        frame.setBounds(x, y, w, h);
    }

    @Override
    public void updatePositionService()
    {
        IPositionService posServ = Services.get(IPositionService.class);
        posServ.setMaxSize(cardPanel.getWidth(), cardPanel.getHeight());
    }
    
    @Override
    public void showMessage(String title, String message)
    {
        // todo use title
        showPanel(PanelDescriptors.MESSAGE, message, c -> {});
    }
    
    @Override
    public void showInternalError()
    {
       // String title = Loc.get(LocKey.DialogTitle_generalerror); // todo
        String msg = Loc.get(LocKey.DialogMessage_generalerror);
        
        showPanel(PanelDescriptors.MESSAGE, msg, c -> {});
    }    
    
    private <IN, OUT> PanelBase createDialog(DataPanelDescriptor<IN, OUT> desc, IN inData, Consumer<PanelOutData<OUT>> callback)
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        IDataPanelManager<IN, OUT> dpMan = panelServ.getPanelManager(desc);
               
        // create the buttons that need to be shown according to the content creator
        EnumSet<PanelButtonType> dbTypes = dpMan.getButtonTypes();
        Map<PanelButtonType, JButton> buttons = new HashMap<>();        
        for (PanelButtonType dbType : dbTypes)
        {
            ActionListener action_close = (e) -> 
            {
                PanelOutData<OUT> outData = new PanelOutData<>();
                OUT out = dpMan.onClose(dbType);
                outData.data_out = out;
                outData.closeType = dbType;
                
                closePanel(PanelType.Dialog);
                callback.accept(outData);
            };
            String loctext = Loc.get(dbType.lockey);
            JButton button = new JButton(loctext); 
            
            button.addActionListener(action_close);
            button.setMinimumSize(new Dimension(75, 20));
            
            buttons.put(dbType, button);            
        }
        
        // prepare the incoming data
        PanelInData<IN> panelInData = new PanelInData<>();
        panelInData.data_in = inData;
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
        panelInData.closeFunc = () -> 
        {
            PanelOutData<OUT> outData = new PanelOutData<>();
            outData.closeType = PanelButtonType.Forced;
            outData.data_out = null;
            closePanel(PanelType.Dialog);
            callback.accept(outData);
        };
        
        // create the entire panel, add the custom content and the buttons
        PanelBase allContent = new PanelBase(desc);
        allContent.setLayout(new MigLayout("insets 5"));
        
        PanelBase customContent = dpMan.create();
        dpMan.load(panelInData);
        allContent.add(customContent, "grow, push, wrap");
        String mig_first = "span, split " + dbTypes.size() + ", pushx, align center, sg buts";
        String mig_sg = "sg buts";
        int cnt = 0;
        for (PanelButtonType dbType : dbTypes)
        {           
            JButton button = buttons.get(dbType);
            allContent.add(button, cnt++ == 0 ? mig_first : mig_sg);
            
            // enabled/disable the button
            panelInData.verifyButtonFunc.accept(dbType);
        }
        
        allContent.setBackground(Color.blue);
        
        return allContent;
    }
}

