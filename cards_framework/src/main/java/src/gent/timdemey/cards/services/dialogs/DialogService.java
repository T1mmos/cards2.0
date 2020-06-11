package gent.timdemey.cards.services.dialogs;

import java.util.EnumSet;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.interfaces.IDialogService;

public final class DialogService implements IDialogService
{
    private final JFrame frame;

    public DialogService(JFrame frame)
    {
        this.frame = frame;
    }

    @Override
    public void ShowNotImplemented()
    {
        ShowMessage("Not implemented", "This feature is not implemented yet.");
    }

    @Override
    public DialogData<Void> ShowMessage(String title, String message)
    {
        MessageDialogContent dialogContent = new MessageDialogContent();
        return ShowAdvanced(title, message, dialogContent, DialogButtonType.Ok);
    }

    @Override
    public DialogData<Void> ShowInternalError()
    {
        MessageDialogContent dialogContent = new MessageDialogContent();
        String title = Loc.get(LocKey.DialogTitle_generalerror);
        String msg = Loc.get(LocKey.DialogMessage_generalerror);
        return ShowAdvanced(title, msg, dialogContent, DialogButtonType.Ok);
    }

    @Override
    public <IN, OUT> DialogData<OUT> ShowAdvanced(String title, IN data, DialogContent<IN, OUT> dialogContent,
            DialogButtonType closeType)
    {
        return ShowAdvanced(title, data, dialogContent, EnumSet.of(closeType));
    }

    @Override
    public <IN, OUT> DialogData<OUT> ShowAdvanced(String title, IN data, DialogContent<IN, OUT> dialogContent,
            EnumSet<DialogButtonType> closeTypes)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        Logger.info("Showing dialog with title: " + title);

     /*   JPanel glass = new JPanel(new MigLayout("insets 0"));
        Container root = frame.getRootPane();
        
        int width = root.getWidth();
        int height = root.getHeight();
        
        BufferedImage bi = new BufferedImage(width, height,  BufferedImage.TYPE_INT_ARGB); 
        Graphics2D g = (Graphics2D) bi.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        root.paint(g);
        
        frame.setGlassPane(glass);
        
        glass.add(new JLabel("TEST"));
        final Timer t = new Timer(100, null);
        int max = 7;
        t.addActionListener(new ActionListener()
        {
            int i = 2;
            @Override
            public void actionPerformed(ActionEvent e)
            {
               
                
                int size = i;
                float div = size*size;
                float[] mask = new float[size*size];
                float value = 1f / div;
                Arrays.fill(mask, value);
                Kernel kernel = new Kernel(size, size, mask);
                BufferedImageOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
                BufferedImage blurred = op.filter(bi, null);
                ImageIcon img = new ImageIcon(blurred);

                JLabel label = new JLabel(img);

                glass.removeAll();
                glass.add(label);

                glass.validate();
                glass.setVisible(true);
                
                if (++i == max)
                {
                    t.stop();
                }
            }
        });
        t.start();*/
        
        
        JDialog dialog = new JDialog(frame, title, true);
        DialogData<OUT> outData = dialogContent.show(dialog, data, closeTypes);

       /* final Timer t2 = new Timer(100, null);
        t2.addActionListener(new ActionListener()
        {
            int i = max - 1;
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int size = i;
                float div = size*size;
                float[] mask = new float[size*size];
                float value = 1f / div;
                Arrays.fill(mask, value);
                Kernel kernel = new Kernel(size, size, mask);
                BufferedImageOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
                BufferedImage blurred = op.filter(bi, null);
                ImageIcon img = new ImageIcon(blurred);

                JLabel label = new JLabel(img);

                glass.removeAll();
                glass.add(label);
                glass.add(new JLabel("HEHEH"), "push, grow, wrap");

                glass.validate();
                
                if (--i == 2)
                {
                    t2.stop();
                    glass.setVisible(false);
                }
            }
        });
        t2.start();
        */
    //   
        
        return outData;
    }

    
}
