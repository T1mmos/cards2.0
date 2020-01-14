package gent.timdemey.cards.readonlymodel;

import java.util.List;

import gent.timdemey.cards.services.context.Change;

public interface IStateListener
{
    public void onChange(List<Change<?>> changes); // todo parameters etc.
}
