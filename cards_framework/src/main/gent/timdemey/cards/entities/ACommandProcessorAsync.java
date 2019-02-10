package gent.timdemey.cards.entities;

import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

abstract class ACommandProcessorAsync implements ICommandProcessor 
{
    protected static final Charset UDP_CHARSET = Charset.forName("UTF8");
    
    protected final class CommandTask implements Runnable, Comparable<CommandTask>
    {
        private final ICommand command;
        private final long time_issued;
        
        protected CommandTask(ICommand command)
        {
            this.command = command;
            this.time_issued = System.currentTimeMillis();
        }

        @Override
        public void run() {
            execute(command);
        }

        @Override
        public int compareTo(CommandTask o) {
            int typediff =  this.command.getCommandType().ordinal() - o.command.getCommandType().ordinal();
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
    
    protected ACommandProcessorAsync() {
        this.incomingCommandQueue = new PriorityBlockingQueue<>();
        this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, incomingCommandQueue, new ThreadFactory() {
            
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, getThreadName());
            }
        });
    }
    
    @Override
    public final void schedule(ICommand command) 
    {
        executor.execute(new CommandTask(command));
    }
    
    protected abstract void execute(ICommand command);
        
    protected abstract String getThreadName();  
}
