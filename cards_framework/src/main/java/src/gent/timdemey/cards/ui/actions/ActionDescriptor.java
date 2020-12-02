package gent.timdemey.cards.ui.actions;

import javax.swing.KeyStroke;

public class ActionDescriptor
{
    public final String id;
    public final KeyStroke accelerator;
    
    public ActionDescriptor(String id, String accelerator)
    {
        this.id = id;
        this.accelerator = accelerator != null ? KeyStroke.getKeyStroke(accelerator) : null;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        ActionDescriptor other = (ActionDescriptor) obj;
        if(id == null)
        {
            if(other.id != null)
                return false;
        }
        else if(!id.equals(other.id))
            return false;
        return true;
    }
    
    @Override
    public String toString()
    {
        return String.format("ActionDescriptor { id=%s, accelerator=%s }", id, accelerator);  
    }
}
