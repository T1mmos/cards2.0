package gent.timdemey.cards.model.entities.commands.payload;

import java.util.UUID;

import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_JoinGame extends PayloadBase
{
    public String clientName;
    public UUID clientId;
    public UUID serverId;
}
