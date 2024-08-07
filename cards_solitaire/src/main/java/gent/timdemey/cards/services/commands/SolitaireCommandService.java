package gent.timdemey.cards.services.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.C_Move;
import gent.timdemey.cards.model.entities.commands.C_Pull;
import gent.timdemey.cards.model.entities.commands.C_Push;
import gent.timdemey.cards.model.entities.commands.C_SolMove;
import gent.timdemey.cards.model.entities.commands.C_SolPull;
import gent.timdemey.cards.model.entities.commands.C_SolPush;
import gent.timdemey.cards.model.entities.commands.C_SolUse;
import gent.timdemey.cards.model.entities.commands.C_Use;
import gent.timdemey.cards.services.interfaces.ICommandService;

public class SolitaireCommandService implements ICommandService
{
    @Override
    public C_Move getMoveCommand(UUID playerId, UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        return new C_SolMove(srcCardStackId, dstCardStackId, cardId);
    }
    
    @Override
    public C_Use getUseCommand(UUID playerId, UUID srcCardStackId, UUID srcCardId)
    {
        return new C_SolUse(srcCardStackId, srcCardId);
    }

    @Override
    public C_Pull getPullCommand(UUID playerId, UUID srcCardStackId, UUID cardId)
    {
        return new C_SolPull(srcCardStackId, cardId);
    }

    @Override
    public C_Push getPushCommand(UUID playerId, UUID dstCardStackId, List<UUID> srcCardIds)
    {
        return new C_SolPush(dstCardStackId, srcCardIds);
    }
}
