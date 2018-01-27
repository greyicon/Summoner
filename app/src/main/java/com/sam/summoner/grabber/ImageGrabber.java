package com.sam.summoner.grabber;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

// Class for downloading images from URLs, returned as a Bitmap
public class ImageGrabber extends AsyncTask<String, Void, Bitmap> {
    private String TAG = "ImageGrabber";

    private Context context;
    private ImageView imgView;

    private ProgressDialog pd;

    public ImageGrabber(Context ctx) {
        context = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(context);
        pd.setMessage("Getting image...");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Log.d(TAG, "Starting to get image from URL...");
        String url = params[0];
        Bitmap img = null;
        try {
            Log.d(TAG, "Downloading image...");
            InputStream in = new URL(url).openStream();
            img = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            Log.e(TAG, "Connection failure: " + e);
        }
        Log.d(TAG, "Image downloaded.");
        return img;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (pd.isShowing()) {pd.dismiss();}
    }
}
