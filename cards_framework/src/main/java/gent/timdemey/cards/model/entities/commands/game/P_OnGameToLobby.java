package gent.timdemey.cards.model.entities.commands.game;

import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import gent.timdemey.cards.model.entities.commands.game.C_OnGameToLobby.GameToLobbyReason;
import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_OnGameToLobby extends CommandPayloadBase
{
    public GameToLobbyReason reason;
}
