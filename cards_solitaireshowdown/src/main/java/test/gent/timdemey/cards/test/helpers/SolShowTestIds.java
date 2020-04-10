package gent.timdemey.cards.test.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.services.boot.SolShowCardStackType;

public class SolShowTestIds
{
    private static final List<UUID> created_uuids = new ArrayList<>();
    
    public static final UUID P1_DEPOT = createCardStackUuid(0, SolShowCardStackType.DEPOT, 0);
    public static final UUID P1_TURNOVER = createCardStackUuid(0, SolShowCardStackType.TURNOVER, 0);
    public static final UUID P1_SPECIAL = createCardStackUuid(0, SolShowCardStackType.SPECIAL, 0);
    public static final UUID P1_MIDDLE1 = createCardStackUuid(0, SolShowCardStackType.MIDDLE, 0);
    public static final UUID P1_MIDDLE2 = createCardStackUuid(0, SolShowCardStackType.MIDDLE, 1);
    public static final UUID P1_MIDDLE3 = createCardStackUuid(0, SolShowCardStackType.MIDDLE, 2);
    public static final UUID P1_MIDDLE4 = createCardStackUuid(0, SolShowCardStackType.MIDDLE, 3);
    public static final UUID P1_LAYDOWN1 = createCardStackUuid(0, SolShowCardStackType.LAYDOWN, 0);
    public static final UUID P1_LAYDOWN2 = createCardStackUuid(0, SolShowCardStackType.LAYDOWN, 1);
    public static final UUID P1_LAYDOWN3 = createCardStackUuid(0, SolShowCardStackType.LAYDOWN, 2);
    public static final UUID P1_LAYDOWN4 = createCardStackUuid(0, SolShowCardStackType.LAYDOWN, 3);
    public static final UUID P2_DEPOT = createCardStackUuid(1, SolShowCardStackType.DEPOT, 0);
    public static final UUID P2_TURNOVER = createCardStackUuid(1, SolShowCardStackType.TURNOVER, 0);
    public static final UUID P2_SPECIAL = createCardStackUuid(1, SolShowCardStackType.SPECIAL, 0);
    public static final UUID P2_MIDDLE1 = createCardStackUuid(1, SolShowCardStackType.MIDDLE, 0);
    public static final UUID P2_MIDDLE2 = createCardStackUuid(1, SolShowCardStackType.MIDDLE, 1);
    public static final UUID P2_MIDDLE3 = createCardStackUuid(1, SolShowCardStackType.MIDDLE, 2);
    public static final UUID P2_MIDDLE4 = createCardStackUuid(1, SolShowCardStackType.MIDDLE, 3);
    public static final UUID P2_LAYDOWN1 = createCardStackUuid(1, SolShowCardStackType.LAYDOWN, 0);
    public static final UUID P2_LAYDOWN2 = createCardStackUuid(1, SolShowCardStackType.LAYDOWN, 1);
    public static final UUID P2_LAYDOWN3 = createCardStackUuid(1, SolShowCardStackType.LAYDOWN, 2);
    public static final UUID P2_LAYDOWN4 = createCardStackUuid(1, SolShowCardStackType.LAYDOWN, 3);
            
    public static UUID getFixedCardStackId(int playerNr, String cardStackType, int cardTypeNumber)
    {
        UUID uuid = createCardStackUuid(playerNr, cardStackType, cardTypeNumber);
        
        int idx = created_uuids.indexOf(uuid);
        if (idx == -1)
        {
            String msg = "Can't find a UUID for playerNr=%s, cardStackType=%s, cardTypeNumber=%s";
            String fmsg = String.format(msg, playerNr, cardStackType, cardTypeNumber);
            throw new IllegalArgumentException(fmsg);
        }
        // always return same instance
        UUID canonical_instance = created_uuids.get(idx);
        return canonical_instance;
    }
    
    public static UUID getFixedCardId(int playerNr, String cardStackType, int cardTypeNumber, int cardNr)
    {
        UUID uuid = createCardStackUuid(playerNr, cardStackType, cardTypeNumber);
        String[] parts = uuid.toString().split("-");
        String part0 = IdHelper.IDPREFIX_CARD;
        String part1 = parts[1];
        String part2 = parts[2];
        String part3 = String.format("%04d", cardNr);
        String part4 = parts[4];
        
        UUID cardId = UUID.fromString(String.join("-", part0, part1, part2, part3, part4));        
        return cardId;
    }
    
    private static UUID createCardStackUuid(int playerNr, String cardStackType, int cardTypeNumber)
    {
        int cardStackNr = -1;
        if (cardStackType.equals(SolShowCardStackType.DEPOT))
        {
            cardStackNr = 0;
        }
        else if (cardStackType.equals(SolShowCardStackType.TURNOVER))
        {
            cardStackNr = 1;
        }
        else if (cardStackType.equals(SolShowCardStackType.SPECIAL))
        {
            cardStackNr = 2;
        }
        else if (cardStackType.equals(SolShowCardStackType.MIDDLE))
        {
            cardStackNr = 3 + cardTypeNumber;
        }
        else if (cardStackType.equals(SolShowCardStackType.LAYDOWN))
        {
            cardStackNr = 7 + cardTypeNumber;
        }
        
        UUID uuid = IdHelper.createFixedCardStackId(playerNr, cardStackNr);
        created_uuids.add(uuid);
        return uuid;
    }
}
