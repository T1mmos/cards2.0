package gent.timdemey.cards.services.context;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.state.State;

abstract class CommandExecutorBase implements ICommandExecutor
{
    private final class CommandTask implements Runnable, Comparable<CommandTask>
    {
        private final CommandBase command;
        private final State state;
        private final long time_issued;
        private final boolean highprio;

        private CommandTask(CommandBase command, State state, boolean highprio)
        {
            if (command == null)
            {
                throw new IllegalArgumentException("command");
            }

            this.command = command;
            this.state = state;
            this.time_issued = System.currentTimeMillis();
            this.highprio = highprio;
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

        @Override
        public int compareTo(CommandTask o)
        {
            if (highprio == o.highprio)
            {
                return (int) (time_issued - o.time_issued);
            }
            else if (highprio)
            {
                return -1;
            }
            else 
            {
                return +1;
            }
        }
    }

    private final BlockingQueue<Runnable> incomingCommandQueue;
    private final ExecutorService executor;

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
        CommandTask task = new CommandTask(command, state, false);
        executor.execute(task);
    }
    
    @Override
    public final void run(CommandBase command, State state)
    {
        CommandTask task = new CommandTask(command, state, true);
        executor.execute(task);
    }

    protected abstract void execute(CommandBase command, State state);
    
    @Override
    public void shutdown()
    {
        executor.shutdown();
        try
        {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

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
