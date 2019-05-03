package textsnoop.rddigi.com.textstats;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.R.id.list;

/**
 * Created by Admin on 3/20/2017.
 */

class Helper {
//todo change this to no longer accept files cause that isn't what is wanted. DERP

    private final String[] words;
    private final String[] sentences;



    public Helper(String filepath){
        String document = filepath;
        words = wordsplitter(document);
        sentences = sentencesplitter(document);


    }

    //TODO decide if need to instantiate class so the document is passed in at the start and the methods don't need to all take in document


    //Returns a map with the integer representing the number of times a word is found and the string is the value of those words
    public HashMap<String, Integer> individualWordCounts(){
        HashMap<String, Integer> WordList = new HashMap<>();
        for (String word: words){
            if (WordList.containsKey(word)){
                Integer val = WordList.get(word);
                val++;
                WordList.put(word, val);
            } else {
                WordList.put(word, 1);
            }
        }
        return WordList;
    }


    //Returns:Total Words in Document
    //Takes: A string representign the whole document
    //Need to strip off any non letter character
    //Break at whitespaces
    public int wordcount(){
        return words.length;
    }

    public int sentencecount(){
        return sentences.length;
    }

    //Returns: Average sentence length in words
    //Takes: A string representign the whole document
    public int avgsentence(){
        int avg = 0;
        int[] wortotals = new int[sentences.length];
        int i = 0;
        for(String sentence: sentences) {
            String[] wordsinsentence = wordsplitter(sentence);
            wortotals[i] = wordsinsentence.length;
            i++;
        }
        int j;
        for (j = 0; j < wortotals.length; j++)
        {
            avg += wortotals[j];
        }
        avg = avg/wortotals.length;
        return avg;
    }

    //Returns: Min sentence length in words
    //Takes: A string representign the whole document
    public int minsentence(){
        int min;
        Integer[] wortotals = new Integer[sentences.length];
        int i = 0;
        for(String sentence: sentences) {
            String[] wordsinsentence = wordsplitter(sentence);
            wortotals[i] = wordsinsentence.length;
            i++;
        }
        List<Integer> mini = Arrays.asList(wortotals);
        min = Collections.min(mini);
        return min;
    }
    //Returns: Max sentence length in words
    //Takes: A string representign the whole document
    public int maxsentence(){
        int max;
        Integer[] wortotals = new Integer[sentences.length];
        int i = 0;
        for(String sentence: sentences) {
            String[] wordsinsentence = wordsplitter(sentence);
            wortotals[i] = wordsinsentence.length;
            i++;
        }
        List<Integer> maxi = Arrays.asList(wortotals);
        max = Collections.max(maxi);
        return max;
    }




    private String[] wordsplitter(String input){
        String trimmed = input.trim();
        String[] words = trimmed.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            // You may want to check for a non-word character before blindly
            // performing a replacement
            // It may also be necessary to adjust the character class
            words[i] = words[i].replaceAll("[^\\w]", "");
        }

        return words;

    }

    private String[] sentencesplitter(String input){
        String[] sarray =  input.split("\\.|\\?|\\!");
        List<String> slist = new ArrayList<>();
        slist.addAll(Arrays.asList(sarray));
        slist.removeAll(Arrays.asList(null,""," ","  ", "   "));
        String[] stockArr = new String[slist.size()];
        return slist.toArray(stockArr);
    }

    public String[] getSentences(){
        return sentences;
    }




}
