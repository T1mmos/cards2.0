package gent.timdemey.cards.serialization.dto.commands;

import java.util.List;

import gent.timdemey.cards.serialization.dto.entities.PlayerDto;

public class C_WelcomeClientDto extends CommandBaseDto
{
    public String serverId;
    public String serverMessage;
    public List<PlayerDto> connected;
}
