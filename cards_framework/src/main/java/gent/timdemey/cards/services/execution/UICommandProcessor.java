    package gent.timdemey.cards.services.execution;

import java.util.UUID;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.contract.ICommand;
import gent.timdemey.cards.model.commands.C_Composite;
import gent.timdemey.cards.model.commands.C_Move;
import gent.timdemey.cards.model.commands.CommandEnvelope;
import gent.timdemey.cards.model.commands.CommandHistory;
import gent.timdemey.cards.readonlymodel.IGameEventListener;
import gent.timdemey.cards.services.ICommandExecutionService;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

class UICommandProcessor implements ICommandExecutionService {    
    
    UICommandProcessor ()
    {
    }
 
    private void execute (CommandEnvelope envelope)
    {
        ICommand command = envelope.command;
        IContextService contextProvider = Services.get(IContextService.class);
        Context context = contextProvider.getThreadContext();
        CommandHistory history = context.getCardGameState().getHistory();
        
        
        history.execCounterRequested = false;
        history.execMinorCounter = 0;
        
        if (command.getCommandType() == CommandType.Gameplay)
        {            
            if (history.current + 1 < history.execLine.size()) // we did some undo's and are not at the end of the execution line
            {
                history.start = history.execLine.size(); // new point where no further undo is possible
            }                    
            history.execLine.add(envelope.command);
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
        
        boolean multiplayer = Services.get(ICardPlugin.class).getPlayerCount() > 1;
        if (multiplayer && (command instanceof C_Move || command instanceof C_Composite))
        {
            UUID localId = context.getLocalId();
            
            if (localId.equals(envelope.getMetaInfo().requestingParty))
            {
                envelope.reschedule(ContextType.Client);
            }
        }
        
    }
    
    @Override
    public void reschedule(CommandEnvelope envelope) 
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(() -> reschedule(envelope));
            return;
        }
        
        execute(envelope);
    }

    @Override
    public void schedule(ICommand command) {
        if (!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(() -> schedule(command));
            return;
        }
        
        CommandEnvelope envelope = CommandEnvelope.createCommandEnvelope(command);
        execute(envelope);
    }
}
