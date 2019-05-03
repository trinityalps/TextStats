package textsnoop.rddigi.com.textstats;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Admin on 3/21/2017.
 */

public class ResultsActivity extends Activity {
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private Button redo;
    private Integer avg;
    private Integer max;
    private Integer min;
    private Integer ts;
    private Integer tw;
    private Boolean POSCheck;
    private Boolean Granular;
    private HashMap<String, Integer> wordcount;
    private HashMap<String, Integer> poscount;
    private ArrayList<Map<String , Integer>> wordcountbypos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO add granularity to the results for telling which words are what type of POS
        //TODO don't show POS results if pos is not done.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent iin = getIntent();
        Bundle b = iin.getExtras();

        if (b != null) {
            avg = (Integer) b.get("Average");
            max = (Integer) b.get("Max");
            min = (Integer) b.get("Min");
            ts = (Integer) b.get("Sentences_in_Document");
            tw = (Integer) b.get("Words_in_Document");
            POSCheck = (Boolean) b.get("POS parsing");
            Granular = (Boolean) b.get("Granlurity");
            wordcount = (HashMap<String, Integer>) b.get("Map_counting_of_individual_words");
            if (POSCheck) {
                poscount = (HashMap<String, Integer>) b.get("POSCount");
            }
            if (Granular) {
                wordcountbypos = (ArrayList<Map<String , Integer>>) b.get("WordPosCount");
            }
        }
        redo = (Button) findViewById(R.id.redo);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.eListView);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        redo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RedoDialog();
            }
        });
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding child data
        listDataHeader.add("Basic Statistics");
        listDataHeader.add("Top Ten words used.");
        if (POSCheck) {
            listDataHeader.add("Parts of Speech use");

        }
        if (Granular){
            listDataHeader.add("Pronouns"); //3
            listDataHeader.add("Adjectives");  //4
            listDataHeader.add("Nouns");  //5
            listDataHeader.add("Determiners");  //6
            listDataHeader.add("Interjections");
            listDataHeader.add("Verbs");
            listDataHeader.add("Conjunctions or Preposistions");
            listDataHeader.add("Adverbs");
            listDataHeader.add("Particles");
            listDataHeader.add("Others");
        }

        // Adding child data
        List<String> basic = new ArrayList<>();
        basic.add("Total words in document: " + tw);
        basic.add("Total sentences in document: " + ts);
        basic.add("Average sentence length: " + avg);
        basic.add("Max sentence length: " + max);
        basic.add("Min sentence length: " + min);


        List<String> wordstats = new ArrayList<>();
        if (wordcount != null) {
            wordstats.addAll(topten(orderhashbyhighest(wordcount)));

        }
        if (POSCheck) {
            if (poscount != null) {
                List<String> POS = new ArrayList<>();
                POS.addAll(orderhashbyhighest(poscount));
                listDataChild.put(listDataHeader.get(2), POS);
            }
        }
        if (Granular){
            int i = 3;
            for(Map<String , Integer> map : wordcountbypos ){
                HashMap<String, Integer> copy = new HashMap<>(map);
                List<String> Parts = new ArrayList<>();
                Parts.addAll(orderhashbyhighest(copy));
                listDataChild.put(listDataHeader.get(i), Parts);
                i++;
            }
        }
        listDataChild.put(listDataHeader.get(0), basic); // Header, Child data
        listDataChild.put(listDataHeader.get(1), wordstats);
    }

    private void RedoDialog() {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Hit okay to retry with another document, this will erase all current data");
        dlgAlert.setTitle("Warning");
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        dlgAlert.setNegativeButton("Cancel", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private List<String> topten(List<String> input){
        List<String> tt = new ArrayList<>();
        if (input.size() <= 10){
            return input;
        } else {
            tt = input.subList(0,10);
        }
        return tt;
    }

    private List<String> orderhashbyhighest(HashMap<String, Integer> hash) {
        List<String> stringlist = new ArrayList<>();
        Map<String, Integer> newMap1 = new TreeMap(Collections.reverseOrder());
        newMap1.putAll(hash);
        Map<String, Integer> newMap = sortByValues(newMap1);
        //newMap.putAll(hash);
        for (Map.Entry<String, Integer> entry : newMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            stringlist.add(key + " : " + value);

            // ...
        }
        Collections.reverse(stringlist);
        return stringlist;
    }

    private static <K, V extends Comparable<V>> Map<K, V>
    sortByValues(final Map<K, V> map) {
        Comparator<K> valueComparator =
                new Comparator<K>() {
                    public int compare(K k1, K k2) {
                        int compare =
                                map.get(k1).compareTo(map.get(k2));
                        if (compare == 0)
                            return 1;
                        else
                            return compare;
                    }
                };

        Map<K, V> sortedByValues =
                new TreeMap<>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }
}
