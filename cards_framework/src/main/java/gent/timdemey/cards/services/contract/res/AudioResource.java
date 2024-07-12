package gent.timdemey.cards.services.contract.res;

public class AudioResource extends Resource<byte[]>
{
    public AudioResource(String filename, boolean fallback, byte[] clip)
    {
        super(filename, fallback, clip);
    }
}
