/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.model.entities.commands.payload;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class P_StartServer extends PayloadBase
{
    public UUID localId;
    public String localName;
    public String srvname;
    public String srvmsg;
    public int udpPort;
    public int tcpPort;
    public boolean autoconnect;
}
