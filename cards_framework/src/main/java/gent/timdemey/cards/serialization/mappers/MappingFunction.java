package gent.timdemey.cards.serialization.mappers;

/**
 * Functional interface providing one method to map a SRC type to a DST type.
 * 
 * @author Tim
 *
 * @param <SRC>
 * @param <DST>
 */
public interface MappingFunction<SRC, DST>
{
    DST map(SRC src);
}
