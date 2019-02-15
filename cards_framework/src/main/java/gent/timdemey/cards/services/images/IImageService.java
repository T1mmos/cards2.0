package gent.timdemey.cards.services.images;

import java.awt.image.BufferedImage;

public interface IImageService {
    BufferedImage read(String filename);
}
