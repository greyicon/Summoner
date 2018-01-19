package com.sam.summoner;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//Class for getting JSON string objects from Riot, holds all other processes while it loads
public class WebGrabber extends AsyncTask<String, String, String>{
    private String TAG = "WebGrabber";

    private Context context;
    private String jString;
    private ProgressDialog pd;

    public WebGrabber(Context ctx) {context = ctx;}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(context);
        pd.setMessage("Getting data...");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "Opening http connection...");
        HttpURLConnection connection = null;
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            Log.d(TAG, "Collecting data from URL...");
            StringBuilder buffer = new StringBuilder();
            String ln = "";
            while ((ln = reader.readLine()) != null) {
                buffer.append(ln).append("\n");
            }

            jString = buffer.toString();

            reader.close();
            inputStream.close();
            Log.d(TAG, "Data collection done.");
            connection.disconnect();
            Log.d(TAG, "Http connection closed.");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return jString;
    }

    @Override
    protected void onPostExecute(String jString) {
        super.onPostExecute(jString);
        if (pd.isShowing()) {pd.dismiss();}
    }
}
