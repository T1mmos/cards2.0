package gent.timdemey.cards.entities;

import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;

abstract class ACommandProcessorAsync implements ICommandProcessor 
{
    protected static final Charset UDP_CHARSET = Charset.forName("UTF8");
    
    private final class CommandTask implements Runnable, Comparable<CommandTask>
    {
        private final CommandEnvelope envelope;
        private final ICommand command;
        private final long time_issued;
        
        private CommandTask(CommandEnvelope envelope)
        {
            Preconditions.checkNotNull(envelope);            
            
            this.envelope = envelope;
            this.command = envelope.command;
            this.time_issued = System.currentTimeMillis();
        }
        
        private CommandTask(ICommand command)
        {
            Preconditions.checkNotNull(command);
            
            this.envelope = null;
            this.command = command;
            this.time_issued = System.currentTimeMillis();
        }

        @Override
        public void run() {
            CommandEnvelope env;
            if (envelope == null) // it's a schedule
            {
                 env = CommandEnvelope.createCommandEnvelope(command);
            }
            else
            {
                 env = envelope;
            }
            execute(env);            
        }

        @Override
        public int compareTo(CommandTask o) {
            ICommand localCmd = this.envelope != null ? this.envelope.command : command;
            ICommand otherCmd = o.envelope != null ? o.envelope.command : o.command;
            int typediff =  localCmd.getCommandType().ordinal() - otherCmd.getCommandType().ordinal();
            if (typediff != 0)
            {
                return typediff;
            }
                        
            // same type, look at issue time, lower time means higher priority
            return (int) (o.time_issued - this.time_issued);
        }
    }
    
    private final ThreadPoolExecutor executor;
    private final BlockingQueue<Runnable> incomingCommandQueue;
    
    protected ACommandProcessorAsync() 
    {
        this.incomingCommandQueue = new PriorityBlockingQueue<>();
        this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, incomingCommandQueue, new ThreadFactory() 
        {
            
            @Override
            public Thread newThread(Runnable r) 
            {
                return new Thread(r, getThreadName());
            }
        });
    }

    @Override
    public void reschedule(CommandEnvelope envelope)
    {
        CommandTask task = new CommandTask(envelope);
        executor.execute(task);
    }

    @Override
    public void schedule(ICommand command) 
    {
        CommandTask task = new CommandTask(command);
        executor.execute(task);
    }
    
    protected abstract void execute(CommandEnvelope envelope);
        
    protected abstract String getThreadName();  
}
