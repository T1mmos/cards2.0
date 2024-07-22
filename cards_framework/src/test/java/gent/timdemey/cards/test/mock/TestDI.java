/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.test.mock;

import gent.timdemey.cards.ICardPlugin;

/**
 *
 * @author Timmos
 */
public class TestDI implements ITestDI 
{
    
    public final ICardPlugin _CardPlugin;
    
    public TestDI(ICardPlugin plugin)
    {
        this._CardPlugin = plugin;
    }
}
