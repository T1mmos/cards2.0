package gent.timdemey.cards.serialization.dto.commands;

import gent.timdemey.cards.serialization.dto.entities.PlayerDto;

public class C_OnLobbyPlayerJoinedDto extends CommandBaseDto
{
    public PlayerDto player;
}
