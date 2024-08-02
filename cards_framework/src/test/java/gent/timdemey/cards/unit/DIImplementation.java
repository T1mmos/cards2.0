/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.unit;

import gent.timdemey.cards.ICardPlugin;

/**
 *
 * @author Timmos
 */
public class DIImplementation implements IDIInterface 
{
    
    public final ICardPlugin _CardPlugin;
    
    public DIImplementation(ICardPlugin plugin)
    {
        this._CardPlugin = plugin;
    }
}
