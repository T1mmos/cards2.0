package gent.timdemey.cards.model.entities.commands.payload;

import java.util.UUID;

public class P_Accept extends CommandPayloadBase
{
    public UUID acceptedCommandId;
    public UUID acceptedCommandSourceId;
}
