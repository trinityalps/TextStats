package textsnoop.rddigi.com.textstats;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private String filepath;
    private CheckBox POS;
    private Boolean isCheck;
    private Boolean GetGranular;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        POS = (CheckBox) findViewById(R.id.checkBox);
        final CheckBox granularity = (CheckBox) findViewById(R.id.checkBox2);
        final Button button2 = (Button) findViewById(R.id.go);
        final EditText input = (EditText) findViewById(R.id.editText);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                filepath = input.getText().toString();
                if (filepath != ""){
                    isCheck = POS.isChecked();
                    GetGranular = granularity.isChecked();
                    new asyncparsefile().execute("");
                } else {
                    quickOkDialog("Warning", "Enter Text before continuing.");

                }
            }
        });


    }



    private void quickOkDialog(String Title, String Message){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(Message);
        dlgAlert.setTitle(Title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }








    private class asyncparsefile extends AsyncTask<String, Void, Boolean> {

        int avg;
        int max;
        int min;
        int totalwords;
        int totalsentences;
        HashMap<String, Integer> words;
        HashMap<String, Integer> poscount;
        ArrayList<Map<String , Integer>> wordcountbypos;
        private final ProgressDialog dialogp = new ProgressDialog(activity);

        /* progress dialog to show user that the backup is processing. */
        /**
         * application context.
         */
        @Override
        protected void onPreExecute() {
            dialogp.setMessage("Please wait");
            dialogp.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {
            try {
                Helper help = new Helper(filepath);
                avg = help.avgsentence();
                max = help.maxsentence();
                min = help.minsentence();
                words = help.individualWordCounts();
                totalsentences = help.sentencecount();
                totalwords = help.wordcount();
                //TODO add in code for get granular boolean
                if (isCheck) {
                    Languageanalytics ana = new Languageanalytics(help.getSentences(), activity);
                    poscount = ana.countPOS();
                    if (GetGranular){
                        wordcountbypos = ana.countWordsbyPOS();
                    }

                }
                return true;
            } catch (Exception e){
                e.printStackTrace();
                return false;

            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            dialogp.dismiss();

            //TODO Add results page, then this thing should throw to said page
            Intent intent = new Intent(getBaseContext(), ResultsActivity.class);
            intent.putExtra("Average", avg);
            intent.putExtra("Max", max);
            intent.putExtra("Min", min);
            intent.putExtra("Map_counting_of_individual_words", words);
            intent.putExtra("Sentences_in_Document", totalsentences);
            intent.putExtra("Words_in_Document", totalwords);
            intent.putExtra("POS parsing", isCheck);
            intent.putExtra("Granlurity", GetGranular);
            if (isCheck){
                intent.putExtra("POSCount", poscount);
            }
            if (GetGranular){
                intent.putExtra("WordPosCount", wordcountbypos);
            }

            startActivity(intent);
        }

    }
}
