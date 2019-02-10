package gent.timdemey.cards.entities;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;

/**
 * A composite command bundles multiple commands in one atomic command.
 * <p>The chained commands must all be undoable because the ability to
 * execute this command is depending on the ability to execute all 
 * commands in the chain, but the ability of command N can only be checked
 * to the state of the game when all previous N commands have been executed 
 * first. After the check has been performed, the state must be set back
 * to where this composite command was not yet executed, which implies that
 * all commands in this composite commands must be undoable. (we could work 
 * with a copy of the state which would be thrown away after the check but 
 * it was chosen not to make this effort)
 * @author Timmos
 */
class C_Composite extends ACommand {
        
    static class CompactConverter extends ACommandSerializer<C_Composite>
    {
        @Override
        protected void writeCommand(SerializationContext<C_Composite> sc)
        {
            CommandEnvelope [] envelopes = new CommandEnvelope[sc.src.commands.length];
            for (int i = 0; i < sc.src.commands.length; i++)
            {
                envelopes[i] = new CommandEnvelope(sc.src.commands[i]);
            }
            sc.obj.add(PROPERTY_SUBCOMMANDS, sc.context.serialize(envelopes));
        }

        @Override
        protected C_Composite readCommand(DeserializationContext dc, MetaInfo metaInfo) {
                                    
            JsonArray commandArray = dc.obj.get(PROPERTY_SUBCOMMANDS).getAsJsonArray();
            ICommand[] commands = new ICommand[commandArray.size()];
            for (int i = 0; i < commandArray.size(); i++)
            {
                CommandEnvelope envelope = dc.context.deserialize(commandArray.get(i), CommandEnvelope.class);
                commands[i] = envelope.command;
            }
            
            return new C_Composite(metaInfo, commands);
        }
                
    }
    
    private final ICommand [] commands;
    private final CommandType commandType;

    C_Composite(MetaInfo metaInfo, ICommand ... commands) {
        super(metaInfo);         
        Preconditions.checkArgument(metaInfo.minor == 0); // composite should always be major so minor == 0
        Preconditions.checkNotNull(commands);
        
        CommandType type = null;
        for (ICommand cmd : commands)
        {
            Preconditions.checkNotNull(cmd);
            if (type == null)
            {
                type = cmd.getCommandType();
            }
            else
            {
                Preconditions.checkArgument(cmd.getCommandType() == type, "A composite command requires all subcommands to be of the same type");
            }
        }
        this.commands = commands;
        this.commandType = type;
    }

    @Override
    public CommandType getCommandType() {
        return commandType;
    }
    
    @Override
    public boolean canExecute() {
        boolean canExecute = true;
        int i = 0;
        while (canExecute && i < commands.length)
        {
            if (commands[i].canExecute())
            {
                try 
                {
                    commands[i++].execute();
                }
                catch (Exception e)
                {
                    throw new IllegalStateException("A composite command's commands must never throw! Cannot recover from this.");
                }
            }
            else
            {
                canExecute = false;
            }
        }
        
        // unexecute
        while (i > 0)
        {
            if (commands[--i].canUndo())
            {
                commands[i].undo();
            }
            else
            {
                throw new IllegalStateException("A composite command's commands must be undoable! Cannot recover from this.");
            }
        }
        return canExecute;
    }
    
    @Override
    public void execute() {
        for (int i = 0 ; i < commands.length; i++)
        {
            commands[i].execute();
        }
    }

    @Override
    public boolean canUndo() {
        return true;
    }
    
    @Override
    public void undo() {
        for (int i = commands.length - 1 ; i >= 0; i--)
        {
            if (commands[i].canUndo())
            {
                commands[i].undo();
            }
            else
            {
                throw new IllegalStateException("A composite command's commands must be undoable! Cannot recover from this.");
            }
        }
    }

    @Override
    public void visitExecuted(IGameEventListener listener) {
        for (int i = 0 ; i < commands.length; i++)
        {
            commands[i].visitExecuted(listener);
        }
    }

    @Override
    public void visitUndone(IGameEventListener listener) {
        for (int i = commands.length - 1 ; i >= 0; i--)
        {
            commands[i].visitUndone(listener);
        }
    }
}
