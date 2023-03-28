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
public class BiddingAuctionNotYetStartException extends Exception{

    public BiddingAuctionNotYetStartException() {
    }

    public BiddingAuctionNotYetStartException(String string) {
        super(string);
    }    
}
