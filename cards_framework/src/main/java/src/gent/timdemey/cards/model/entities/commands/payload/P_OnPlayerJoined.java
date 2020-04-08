package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.game.Player;

import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_OnPlayerJoined extends PayloadBase
{
    public Player player;
}
