package gent.timdemey.cards.services.gamepanel;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.contract.ExecutionState;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityList;
import gent.timdemey.cards.services.ICommandService;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IGamePanelService;
import gent.timdemey.cards.services.IScalableImageManager;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.scaleman.IScalableComponent;
import gent.timdemey.cards.services.scaleman.ScalableComponent;

class GamePanelMouseListener extends MouseAdapter
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
    private final List<IScalableComponent> draggedComps;

    GamePanelMouseListener()
    {
        dragStates = new ArrayList<>();
        draggedComps = new ArrayList<>();
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
                IScalableComponent scaleImg = draggedComps.get(i);
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
        if (!isLeftMouseButton(e))
        {
            return;
        }
        
        GamePanel gpMan = (GamePanel) e.getComponent();

        Component comp = gpMan.getComponentAt(e.getPoint());
        if (!(comp instanceof JScalableImage))
        {
            return;
        }

        JScalableImage jscalable = (JScalableImage) comp;
        UUID entityId = Services.get(IScalableImageManager.class).getUUID(jscalable);
        Context context = Services.get(IContextService.class).getThreadContext();
        ICommandService operations = Services.get(ICommandService.class);
        ReadOnlyCardGame cardGame = context.getReadOnlyState().getCardGame();

        UUID playerId = context.getReadOnlyState().getLocalId();

        if (cardGame.isCard(entityId))
        {
            ReadOnlyCard card = cardGame.getCard(entityId);
            ReadOnlyCardStack stack = card.getCardStack();

            CommandBase cmdPull = operations.getPullCommand(playerId, stack.getId(), card.getId());
            CommandBase cmdUse = operations.getUseCommand(playerId, null, entityId);
            boolean pullable = canExecute(context, cmdPull, "mousePressed/card");
            boolean useable = canExecute(context, cmdUse, "mousePressed/card");

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

                    ((GamePanel) currentJcard.getParent()).setLayer(currentJcard, GamePanelService.LAYER_DRAG + i);

                    CardDragState dragState = new CardDragState(card_xstart, card_ystart);
                    dragStates.add(dragState);
                    draggedImages.add(currentJcard);
                }
            }
            else if (use)
            {
                context.schedule(cmdUse);
                draggedImages.clear();
            }

            mouse_xstart = e.getX();
            mouse_ystart = e.getY();

        }
        else if (cardGame.isCardStack(entityId))
        {
            CommandBase cmdUse = operations.getUseCommand(playerId, entityId, null);
            if (canExecute(context, cmdUse, "mousePressed/cardstack"))
            {
                context.schedule(cmdUse);
                draggedImages.clear();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (!isLeftMouseButton(e))
        {
            return;
        }
        
        Context context = Services.get(IContextService.class).getThreadContext();
        ReadOnlyCardGame cardGame = context.getReadOnlyState().getCardGame();

        UUID playerId = context.getReadOnlyState().getLocalId();

        if (!draggedImages.isEmpty())
        {
            JScalableImage rootJCard = draggedImages.get(0);

            int intersectAMax = 0;
            CommandBase cmdMove = null;
            List<UUID> visitedStackIds = new ArrayList<>();
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
                Rectangle intersection = rootJCard.getBounds().intersection(otherScalable.getBounds());
                if (intersection.isEmpty())
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
                
                UUID dstCardStackId = dstCardStack.getId();
                if (visitedStackIds.contains(dstCardStackId))
                {
                    continue;
                }                
                visitedStackIds.add(dstCardStackId);

                List<ReadOnlyCard> cards = draggedImages.stream()
                        .map(jcard -> Services.get(IScalableImageManager.class).getUUID(jcard))
                        .map(cardId -> cardGame.getCard(cardId)).collect(Collectors.toList());
                ReadOnlyEntityList<ReadOnlyCard> roCards = new ReadOnlyEntityList<>(cards);

                ICommandService operationsServ = Services.get(ICommandService.class);
                CommandBase cmdPush = operationsServ.getPushCommand(playerId, dstCardStack.getId(), roCards.getIds());
                if (canExecute(context, cmdPush, "mouseReleased"))
                {
                    int intersectA = intersection.width * intersection.height;
                    if (intersectA < intersectAMax)
                    {
                        continue;
                    }

                    intersectAMax = intersectA;
                    cmdMove = operationsServ.getMoveCommand(playerId, cards.get(0).getCardStack().getId(),
                            dstCardStack.getId(), cards.get(0).getId());

                }
            }

            if (cmdMove != null)
            {
                context.schedule(cmdMove);
            }
            else 
            {
                for (int i = 0; i < draggedImages.size(); i++)
                {
                    JScalableImage scaledImg = draggedImages.get(i);

                    UUID id = Services.get(IScalableImageManager.class).getUUID(scaledImg);
                    ReadOnlyCard card = cardGame.getCard(id);
                    Services.get(IGamePanelService.class).animatePosition(card);
                }
            }

            draggedImages.clear();
            dragStates.clear();
        }
    }
    
    private boolean isLeftMouseButton(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            return true;
        }
        return false;
    }
    
    private boolean canExecute(Context context, CommandBase command, String dragContext)
    {
        CanExecuteResponse response = context.canExecute(command);
        if (response.execState == ExecutionState.Yes)
        {
            return true;
            
        }
        else if (response.execState == ExecutionState.No)
        {
            Logger.trace("Cannot execute command %s (%s) because: %s", command.getName(), dragContext, response.reason);            
            return false;
        }
        else
        {
            Logger.error("Cannot execute command %s (%s) because of a state error: %s", command.getName(), dragContext, response.reason);
            return false;
        }
    }
}
