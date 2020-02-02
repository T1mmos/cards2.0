package gent.timdemey.cards.services;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.commands.C_Move;
import gent.timdemey.cards.model.commands.C_Pull;
import gent.timdemey.cards.model.commands.C_Push;
import gent.timdemey.cards.model.commands.C_Use;
import gent.timdemey.cards.model.commands.CommandBase;

public class SolitaireOperationsService implements ICommandService
{
    @Override
    public C_Move getMoveCommand(UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        return new C_Move(srcCardStackId, dstCardStackId, cardId);
    }

    @Override
    public C_Use getCardUseCommand(UUID srcCardId)
    {
        return new C_Use(null, srcCardId);
    }
    
    @Override
    public CommandBase getCardStackUseCommand(UUID srcCardStackId)
    {
        return new C_Use(srcCardStackId, null);
    }

    @Override
    public C_Pull getPullCommand(UUID srcCardStackId, UUID cardId)
    {
        return new C_Pull(srcCardStackId, cardId);
    }

    @Override
    public C_Push getPushCommand(UUID dstCardStackId, List<UUID> srcCardIds)
    {
        return new C_Push(dstCardStackId, srcCardIds);
    }
}
