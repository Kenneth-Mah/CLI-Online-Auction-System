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
public class InvalidStartAndEndDatesException extends Exception {

    /**
     * Creates a new instance of <code>InvalidStartAndEndDatesException</code>
     * without detail message.
     */
    public InvalidStartAndEndDatesException() {
    }

    /**
     * Constructs an instance of <code>InvalidStartAndEndDatesException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidStartAndEndDatesException(String msg) {
        super(msg);
    }
}
