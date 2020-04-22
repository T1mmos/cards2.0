package gent.timdemey.cards.serialization.dto.commands;

import java.util.List;

import gent.timdemey.cards.serialization.dto.entities.PlayerDto;

public class C_OnLobbyWelcomeDto extends CommandBaseDto
{
    public String clientId;
    public String serverId;
    public String serverMessage;
    public List<PlayerDto> connected;
    public String lobbyAdminId;
}
