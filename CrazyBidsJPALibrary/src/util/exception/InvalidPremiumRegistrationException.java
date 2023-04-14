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
public class InvalidPremiumRegistrationException extends Exception {

    /**
     * Creates a new instance of
     * <code>InvalidPremiumRegistrationException</code> without detail message.
     */
    public InvalidPremiumRegistrationException() {
    }

    /**
     * Constructs an instance of
     * <code>InvalidPremiumRegistrationException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public InvalidPremiumRegistrationException(String msg) {
        super(msg);
    }
}
