/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.model.net;

import java.net.InetAddress;

/**
 *
 * @author Timmos
 */
public interface IUdpMessageListener 
{
    public void OnMessageReceived(InetAddress sourceAddress, int sourcePort, String msg);
}
