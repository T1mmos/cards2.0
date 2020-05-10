package gent.timdemey.cards.serialization.dto.cards;

import java.util.List;

import gent.timdemey.cards.serialization.dto.EntityBaseDto;

public class PlayerConfigurationDto extends EntityBaseDto
{
    public String playerId;
    public List<CardStackDto> cardStacks;
}
