package gent.timdemey.cards.model.entities.commands.payload;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.Player;

public class P_OnLobbyWelcome extends CommandPayloadBase
{ 
    public UUID clientId;
    public UUID serverId;
    public String serverMessage;
    public List<Player> connected;
    public UUID lobbyAdminId;
}
