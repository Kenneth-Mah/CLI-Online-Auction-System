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
public class PasswordOrUsernameWrong extends Exception{

    public PasswordOrUsernameWrong() {
    }

    public PasswordOrUsernameWrong(String string) {
        super(string);
    }
    
}