package textsnoop.rddigi.com.textstats;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

/**
 * Created by Admin on 3/20/2017.
 */

class Languageanalytics {


    private POSModel posModel;
    POSTagger posInstance = null;
    private final List<String> words;


    public Languageanalytics(String[] input, Context mycontext) {
        words = new ArrayList();
        try {
            System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
            AssetManager am = mycontext.getAssets();
            //AssetFileDescriptor fileDescriptor = am.openFd("models/en-pos-maxent.bin");
            AssetFileDescriptor fileDescriptor = am.openFd("models/en-pos-perceptron.bin");
            FileInputStream stream = fileDescriptor.createInputStream();
            posModel = new POSModel(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (posModel != null) {
                POSTaggerME tagger = new POSTaggerME(posModel);
                if (tagger != null) {
                    // Call Sentence Detector
                    for (String sentence : input) {
                        String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
                                .tokenize(sentence);

                        String[] tags = tagger.tag(whitespaceTokenizerLine);
                        for (int i = 0; i < whitespaceTokenizerLine.length; i++) {
                            String word = whitespaceTokenizerLine[i].trim();
                            String tag = tags[i].trim();
                            words.add(word + ":" + tag);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public HashMap<String, Integer> countPOS() {
        HashMap<String, Integer> WordList = new HashMap<>();
        for (String word : words) {
            String pos = POS(word);
            if (WordList.containsKey(pos)) {
                Integer val = WordList.get(pos);
                val++;
                WordList.put(pos, val);
            } else {
                WordList.put(pos, 1);
            }
        }
        return WordList;
    }

    public ArrayList<Map<String , Integer>> countWordsbyPOS(){
        Map<String,Integer> Pronoun = new HashMap<>();
        Map<String,Integer> Adjective = new HashMap<>();
        Map<String,Integer> Noun = new HashMap<>();
        Map<String,Integer> Determiner = new HashMap<>();
        Map<String,Integer> Interjection = new HashMap<>();
        Map<String,Integer> Verb = new HashMap<>();
        Map<String,Integer> ConOrPrepo = new HashMap<>();
        Map<String,Integer> Adverb = new HashMap<>();
        Map<String,Integer> Particle = new HashMap<>();
        Map<String,Integer> Other = new HashMap<>();
        for (String word : words) {
            if (word.contains(":WP") || word.contains(":PRP")) {
                PutErThere(Pronoun, word);
            } else if (word.contains(":JJ")) {
                PutErThere(Adjective, word);
            } else if (word.contains(":NN")) {
                PutErThere(Noun, word);
            } else if (word.contains(":DT") || word.contains(":PDT") || word.contains(":WDT")) {
                PutErThere(Determiner, word);
            } else if (word.contains(":UH")) {
                PutErThere(Interjection, word);
            } else if (word.contains(":VB")) {
                PutErThere(Verb, word);
            } else if (word.contains(":CC") || word.contains(":IN") || word.contains(":TO")) {
                PutErThere(ConOrPrepo, word);
            } else if (word.contains(":RB") || word.contains(":WRB")) {
                PutErThere(Adverb, word);
            } else if (word.contains(":RP")) {
                PutErThere(Particle, word);
            } else {
                PutErThere(Other, word);
            }
        }
        ArrayList<Map<String , Integer>> Listofmaps  = new ArrayList<>();
        Listofmaps.add(Pronoun);
        Listofmaps.add(Adjective);
        Listofmaps.add(Noun);
        Listofmaps.add(Determiner);
        Listofmaps.add(Interjection);
        Listofmaps.add(Verb);
        Listofmaps.add(ConOrPrepo);
        Listofmaps.add(Adverb);
        Listofmaps.add(Particle);
        Listofmaps.add(Other);
        return Listofmaps;
    }

    //Returns a part of speech like adverb, adjective whatever based on the code created with POS tags
    //http://blog.thedigitalgroup.com/sagarg/2015/06/18/part-of-speech-tagging-using-opennlp/
    private String POS(String word) {
        if (word.contains(":WP") || word.contains(":PRP")) {
            return "Pronoun";
        } else if (word.contains(":JJ")) {
            return "Adjective";
        } else if (word.contains(":NN")) {
            return "Noun";
        } else if (word.contains(":DT") || word.contains(":PDT") || word.contains(":WPT")) {
            return "Determiner";
        } else if (word.contains(":UH")) {
            return "Interjection";
        } else if (word.contains(":VB")) {
            return "Verb";
        } else if (word.contains(":CC") || word.contains(":IN") || word.contains(":TO")) {
            return "Conjunction or Preposition";
        } else if (word.contains(":RB") || word.contains(":WRB")) {
            return "Adverb";
        } else if (word.contains(":RP")) {
            return "Particle";
        } else {
            return "Other";
        }
    }

    private void PutErThere(Map<String, Integer> map, String content){
        //TODO change this so that it doesn't use word but content so the code can be seen at the end.
        String[] parts = content.split(":");
        String word1 = parts[0];
        //Change to content for tags, words1 for no tags
        String word = word1.toLowerCase();
        word.replaceAll("[^A-Za-z0-9]", "");
        if (map.containsKey(word)) {
            Integer val = map.get(word);
            val++;
            map.put(word, val);
        } else {
            map.put(word, 1);
        }
    }
}
