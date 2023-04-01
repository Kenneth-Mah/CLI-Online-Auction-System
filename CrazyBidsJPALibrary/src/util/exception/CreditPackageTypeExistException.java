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
public class CreditPackageTypeExistException extends Exception {

    /**
     * Creates a new instance of <code>CreditPackageTypeExistException</code>
     * without detail message.
     */
    public CreditPackageTypeExistException() {
    }

    /**
     * Constructs an instance of <code>CreditPackageTypeExistException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreditPackageTypeExistException(String msg) {
        super(msg);
    }
}
