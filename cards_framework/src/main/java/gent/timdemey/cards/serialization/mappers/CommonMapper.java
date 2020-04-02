package gent.timdemey.cards.serialization.mappers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class CommonMapper
{
    private static MapperDefs mapper = new MapperDefs();
    {
        // domain objects to DTO
        mapper.addMapping(UUID.class, String.class, CommonMapper::toDto);
        mapper.addMapping(InetAddress.class, String.class, CommonMapper::toDto);

        // DTO to domain object
        mapper.addMapping(String.class, UUID.class, CommonMapper::toUuid);
        mapper.addMapping(String.class, InetAddress.class, CommonMapper::toInetAddress);
    }

    static String toDto(UUID obj)
    {
        String dto = obj.toString();
        return dto;
    }

    static UUID toUuid(String dto)
    {
        UUID uuid = UUID.fromString(dto);
        return uuid;
    }

    static String toDto(InetAddress obj)
    {
        String str = obj.getHostAddress();
        return str;
    }

    static InetAddress toInetAddress(String dto)
    {
        try
        {
            InetAddress domObj = InetAddress.getByName(dto);
            return domObj;
        }
        catch (UnknownHostException e)
        {
            throw new IllegalArgumentException("Cannot map into InetAddress: " + dto);
        }
    }

    static <SRC, DST> List<DST> mapList(MapperDefs mapDefs, List<SRC> srcList, Class<DST> dstClazz)
    {
        List<DST> destList = new ArrayList<>();
        
        for (SRC src : srcList)
        {
            DST dst = mapDefs.map(src, dstClazz);
            destList.add(dst);
        }
        return destList;
    }
}
