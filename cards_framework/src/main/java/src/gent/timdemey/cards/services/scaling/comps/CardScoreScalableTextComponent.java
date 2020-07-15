package gent.timdemey.cards.services.scaling.comps;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.services.contract.GetCardScaleInfoRequest;
import gent.timdemey.cards.services.contract.GetCardScoreScaleInfoRequest;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.scaling.text.ScalableFontResource;
import gent.timdemey.cards.services.scaling.text.ScalableTextComponent;

public class CardScoreScalableTextComponent extends ScalableTextComponent
{
    private final ReadOnlyCard card;
    
    public CardScoreScalableTextComponent (UUID id, String text, ScalableFontResource fontRes, ReadOnlyCard card)
    {
        super(id, text, fontRes);
        
        this.card = card;
    }
    
    public ReadOnlyCard getCard()
    {
        return card;
    }

    @Override
    public void update()
    {
        // text is set at constructor time and doesn't change with model changes
    }
    
    @Override
    protected int getFontHeight()
    {
        IPositionService posServ = Services.get(IPositionService.class);
        GetCardScoreScaleInfoRequest infoReq = new GetCardScoreScaleInfoRequest();
        int height = posServ.getDimension(infoReq).height;
        return height;
    }
}
