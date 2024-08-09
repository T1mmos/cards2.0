package gent.timdemey.cards.model.entities.commands.game;

import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import java.util.UUID;

import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_RemovePlayer extends CommandPayloadBase
{
    public UUID playerId;
}
