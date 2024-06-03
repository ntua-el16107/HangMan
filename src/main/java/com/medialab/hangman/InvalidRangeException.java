package com.medialab.hangman;

public class InvalidRangeException extends Exception{
    public InvalidRangeException(){ super("No words under six letters");}
}
