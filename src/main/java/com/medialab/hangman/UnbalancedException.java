package com.medialab.hangman;

public class UnbalancedException extends Exception{
    public UnbalancedException(){
        super("Unbalancded dictionary:9-letter words under 20%");
    }
}
