package com.medialab.hangman;

public class InvalidLetterException extends Exception{
    public InvalidLetterException(Character letter){
        super("Letter " + letter +" is not available");
    }

}
