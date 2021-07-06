package gent.timdemey.cards.utils;

import java.util.function.Function;
import java.util.stream.Stream;

public class StreamUtils
{
    public static <A, B> Stream<B> combine(
            Stream<A> streamA, Function<Stream<A>, 
            Stream<B>> streamFuncB)
    {
        return streamFuncB.apply(streamA);
    }
    
    public static <A, B, C> Stream<C> combine(
            Stream<A> streamA, Function<Stream<A>, 
            Stream<B>> streamFuncB, Function<Stream<B>, 
            Stream<C>> streamFuncC)
    {
        return streamFuncC.apply(combine(streamA, streamFuncB));
    }
    
    public static <A, B, C, D> Stream<D> combine(
            Stream<A> streamA, 
            Function<Stream<A>, Stream<B>> streamFuncB, 
            Function<Stream<B>, Stream<C>> streamFuncC, 
            Function<Stream<C>, Stream<D>> streamFuncD)
    {
        return streamFuncD.apply(combine(streamA, streamFuncB, streamFuncC));
    }
}
