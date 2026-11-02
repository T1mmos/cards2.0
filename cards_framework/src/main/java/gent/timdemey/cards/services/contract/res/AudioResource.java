package gent.timdemey.cards.services.contract.res;

import gent.timdemey.cards.services.sound.PcmSample;

public class AudioResource extends Resource<PcmSample>
{
    public AudioResource(String filename, boolean fallback, PcmSample sample)
    {
        super(filename, fallback, sample);
    }
}
