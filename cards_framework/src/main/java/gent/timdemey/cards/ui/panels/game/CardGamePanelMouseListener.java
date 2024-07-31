package gent.timdemey.cards.ui.panels.game;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.JComponent;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.contract.ExecutionState;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityList;
import gent.timdemey.cards.services.animation.LayerRange;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.ui.components.ext.IComponent;
import gent.timdemey.cards.ui.components.ext.IHasComponent;
import gent.timdemey.cards.ui.components.swing.JSImage;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.IPanelManager;

class CardGamePanelMouseListener extends MouseAdapter
{

    private final IPanelService _PanelService;
    private final IContextService _ContextService;
    private final IPositionService _PositionService;
    private final CommandFactory _CommandFactory;
    private final Logger _Logger;
    
    
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
    private final List<JComponent> draggedJComps;

    CardGamePanelMouseListener(
            Logger logger,
            IPanelService panelService,
            IContextService contextService,
            IPositionService positionService,
            CommandFactory commandFactory
    )
    {
        this._Logger = logger;
        this._PanelService = panelService;
        this._ContextService = contextService;
        this._PositionService = positionService;
        this._CommandFactory = commandFactory;
        
        dragStates = new ArrayList<>();
        draggedJComps = new ArrayList<>();
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
                JComponent comp = draggedJComps.get(i);
                int card_x = state.xstart + dx;
                int card_y = state.ystart + dy;

                comp.setLocation(card_x, card_y);
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

        IPanelManager pm = _PanelService.getPanelManager(PanelDescriptors.Game);
        
        // filter components, retain only good drag candidates
        JSLayeredPane pane = (JSLayeredPane) e.getSource();
        List<JSImage> comps = pm.getComponentsAt(e.getPoint())
                .stream()
                .filter(c -> c instanceof JSImage)
                .map(c -> (JSImage) c)
                .sorted((c1, c2) -> pane.getLayer((Component) c2) - pane.getLayer((Component) c1)) // sort from highest layer to lowest
                .collect(Collectors.toList());
       
        // nothing to drag here
        if (comps.isEmpty())
        {
            return;
        }
                
        JSImage jsimage = comps.get(0);

        Context context = _ContextService.getThreadContext();

        UUID playerId = context.getReadOnlyState().getLocalId();

        if (jsimage.getComponent().getComponentType().hasTypeName(ComponentTypes.CARD))
        {
            ReadOnlyCard card = (ReadOnlyCard) jsimage.getComponent().getPayload();
            ReadOnlyCardStack stack = card.getCardStack();

            CommandBase cmdPull = _CommandFactory.CreatePull(stack.getId(), card.getId());
            CommandBase cmdUse = _CommandFactory.CreateUse(null, card.getId());
            boolean pullable = canExecute(context, cmdPull, "mousePressed/card");
            boolean useable = canExecute(context, cmdUse, "mousePressed/card");

            boolean pull = pullable && (!useable || useable && e.getClickCount() % 2 == 1);
            boolean use = useable && (!pullable || pullable && e.getClickCount() % 2 == 0);
            
            if (pull)
            {
                List<ReadOnlyCard> cards = stack.getCardsFrom(card);
                int cardCnt = cards.size();
                
                LayerRange range = _PositionService.getDragLayerRange();
                int dragMax = range.layerEnd - range.layerStart + 1;
                if (cardCnt > dragMax)
                {
                    throw new IllegalStateException("Maximum number of dragging components exceeded: " + cardCnt + " > " + dragMax);
                }
                
                for (int i = 0; i < cardCnt; i++)
                {
                    ReadOnlyCard currentCard = cards.get(i);

                    JSImage jsimage_card = (JSImage) pm.getComponent(currentCard);
                    int card_xstart = jsimage_card.getBounds().x;
                    int card_ystart = jsimage_card.getBounds().y;
                    
                    // ensure to stop any animations running for this card
                    pm.stopAnimate(jsimage_card);       
                    pm.getPanel().setLayer(jsimage_card, range.layerStart + i);
                
                    CardDragState dragState = new CardDragState(card_xstart, card_ystart);
                    dragStates.add(dragState);
                    draggedJComps.add(jsimage_card);
                }
            }
            else if (use)
            {
                // stop ongoing animations for the cards that will be used.
                // at this time we assume that all cards starting from the double-clicked card are used.
                // if this is no longer the case then we would need to inspect the C_Use command.
                List<ReadOnlyCard> cards = stack.getCardsFrom(card);
                for (int i = 0; i < cards.size(); i++)
                {
                    ReadOnlyCard currentCard = cards.get(i);
                    JSImage jsimage_card = (JSImage) pm.getComponent(currentCard);
                                     
                    // ensure to stop any animations running for this card. Don't change the layer here
                    // it will either be its normal layer, or the dragged layer if the first click 
                    // made the card(s) go into drag mode. The state listener will set the card's layer
                    // to the animation layer after the model changed (induced by scheduling the use command).
                    pm.stopAnimate(jsimage_card);
                }
                
                context.schedule(cmdUse);
            }

            mouse_xstart = e.getX();
            mouse_ystart = e.getY();
        }
        else if (jsimage.getComponent().getComponentType().hasTypeName(ComponentTypes.CARDSTACK))
        {
            CommandBase cmdUse = _CommandFactory.CreateUse(((ReadOnlyCardStack)jsimage.getComponent().getPayload()).getId(), null);
            if (canExecute(context, cmdUse, "mousePressed/cardstack"))
            {
                context.schedule(cmdUse);
                draggedJComps.clear();
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

        Context context = _ContextService.getThreadContext();
        IPanelManager pm = _PanelService.getPanelManager(PanelDescriptors.Game);

        UUID playerId = context.getReadOnlyState().getLocalId();

        if (!draggedJComps.isEmpty())
        {
            JComponent jcomp = draggedJComps.get(0);
            IComponent comp = ((IHasComponent<?>) jcomp).getComponent();

            int intersectAMax = 0;
            CommandBase cmdMove = null;
            List<UUID> visitedStackIds = new ArrayList<>();

            List<JComponent> overlapComps = pm.getComponentsIn(comp.getAbsCoords().getBounds());
            for (JComponent oljComp : overlapComps)
            {
                if (!(oljComp instanceof JSImage))
                {
                    continue;
                }
                JSImage olimage = (JSImage) oljComp;
                IComponent olComp = olimage.getComponent();

                // exclude overlaps with components being dragged
                if (draggedJComps.contains(olimage))
                {
                    continue;
                }

                // exclude overlaps that are not cards or cardstacks
                ReadOnlyCardStack dstCardStack;
                if (olComp.getComponentType().hasTypeName(ComponentTypes.CARD))
                {
                    dstCardStack = ((ReadOnlyCard) olComp.getPayload()).getCardStack();
                }
                else if (olComp.getComponentType().hasTypeName(ComponentTypes.CARDSTACK))
                {
                    dstCardStack = (ReadOnlyCardStack) olComp.getPayload();
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

                List<ReadOnlyCard> cards = draggedJComps.stream()
                        .map(jc -> (IHasComponent<?>) jc)
                        .map(c -> (ReadOnlyCard) c.getComponent().getPayload())
                        .collect(Collectors.toList());
                ReadOnlyEntityList<ReadOnlyCard> roCards = new ReadOnlyEntityList<>(cards);
                CommandBase cmdPush = _CommandFactory.CreatePush(dstCardStack.getId(), roCards.getIds());
                if (canExecute(context, cmdPush, "mouseReleased"))
                {
                    Rectangle intersection = comp.getAbsCoords().getBounds().intersection(olComp.getAbsCoords().getBounds());
                    int intersectA = intersection.width * intersection.height;
                    if (intersectA < intersectAMax)
                    {
                        continue;
                    }

                    intersectAMax = intersectA;
                    cmdMove = _CommandFactory.CreateMove(cards.get(0).getCardStack().getId(), dstCardStack.getId(), cards.get(0).getId());
                }
            }

            if (cmdMove != null)
            {
                // the cards will be animated towards their destination stack 
                // by the state listener that will react to the model change
                context.schedule(cmdMove);
            }
            else
            {
                // there is no model change so we animate the cards back to their current stack
                IPanelManager gamePanelMgr = _PanelService.getPanelManager(PanelDescriptors.Game);
                for (int i = 0; i < draggedJComps.size(); i++)
                {
                    JComponent jcomp_anim = draggedJComps.get(i);                    
                    gamePanelMgr.startAnimate(jcomp_anim);
                }
            }

            draggedJComps.clear();
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
            _Logger.trace("Cannot execute command %s (%s) because: %s", command.getName(), dragContext, response.reason);
            return false;
        }
        else
        {
            _Logger.error("Cannot execute command %s (%s) because of a state error: %s", command.getName(), dragContext, response.reason);
            return false;
        }
    }
}
