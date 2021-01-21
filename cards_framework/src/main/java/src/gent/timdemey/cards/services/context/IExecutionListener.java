package gent.timdemey.cards.services.context;

import gent.timdemey.cards.model.entities.commands.CommandBase;

public interface IExecutionListener
{
    public void onExecuted(CommandBase command);
}
