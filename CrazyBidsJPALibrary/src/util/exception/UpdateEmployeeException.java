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
public class UpdateEmployeeException extends Exception {

    /**
     * Creates a new instance of <code>UpdateEmployeeException</code> without
     * detail message.
     */
    public UpdateEmployeeException() {
    }

    /**
     * Constructs an instance of <code>UpdateEmployeeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateEmployeeException(String msg) {
        super(msg);
    }
}
