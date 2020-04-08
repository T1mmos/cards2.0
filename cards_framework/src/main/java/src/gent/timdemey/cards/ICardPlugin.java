package gent.timdemey.cards;

public interface ICardPlugin
{
    public void installServices();

    public int getPlayerCount();

    public String getName();

    public int getMajorVersion();

    public int getMinorVersion();

}
