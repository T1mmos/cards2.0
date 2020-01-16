package gent.timdemey.cards.serialization.mappers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gent.timdemey.cards.serialization.dto.commands.CommandDto;
import gent.timdemey.cards.serialization.gson.CommandDtoAdapter;

class JsonMapper
{
    private static final Gson gson;

    static
    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(CommandDto.class, new CommandDtoAdapter());
        gson = builder.create();
    }

    private JsonMapper()
    {
    }

    static CommandDto toCommandDto(String json)
    {
        CommandDto dto = gson.fromJson(json, CommandDto.class);
        return dto;
    }

    static String toJson(CommandDto dto)
    {
        String json = gson.toJson(dto, CommandDto.class);
        return json;
    }
}
