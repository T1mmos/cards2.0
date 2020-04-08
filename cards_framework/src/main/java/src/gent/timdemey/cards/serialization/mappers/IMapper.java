package gent.timdemey.cards.serialization.mappers;

public interface IMapper
{
    public <SRC, DST> DST map(SRC src, Class<DST> dstClazz);
}
