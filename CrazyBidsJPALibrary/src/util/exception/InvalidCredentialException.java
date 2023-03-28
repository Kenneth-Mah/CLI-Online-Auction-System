/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author yeowh
 */
public class InvalidCredentialException extends Exception{

    public InvalidCredentialException() {
    }

    public InvalidCredentialException(String string) {
        super(string);
    }
    
}
