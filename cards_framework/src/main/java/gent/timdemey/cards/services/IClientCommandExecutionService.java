package gent.timdemey.cards.services;

import gent.timdemey.cards.model.commands.C_HandleConnectionLoss;

public interface IClientCommandExecutionService extends ICommandExecutionService
{
    public void HandleConnectionLoss (C_HandleConnectionLoss request);
}
