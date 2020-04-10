package gent.timdemey.cards.test.helpers;

import java.util.UUID;

public class IdHelper
{
    public static final String IDPREFIX_PLAYER = "BABE0000";
    public static final String IDPREFIX_PLAYERCONFIGURATION = "BBBBC0F6";
    public static final String IDPREFIX_CARD = "CA2D0000";
    public static final String IDPREFIX_CARDSTACK = "57ACC000";
    public static final String IDPREFIX_CARDGAME = "BA5EBA11";
    public static final String IDPREFIX_SERVER = "B0BB1E00";
    
    public static UUID createFixedPlayerId(int playerNr)
    {
        return create(IDPREFIX_PLAYER, playerNr, -1, -1, false);
    }

    public static UUID createFixedPlayerConfigurationId(int pcNr)
    {
        return create(IDPREFIX_PLAYERCONFIGURATION, pcNr, -1, -1, false);
    }

    public static UUID createFixedCardGameId()
    {
        return create(IDPREFIX_CARDGAME, -1, -1, -1, false);
    }
    
    public static UUID createFixedCardStackId(int playerNr, int cardStackNr)
    {
        return create(IDPREFIX_CARDSTACK, playerNr, cardStackNr, -1, false);
    }
    
    public static UUID createFixedCardId(int playerNr, int cardStackNr, int cardNr)
    {
        return create(IDPREFIX_CARD, playerNr, cardStackNr, cardNr, false);
    }
    
    public static UUID createFixedServerId()
    {
        return create(IDPREFIX_SERVER, -1, -1, -1, false);
    }
    
    public static UUID createTestPlayerId(int playerNr)
    {
        return create(IDPREFIX_PLAYER, playerNr, -1, -1, true);
    }
    
    public static UUID createTestPlayerConfigurationId(int pcNr)
    {
        return create(IDPREFIX_PLAYERCONFIGURATION, pcNr, -1, -1, true);
    }

    public static UUID createTestCardGameId()
    {
        return create(IDPREFIX_CARDGAME, -1, -1, -1, true);
    }
    
    public static UUID createTestCardStackId(int playerNr, int cardStackNr)
    {
        return create(IDPREFIX_CARDSTACK, playerNr, cardStackNr, -1, true);
    }
    
    public static UUID createTestCardId(int playerNr, int cardStackNr, int cardNr)
    {
        return create(IDPREFIX_CARD, playerNr, cardStackNr, cardNr, true);
    }

    public static UUID createTestServerId()
    {
        return create(IDPREFIX_SERVER, -1, -1, -1, true);
    }
    
    private static UUID create(String prefix, int playerNr, int cardStackNr, int cardNr, boolean random)
    {        
        String _1prefix = prefix;
        String _2playerPart = playerNr == -1 ? "FFFF" : String.format("%04d", playerNr);
        String _3cardStackPart = cardStackNr == -1 ? "FFFF" : String.format("%04d", cardStackNr);
        String _4cardPart = cardNr == -1 ? "FFFF" : String.format("%04d", cardNr);
        String _5fifthPart;
        if (random)
        {
            String[] allParts = UUID.randomUUID().toString().split("-");
            _5fifthPart = allParts[4];
        }
        else 
        {
            _5fifthPart = "FFFFFFFFFFFF";
        }
        
        String uuidStr = String.join("-", _1prefix, _2playerPart, _3cardStackPart, _4cardPart, _5fifthPart);
        return UUID.fromString(uuidStr);
    }
}
