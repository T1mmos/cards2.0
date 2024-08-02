/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.server;

import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.services.context.ICommandExecutor;

/**
 *
 * @author Timmos
 */
public class StartServer
{

    private final ICommandExecutor _CommandExecutor;
    private final CommandFactory _CommandFactory;
    
    public StartServer (ICommandExecutor commandExecutor, CommandFactory commandFactory)
    {
        this._CommandExecutor = commandExecutor;
        this._CommandFactory = commandFactory;
    }
    
    public void startServer()
    {                   
        _CommandExecutor.schedule(_CommandFactory.CreateHelloWorld());
    }
}
