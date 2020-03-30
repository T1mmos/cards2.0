package gent.timdemey.cards.serialization.dto.commands;

public abstract class CommandBaseDto
{
    public final String commandClass;

    public String id;

    protected CommandBaseDto()
    {
        commandClass = getClass().getName();
    }
}
