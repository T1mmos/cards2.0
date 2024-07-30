/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.config.ConfigurationFactory;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.net.NetworkFactory;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.IConfigurationService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFileService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.INetworkService;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class SolCommandFactory extends CommandFactory
{
    
    public SolCommandFactory(
        ICardPlugin cardPlugin, 
        ICardGameService cardGameService, 
        StateFactory stateFactory, 
        NetworkFactory networkFactory, 
        ConfigurationFactory configurationFactory, 
        IConfigurationService configurationService, 
        IContextService contextService, 
        IFrameService frameService, 
        INetworkService networkService, 
        IFileService fileService, 
        CommandDtoMapper commandDtoMapper, 
        Loc loc, 
        Logger logger)
    {
        super(cardPlugin, cardGameService, stateFactory, networkFactory, configurationFactory, configurationService, contextService, frameService, networkService, fileService, commandDtoMapper, loc, logger);
    }

    @Override
    public C_Move CreateMove(UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        return new C_SolMove(_ContextService, this, UUID.randomUUID(), srcCardStackId, dstCardStackId, cardId);
    }

    @Override
    public C_Move CreateMove(UUID id, UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        return new C_SolMove(_ContextService, this, id, srcCardStackId, dstCardStackId, cardId);
    }

    @Override
    public C_Push CreatePush(UUID dstCardStackId, List<UUID> srcCardIds)
    {
         return new C_SolPush(_ContextService, UUID.randomUUID(), dstCardStackId, srcCardIds);
    }

    @Override
    public C_Push CreatePush(UUID id, UUID dstCardStackId, List<UUID> srcCardIds)
    {
        return new C_SolPush(_ContextService, id, dstCardStackId, srcCardIds);
    }

    @Override
    public C_Use CreateUse(UUID initiatorCardStackId, UUID initiatorCardId)
    {
        return new C_SolUse(_ContextService, this, UUID.randomUUID(), initiatorCardStackId, initiatorCardId);
    }
    
    @Override
    public C_Use CreateUse(UUID id, UUID initiatorStackId, UUID initiatorCardId)
    {
        return new C_SolUse(_ContextService, this, id, initiatorStackId, initiatorCardId);
    }
    
    @Override
    public C_Pull CreatePull(UUID cardStackId, UUID cardId)
    {
        return new C_SolPull(_ContextService, UUID.randomUUID(), cardStackId, cardId);
    }

    @Override
    public C_Pull CreatePull(UUID id, UUID cardStackId, UUID cardId)
    {
        return new C_SolPull(_ContextService, id, cardStackId, cardId);
    }


}
