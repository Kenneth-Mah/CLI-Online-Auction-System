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
public class UpdateAddressException extends Exception {

    /**
     * Creates a new instance of <code>UpdateAddressException</code> without
     * detail message.
     */
    public UpdateAddressException() {
    }

    /**
     * Constructs an instance of <code>UpdateAddressException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateAddressException(String msg) {
        super(msg);
    }
}
