package gent.timdemey.cards.services.context;

import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.state.State;

abstract class CommandExecutorBase implements ICommandExecutor
{
    protected static final Charset UDP_CHARSET = Charset.forName("UTF8");

    private final class CommandTask implements Runnable
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
            if(command.getSourceId() == null)
            {
                command.setSourceId(state.getLocalId());
            }

            execute(command, state);
        }
    }

    private final BlockingQueue<Runnable> incomingCommandQueue;
    private final Executor executor;

    protected CommandExecutorBase(ContextType contextType)
    {
        this.incomingCommandQueue = new PriorityBlockingQueue<>();
        this.executor = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, incomingCommandQueue, new ThreadFactory()
        {
            @Override
            public Thread newThread(Runnable r)
            {
                return new CommandExecutionThread(r, contextType);
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

    @Override
    public final void addExecutionListener(IExecutionListener executionListener)
    {
        throw new UnsupportedOperationException("Currently ExecutionListeners are not supported in the this executor");
    }

    @Override
    public final void removeExecutionListener(IExecutionListener executionListener)
    {
        throw new UnsupportedOperationException("Currently ExecutionListeners are not supported in the this executor");
    }
}
