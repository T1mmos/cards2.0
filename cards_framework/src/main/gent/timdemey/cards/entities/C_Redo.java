package gent.timdemey.cards.entities;

import gent.timdemey.cards.Services;

/**
 * Special meta command to redo the last undone command. This command should not be tracked and can as such not 
 * be undone or re-executed.
 * @author Timmos
 */
final class C_Redo extends ACommandPill
{  
    C_Redo(MetaInfo info) {
        super(info);
    }
    
    @Override
    public CommandType getCommandType()
    {
        return CommandType.Correction;
    }

    @Override
    public void visitExecuted(IGameEventListener listener) {
        listener.onUndoRedoChanged();
    }    
    
    @Override
    public void execute() 
    {
        ContextFull context = Services.get(IContextProvider.class).getThreadContext();
        History history = context.getCardGameState().getHistory();
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
