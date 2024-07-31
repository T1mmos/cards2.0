/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.di;

/**
 *
 * @author Timmos
 */
public class DIException extends RuntimeException 
{
    DIException(String message) 
    {
        super(message);
    }

    DIException(Exception ex) 
    {
        super(ex);
    }
    
}
