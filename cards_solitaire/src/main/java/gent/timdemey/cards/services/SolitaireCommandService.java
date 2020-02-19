package gent.timdemey.cards.services;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.commands.C_SolMove;
import gent.timdemey.cards.model.commands.C_SolPull;
import gent.timdemey.cards.model.commands.C_SolPush;
import gent.timdemey.cards.model.commands.C_SolUse;
import gent.timdemey.cards.model.commands.CommandBase;

public class SolitaireCommandService implements ICommandService
{
    private static List<Class<?>> SYNCED_COMMANDS = Arrays.asList(C_SolMove.class, C_SolUse.class);
      
    @Override
    public C_SolMove getMoveCommand(UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        return new C_SolMove(srcCardStackId, dstCardStackId, cardId);
    }

    @Override
    public C_SolUse getCardUseCommand(UUID srcCardId)
    {
        return new C_SolUse(null, srcCardId);
    }
    
    @Override
    public CommandBase getCardStackUseCommand(UUID srcCardStackId)
    {
        return new C_SolUse(srcCardStackId, null);
    }

    @Override
    public C_SolPull getPullCommand(UUID srcCardStackId, UUID cardId)
    {
        return new C_SolPull(srcCardStackId, cardId);
    }

    @Override
    public C_SolPush getPushCommand(UUID dstCardStackId, List<UUID> srcCardIds)
    {
        return new C_SolPush(dstCardStackId, srcCardIds);
    }

    @Override
    public boolean isSyncedCommand(CommandBase command)
    {
        return isSyncedCommand(command.getClass());        
    }

    @Override
    public boolean isSyncedCommand(Class<? extends CommandBase> clazz)
    {
        return SYNCED_COMMANDS.contains(clazz);    
    }
}
