package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.Player;

public class P_OnLobbyPlayerJoined extends PayloadBase
{
    public Player player;
}
