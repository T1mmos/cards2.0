package gent.timdemey.cards.ui;

import java.util.Arrays;
import java.util.List;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.C_SaveConfig;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyProperty;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.interfaces.IContextService;

public class StateExportListener implements IStateListener
{
    private static final List<ReadOnlyProperty<?>> PersistedProperties = Arrays.asList(
        ReadOnlyState.LocalName
       // ReadOnlyState.ServerName,
    );
        

    @Override
    public void onChange(ReadOnlyChange roChange)
    {
        if (PersistedProperties.contains(roChange.property))
        {
            Services.get(IContextService.class).getThreadContext().schedule(new C_SaveConfig());
        }
    }

}
