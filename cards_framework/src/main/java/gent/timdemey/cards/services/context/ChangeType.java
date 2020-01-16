package gent.timdemey.cards.services.context;

public enum ChangeType
{
    /**
     * Reference was updated with a new value.
     */
    Set,
    /**
     * An item was added to a list.
     */
    Add,
    /**
     * An item was removed from a list.
     */
    Remove
}
