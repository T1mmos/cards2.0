package gent.timdemey.cards.serialization.dto.cards;

import java.util.List;

import gent.timdemey.cards.serialization.dto.EntityBaseDto;

public final class CardStackDto extends EntityBaseDto
{
    public List<CardDto> cards;
    public String cardStackType;
    public int typeNumber;
}
