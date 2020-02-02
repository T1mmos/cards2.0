package gent.timdemey.cards.services;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.commands.CommandBase;

public interface ICommandService
{
    public CommandBase getMoveCommand(UUID srcCardStackId, UUID dstCardStackId, UUID cardId);

    public CommandBase getCardUseCommand(UUID srcCardId);

    public CommandBase getCardStackUseCommand(UUID srcCardStackId);

    public CommandBase getPullCommand(UUID srcCardStackId, UUID cardId);

    public CommandBase getPushCommand(UUID dstCardStackId, List<UUID> srcCardIds);
    
    /**
     * Indicates if the given command is a synchronized command, meaning that the execution
     * order is decided upon by the server and that all clients need to execute them in the
     * this order.
     * @param command
     * @return
     */
    public boolean isSyncedCommand(CommandBase command);
    
    /**
     * Indicates if the given command class are blueprints for synchronized commands, meaning that the execution
     * order is decided upon by the server and that all clients need to execute them in the
     * this order.
     * @param command
     * @return
     */
    public boolean isSyncedCommand(Class<? extends CommandBase> clazz);
}
