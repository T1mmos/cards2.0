package gent.timdemey.cards.services.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.services.ICommandService;

public class SolShowCommandService implements ICommandService
{
    @Override
    public CommandBase getMoveCommand(UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CommandBase getCardUseCommand(UUID srcCardId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CommandBase getCardStackUseCommand(UUID srcCardStackId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CommandBase getPullCommand(UUID srcCardStackId, UUID cardId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CommandBase getPushCommand(UUID dstCardStackId, List<UUID> srcCardIds)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isSyncedCommand(CommandBase command)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSyncedCommand(Class<? extends CommandBase> clazz)
    {
        // TODO Auto-generated method stub
        return false;
    }
}
