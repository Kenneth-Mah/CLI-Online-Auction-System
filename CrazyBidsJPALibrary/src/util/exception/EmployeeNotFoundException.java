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
public class EmployeeNotFoundException extends Exception{

    public EmployeeNotFoundException() {
    }

    public EmployeeNotFoundException(String string) {
        super(string);
    }
    
}
