package com.medialab.hangman;

public class UndersizeException extends Exception{
    public UndersizeException(){
        super("Undersized dictionary: Word set of dictionary is under 20 words.");
    }
}
