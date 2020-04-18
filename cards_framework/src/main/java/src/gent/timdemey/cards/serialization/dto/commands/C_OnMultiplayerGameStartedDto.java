package gent.timdemey.cards.serialization.dto.commands;

import gent.timdemey.cards.serialization.dto.entities.CardGameDto;

public class C_OnMultiplayerGameStartedDto extends CommandBaseDto
{
    public CardGameDto cardGame;
}
