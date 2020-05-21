package gent.timdemey.cards.ui.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.C_Disconnect;
import gent.timdemey.cards.model.entities.commands.C_Disconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.contract.ExecutionState;
import gent.timdemey.cards.model.entities.commands.C_Redo;
import gent.timdemey.cards.model.entities.commands.C_StartLocalGame;
import gent.timdemey.cards.model.entities.commands.C_StartMultiplayerGame;
import gent.timdemey.cards.model.entities.commands.C_Undo;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.D_Connect;
import gent.timdemey.cards.model.entities.commands.D_StartServer;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IGamePanelService;
import gent.timdemey.cards.services.context.ContextType;

public class ActionService implements IActionService
{
    private Map<String, Supplier<CommandBase>> ID_TO_COMMAND = new HashMap<>();
    {
        addCommand(Actions.ACTION_CREATE_MULTIPLAYER, () -> new D_StartServer());
        addCommand(Actions.ACTION_JOIN, () -> new D_Connect());
        addCommand(Actions.ACTION_LEAVE, () -> new C_Disconnect(DisconnectReason.LocalPlayerLeft));
        addCommand(Actions.ACTION_REDO, () -> new C_Redo());
        addCommand(Actions.ACTION_START, () -> new C_StartLocalGame());
        addCommand(Actions.ACTION_STARTMULTIPLAYER, () -> new C_StartMultiplayerGame());
        addCommand(Actions.ACTION_UNDO, () -> new C_Undo());
    }

    private void addCommand(String id, Supplier<CommandBase> cmdSupplier)
    {
        ID_TO_COMMAND.put(id, cmdSupplier);
    }

    private CommandBase getCommand(String id)
    {
        Supplier<CommandBase> cmdSupp = ID_TO_COMMAND.get(id);
        if (cmdSupp == null)
        {
            throw new UnsupportedOperationException("No command mapped to id: " + id);
        }
        CommandBase cmd = cmdSupp.get();
        return cmd;
    }

    private boolean canExecute(CommandBase command)
    {
        CanExecuteResponse response = Services.get(IContextService.class).getThreadContext().canExecute(command);
        if (response.execState == ExecutionState.Yes)
        {
            return true;
        }
        else if (response.execState == ExecutionState.No)
        {
            Logger.trace("Cannot execute command %s (%s) because: %s", command.getName(), "ActionService",
                    response.reason);

            return false;
        }
        else
        {
            Logger.error("Cannot execute command %s (%s) because of a state error: %s", command.getName(),
                    "ActionService", response.reason);

            return false;
        }
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
        case Actions.ACTION_DEBUG_DRAWOUTLINES:
        case Actions.ACTION_DEBUG_GC:
        case Actions.ACTION_QUIT:
            return true;
        default:
            CommandBase cmd = getCommand(id);
            return canExecute(cmd);
        }
    }

    @Override
    public void executeAction(String id)
    {
        switch (id)
        {
        case Actions.ACTION_DEBUG_DRAWOUTLINES:
            IGamePanelService manager = Services.get(IGamePanelService.class);
            manager.setDrawDebug(!manager.getDrawDebug());
            break;
        case Actions.ACTION_DEBUG_GC:
            System.gc();
            break;
        case Actions.ACTION_QUIT:
            System.exit(0);
            break;
        default:
            CommandBase cmd = getCommand(id);
            execute(cmd);
        }
    }
}
