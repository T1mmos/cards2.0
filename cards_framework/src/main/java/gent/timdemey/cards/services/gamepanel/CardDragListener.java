package gent.timdemey.cards.services.gamepanel;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityList;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IGamePanelManager;
import gent.timdemey.cards.services.ICommandService;
import gent.timdemey.cards.services.IPositionManager;
import gent.timdemey.cards.services.IScalableImageManager;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.scaleman.JScalableImage;

class CardDragListener extends MouseAdapter
{

    private static class CardDragState
    {
        private final int xstart;
        private final int ystart;

        private CardDragState(int xstart, int ystart)
        {
            this.xstart = xstart;
            this.ystart = ystart;
        }
    }

    private int mouse_xstart;
    private int mouse_ystart;

    private final List<CardDragState> dragStates;
    private final List<JScalableImage> draggedImages;

    CardDragListener()
    {
        dragStates = new ArrayList<>();
        draggedImages = new ArrayList<>();
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {

        if (!dragStates.isEmpty())
        {
            int mouse_x = e.getX();
            int mouse_y = e.getY();

            int dx = mouse_x - mouse_xstart;
            int dy = mouse_y - mouse_ystart;

            for (int i = 0; i < dragStates.size(); i++)
            {
                CardDragState state = dragStates.get(i);
                JScalableImage scaleImg = draggedImages.get(i);
                int card_x = state.xstart + dx;
                int card_y = state.ystart + dy;

                scaleImg.setLocation(card_x, card_y);
            }

        }
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {

    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        GamePanel gpMan = (GamePanel) e.getComponent();

        Component comp = gpMan.getComponentAt(e.getPoint());
        if (!(comp instanceof JScalableImage))
        {
            return;
        }

        JScalableImage jscalable = (JScalableImage) comp;
        UUID id = Services.get(IScalableImageManager.class).getUUID(jscalable);
        Context context = Services.get(IContextService.class).getThreadContext();
        ICommandService operations = Services.get(ICommandService.class);
        ReadOnlyCardGame cardGame = context.getReadOnlyState().getCardGame();

        if (cardGame.isCard(id))
        {
            ReadOnlyCard card = cardGame.getCard(id);
            ReadOnlyCardStack stack = card.getCardStack();

            CommandBase cmdPull = operations.getPullCommand(stack.getId(), card.getId());
            CommandBase cmdUse = operations.getCardUseCommand(id);
            boolean pullable = context.canExecute(cmdPull);
            boolean useable = context.canExecute(cmdUse);

            boolean pull = pullable && (!useable || useable && e.getClickCount() % 2 == 1);
            boolean use = useable && (!pullable || pullable && e.getClickCount() % 2 == 0);

            if (pull)
            {
                List<ReadOnlyCard> cards = stack.getCardsFrom(card);

                dragStates.clear(); // ensure list is empty (might miss mouseReleased event)
                draggedImages.clear();

                for (int i = 0; i < cards.size(); i++)
                {
                    ReadOnlyCard currentCard = cards.get(i);
                    JScalableImage currentJcard = Services.get(IScalableImageManager.class)
                            .getJScalableImage(currentCard.getId());
                    int card_xstart = currentJcard.getLocation().x;
                    int card_ystart = currentJcard.getLocation().y;

                    ((GamePanel) currentJcard.getParent()).setLayer(currentJcard, 10000 + i);

                    CardDragState dragState = new CardDragState(card_xstart, card_ystart);
                    dragStates.add(dragState);
                    draggedImages.add(currentJcard);
                }
            }
            else if (use)
            {
                context.schedule(cmdUse);
            }

            mouse_xstart = e.getX();
            mouse_ystart = e.getY();

        }
        else if (cardGame.isCardStack(id))
        {
            ReadOnlyCardStack cardStack = Services.get(IPositionManager.class).getCardStackAt(e.getPoint());
            if (cardStack != null)
            {
                CommandBase cmdUse = operations.getCardStackUseCommand(id);
                if (context.canExecute(cmdUse))
                {
                    context.schedule(cmdUse);
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        Context context = Services.get(IContextService.class).getThreadContext();
        ReadOnlyCardGame cardGame = context.getReadOnlyState().getCardGame();

        if (!draggedImages.isEmpty())
        {
            // check if first card is over another card or an empty card stack
            boolean pushed = false;

            JScalableImage rootJCard = draggedImages.get(0);

            for (Component comp : rootJCard.getParent().getComponents()) // inefficient?
            {
                if (!(comp instanceof JScalableImage))
                {
                    continue;
                }
                JScalableImage otherScalable = (JScalableImage) comp;
                if (draggedImages.contains(otherScalable))
                {
                    continue;
                }
                if (!rootJCard.getBounds().intersects(otherScalable.getBounds()))
                {
                    continue;
                }

                // intersection with a JScalableImage
                UUID id = Services.get(IScalableImageManager.class).getUUID(otherScalable);
                ReadOnlyCardStack dstCardStack;
                if (cardGame.isCard(id))
                {
                    dstCardStack = cardGame.getCard(id).getCardStack();
                }
                else if (cardGame.isCardStack(id))
                {
                    dstCardStack = cardGame.getCardStack(id);
                }
                else
                {
                    continue;
                }

                List<ReadOnlyCard> cards = draggedImages.stream()
                        .map(jcard -> Services.get(IScalableImageManager.class).getUUID(jcard))
                        .map(cardId -> cardGame.getCard(cardId)).collect(Collectors.toList());
                ReadOnlyEntityList<ReadOnlyCard> roCards = new ReadOnlyEntityList<>(cards);

                ICommandService operationsServ = Services.get(ICommandService.class);
                CommandBase cmdPush = operationsServ.getPushCommand(dstCardStack.getId(), roCards.getIds());
                if (context.canExecute(cmdPush))
                {
                    CommandBase cmdMove = operationsServ.getMoveCommand(cards.get(0).getCardStack().getId(),
                            dstCardStack.getId(), cards.get(0).getId());
                    context.schedule(cmdMove);
                    pushed = true;
                    break;
                }
            }
            /*
             * if (!pushed) { // try empty card stacks below the dragged card
             * List<CardStack> cardStacks =
             * Services.get(IPositionManager.class).getCardStacksIn(rootJCard.getBounds());
             * List<Card> cards = draggedImages.stream() .map(jcard -> (Card)
             * Services.get(IScalableImageManager.class).getManagedObject(jcard))
             * .collect(Collectors.toList()); for (CardStack cardStack : cardStacks) { if
             * (!cardStack.isEmpty()) { continue; } if
             * (Services.get(IGameOperations.class).canPush(cardStack, cards)) {
             * Services.get(IGameOperations.class).move(cards.get(0).getCardStack(),
             * cardStack, cards.get(0)); pushed = true; break; } } }
             */
            if (!pushed)
            {
                for (int i = 0; i < draggedImages.size(); i++)
                {
                    JScalableImage scaledImg = draggedImages.get(i);

                    UUID id = Services.get(IScalableImageManager.class).getUUID(scaledImg);
                    ReadOnlyCard card = cardGame.getCard(id);
                    Services.get(IGamePanelManager.class).animatePosition(card);
                }
            }

            draggedImages.clear();
            dragStates.clear();
        }
    }
}
