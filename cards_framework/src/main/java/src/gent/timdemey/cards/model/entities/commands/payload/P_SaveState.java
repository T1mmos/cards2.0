package gent.timdemey.cards.model.entities.commands.payload;

import java.util.function.Supplier;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.services.panels.settings.Settings;

public class P_SaveState extends PayloadBase
{
    public Supplier<Settings> settingsSupplier;
}
