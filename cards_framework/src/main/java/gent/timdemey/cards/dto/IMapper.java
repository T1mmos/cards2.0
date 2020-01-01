package gent.timdemey.cards.dto;

public interface IMapper
{
    public <SRC,DST> DST map(SRC src, Class<DST> dstClazz);
}
