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
public class EmployeeAlreadyExistException extends Exception{

    public EmployeeAlreadyExistException() {
    }

    public EmployeeAlreadyExistException(String string) {
        super(string);
    }
    
}
