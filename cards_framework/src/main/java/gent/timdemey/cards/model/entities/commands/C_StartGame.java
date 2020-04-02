package gent.timdemey.cards.model.entities.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.commands.payload.P_StartGame;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_StartGame extends CommandBase
{
    private final CardGame cardGame;

    public C_StartGame(CardGame cardGame)
    {
        this.cardGame = cardGame;
    }
    
    public C_StartGame(P_StartGame pl)
    {
        super(pl);
        this.cardGame = pl.cardGame;
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return state.getCardGame() == null;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        if (type == ContextType.UI)
        {
            state.setCardGame(cardGame);
        }
        else if (type == ContextType.Client)
        {
            reschedule(ContextType.UI);
        }
        else
        {
            state.setCardGame(cardGame);

            String json = CommandDtoMapper.toJson(this);
            List<UUID> remoteIds = state.getPlayers().getExceptUUID(state.getServerId());
            state.getTcpConnectionPool().broadcast(remoteIds, json);
        }
    }
}
