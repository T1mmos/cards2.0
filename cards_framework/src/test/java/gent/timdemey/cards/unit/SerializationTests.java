package gent.timdemey.cards.unit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.helpers.CardGameHelper;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.mock.MockCommandFactory;
import gent.timdemey.cards.mock.MockLogManager;
import gent.timdemey.cards.model.delta.IChangeTracker;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.commands.lobby.P_Enter;
import gent.timdemey.cards.model.entities.commands.lobby.P_HandleGameStarted;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.serialization.mappers.PayloadMapper;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.NopChangeTracker;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Timmos
 */
public class SerializationTests
{   
    private Container WireUp ()
    {
        Container c = new Container();
        
        c.AddSingleton(ILogManager.class, MockLogManager.class);
        c.AddSingleton(IChangeTracker.class, NopChangeTracker.class);
        c.AddSingleton(ContextType.class, ContextType.UI);
        c.AddSingleton(CommandFactory.class, MockCommandFactory.class);
        
        return c;
    }
    
    /**
     * Tests OOTB support in Gson for UUID.
     */
    @Test
    public void uuid()
    {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        
        UUID uuid = UUID.randomUUID();
        
        String uuid_str = gson.toJson(uuid);
        UUID uuid2 = gson.fromJson(uuid_str, UUID.class);
        assertEquals(uuid, uuid2);
    }
    
    /**
     * Tests serialization between PayloadBase and JSON strings.
     */
    @Test
    public void payload()
    {
        Container c = WireUp();        
        PayloadMapper pMapper = c.Get(PayloadMapper.class);        
        
        P_Enter p = new P_Enter();
        p.id = UUID.randomUUID();
        p.clientName = "clientname-test";
        p.creatorContextType = ContextType.Server;
        p.creatorId = UUID.randomUUID();
        
        String p_str = pMapper.toJson(p);
        PayloadBase p2 = pMapper.toPayload(p_str);
        
        P_Enter p_enter = Assertions.assertInstanceOf(P_Enter.class, p2);
        assertEquals(p.id, p_enter.id);
        assertEquals(p.clientName, p_enter.clientName);
        assertEquals(p.creatorContextType, p_enter.creatorContextType);
        assertEquals(p.creatorId, p_enter.creatorId);
    }
    
    /**
     * Tests serialization of a command parameter object that includes nested domain objects 
     * (CardGame, CardStack, Cards).
     */
    @Test
    public void P_HandleGameStarted()
    {
        Container c = WireUp();        
        PayloadMapper pMapper = c.Get(PayloadMapper.class);      
        CardGameHelper cgHelper = c.Get(CardGameHelper.class);
        
        P_HandleGameStarted p = new P_HandleGameStarted();
        p.cardGame = cgHelper.CreateCardGame();
        p.id = UUID.randomUUID();
        p.creatorContextType = ContextType.Server;
        p.creatorId = UUID.randomUUID();
        
        String json = pMapper.toJson(p);
        P_HandleGameStarted p2 = (P_HandleGameStarted) pMapper.toPayload(json);
        
        assertNotNull(p2);
        assertNotNull(p2.id);
        assertNotNull(p2.cardGame);
        assertNotNull(p2.creatorId);
        assertNotNull(p2.creatorContextType);
        assertNotNull(p2.cardGame);
        CardGameHelper.assertEquals(p.cardGame, p2.cardGame);
    }
}
