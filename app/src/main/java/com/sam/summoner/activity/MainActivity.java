package com.sam.summoner.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.sam.summoner.GameStaticsManager;
import com.sam.summoner.R;
import com.sam.summoner.RequestManager;
import com.sam.summoner.StaticsDatabaseHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    EditText summonerText;
    Button searchBtn;

    private RequestManager requestManager;
    private GameStaticsManager championManager;
    private StaticsDatabaseHelper helper;
    static {
        System.loadLibrary("keys");
    }

    public native String getRiotApiKey();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestManager = RequestManager.getInstance();
        requestManager.updateDdVersion();

        String key = new String(Base64.decode(getRiotApiKey(), Base64.DEFAULT));
        requestManager.setApiKey(key);

        championManager = new GameStaticsManager(this);
        championManager.init();

        helper = new StaticsDatabaseHelper(this);

        // init search bar
        summonerText = (EditText) findViewById(R.id.summonerText);

        // init search button
        searchBtn = (Button) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        Button viewFavsBtn = (Button) findViewById(R.id.viewFavsBtn);
        viewFavsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFavList();
            }
        });

        Button test = (Button) findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });
    }

    // search for the summoner name in summonerText
    // if summonerText is empty, do nothing
    // if summonerText is not empty, search for summoner data
    private void search() {
        Log.d(TAG, "Starting summoner search...");
        String summonerName = summonerText.getText().toString();
        if (summonerName.equals("")) {return;}
        String jString = requestManager.getAccountJObject(summonerName);
        if (jString != null) {
            Log.d(TAG, "Got summoner info. Launching InfoActivity...");
            Intent i = new Intent(this, InfoActivity.class);
            i.putExtra("jString", jString);
            startActivity(i);
        } else {
            Log.e(TAG, "Failed to find summoner: " + summonerName);
            Toast.makeText(this, "Failed to find summoner.", Toast.LENGTH_SHORT).show();
        }
    }

    // search for the summoner name in summonerText
    // if summonerText is empty, do nothing
    // if summonerText is not empty, search for summoner data
    private void search(String name) {
        Log.d(TAG, "Starting summoner search...");
        if (name.equals("")) {return;}
        String jString = requestManager.getAccountJObject(name);
        if (jString != null) {
            Log.d(TAG, "Got summoner info. Launching InfoActivity...");
            Intent i = new Intent(this, InfoActivity.class);
            i.putExtra("jString", jString);
            startActivity(i);
        } else {
            Log.e(TAG, "Failed to find summoner: " + name);
            Toast.makeText(this, "Failed to find summoner.", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewFavList() {
        ArrayList<String> favs = helper.getFriends();
        if (favs.size() == 0) {
            Toast.makeText(this, "Favorites list is empty! Add some by searching for your friends", Toast.LENGTH_SHORT).show();
            return;
        }

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Your favorite summoners");

        final View view = LayoutInflater.from(this).inflate(R.layout.layout_favorites, (ViewGroup) findViewById(R.id.favPromptLayout), false);
        final RadioGroup parent = (RadioGroup) view.findViewById(R.id.favList);

        int i = 0;
        for (String name : favs) {
            RadioButton rad = new RadioButton(this);
            rad.setText(name);
            rad.setId(i); i++;
            parent.addView(rad);
        }

        builder.setView(view);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int id = parent.getCheckedRadioButtonId();
                if (id == -1) {
                    Toast.makeText(MainActivity.this, "Please make a selection", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioButton selected = (RadioButton) parent.getChildAt(id);
                helper.removeFriend((String) selected.getText());
                Toast.makeText(MainActivity.this, "Favorite removed.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int id = parent.getCheckedRadioButtonId();
                if (id == -1) {
                    Toast.makeText(MainActivity.this, "Please make a selection", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioButton selected = (RadioButton) parent.getChildAt(id);
                dialog.dismiss();
                search((String) selected.getText());
            }
        });
        builder.show();
    }

    private void test() {
        Toast.makeText(this, "Cores: " + Runtime.getRuntime().availableProcessors(), Toast.LENGTH_SHORT).show();
    }
}
