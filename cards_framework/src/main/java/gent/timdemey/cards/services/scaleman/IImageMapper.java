package gent.timdemey.cards.services.scaleman;

import java.awt.image.BufferedImage;

public interface IImageMapper<T> {
    
    /**
     * Gets the filename of the resource mapped that the given object should map to, to be read in as
     * BufferedImage, which is then injected in a JScalableImage and managed.
     * @return
     */
    String getFileName(T obj);
    
    /**
     * Programmatically generate an error image for this scalable, if using the file resource
     * pointed to by getFileName() fails.
     * @return
     */
    BufferedImage createErrorImage(T obj);
}
