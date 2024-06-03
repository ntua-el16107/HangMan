package com.medialab.hangman;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class Dictionary implements java.io.Serializable{

    private final String dictionary_id;
    private Set<String> wordSet;
    private int longwords;

    public Set<String> getWordSet(){
        return wordSet;
    }

    public int getWordSetSize(){
        return wordSet.size();
    }

    public Dictionary(String name){
        this.dictionary_id = name;
        this.wordSet = new HashSet<>();
        this.longwords = 0;
    }

    public static void  createDictionaryFile(String id) throws JSONException, IOException, InvalidCountException, InvalidRangeException, UndersizeException, UnbalancedException {
        var d = new Dictionary(id);
        ReadJson reader = new ReadJson();
        JSONObject json = reader.readJsonFromUrl("https://openlibrary.org/works/"+id+".json");
        String sourceTxt = json.getJSONObject("description").getString("value");
        StringTokenizer st = new StringTokenizer(sourceTxt);
        while (st.hasMoreTokens())
            d.addWord(st.nextToken());
        if (d.valid()) serialize(d);
    }

    public static Dictionary loadDictionaryFile(String id) throws IOException, ClassNotFoundException {
        File file = new File("/Users/Tonaras/IdeaProjects/HangMan/src/main/resources/com/medialab/hangman/medialab"+id);
        return deserialize(file);
    }

    public void addWord(String word) throws InvalidCountException {
        if (word.length() >= 6) {
            if (!this.wordSet.add(word.toUpperCase()))
                throw new InvalidCountException(word);
            else if (word.length() >= 9) this.longwords++;
        }
    }

    public static void serialize(Dictionary d) throws IOException {
        try {
            FileOutputStream fileOut = new FileOutputStream("hangman_" + d.dictionary_id);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(d.wordSet);
            out.close();
            fileOut.close();
        }catch(IOException i)
        {
            i.printStackTrace();
            throw i;
        }
    }

    public static Dictionary deserialize(File f) throws IOException, ClassNotFoundException {
        String st = f.getName();
        Dictionary d = new Dictionary(st.substring(8));
        try {
            FileInputStream fileIn = new FileInputStream(f);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            d.wordSet = (Set)in.readObject();
            in.close();
            fileIn.close();
        }catch(IOException i)
        {
            i.printStackTrace();
            throw i;
        }
        catch (ClassNotFoundException c)
        {
            System.out.println("createDictionary class not found");
            c.printStackTrace();
            throw c;
        }
        return d;
    }

    public String selectRandomWord(){
        //convert HashSet to an array
        String[] arrayStrings = wordSet.toArray(new String[wordSet.size()]);
        //generate a random number
        Random rndm = new Random();
        // this will generate a random number between 0 and
        // HashSet.size - 1
        int rndmNumber = rndm.nextInt(wordSet.size());
        // get the element at random number index
        wordSet.remove(arrayStrings[rndmNumber]);
        return arrayStrings[rndmNumber];
    }

    public boolean valid() throws InvalidRangeException, UndersizeException, UnbalancedException {
        if (!validRange()) throw new InvalidRangeException();
        else if (!validSize()) throw new UndersizeException();
        else if (!balanced()) throw new UnbalancedException();
        return true;
    }

    public boolean validRange(){
        for (String word : wordSet)
            if (word.length() < 6) return false;
        return true;
    }

    public boolean validSize(){
        return wordSet.size() >= 20;
    }

    public boolean balanced(){
        return ((float)longwords/wordSet.size() >= 0.2);
    }
}
