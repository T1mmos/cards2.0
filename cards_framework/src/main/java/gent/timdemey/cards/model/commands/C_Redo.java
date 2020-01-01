package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.IGameEventListener;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;

/**
 * Special meta command to redo the last undone command. This command should not be tracked and can as such not 
 * be undone or re-executed.
 * @author Timmos
 */
public final class C_Redo extends CommandBase
{  
    C_Redo() 
    {
    }
        
    @Override
    public void execute() 
    {
        Context context = Services.get(IContextService.class).getThreadContext();
        CommandHistory history = context.getCardGameState().getHistory();
        CardGameState state = context.getCardGameState();        
        
        if (!history.canRedo()) 
        {
            throw new IllegalStateException("Not in the redoable state");
        }
        
        // ICommandProcessor processor = context.getCommandProcessor();

        ICommand cmdToRedo = history.execLine.get(history.current + 1);
        
        cmdToRedo.execute();
        history.current++;
        
        for (IGameEventListener listener : state.gameEventListeners) {
            cmdToRedo.visitExecuted(listener);
        }
    } 
}
