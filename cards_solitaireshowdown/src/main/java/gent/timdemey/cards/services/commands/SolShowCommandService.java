package gent.timdemey.cards.services.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.commands.C_SolShowMove;
import gent.timdemey.cards.model.commands.C_SolShowPull;
import gent.timdemey.cards.model.commands.C_SolShowPush;
import gent.timdemey.cards.model.commands.C_SolShowUse;
import gent.timdemey.cards.model.entities.commands.C_Move;
import gent.timdemey.cards.model.entities.commands.C_Pull;
import gent.timdemey.cards.model.entities.commands.C_Push;
import gent.timdemey.cards.model.entities.commands.C_Use;
import gent.timdemey.cards.services.ICommandService;

public class SolShowCommandService implements ICommandService
{
    @Override
    public C_Move getMoveCommand(UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        return new C_SolShowMove(srcCardStackId, dstCardStackId, cardId);
    }

    @Override
    public C_Use getUseCommand(UUID srcCardStackId, UUID srcCardId)
    {
        return new C_SolShowUse(srcCardStackId, srcCardId);
    }

    @Override
    public C_Pull getPullCommand(UUID srcCardStackId, UUID cardId)
    {
        return new C_SolShowPull(srcCardStackId, cardId);
    }

    @Override
    public C_Push getPushCommand(UUID dstCardStackId, List<UUID> srcCardIds)
    {
        return new C_SolShowPush(dstCardStackId, srcCardIds);
    }
}
