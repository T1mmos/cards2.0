package gent.timdemey.cards.services.execution;

import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.ICommandExecutionService;
import gent.timdemey.cards.services.context.ContextType;

abstract class CommandExecutionServiceBase implements ICommandExecutionService
{
    protected static final Charset UDP_CHARSET = Charset.forName("UTF8");

    private final class CommandTask implements Runnable, Comparable<CommandTask>
    {
        private final CommandBase command;
        private final State state;
        private final long time_issued;

        private CommandTask(CommandBase command, State state)
        {
            Preconditions.checkNotNull(command);

            this.command = command;
            this.state = state;
            this.time_issued = System.currentTimeMillis();
        }

        @Override
        public void run()
        {
            // if the command has no source id set yet, it means
            // it is incoming from the local context and not from
            // a TCP connection.
            if (command.getSourceId() == null)
            {
                command.setSourceId(state.getLocalId());
            }

            execute(command, state);
        }

        @Override
        public int compareTo(CommandTask o)
        {
            return (int) (o.time_issued - this.time_issued);
        }
    }

    private final BlockingQueue<Runnable> incomingCommandQueue;
    private final ThreadPoolExecutor executor;
    private final ContextType contextType;

    protected CommandExecutionServiceBase(ContextType contextType)
    {
        this.contextType = contextType;
        this.incomingCommandQueue = new PriorityBlockingQueue<>();
        this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, incomingCommandQueue, new ThreadFactory()
        {
            @Override
            public Thread newThread(Runnable r)
            {
                return new CommandExecutionThread(contextType);
            }
        });
    }

    @Override
    public void schedule(CommandBase command, State state)
    {
        CommandTask task = new CommandTask(command, state);
        executor.execute(task);
    }

    protected abstract void execute(CommandBase command, State state);
}
