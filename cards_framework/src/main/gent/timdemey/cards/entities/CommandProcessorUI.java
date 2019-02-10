    package gent.timdemey.cards.entities;

import java.util.UUID;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.Services;

class CommandProcessorUI implements ICommandProcessor {    
    
    CommandProcessorUI ()
    {
    }
 
    private void execute (ICommand command)
    {
        IContextProvider contextProvider = Services.get(IContextProvider.class);
        ContextFull context = contextProvider.getThreadContext();
        History history = context.getCardGameState().getHistory();
        
        
        history.execCounterRequested = false;
        history.execMinorCounter = 0;
        
        if (command.getCommandType() == CommandType.Gameplay)
        {            
            if (history.current + 1 < history.execLine.size()) // we did some undo's and are not at the end of the execution line
            {
                history.start = history.execLine.size(); // new point where no further undo is possible
            }                    
            history.execLine.add(command);
            command.execute();                    
            history.current = history.execLine.size() - 1;
            
            for (IGameEventListener listener : context.getCardGameState().gameEventListeners) {
                command.visitExecuted(listener);
                listener.onUndoRedoChanged();
            }
        }
        else if (command.getCommandType() == CommandType.Correction)
        {   // this is a command that executes another command from history. It is handled in the command, not here.
            command.execute();
            for (IGameEventListener listener : context.getCardGameState().gameEventListeners) {
                command.visitExecuted(listener);
            }
        }
        else if (command.getCommandType() == CommandType.Meta)
        {
            command.execute();
            for (IGameEventListener listener : context.getCardGameState().gameEventListeners) {
                command.visitExecuted(listener);
            }
        }
        else 
        {
            throw new UnsupportedOperationException("Not supported command type: " + command.getCommandType());
        }
        
        if (command instanceof C_Move || command instanceof C_Composite)
        {
            UUID localId = context.getLocalId();
            
            if (localId.equals(command.getMetaInfo().requestingParty))
            {
                command.scheduleOn(ContextType.Client);
            }
        }
        
    }
    
    @Override
    public void schedule(ICommand command) 
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(() -> schedule(command));
            return;
        }
        
        
        execute(command);
    }
}
