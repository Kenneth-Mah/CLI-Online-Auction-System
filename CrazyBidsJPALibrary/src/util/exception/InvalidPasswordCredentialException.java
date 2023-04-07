/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author kenne
 */
public class InvalidPasswordCredentialException extends Exception {

    /**
     * Creates a new instance of <code>InvalidPasswordCredentialException</code>
     * without detail message.
     */
    public InvalidPasswordCredentialException() {
    }

    /**
     * Constructs an instance of <code>InvalidPasswordCredentialException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidPasswordCredentialException(String msg) {
        super(msg);
    }
}
