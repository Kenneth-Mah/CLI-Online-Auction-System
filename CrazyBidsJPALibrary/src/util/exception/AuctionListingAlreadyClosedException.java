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
public class AuctionListingAlreadyClosedException extends Exception {

    /**
     * Creates a new instance of <code>AuctionListingNameExistException</code>
     * without detail message.
     */
    public AuctionListingAlreadyClosedException() {
    }

    /**
     * Constructs an instance of <code>AuctionListingNameExistException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public AuctionListingAlreadyClosedException(String msg) {
        super(msg);
    }
}
