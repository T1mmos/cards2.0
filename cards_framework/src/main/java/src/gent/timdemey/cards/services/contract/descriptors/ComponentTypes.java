package gent.timdemey.cards.services.contract.descriptors;

public final class ComponentTypes
{
    public static final String TYPENAME_CARD = "Card";
    public static final String TYPENAME_CARDSTACK = "CardStack";
    public static final String TYPENAME_CARDSCORE = "CardScore";

    public static final ComponentType CARD = new ComponentType(TYPENAME_CARD);
    public static final ComponentType CARDSTACK = new ComponentType(TYPENAME_CARDSTACK);
    public static final ComponentType CARDSCORE = new ComponentType(TYPENAME_CARDSCORE);

    private ComponentTypes()
    {
    }
}
