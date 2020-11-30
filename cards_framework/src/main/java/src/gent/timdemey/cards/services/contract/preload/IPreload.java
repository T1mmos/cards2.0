package gent.timdemey.cards.services.contract.preload;

import java.util.List;

public interface IPreload
{
    /**
     * Preloads this service. This method is called in a background thread.
     */
    public void preload();
}
