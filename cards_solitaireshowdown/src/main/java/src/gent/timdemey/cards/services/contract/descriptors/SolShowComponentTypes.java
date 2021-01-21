package gent.timdemey.cards.services.contract.descriptors;

public class SolShowComponentTypes
{
    public static final String TYPENAME_SPECIALSCORE = "SPECIALSCORE";
    public static final String TYPENAME_SPECIALBACKGROUND = "SPECIALBACKGROUND";
    public static final String TYPENAME_PLAYERNAME = "PlayerName";
    public static final String TYPENAME_CARDAREABG = "CARDAREA_BACKGROUND";
    public static final String TYPENAME_PLAYERBG = "PLAYER_BACKGROUND";
    public static final String TYPENAME_VS = "VS";

    public static final ComponentType SPECIALSCORE = new ComponentType(TYPENAME_SPECIALSCORE);
    public static final ComponentType SPECIALBACKGROUND = new ComponentType(TYPENAME_SPECIALBACKGROUND);
    public static final ComponentType PLAYERNAME = new ComponentType(TYPENAME_PLAYERNAME);
    public static final ComponentType CARDAREABG = new ComponentType(TYPENAME_CARDAREABG);
    public static final ComponentType PLAYERBG = new ComponentType(TYPENAME_PLAYERBG);
    public static final ComponentType VS = new ComponentType(TYPENAME_VS);
}
