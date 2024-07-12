package gent.timdemey.cards.serialization.mappers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gent.timdemey.cards.serialization.dto.commands.CommandBaseDto;
import gent.timdemey.cards.serialization.gson.CommandDtoAdapter;

class JsonMapper
{
    private static final Gson gson;

    static
    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(CommandBaseDto.class, new CommandDtoAdapter());
        gson = builder.create();
    }

    private JsonMapper()
    {
    }

    static CommandBaseDto toCommandDto(String json)
    {
        CommandBaseDto dto = gson.fromJson(json, CommandBaseDto.class);
        return dto;
    }

    static String toJson(CommandBaseDto dto)
    {
        String json = gson.toJson(dto, CommandBaseDto.class);
        return json;
    }
}
