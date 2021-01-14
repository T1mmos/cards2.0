package gent.timdemey.cards.common;

public final class Version
{
    private final int major;
    private final int minor;
    
    public Version(int major)
    {
        this(major, 0);
    }
    
    public Version (int major, int minor)
    {
        if (major < 0)
        {
            throw new IllegalArgumentException("major");
        }
        if (minor < 0)
        {
            throw new IllegalArgumentException("minor");
        }

        this.major = major;
        this.minor = minor;
        
    }
    
    public int getMajor()
    {
        return major;
    }

    public int getMinor()
    {
        return minor;
    }
    
    @Override
    public String toString()
    {
        return "Version="+major+"."+minor;
    }
}
