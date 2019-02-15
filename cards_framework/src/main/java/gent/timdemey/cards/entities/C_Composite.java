package gent.timdemey.cards.entities;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

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
        
    static class CompactConverter extends ASerializer<C_Composite>
    {
        @Override
        protected void write(SerializationContext<C_Composite> sc) {
            List<CommandDto> cmdDtos = sc.src.commands.stream().map(c->new CommandDto(c)).collect(Collectors.toList());
            writeList(sc, PROPERTY_SUBCOMMANDS, cmdDtos);
        }

        @Override
        protected C_Composite read(DeserializationContext dc) 
        {
            List<CommandDto> cmdDtos = readList(dc, PROPERTY_SUBCOMMANDS, CommandDto.class);
            List<ICommand> commands = cmdDtos.stream().map(c->c.command).collect(Collectors.toList());
            return new C_Composite(commands);
        }
                
    }
    
    private final List<ICommand> commands;
    private final CommandType commandType;

    private C_Composite(List<ICommand> commands)
    {
        Preconditions.checkNotNull(commands);
        Preconditions.checkArgument(commands.size() > 0);
        
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
    
    C_Composite(ICommand ... commands)
    {
        this(Arrays.asList(commands));
    }

    @Override
    public CommandType getCommandType() {
        return commandType;
    }
    
    @Override
    public boolean canExecute() {
        boolean canExecute = true;
        int i = 0;
        while (canExecute && i < commands.size())
        {
            if (commands.get(i).canExecute())
            {
                try 
                {
                    commands.get(i).execute();
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
            if (commands.get(--i).canUndo())
            {
                commands.get(i).undo();
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
        for (int i = 0 ; i < commands.size(); i++)
        {
            commands.get(i).execute();
        }
    }

    @Override
    public boolean canUndo() {
        return true;
    }
    
    @Override
    public void undo() {
        for (int i = commands.size() - 1 ; i >= 0; i--)
        {
            if (commands.get(i).canUndo())
            {
                commands.get(i).undo();
            }
            else
            {
                throw new IllegalStateException("A composite command's commands must be undoable! Cannot recover from this.");
            }
        }
    }

    @Override
    public void visitExecuted(IGameEventListener listener) {
        for (int i = 0 ; i < commands.size(); i++)
        {
            commands.get(i).visitExecuted(listener);
        }
    }

    @Override
    public void visitUndone(IGameEventListener listener) {
        for (int i = commands.size() - 1 ; i >= 0; i--)
        {
            commands.get(i).visitUndone(listener);
        }
    }
}
