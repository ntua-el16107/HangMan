package com.medialab.hangman;

import java.util.*;

public class Round {
    private Game g;

    public Round(Game game){
        this.g = game;
    }

    public boolean checkLetter(char letter) throws InvalidLetterException {
        if (!g.lettersAvailable.contains(letter)) throw new InvalidLetterException(letter);
        boolean success = false;
        StringBuffer answerBuf= new StringBuffer(g.incompleteWord);
        g.lettersAvailable.remove(letter);
        for(int i = 0; i < g.selectedWord.length(); i++) {
            if (Objects.equals(letter, g.selectedWord.charAt(i))) {
                calculateRoundPoints((Integer)previewMap(i).get(letter));
                answerBuf.setCharAt(i, letter);
                g.successCount++;
                success = true;
            }
        }
        if(!success) {
            --g.trialsRemaining;
            calculateRoundPoints(0);
        }
        else {
            g.incompleteWord = answerBuf.toString();
            this.updatePotentialWords();
        }

        return success;
    }

    public Map previewMap(int position){
        if (position<0 || position>20) throw new RuntimeException();
        if (!Objects.equals("_", g.incompleteWord.charAt(position))) throw new RuntimeException();

        //list of letters (inserted more than once) found in this position of potential words
        ArrayList<Character> letters = new ArrayList<Character>();
        Iterator<String> words = g.potentialWords.iterator();
        while(words.hasNext())
            letters.add(words.next().charAt(position));

        //map of letters (inserted once) found in this position of potential words
        Map<Character, Integer> map = new HashMap<Character, Integer>();
        for (Character c : letters){
            if (map.containsKey(c)){
                map.put(c, map.get(c) + 1);
            }else{
                map.put(c, 1);
            }
        }

        //map with key-letters in this position of potential words and values their probability
        int length = map.size();
        for(Character c : map.keySet())
            map.put(c, map.get(c)/length);

        //append letters available and not included
        for (Character key : g.lettersAvailable)
            if (!map.containsKey(key)) map.put(key, 0);

        ValueComparator<Character, Integer> comparator =new ValueComparator<Character, Integer>(map);
        Map<Character, Integer> sortedMap = new TreeMap<Character, Integer> (comparator);
        sortedMap.putAll(map);
        return sortedMap;
    }

    public List previewList(int position){
        Map sortedMap = previewMap(position);
        List<Character> sortedList = new ArrayList<Character> (sortedMap.keySet());
        return sortedList;
    }

    public void updatePotentialWords(){
        for (String word : g.potentialWords) {
            if(g.selectedWord.length() != word.length())
                g.potentialWords.remove(word);
            else
                for(int i = 0; i < g.incompleteWord.length(); i++)
                    if((!Objects.equals("_", g.incompleteWord.charAt(i))) && (!Objects.equals(word.charAt(i), g.incompleteWord.charAt(i))))
                        g.potentialWords.remove(word);
        }
    }

    public void calculateRoundPoints(int probability){
        if(probability >= 0.6) g.totalPoints = g.totalPoints + 5;
        else if(probability >= 0.4) g.totalPoints = g.totalPoints + 10;
        else if(probability >= 0.25) g.totalPoints = g.totalPoints + 15;
        else if(probability > 0) g.totalPoints = g.totalPoints + 30;
        else if(probability == 0) {
            if(g.totalPoints>15)
                g.totalPoints = g.totalPoints - 15;
            else g.totalPoints = 0;
        }
    }

    private static class ValueComparator<K, V extends Comparable<V>> implements Comparator<K> {

        Map<K, V> map;

        public ValueComparator(Map<K, V> base) {
            this.map = base;
        }

        @Override
        public int compare(K o1, K o2) {
            return map.get(o2).compareTo(map.get(o1));
        }
    }
}
