package gent.timdemey.cards.services.contract.descriptors;

public class FileDescriptor
{
    private final String id;
    
    FileDescriptor(String id)
    {
        this.id = id;
    }
    
    @Override
    public String toString()
    {
        return String.format("FileDescriptor { id=%s }", id);  
    }
}
