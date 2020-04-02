package gent.timdemey.cards.model.entities.commands.payload;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.game.Player;

public class P_WelcomeClient extends PayloadBase
{ 
    public UUID serverId;
    public String serverMessage;
    public List<Player> connected;
}
