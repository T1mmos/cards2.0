/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.CommandExecution;
import java.util.List;

/**
 *
 * @author Timmos
 */
public class P_ShowReexecutionFail extends PayloadBase
{

    public List<CommandExecution> fails;
    
}
