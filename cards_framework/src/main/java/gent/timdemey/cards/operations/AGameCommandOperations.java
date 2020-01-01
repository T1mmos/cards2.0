package gent.timdemey.cards.operations;

import java.net.InetAddress;
import java.util.List;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.commands.C_CreateServer;
import gent.timdemey.cards.model.commands.C_Move;
import gent.timdemey.cards.model.commands.C_StartGame;
import gent.timdemey.cards.model.commands.C_StopGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IGameOperationsService;

public class AGameCommandOperations implements IGameOperationsService {
    
    @Override
    public boolean canStartGame(C_StartGame command) {
        return Services.get(IGameOperationsService.class).canStartGame();
    }

    @Override
    public boolean canStopGame(C_StopGame command) {
        return Services.get(IGameOperationsService.class).canStopGame();
    }
    
    @Override
    public boolean canMove(C_Move command) {
        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getCardGameState().getCardGame();
        
        ReadOnlyCardStack srcCardStack = cardGame.getCardStack(command.srcCardStackId);
        ReadOnlyCardStack dstCardStack = cardGame.getCardStack(command.dstCardStackId);
        ReadOnlyCard card = cardGame.getCard(command.cardId);
        
        return Services.get(IGameOperationsService.class).canMove(srcCardStack, dstCardStack, card);
    }
}
