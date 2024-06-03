package com.medialab.hangman;

import java.io.IOException;
import java.util.*;

/**
 *Game is a class for describing the games created in the application.
 * A Game object encapsulates the operations executed throughout the hangman
 * game as well as the state information needed for those operations.
 * This state information includes:
 * <ul>
 * <li> The number of successful letter trials
 * <li> The number of trials remaining
 * <li> THe total points earned by the player
 * <li> The list of letters available to choose from
 * <li> The dictionary selected for the game
 * <li> The word selected from the dictionary
 * <li> The current incomplete word of letters found so far
 * <li> The set of words from the dictionary compatible with the current incomplete word
 * <li> The current round
 * <li>The <code>Truple</code> class containing information about a finished game
 * <li> The list of  of up to five most recent game <code>Truples</code>
 * </ul>
 * <p>
 *
 *
 *
 * @author      Antonis Agoris
 * @version     %I%, %G%
 * @since       1.0
 */
public class Game {
    protected byte successCount;
    protected byte trialsRemaining;
    protected int totalPoints;
    protected List<Character> lettersAvailable;
    protected Dictionary dict;
    protected String selectedWord;
    protected String incompleteWord = "";
    protected Set<String> potentialWords= new HashSet<>();
    protected Round round;

    /**
     *Static class <code>Truple</code> of Game class used for registering the <code>word</code>,
     * the number of <code>trials</code> and the <code>winner</code> in each game.
     *
     * @since     1.0
     */
    protected static class Truple{
        String word;
        Integer trials;
        String winner;

        /**
         *Constructor of <code>Truple</code> objects created.
         * @param word
         * @param trials
         * @param winner
         *
         * @since     1.0
         */
        public Truple(String word, Integer trials, String winner) {
            this.word = word;
            this.trials = trials;
            this.winner = winner;
        }

        /**
         *This method returns a string with the attributes of the object.
         * @return  <code>String<code/> with the objects concatenated.
         *
         * @since     1.0
         */
        public String returnTruple(){
            return this.word + " " + this.trials.toString() + " " + this.winner;
        }
    }

    /**
     * Static LinkedHashMap field that contains up to five most recent Truple
     * objects that were registered.
     *
     * @since     1.0
     */
    protected static LinkedHashMap<String, Truple> Games = new LinkedHashMap<>()
    {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Truple> eldest)
        {
            return this.size() > 5;
        }
    };

    /**
     *This method initializes the field lettersAvailable to a Character list
     * containing all the letters of the english alphabet in capitals.
     *
     * @since     1.0
     */
    public void initializeLettersAvailable(){
        lettersAvailable.clear();
        for(char chr= 'A'; chr <= 'Z'; chr++){
            lettersAvailable.add(chr);
        }
    }

    /**
     *Constructor of the Game class creating a game object with a specific
     * dictionary passed as a parameter. This constructor is called in case user
     * selects the Load item in the Application Menu Bar while playing another game
     * or in the beginning of the application starting up.
     * @param id                        the dictionary id to be loaded
     * @throws IOException              If an input or output exception occurred
     * @throws ClassNotFoundException   If an exception with input stream object occurred
     *
     *
     * @since     1.0
     */
    public Game(String id) throws IOException, ClassNotFoundException {
        this.successCount = 0;
        this.trialsRemaining = 6;
        this.totalPoints = 0;
        this.initializeLettersAvailable();
        round = new Round(this);
        this.dict = Dictionary.loadDictionaryFile(id);
        this.selectedWord = this.dict.selectRandomWord();
        this.potentialWords.addAll(this.dict.getWordSet());
        for(int i = 0; i < this.selectedWord.length(); i++)
            this.incompleteWord = this.incompleteWord.concat("_");
    }

    /**
     *Constructor of the Game class creating a  new game object passing as a parameter
     * the object of the game running before. This constructor is called in case user
     * selects the Start item from the Application Menu Bar while playing another game
     * or automatically when there is a win/loss.
     * @param before    game played before
     *
     * @since     1.0
     */
    public Game(Game before) {
        this.successCount = 0;
        this.trialsRemaining = 6;
        this.totalPoints = 0;
        this.initializeLettersAvailable();
        round = new Round(this);
        this.dict = before.dict;
        this.selectedWord = this.dict.selectRandomWord();
        this.potentialWords.addAll(this.dict.getWordSet());
        for(int i = 0; i < this.selectedWord.length(); i++)
            this.incompleteWord = this.incompleteWord.concat("_");
    }

    /**
     *This method checks if the incomplete word is completed and if so registers a winning
     * Truple in the Games LinkedHashMap.
     * @return     <code>true<code/> if the incomplete word is completed;
     *              <code>false<code/> otherwise.
     *
     *  @since     1.0
     */
    public boolean win() {
        if(!this.incompleteWord.contains("_")) {
            var t = new Truple(this.selectedWord, (6 - this.trialsRemaining), "me");
            Games.put(this.selectedWord, t);
            return true;
        }
        return false;
    }

    /**
     *This method checks if the remaining trials are over and if so registers a losing
     *Truple in the Games LinkedHashMap.
     * @return      <code>true<code/> if the remaining trials are over;
     *              <code>false<code/> otherwise.
     *
     * @since     1.0
     */
    public boolean gameOver(){
        if(this.trialsRemaining == 0) {
            var t = new Truple(this.selectedWord, (6 - this.trialsRemaining), "computer");
            Games.put(this.selectedWord, t);
            return true;
        }
        return false;
    }

    /**
     *This method registers a losing Truple in the Game LinkedHashMap and returns
     * the solution of the game.
     * @return      <code>String<code/> of the selected word of the game.
     *
     * @since     1.0
     */
    public String giveUp(){
        var t = new Truple(this.selectedWord, (6 - this.trialsRemaining), "computer");
        Games.put(this.selectedWord, t);
        return selectedWord;
    }

    /**
     *This method calculates the number of word in the dictionary of the game
     * with 6 letters, with 7-9 letters, with 10 letters and above and passes
     * those into a list of three integers.
     * @return      list of 3 integers with amount of word of different sizes.
     *
     * @since     1.0
     */
    public ArrayList<Integer> statistics(){
        ArrayList<Integer> l = new ArrayList<>(3);
        int big = 0, huge = 0, enormous = 0;
        Iterator<String> word = this.dict.getWordSet().iterator();
        while(word.hasNext()){
            if (word.next().length() >= 10) enormous++;
            else if(word.next().length() >=7) huge++;
            else if(word.next().length() == 6) big++;
        }
        l.add(big);
        l.add(huge);
        l.add(enormous);
        return l;
    }



}
