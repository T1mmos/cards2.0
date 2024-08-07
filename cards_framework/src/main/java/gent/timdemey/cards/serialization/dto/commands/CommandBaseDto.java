package gent.timdemey.cards.serialization.dto.commands;

import gent.timdemey.cards.serialization.dto.EntityBaseDto;

public abstract class CommandBaseDto extends EntityBaseDto
{
    public String creatorId;
    
    protected CommandBaseDto()
    {
    }
}
