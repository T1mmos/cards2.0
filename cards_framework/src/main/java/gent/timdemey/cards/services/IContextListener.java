package gent.timdemey.cards.services;

import java.util.UUID;

import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;

public interface IContextListener
{
    public void onPlayerAdded(ReadOnlyPlayer player);
    public void onPlayerRemoved(ReadOnlyPlayer player);
    public void onServerIdSet();
    public void onNameChanged(UUID id);
    public void onServerMessageChanged();
}
