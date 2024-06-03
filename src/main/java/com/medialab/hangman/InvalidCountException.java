package com.medialab.hangman;

public class InvalidCountException extends Exception {
    public InvalidCountException(String word){
        super("Invalid count: Word: "+word+" registered more than once.");
    }
}
