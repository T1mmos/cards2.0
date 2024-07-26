/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.model.entities.config;

import gent.timdemey.cards.model.delta.IChangeTracker;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class ConfigurationFactory 
{
    private final IChangeTracker _ChangeTracker;
    
    public ConfigurationFactory(IChangeTracker changeTracker)
    {
        this._ChangeTracker = changeTracker;
    }

    public Configuration CreateConfiguration() 
    {
        return new Configuration(_ChangeTracker, UUID.randomUUID());
    }
    
    public Configuration CreateConfiguration(UUID id) 
    {
        return new Configuration(_ChangeTracker, id);
    }
}
