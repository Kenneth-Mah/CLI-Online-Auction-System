package util.exception;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yeowh
 */
public class CustomerAlreadyExistException extends Exception{

    public CustomerAlreadyExistException() {
    }

    public CustomerAlreadyExistException(String string) {
        super(string);
    }    
}
