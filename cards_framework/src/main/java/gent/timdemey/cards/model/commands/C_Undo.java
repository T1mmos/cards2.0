package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.CommandType;
import gent.timdemey.cards.readonlymodel.IGameEventListener;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;

/**
 * Special meta command to undo the last command. This command should not be tracked and can as such not 
 * be undone or re-executed.
 * @author Timmos
 */
public final class C_Undo extends CommandBase 
{
    C_Undo() 
    {
    }

    @Override
    public void execute() {        
        Context context = Services.get(IContextService.class).getThreadContext();
        CardGameState state = context.getCardGameState();
        CommandHistory history = state.getHistory();

        if (!history.canUndo()) 
        {
            throw new IllegalStateException("Not in the redoable state");
        }
        
        ICommand cmdToUndo = history.execLine.get(history.current);
        cmdToUndo.undo();
        history.current--;
        
        for (IGameEventListener listener : state.gameEventListeners) {
            cmdToUndo.visitUndone(listener);
        }
    } 
}
