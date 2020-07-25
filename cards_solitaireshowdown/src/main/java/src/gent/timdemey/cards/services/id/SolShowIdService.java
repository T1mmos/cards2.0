package gent.timdemey.cards.services.id;

import java.util.UUID;

import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;
import gent.timdemey.cards.services.interfaces.ISolShowIdService;

public class SolShowIdService extends IdService implements ISolShowIdService
{
    private static final String COMPID_SPECIALCOUNTER = "compid.specialcounter.%s";
    private static final String COMPID_SPECIALBACKGROUND = "compid.specialbackground.%s";
    private static final String RESID_SPECIALBACKGROUND = "resid.specialcounter";
    private static final String COMPID_PLAYERNAME = "compid.playername.%s";

    @Override
    public UUID createCardStackScalableResourceId(String cardStackType)
    {
        return getUUID(RESID_CARDSTACK, cardStackType); 
    }

    @Override
    public UUID createSpecialCounterComponentId(ReadOnlyCardStack cs)
    {
        return getUUID(COMPID_SPECIALCOUNTER, cs.getId().toString());
    }

    @Override
    public UUID createSpecialBackgroundResourceId()
    {
        return getUUID(RESID_SPECIALBACKGROUND);
    }

    @Override
    public UUID createSpecialBackgroundComponentId(ReadOnlyCardStack cs)
    {
        return getUUID(COMPID_SPECIALBACKGROUND, cs.getId().toString());
    }

    @Override
    public UUID createPlayerComponentId(ReadOnlyPlayer player)
    {
        return getUUID(COMPID_PLAYERNAME, player.getId().toString());
    }

}
