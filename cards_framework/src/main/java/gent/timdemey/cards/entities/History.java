package gent.timdemey.cards.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class History {

    final List<ICommand> execLine = new ArrayList<>();
    int start = -1;
    int current = -1;

    int execMajorCounter = 0;
    int execMinorCounter = 0;
    boolean execCounterRequested = false;
    
    public int getStart()
    {
        return start;
    }
    
    public int getCurrent()
    {
        return current;
    }
    
    public List<ICommand> getExecutionLine()
    {
        return Collections.unmodifiableList(execLine);
    }
    
    public boolean canUndo ()
    {
        return current >= start && execLine.size() > 0 && execLine.get(current).canUndo();        
    }
    
    public boolean canRedo()
    {
        return current + 1 < execLine.size() && execLine.size() > 0 && execLine.get(current + 1).canExecute();
    }    
    
    /**
     * Returns a sequence number where commands can be easily identified with.
     * @return
     */
    public int newCommandMajorId() {
       /* if (execCounterRequested)
        {
            throw new IllegalStateException("The current command counter is already requested for a command "
                    + "that has not been inserted in the command processor yet, so you're creating commands "
                    + "that you are not executing. You can only create minor commands (commands within "
                    + "composite commands) until the created major command is processed by injecting it in "
                    + "this command processor.");
        }*/
        
        execCounterRequested = true;
        return execMajorCounter++;
    }

    /**
     * For composite commands, the subcommands can retrieve this minor id.
     * @return
     */
    public int newCommandMinorId() {
        return execMinorCounter++;
    }
}
