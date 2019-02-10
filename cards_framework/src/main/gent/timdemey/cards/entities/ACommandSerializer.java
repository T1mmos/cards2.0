package gent.timdemey.cards.entities;

abstract class ACommandSerializer<T extends ICommand> extends ASerializer<T>
{    
    @Override
    protected final void write(SerializationContext<T> sc) 
    {
        writeMetaInfo(sc);
        writeCommandType(sc);
        writeCommand(sc);
    }
    
    @Override
    protected final T read (DeserializationContext dc)
    {
        MetaInfo metaInfo = readMetaInfo(dc);
        CommandType commandType = readCommandType(dc);
        return readCommand(dc, metaInfo);
    }
    
    protected abstract void writeCommand (SerializationContext<T> sc);
    protected abstract T readCommand (DeserializationContext dc, MetaInfo metaInfo);

    private final void writeMetaInfo(SerializationContext<T> sc)
    {
        sc.obj.add(PROPERTY_META_INFO, sc.context.serialize(sc.src.getMetaInfo()));
    }
    
    private final MetaInfo readMetaInfo(DeserializationContext dc)
    {
        return dc.context.deserialize(dc.obj.get(PROPERTY_META_INFO), MetaInfo.class);
    }
    
    protected final void writeCommandType(SerializationContext<T> sc)
    {
        writeString(sc, PROPERTY_COMMAND_TYPE, sc.src.getCommandType().toString());
    }
    
    protected final CommandType readCommandType(DeserializationContext dc)
    {
        return CommandType.valueOf(readString(dc, PROPERTY_COMMAND_TYPE));
    }
}
