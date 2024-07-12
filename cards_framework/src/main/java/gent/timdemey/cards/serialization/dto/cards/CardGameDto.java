package gent.timdemey.cards.serialization.dto.cards;

import java.util.List;

import gent.timdemey.cards.serialization.dto.EntityBaseDto;

public class CardGameDto extends EntityBaseDto
{
    public List<PlayerConfigurationDto> playerConfigurations;
}
