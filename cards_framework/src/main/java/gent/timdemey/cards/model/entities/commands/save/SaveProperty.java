package gent.timdemey.cards.model.entities.commands.save;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import gent.timdemey.cards.model.entities.commands.payload.P_SaveState;
import gent.timdemey.cards.model.state.State;

public class SaveProperty
{
    public final BiFunction<State, P_SaveState, Boolean> canExecFunc;
    public final BiConsumer<State, P_SaveState> execFunc;
    
    public SaveProperty(BiFunction<State, P_SaveState, Boolean> canExecFunc, BiConsumer<State, P_SaveState> execFunc)
    {
        this.canExecFunc = canExecFunc;
        this.execFunc = execFunc;
    }
}