package gent.timdemey.cards.model.entities.commands.lobby;

import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.Player;

public class P_HandlePlayerJoined extends CommandPayloadBase
{
    public Player player;
}
