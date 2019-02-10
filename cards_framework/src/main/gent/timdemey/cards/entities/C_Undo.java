package gent.timdemey.cards.entities;

import gent.timdemey.cards.Services;

/**
 * Special meta command to undo the last command. This command should not be tracked and can as such not 
 * be undone or re-executed.
 * @author Timmos
 */
final class C_Undo extends ACommandPill 
{
    C_Undo(MetaInfo info) {
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
    public void execute() {        
        ContextFull context = Services.get(IContextProvider.class).getThreadContext();
        CardGameState state = context.getCardGameState();
        History history = state.getHistory();

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
