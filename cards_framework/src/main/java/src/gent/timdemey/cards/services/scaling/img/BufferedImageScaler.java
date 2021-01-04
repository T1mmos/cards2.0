package gent.timdemey.cards.services.scaling.img;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import com.google.common.base.Preconditions;

final class BufferedImageScaler
{
    private final BufferedImage srcImage;
    private final int targetWidth;
    private final int targetHeight;
    private final boolean higherQuality = true;

    BufferedImageScaler(BufferedImage srcImage, int targetWidth, int targetHeight)
    {
        Preconditions.checkNotNull(srcImage);
        Preconditions.checkArgument(targetWidth > 0);
        Preconditions.checkNotNull(targetHeight > 0);

        int maxWidth = srcImage.getWidth();
        int maxHeight = srcImage.getHeight();

        double ratioW = 1.0 * targetWidth / maxWidth;
        double ratioH = 1.0 * targetHeight / maxHeight;

        if (ratioW > 1.0 && ratioH > 1.0)
        {
            double ratio = Math.max(ratioW, ratioH);
            this.targetWidth = (int) (targetWidth / ratio);
            this.targetHeight = (int) (targetHeight / ratio);
        }
        else if (ratioW > 1.0)
        {
            this.targetWidth = (int) (targetWidth / ratioW);
            this.targetHeight = (int) (targetHeight / ratioW);
        }
        else if (ratioH > 1.0)
        {
            this.targetWidth = (int) (targetWidth / ratioH);
            this.targetHeight = (int) (targetHeight / ratioH);
        }
        else
        {
            this.targetWidth = targetWidth;
            this.targetHeight = targetHeight;
        }

        this.srcImage = srcImage;
    }

    BufferedImage getScaledInstance()
    {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration config = device.getDefaultConfiguration();

        BufferedImage ret = (BufferedImage) srcImage;
        int w, h;
        if (higherQuality)
        {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = srcImage.getWidth();
            h = srcImage.getHeight();
        }
        else
        {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do
        {
            if (higherQuality && w > targetWidth)
            {
                w /= 2;
                if (w < targetWidth)
                {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight)
            {
                h /= 2;
                if (h < targetHeight)
                {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = config.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        }
        while (w != targetWidth || h != targetHeight);

        return ret;
    }
}
