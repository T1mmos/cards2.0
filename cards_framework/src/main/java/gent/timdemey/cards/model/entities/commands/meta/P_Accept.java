package gent.timdemey.cards.model.entities.commands.meta;

import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import java.util.UUID;

public class P_Accept extends CommandPayloadBase
{
    public UUID acceptedCommandId;
    public UUID acceptedCommandSourceId;
}
