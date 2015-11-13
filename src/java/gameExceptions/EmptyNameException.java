/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gameExceptions;

/**
 *
 * @author EyalEngel
 */
public class EmptyNameException extends RuntimeException {
    
     public EmptyNameException() { 
         super("Error: Can't enter an empty name"); 
     }
    
}
