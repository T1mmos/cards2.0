package gent.timdemey.cards.test.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.services.boot.SolShowCardStackType;

public class SolShowCardStackIdHelper
{
    private static final List<UUID> created_uuids = new ArrayList<>();
    
    public static final UUID P1_DEPOT = createUuid(0, SolShowCardStackType.DEPOT, 0);
    public static final UUID P1_TURNOVER = createUuid(0, SolShowCardStackType.TURNOVER, 0);
    public static final UUID P1_SPECIAL = createUuid(0, SolShowCardStackType.SPECIAL, 0);
    public static final UUID P1_MIDDLE1 = createUuid(0, SolShowCardStackType.MIDDLE, 0);
    public static final UUID P1_MIDDLE2 = createUuid(0, SolShowCardStackType.MIDDLE, 1);
    public static final UUID P1_MIDDLE3 = createUuid(0, SolShowCardStackType.MIDDLE, 2);
    public static final UUID P1_MIDDLE4 = createUuid(0, SolShowCardStackType.MIDDLE, 3);
    public static final UUID P1_LAYDOWN1 = createUuid(0, SolShowCardStackType.LAYDOWN, 0);
    public static final UUID P1_LAYDOWN2 = createUuid(0, SolShowCardStackType.LAYDOWN, 1);
    public static final UUID P1_LAYDOWN3 = createUuid(0, SolShowCardStackType.LAYDOWN, 2);
    public static final UUID P1_LAYDOWN4 = createUuid(0, SolShowCardStackType.LAYDOWN, 3);
    public static final UUID P2_DEPOT = createUuid(1, SolShowCardStackType.DEPOT, 0);
    public static final UUID P2_TURNOVER = createUuid(1, SolShowCardStackType.TURNOVER, 0);
    public static final UUID P2_SPECIAL = createUuid(1, SolShowCardStackType.SPECIAL, 0);
    public static final UUID P2_MIDDLE1 = createUuid(1, SolShowCardStackType.MIDDLE, 0);
    public static final UUID P2_MIDDLE2 = createUuid(1, SolShowCardStackType.MIDDLE, 1);
    public static final UUID P2_MIDDLE3 = createUuid(1, SolShowCardStackType.MIDDLE, 2);
    public static final UUID P2_MIDDLE4 = createUuid(1, SolShowCardStackType.MIDDLE, 3);
    public static final UUID P2_LAYDOWN1 = createUuid(1, SolShowCardStackType.LAYDOWN, 0);
    public static final UUID P2_LAYDOWN2 = createUuid(1, SolShowCardStackType.LAYDOWN, 1);
    public static final UUID P2_LAYDOWN3 = createUuid(1, SolShowCardStackType.LAYDOWN, 2);
    public static final UUID P2_LAYDOWN4 = createUuid(1, SolShowCardStackType.LAYDOWN, 3);
            
    public static UUID getFixedId(int playerNr, String cardStackType, int cardTypeNumber)
    {
        UUID uuid = createUuid(playerNr, cardStackType, cardTypeNumber);
        
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
    
    private static UUID createUuid(int playerNr, String cardStackType, int cardTypeNumber)
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
