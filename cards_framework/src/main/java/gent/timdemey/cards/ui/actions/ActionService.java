package gent.timdemey.cards.ui.actions;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardStack;
import gent.timdemey.cards.model.commands.C_Redo;
import gent.timdemey.cards.model.commands.C_StartGame;
import gent.timdemey.cards.model.commands.C_StopGame;
import gent.timdemey.cards.model.commands.C_Undo;
import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.commands.D_CreateGame;
import gent.timdemey.cards.model.commands.D_JoinGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.ICardGameCreationService;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IGamePanelManager;
import gent.timdemey.cards.services.context.ContextType;

public class ActionService implements IActionService
{

    private ReadOnlyState getReadOnlyState()
    {
        return Services.get(IContextService.class).getThreadContext().getReadOnlyState();
    }

    private boolean canExecute(CommandBase command)
    {
        return Services.get(IContextService.class).getThreadContext().canExecute(command);
    }

    private void execute(CommandBase command)
    {
        Services.get(IContextService.class).getContext(ContextType.UI).schedule(command);
    }

    @Override
    public boolean canExecuteAction(String id)
    {
        switch (id)
        {
        case AAction.ACTION_UNDO:
            return canExecute(new C_Undo());
        case AAction.ACTION_REDO:
            return canExecute(new C_Redo());
        case AAction.ACTION_CREATE:
            return canExecute(new D_CreateGame());
        case AAction.ACTION_START:
            return true;
        case AAction.ACTION_STOP:
            return canExecute(new C_StopGame());
        case AAction.ACTION_QUIT:
            return true;
        case AAction.ACTION_DEBUG:
            return true;
        case AAction.ACTION_JOIN:
            return true;
        case AAction.ACTION_LEAVE:
            return true;
        default:
            throw new UnsupportedOperationException("No such action id: " + id);
        }
    }

    @Override
    public void executeAction(String id)
    {
        switch (id)
        {
        case AAction.ACTION_UNDO:
            execute(new C_Undo());
            break;
        case AAction.ACTION_REDO:
            execute(new C_Redo());
            break;
        case AAction.ACTION_CREATE:
            execute(new D_CreateGame());
            break;
        case AAction.ACTION_JOIN:
            execute(new D_JoinGame());
            break;
        case AAction.ACTION_START:
            ICardGameCreationService creator = Services.get(ICardGameCreationService.class);
            List<List<Card>> cards = creator.getCards();

            List<UUID> playerIds = getReadOnlyState().getPlayers().getIds();
            Map<UUID, List<CardStack>> playerStacks = creator.createStacks(playerIds, cards);

            C_StartGame command = new C_StartGame(UUID.randomUUID(), playerStacks);
            IContextService contextServ = Services.get(IContextService.class);
            contextServ.getContext(ContextType.Server).schedule(command);
            break;
        case AAction.ACTION_STOP:
            execute(new C_StopGame());
            break;
        case AAction.ACTION_QUIT:
            System.exit(0);
            break;
        case AAction.ACTION_DEBUG:
            IGamePanelManager manager = Services.get(IGamePanelManager.class);
            manager.setDrawDebug(!manager.getDrawDebug());
            break;
        default:
            throw new UnsupportedOperationException("No such action id: " + id);
        }
    }
}
