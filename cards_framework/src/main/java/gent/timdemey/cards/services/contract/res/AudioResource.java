package gent.timdemey.cards.services.contract.res;

import javax.sound.sampled.Clip;

public class AudioResource extends Resource<Clip>
{
    public AudioResource(String filename, boolean fallback, Clip clip)
    {
        super(filename, fallback, clip);
    }
}
