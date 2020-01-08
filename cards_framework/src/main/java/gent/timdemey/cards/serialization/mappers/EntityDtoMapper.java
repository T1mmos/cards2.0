package gent.timdemey.cards.serialization.mappers;

import java.util.ArrayList;
import java.util.UUID;

import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardStack;
import gent.timdemey.cards.model.cards.Suit;
import gent.timdemey.cards.model.cards.Value;
import gent.timdemey.cards.serialization.dto.entities.CardDto;
import gent.timdemey.cards.serialization.dto.entities.CardStackDto;

class EntityDtoMapper
{
	private static MapperBase mapper = new MapperBase();
	{
		// domain objects to DTO
		mapper.addMapping(CardStack.class, CardStackDto.class, EntityDtoMapper::toDto);
		mapper.addMapping(Card.class, CardDto.class, EntityDtoMapper::toDto);
		mapper.addMapping(Suit.class, String.class, suit -> suit.toString());
		mapper.addMapping(Value.class, String.class, value -> value.toString());

		// DTO to domain object
		mapper.addMapping(CardStackDto.class, CardStack.class, EntityDtoMapper::toDomainObject);
		mapper.addMapping(CardDto.class, Card.class, EntityDtoMapper::toDomainObject);
		mapper.addMapping(String.class, Suit.class, str -> Suit.valueOf(str));
		mapper.addMapping(String.class, Value.class, str -> Value.valueOf(str));
	}

	private EntityDtoMapper()
	{
		
	}
	
	static CardStackDto toDto(CardStack cardStack)
	{
		CardStackDto dto = new CardStackDto();

		dto.id = mapper.map(cardStack.id, String.class);
		dto.cardStackType = cardStack.cardStackType;
		dto.typeNumber = cardStack.typeNumber;

		dto.cards = new ArrayList<CardDto>(cardStack.cards.size());
		for (Card card : cardStack.cards)
		{
			CardDto cardDto = mapper.map(card, CardDto.class);
			dto.cards.add(cardDto);
		}

		return dto;
	}

	static CardStack toDomainObject(CardStackDto dto)
	{
		UUID id = CommonMapper.toUuid(dto.id);
		CardStack cardStack = new CardStack(id, dto.cardStackType, dto.typeNumber);
		for (CardDto cardDto : dto.cards)
		{
			Card card = mapper.map(cardDto, Card.class);

			// domain model link
			card.cardStackRef.set(cardStack);

			cardStack.cards.add(card);
		}
		return cardStack;
	}

	static CardDto toDto(Card card)
	{
		CardDto cardDto = new CardDto();

		cardDto.id = CommonMapper.toDto(card.id);
		cardDto.suit = mapper.map(card.suit, String.class);
		cardDto.value = mapper.map(card.value, String.class);
		cardDto.visible = card.visibleRef.get();

		return cardDto;
	}

	static Card toDomainObject(CardDto dto)
	{
		UUID id = mapper.map(dto.id, UUID.class);
		Suit suit = mapper.map(dto.suit, Suit.class);
		Value value = mapper.map(dto.value, Value.class);
		boolean visible = dto.visible;

		Card card = new Card(id, suit, value, visible);
		return card;
	}

}
