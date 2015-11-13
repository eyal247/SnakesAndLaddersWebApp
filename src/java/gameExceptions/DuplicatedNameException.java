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
public class DuplicatedNameException extends RuntimeException {
    
    public DuplicatedNameException(){
        super("Error: Duplicate names entered");
    }
    
}
