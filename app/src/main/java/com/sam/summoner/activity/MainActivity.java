package com.sam.summoner.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
    private final Context mContext = this;

    EditText summonerText;
    Button searchBtn;

    private RequestManager mRequestManager;
    private StaticsDatabaseHelper mHelper;

    static {
        System.loadLibrary("keys");
    }
    public native String getRiotApiKey();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new LoadUI().execute();
    }

    private class LoadUI extends AsyncTask <Void, Void, Void> {
        ProgressDialog dialog;
        private final String TAG_SUFFIX = ".LoadUI";

        @Override
        protected void onPreExecute() {
            Log.d(TAG + TAG_SUFFIX, "onPreExecute()");
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Initializing...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG + TAG_SUFFIX, "doInBackground()");
            initBackEnd();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG + TAG_SUFFIX, "onPostExecute()");
            initFrontEnd();
            if (dialog.isShowing()) {dialog.dismiss();}
        }
    }

    private void initBackEnd() {
        Log.d(TAG, "initBackEnd()");
        mHelper = new StaticsDatabaseHelper(this);
        mRequestManager = RequestManager.getInstance();
        mRequestManager.updateDdVersion();

        String key = new String(Base64.decode(getRiotApiKey(), Base64.DEFAULT));
        mRequestManager.setApiKey(key);

        new GameStaticsManager(mContext).init();
    }

    private void initFrontEnd() {
        Log.d(TAG, "initFrontEnd()");
        summonerText = (EditText) findViewById(R.id.summonerText);

        searchBtn = (Button) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = summonerText.getText().toString();
                search(name);
            }
        });

        Button viewFavsBtn = (Button) findViewById(R.id.viewFavsBtn);
        viewFavsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFavList();
            }
        });

        // Dev button
        Button test = (Button) findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });
    }

    private void search(String name) {
        Log.d(TAG, "search()");
        if (name.equals("")) {Toast.makeText(mContext, "Please enter a summoner name.", Toast.LENGTH_SHORT).show();}
        String jString = mRequestManager.getAccountJObject(name);
        if (jString != null) {
            Log.d(TAG, "Search successful. Launching InfoActivity...");
            Intent i = new Intent(mContext, InfoActivity.class);
            i.putExtra("jString", jString);
            startActivity(i);
        } else {
            Log.e(TAG, "Search failed: " + name);
            Toast.makeText(this, "Failed to find summoner.", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewFavList() {
        Log.d(TAG, "viewFavList()");
        ArrayList<String> favs = mHelper.getFriends();
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
                Log.d(TAG, "Dialog - dismissed");
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Dialog - delete");
                int id = parent.getCheckedRadioButtonId();
                if (id == -1) {
                    Toast.makeText(MainActivity.this, "Please make a selection", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    RadioButton selected = (RadioButton) parent.getChildAt(id);
                    mHelper.removeFriend(selected.getText().toString());
                    Toast.makeText(MainActivity.this, "Favorite removed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Dialog - search");
                int id = parent.getCheckedRadioButtonId();
                if (id == -1) {
                    Toast.makeText(MainActivity.this, "Please make a selection", Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton selected = (RadioButton) parent.getChildAt(id);
                    dialog.dismiss();
                    search(selected.getText().toString());
                }
            }
        });

        builder.show();
    }

    // Dev method
    private void test() {
        Toast.makeText(this, "Cores: " + Runtime.getRuntime().availableProcessors(), Toast.LENGTH_SHORT).show();
    }
}
