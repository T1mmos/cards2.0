package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.game.C_Use;
import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.game.P_Use;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.services.contract.descriptors.SolitaireComponentTypes;

public class C_SolUse extends C_Use
{
    private final CommandFactory _CommandFactory;
    
    public C_SolUse(
        Container container, CommandFactory commandFactory, 
        P_Use parameters)
    {
        super(container, parameters);
        
        this._CommandFactory = commandFactory;
    }

    @Override
    protected CommandBase resolveCommand()
    {
        CardGame cardGame = _State.getCardGame();
        List<CommandBase> eligible = new ArrayList<>();
        UUID localId = _Context.getReadOnlyState().getLocalId();

        if(initiatorStackId != null)
        {
            CardStack initiatorStack = cardGame.getCardStack(initiatorStackId);

            String cardStackType = initiatorStack.cardStackType;

            if(cardStackType.equals(SolitaireComponentTypes.DEPOT))
            {
                if(initiatorStack.getCards().isEmpty()) // direction turnover -> depot, all cards
                {
                    CardStack srcCardStack = cardGame.getCardStack(localId, SolitaireComponentTypes.TURNOVER, 0);
                    CardStack dstCardStack = cardGame.getCardStack(localId, SolitaireComponentTypes.DEPOT, 0);
                    if(!srcCardStack.getCards().isEmpty())
                    {
                        eligible.add(_CommandFactory.CreateMove(srcCardStack.id, dstCardStack.id, srcCardStack.getLowestCard().id));
                    }
                }
                else // direction depot -> turnover, 1 card
                {
                    CardStack srcCardStack = cardGame.getCardStack(localId, SolitaireComponentTypes.DEPOT, 0);
                    CardStack dstCardStack = cardGame.getCardStack(localId, SolitaireComponentTypes.TURNOVER, 0);
                    Card card = srcCardStack.getHighestCard();
                    eligible.add(_CommandFactory.CreateMove(srcCardStack.id, dstCardStack.id, card.id));
                }
            }

        }
        else
        {
            Card initiatorCard = cardGame.getCard(initiatorCardId);
            CardStack initiatorStack = initiatorCard.cardStack;
            String cardStackType = initiatorStack.cardStackType;

            if(cardStackType.equals(SolitaireComponentTypes.TURNOVER) || cardStackType.equals(SolitaireComponentTypes.MIDDLE))
            {
                if(!initiatorStack.getCards().isEmpty())
                {
                    Card card = initiatorStack.getHighestCard();
                    if(card.visibleRef.get())
                    {
                        for (CardStack dstCardStack : cardGame.getCardStacks(localId, SolitaireComponentTypes.LAYDOWN))
                        {
                            eligible.add(_CommandFactory.CreateMove(initiatorStack.id, dstCardStack.id, card.id));
                        }
                    }
                    else
                    {
                        List<Card> cards = initiatorStack.getCardsFrom(card);
                        eligible.add(_CommandFactory.CreateSetVisible(cards, true));
                    }
                }
            }
            else if (cardStackType.contentEquals(SolitaireComponentTypes.DEPOT))
            {
                Card card = initiatorStack.getHighestCard();
                CardStack dstCardStack = cardGame.getCardStack(localId, SolitaireComponentTypes.TURNOVER, 0);
                eligible.add(_CommandFactory.CreateMove(initiatorStack.id, dstCardStack.id, card.id));
            }
        }
        for (CommandBase cmd : eligible)
        {
            if(cmd.canExecute().canExecute())
            {
                return cmd;
            }
        }
        return null;
    }
}
