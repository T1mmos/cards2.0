package gent.timdemey.cards.services.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.C_Move;
import gent.timdemey.cards.model.entities.commands.C_Pull;
import gent.timdemey.cards.model.entities.commands.C_Push;
import gent.timdemey.cards.model.entities.commands.C_SolShowMove;
import gent.timdemey.cards.model.entities.commands.C_SolShowPull;
import gent.timdemey.cards.model.entities.commands.C_SolShowPush;
import gent.timdemey.cards.model.entities.commands.C_SolShowUse;
import gent.timdemey.cards.model.entities.commands.C_Use;
import gent.timdemey.cards.services.interfaces.ICommandService;

public class SolShowCommandService implements ICommandService
{
    @Override
    public C_Move getMoveCommand(UUID playerId, UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        C_SolShowMove cmd = new C_SolShowMove(srcCardStackId, dstCardStackId, cardId);
        cmd.setSourceId(playerId);
        return cmd;
    }

    @Override
    public C_Use getUseCommand(UUID playerId, UUID srcCardStackId, UUID srcCardId)
    {
        C_SolShowUse cmd = new C_SolShowUse(srcCardStackId, srcCardId);
        cmd.setSourceId(playerId);
        return cmd;
    }

    @Override
    public C_Pull getPullCommand(UUID playerId, UUID srcCardStackId, UUID cardId)
    {
        C_SolShowPull cmd = new C_SolShowPull(srcCardStackId, cardId);
        cmd.setSourceId(playerId);
        return cmd;
    }

    @Override
    public C_Push getPushCommand(UUID playerId, UUID dstCardStackId, List<UUID> srcCardIds)
    {
        C_SolShowPush cmd = new C_SolShowPush(dstCardStackId, srcCardIds);
        cmd.setSourceId(playerId);
        return cmd;
    }
}
