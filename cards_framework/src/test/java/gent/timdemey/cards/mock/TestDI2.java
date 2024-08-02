/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.mock;

import gent.timdemey.cards.ICardPlugin;

/**
 *
 * @author Timmos
 */
public class TestDI2 implements ITestDI2 
{
    
    public final ICardPlugin _CardPlugin;
    
    public TestDI2(ICardPlugin plugin)
    {
        this._CardPlugin = plugin;
    }
}
