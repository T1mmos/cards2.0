package gent.timdemey.cards.services.cardgame;

public class SolShowCardStackType {
    
    // add stack types
    public static final String DEPOT = "DEPOT";
    public static final String TURNOVER = "TURNOVER";
    
    /**
     * The stacks where 13 cards are put, each player has one. 
     * When a player empties his special stack, he wins the game. Cards may be 
     * pulled one at a time from it, when such cards can be put down on a different
     * stack (e.g. the laydown or middle stacks).
     * Both players have their private set of these stacks.
     */
    public static final String SPECIAL = "SPECIAL";
    
    /**
     * The card stacks where cards ultimately are layed down, you cannot get back
     * cards on these stacks. There are 8 in total, and are the only type of 
     * card stacks that are shared between the players.
     */
    public static final String LAYDOWN = "LAYDOWN";
    
    /**
     * The stacks where cards are put down in a Solitaire fashion: a red card
     * must be stacked on top of a black one and vica versa. A card must be stacked 
     * on top of "one higher" card e.g. a Queen on top of a King. Any card may be dropped
     * onto an empty card stack.
     * Both players have their private set of these stacks.
     */
    public static final String MIDDLE = "MIDDLE";
     
    private SolShowCardStackType () 
    {
        
    }
} 
