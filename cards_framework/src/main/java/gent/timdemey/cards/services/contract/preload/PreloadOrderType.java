package gent.timdemey.cards.services.contract.preload;

public enum PreloadOrderType
{
    /**
     * Preloadable services declaring to be dependent will only be loaded after all isolated ones have loaded.
     */
    DEPENDENT,
    
    /**
     * Preloadable services declaring to be isolated will be loaded first, as others may depend on them.
     */
    ISOLATED
}
