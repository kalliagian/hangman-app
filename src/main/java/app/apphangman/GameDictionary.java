package app.apphangman;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class GameDictionary {
    private Dict dictionary;
    private String word;
    private List<Map<Character,Integer>> potentialChars;
    private Boolean[] found;
    private ArrayList<String> potentialwordlist;


    public GameDictionary(Dict dict)
    {
//        this.wordlist = Utilities.readFile(dictionaryID);
        this.dictionary = dict;
        ArrayList<String> wordlist = this.dictionary.getWordlist();
        this.word = wordlist.get((new Random()).nextInt(wordlist.size()));
        this.found = new Boolean[word.length()];
        this.potentialwordlist = new ArrayList<>();
        this.potentialChars = new ArrayList<Map<Character, Integer>>();
        for(String curr_word: wordlist) {
            if(curr_word.length() == this.word.length()) {
                this.potentialwordlist.add(curr_word);
            }
        }
        System.out.println(this.word);
        System.out.println(potentialwordlist);
        for(int i = 0; i < word.length(); i++) {
            found[i] = false;
            HashMap<Character,Integer> letters = new HashMap<>();
            HashMap<Character,Integer> sortedLetters;
//            char c = word.charAt(i);
            for(String curr_word: potentialwordlist) {
//                if(c == curr_word.charAt(i)) {
                char c = curr_word.charAt(i);

                letters.compute(c, (k, v) -> v == null ? 1 : v + 1);
//                }
            }
            sortedLetters = sortLetters(letters);
            this.potentialChars.add(sortedLetters);

        }

    }

    public boolean allFound() {
        for (int i = 0; i < found.length; i++) {
            if (!found[i]) return false;
        }
        return true;
    }

    public String getWord() {
        return word;
    }

    public String getWordSolved() {
        int i;
        String wordcurr = "";
        for(i = 0; i < word.length() - 1; i++){
            wordcurr = wordcurr + word.charAt(i) + "  ";
        }
        wordcurr = wordcurr + word.charAt(i);
        return wordcurr;
    }

    public String getWordcurrentstate() {
        int i;
        String wordcurr = "";
        for(i = 0; i < word.length() - 1; i++){
            if (found[i]) wordcurr = wordcurr + word.charAt(i) + "  ";
            else wordcurr = wordcurr + "_  ";
        }
        if (found[i]) wordcurr = wordcurr + word.charAt(i);
        else wordcurr = wordcurr + "_";
        return wordcurr;
    }

    public List<Map<Character, Integer>> getPotentialChars() {
        return potentialChars;
    }

    public double updateDictionary(char l, int i) { //"coordinate" object
        if (i >= word.length() || i < 0 ) return -2;
        if (found[i] == true) return -1;
        if (word.charAt(i) != l) {
            updatePotentialwordlist(i, l); //+potential chars?
            return 0;
        }
        int sum = potentialChars.get(i).get(l);

        int count = getNoPotentialWords();
        double prob = sum * 1.0 / count;
        found[i] = true;
        updatePotentialwordlist(i, ' ');
        return prob;

    }

    private void updatePotentialwordlist(int i, char letter) {
        ArrayList<String> tobeRemoved = new ArrayList<>();

        for(String curr_word: potentialwordlist) {
            if((letter == ' ' && word.charAt(i) != curr_word.charAt(i)) || letter == curr_word.charAt(i)) {
                tobeRemoved.add(curr_word);
            }
        }
        for(String deleteWord: tobeRemoved) {
            this.potentialwordlist.remove(deleteWord);
        }
        System.out.println(potentialwordlist);
        this.potentialChars = new ArrayList<Map<Character, Integer>>();

        for(int ii = 0; ii < word.length(); ii++) { //////
            HashMap<Character,Integer> letters = new HashMap<>();
            HashMap<Character,Integer> sortedLetters;
            for(String curr_word: potentialwordlist) {
                char c = curr_word.charAt(ii);

                letters.compute(c, (k, v) -> v == null ? 1 : v + 1);

            }
            sortedLetters = sortLetters(letters);
            this.potentialChars.add(sortedLetters);

        }
    }

    private static HashMap<Character,Integer> sortLetters(HashMap<Character,Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<Character,Integer>> list = new LinkedList<Map.Entry<Character,Integer>>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Character, Integer> >() {
            public int compare(Map.Entry<Character, Integer> o1,
                               Map.Entry<Character, Integer> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        }.reversed());

        // put data from sorted list to hashmap
        HashMap<Character, Integer> temp = new LinkedHashMap<Character, Integer>();
        for (Map.Entry<Character, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public int getNoPotentialWords() {
        return potentialwordlist.size();
    }

}
