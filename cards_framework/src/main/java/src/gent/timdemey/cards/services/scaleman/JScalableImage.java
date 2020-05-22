package gent.timdemey.cards.services.scaleman;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.UUID;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IGamePanelService;
import gent.timdemey.cards.services.IScalableImageManager;

@SuppressWarnings("serial")
public class JScalableImage extends JPanel
{
    // black-pink checkerboard pattern that can be tiled
    private static BufferedImage ERROR_IMAGE;

    private static BufferedImage getErrorImage()
    {
        if(ERROR_IMAGE == null)
        {
            BufferedImage img = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) img.getGraphics();

            g.setColor(Color.black);
            g.fillRect(0, 0, 80, 80);
            g.setColor(new Color(249, 14, 245));
            g.fillRect(40, 0, 40, 40);
            g.fillRect(0, 40, 40, 40);
            g.drawRect(0, 0, 40, 40);
            g.drawRect(40, 40, 40, 40);
            g.setColor(Color.black);
            g.drawRect(40, 0, 40, 40);
            g.drawRect(0, 40, 40, 40);

            g.dispose();

            ERROR_IMAGE = img;
        }
        return ERROR_IMAGE;
    }

    private BufferedImage image = null;
    private String file = null;
    private boolean pointMirror = false;

    JScalableImage()
    {
        setOpaque(false);
    }

    void setImage(BufferedImage image, String file)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        this.image = image;
        this.file = file;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        if(image != null)
        {
            Graphics2D g3 = (Graphics2D) g.create();
            if(pointMirror)
            {
                g3.scale(-1.0, -1.0);
                g3.translate(-getWidth(), -getHeight());
            }

            int imgW = image.getWidth();
            int compW = getWidth();

            if(imgW == compW)
            {
                // best image quality
                g3.drawImage(image, 0, 0, null);
            }
            else
            {
                // quick rescale until bufferedimage is updated with high quality
                g3.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
                double sx = 1.0 * compW / imgW;
                g3.scale(sx, sx);
                g3.drawImage(image, 0, 0, null);
            }
            g3.dispose();
        }
        else
        {
            BufferedImage errorImg = getErrorImage();
            if(errorImg != null)
            {
                int tileWidth = errorImg.getWidth();
                int tileHeight = errorImg.getHeight();
                for (int y = 0; y < getHeight(); y += tileHeight)
                {
                    for (int x = 0; x < getWidth(); x += tileWidth)
                    {
                        g2.drawImage(errorImg, x, y, this);
                    }
                }
            }
        }

        if(Services.get(IGamePanelService.class).getDrawDebug())
        {
            ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState().getCardGame();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(50, 50, 50, 100));
            g2.fillRect(0, 0, getWidth(), getHeight());

            IScalableImageManager scaleMan = Services.get(IScalableImageManager.class);
            UUID id = scaleMan.getUUID(this);
            Color debugColor = cardGame.isCard(id) ? Color.cyan : Color.pink;

            g2.setColor(debugColor);
            g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 2.0f, new float[]
            { 5.0f, 5.0f }, 2.0f));
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            int layer = JLayeredPane.getLayer(this);
            int zorder = ((JLayeredPane) getParent()).getComponentZOrder(this);
            String[] strings = new String[]
            { file, "rect=" + getX() + "," + getY() + ", " + getWidth() + "x" + getHeight(), "layer=" + layer, "zorder=" + zorder };

            drawDebugStrings(g2, strings);
        }

        g2.dispose();
    }

    private void drawDebugStrings(Graphics2D g2, String[] strings)
    {
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.white);

        if(strings.length == 0)
        {
            return;
        }

        g2.setFont(Font.decode("Arial bold 8"));
        int hFat = g2.getFontMetrics().getHeight();
        g2.drawString(strings[0], 5, hFat);

        g2.setFont(Font.decode("Arial 8"));
        int hSmall = g2.getFontMetrics().getHeight();

        for (int i = 1; i < strings.length; i++)
        {
            g2.drawString(strings[i], 5, hFat + i * hSmall);
        }

    }

    public void mirror()
    {
        pointMirror = true;
    }
}
