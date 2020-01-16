package gent.timdemey.cards.services;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.commands.CommandBase;

public interface IOperationsService
{
    public CommandBase getMoveCommand(UUID srcCardStackId, UUID dstCardStackId, UUID cardId);

    public CommandBase getCardUseCommand(UUID srcCardId);

    public CommandBase getCardStackUseCommand(UUID srcCardStackId);

    public CommandBase getPullCommand(UUID srcCardStackId, UUID cardId);

    public CommandBase getPushCommand(UUID dstCardStackId, List<UUID> srcCardIds);
}
