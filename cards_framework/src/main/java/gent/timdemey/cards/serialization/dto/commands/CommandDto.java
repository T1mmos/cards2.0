package gent.timdemey.cards.serialization.dto.commands;

public abstract class CommandDto
{
    public final String commandClass;
    
    public String id;
    
    protected CommandDto()
    {
    	commandClass = getClass().getName();
    }
}
