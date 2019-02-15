package gent.timdemey.cards.entities;

import java.util.UUID;

public interface IContextListener
{
    public void onPlayerAdded(Player player);
    public void onPlayerRemoved(Player player);
    public void onServerIdSet();
    public void onNameChanged(UUID id);
    public void onServerMessageChanged();
}
