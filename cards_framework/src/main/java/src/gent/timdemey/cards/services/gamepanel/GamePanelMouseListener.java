package gent.timdemey.cards.services.gamepanel;

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
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityList;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.interfaces.ICommandService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IGamePanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IScalableComponentService;
import gent.timdemey.cards.services.scaleman.IScalableComponent;
import gent.timdemey.cards.services.scaleman.comps.CardScalableImageComponent;
import gent.timdemey.cards.services.scaleman.comps.CardStackScalableImageComponent;
import gent.timdemey.cards.services.scaleman.img.ScalableImageComponent;

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

        IPositionService posServ = Services.get(IPositionService.class);
        IScalableComponentService scaleServ = Services.get(IScalableComponentService.class);
        IScalableComponent scaleComp = scaleServ.getComponentAt(e.getPoint());
        IGamePanelService gpServ = Services.get(IGamePanelService.class);

        if (!(scaleComp instanceof ScalableImageComponent))
        {
            return;
        }

        Context context = Services.get(IContextService.class).getThreadContext();
        ICommandService operations = Services.get(ICommandService.class);

        UUID playerId = context.getReadOnlyState().getLocalId();

        if (scaleComp instanceof CardScalableImageComponent)
        {
            CardScalableImageComponent cardImgComp = (CardScalableImageComponent) scaleComp;

            ReadOnlyCard card = cardImgComp.getCard();
            ReadOnlyCardStack stack = card.getCardStack();

            CommandBase cmdPull = operations.getPullCommand(playerId, stack.getId(), card.getId());
            CommandBase cmdUse = operations.getUseCommand(playerId, null, card.getId());
            boolean pullable = canExecute(context, cmdPull, "mousePressed/card");
            boolean useable = canExecute(context, cmdUse, "mousePressed/card");

            boolean pull = pullable && (!useable || useable && e.getClickCount() % 2 == 1);
            boolean use = useable && (!pullable || pullable && e.getClickCount() % 2 == 0);

            if (pull)
            {
                List<ReadOnlyCard> cards = stack.getCardsFrom(card);

                dragStates.clear(); // ensure list is empty (might miss
                                    // mouseReleased event)
                draggedComps.clear();

                for (int i = 0; i < cards.size(); i++)
                {
                    ReadOnlyCard currentCard = cards.get(i);

                    IScalableComponent currScaleImg = Services.get(IScalableComponentService.class).getOrCreateScalableComponent(currentCard);
                    int card_xstart = currScaleImg.getBounds().x;
                    int card_ystart = currScaleImg.getBounds().y;

                    int layer = posServ.getDragLayer();
                    gpServ.setLayer(currScaleImg, layer);
                    gpServ.setZOrder(currScaleImg, i);

                    CardDragState dragState = new CardDragState(card_xstart, card_ystart);
                    dragStates.add(dragState);
                    draggedComps.add(currScaleImg);
                }
            }
            else if (use)
            {
                context.schedule(cmdUse);
                draggedComps.clear();
            }

            mouse_xstart = e.getX();
            mouse_ystart = e.getY();

        }
        else if (scaleComp instanceof CardStackScalableImageComponent)
        {
            CardStackScalableImageComponent csScaleComp = (CardStackScalableImageComponent) scaleComp;
            CommandBase cmdUse = operations.getUseCommand(playerId, csScaleComp.getCardStack().getId(), null);
            if (canExecute(context, cmdUse, "mousePressed/cardstack"))
            {
                context.schedule(cmdUse);
                draggedComps.clear();
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

        UUID playerId = context.getReadOnlyState().getLocalId();

        if (!draggedComps.isEmpty())
        {
            IScalableComponent scaleComp = draggedComps.get(0);

            int intersectAMax = 0;
            CommandBase cmdMove = null;
            List<UUID> visitedStackIds = new ArrayList<>();
            IGamePanelService gpServ = Services.get(IGamePanelService.class);
            IScalableComponentService scaleServ = Services.get(IScalableComponentService.class);

            List<IScalableComponent> overlapComps = scaleServ.getComponentsIn(scaleComp.getBounds());
            for (IScalableComponent comp : overlapComps)
            {
                if (!(comp instanceof ScalableImageComponent))
                {
                    continue;
                }

                // exclude overlaps with components being dragged
                ScalableImageComponent scaleImgComp = (ScalableImageComponent) comp;
                if (draggedComps.contains(scaleImgComp))
                {
                    continue;
                }

                // exclude overlaps that are not cards or cardstacks
                ReadOnlyCardStack dstCardStack;
                if (comp instanceof CardScalableImageComponent)
                {
                    dstCardStack = ((CardScalableImageComponent) comp).getCard().getCardStack();
                }
                else if (comp instanceof CardStackScalableImageComponent)
                {
                    dstCardStack = ((CardStackScalableImageComponent) comp).getCardStack();
                }
                else
                {
                    continue;
                }

                // don't visit the same cardstacks again
                UUID dstCardStackId = dstCardStack.getId();
                if (visitedStackIds.contains(dstCardStackId))
                {
                    continue;
                }
                visitedStackIds.add(dstCardStackId);

                List<ReadOnlyCard> cards = draggedComps.stream().map(sc -> ((CardScalableImageComponent) sc).getCard()).collect(Collectors.toList());
                ReadOnlyEntityList<ReadOnlyCard> roCards = new ReadOnlyEntityList<>(cards);

                ICommandService operationsServ = Services.get(ICommandService.class);
                CommandBase cmdPush = operationsServ.getPushCommand(playerId, dstCardStack.getId(), roCards.getIds());
                if (canExecute(context, cmdPush, "mouseReleased"))
                {
                    Rectangle intersection = scaleComp.getBounds().intersection(scaleImgComp.getBounds());
                    int intersectA = intersection.width * intersection.height;
                    if (intersectA < intersectAMax)
                    {
                        continue;
                    }

                    intersectAMax = intersectA;
                    cmdMove = operationsServ.getMoveCommand(playerId, cards.get(0).getCardStack().getId(), dstCardStack.getId(), cards.get(0).getId());

                }
            }

            if (cmdMove != null)
            {
                context.schedule(cmdMove);
            }
            else
            {
                for (int i = 0; i < draggedComps.size(); i++)
                {
                    CardScalableImageComponent scaleImg = (CardScalableImageComponent) draggedComps.get(i);
                    gpServ.animateCard(scaleImg.getCard());
                }
            }

            draggedComps.clear();
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
