package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.services.contract.descriptors.ConfigKeyDescriptor;

public interface IConfigurationService
{
    public <T> T parse(ConfigKeyDescriptor<T> key, String input);
}
