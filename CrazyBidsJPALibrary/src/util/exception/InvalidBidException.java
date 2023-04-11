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
public class InvalidBidException extends Exception {

    /**
     * Creates a new instance of <code>AddressNotFoundException</code> without
     * detail message.
     */
    public InvalidBidException() {
    }

    /**
     * Constructs an instance of <code>AddressNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidBidException(String msg) {
        super(msg);
    }
}
