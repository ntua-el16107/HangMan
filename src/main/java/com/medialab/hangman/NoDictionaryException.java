package com.medialab.hangman;

public class NoDictionaryException extends Exception{
    public NoDictionaryException(){
        super("Dictionary not selected");
    }
}
