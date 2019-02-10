package gent.timdemey.cards.entities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class Json {
     
    static 
    {
        // all builders
        GsonBuilder verboseBuilder = new GsonBuilder();
        GsonBuilder flatBuilder = new GsonBuilder();
        GsonBuilder prettyBuilder = new GsonBuilder();
        
        // register all common mappers
        ConfigureCommon(verboseBuilder);
        ConfigureCommon(flatBuilder);
        ConfigureCommon(prettyBuilder);
        
        ConfigureFlat(flatBuilder);
        ConfigurePretty(prettyBuilder);
        
        prettyBuilder.setPrettyPrinting();
        
        VERBOSE = verboseBuilder.create();
        FLAT = flatBuilder.create();        
        PRETTY = prettyBuilder.create();
    }
    
    private static final Gson VERBOSE; // for over the wire
    private static final Gson PRETTY;  // for debugging
    private static final Gson FLAT;    // for small messages e.g. logging
    
    public static Gson getVerbose()
    {
        return VERBOSE;
    }
    
    public static Gson getPretty()
    {
        return PRETTY;
    }
    
    public static Gson getFlat()
    {
        return FLAT;
    }
    
    private static void ConfigureCommon(GsonBuilder builder)
    {
        // entities

        builder.registerTypeAdapter(E_CardGame.class, new E_CardGame.Converter());
        builder.registerTypeAdapter(E_CardStack.class, new E_CardStack.Converter());
        builder.registerTypeAdapter(E_Card.class, new E_Card.CompactConverter());
        builder.registerTypeAdapter(Player.class, new Player.CompactConverter());
        
        // commands
        builder.registerTypeAdapter(CommandEnvelope.class, new CommandEnvelope.CompactConverter());
        builder.registerTypeAdapter(MetaInfo.class, new MetaInfo.CompactConverter());
        builder.registerTypeAdapter(C_Composite.class, new C_Composite.CompactConverter());
        builder.registerTypeAdapter(C_JoinGame.class, new C_JoinGame.CompactConverter());
        builder.registerTypeAdapter(C_Move.class, new C_Move.CompactConverter());
        builder.registerTypeAdapter(C_PlayerJoined.class, new C_PlayerJoined.CompactConverter());
        builder.registerTypeAdapter(C_StartGame.class, new C_StartGame.CompactConverter());
        builder.registerTypeAdapter(C_UDP_HelloServer.class, new C_UDP_HelloServer.CompactConverter());
        builder.registerTypeAdapter(C_UDP_HelloClient.class, new C_UDP_HelloClient.CompactConverter());
        builder.registerTypeAdapter(C_WelcomeClient.class, new C_WelcomeClient.CompactConverter());        
    }
    
    private static void ConfigurePretty(GsonBuilder builder)
    {
        builder.registerTypeAdapter(E_Card.class, new E_Card.CompactConverter());
    }
    
    private static void ConfigureFlat(GsonBuilder builder)
    {
        builder.registerTypeAdapter(E_Card.class, new E_Card.CompactConverter());
    }
    
    public static String send (CommandEnvelope envelope)
    {
        return Json.getFlat().toJson(envelope);
    }
    
    public static CommandEnvelope receive(String envelope) throws Exception
    {
        return Json.getFlat().fromJson(envelope, CommandEnvelope.class);
    }
}
