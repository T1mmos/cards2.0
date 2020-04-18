package gent.timdemey.cards.utils;

import java.util.function.Supplier;

public class Lazy<T>
{
    private Supplier<T> creatorFunc;
    private T wrappee = null;
    
    public Lazy(Supplier<T> creatorFunc)
    {
        if (creatorFunc == null)
        {
            throw new NullPointerException("creatorFunc must be non-null");
        }
        
        this.creatorFunc = creatorFunc;
    }
    
    public T get()
    {
        if (wrappee == null)
        {
            wrappee = creatorFunc.get();
        }
        return wrappee;
    }
}
