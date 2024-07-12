package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.commands.C_OnGameToLobby.GameToLobbyReason;
import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_OnGameToLobby extends PayloadBase
{
    public GameToLobbyReason reason;
}
