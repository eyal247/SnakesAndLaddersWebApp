/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameExceptions;

/**
 *
 * @author shaiyahleba
 */
public class FailedLoadingFileException extends RuntimeException {
    
    public FailedLoadingFileException(String message) {
        super(message);
    }
}
