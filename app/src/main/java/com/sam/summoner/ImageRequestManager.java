package com.sam.summoner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.nfc.Tag;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.sam.summoner.grabber.ImageGrabber;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class ImageRequestManager {
    public static final String TAG = "ImageRequestManager";

    private static ImageRequestManager instance = null;

    public static ImageRequestManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new ImageRequestManager(ctx);
        }
        return instance;
    }

    private HashMap<String, SoftReference<Bitmap>> images = new HashMap<String, SoftReference<Bitmap>>();
    private Context ctx;
    private File cache;

    private ImageRequestManager(Context ctx) {
        this.ctx = ctx;
        cache = ctx.getCacheDir();
    }

    public void showImage(String url, ImageView view) {
        Log.d(TAG, "Getting image: " + url + " ...");
        String key = url;
        view.setTag(key);
        if (images.containsKey(key) && images.get(key).get() != null) {
            Log.d(TAG, "Image is in hashmap. Setting view.");
            Bitmap bit = images.get(key).get();
            view.setImageBitmap(bit);
        } else {
            Log.d(TAG, "Image queued.");
            queueImage(url, view);
            view.setImageResource(R.drawable.wait);
        }
    }

    private void queueImage(String url, ImageView view) {
        ImgRef ref = new ImgRef(url, view);
        Thread imgLoaderThread = new Thread(new ImgQueueManager(ref));
        imgLoaderThread.setPriority(Thread.NORM_PRIORITY - 2);
        imgLoaderThread.run();
    }

    private Bitmap getBitmap(String url) {
        Log.d(TAG, "Getting bitmap...");
        String fileName = String.valueOf(url.hashCode());
        File file = new File(cache, fileName);
        try {
            Log.d(TAG, "Decoding file: " + fileName);
            Bitmap bit = BitmapFactory.decodeFile(file.getPath());
            if (bit != null) return bit;
            bit = downloadBitmap(url);
            writeFile(bit, file);
            return bit;
        } catch (Exception e) {
            Log.e(TAG, "Failed to get bitmap, downloading...: " + e);
            Bitmap bit = downloadBitmap(url);
            writeFile(bit, file);
            return bit;
        }
    }

    private Bitmap downloadBitmap(String url) {
        Log.d(TAG, "Getting image...");
        Bitmap bit = null;
        try {
            bit = new ImageGrabber(ctx).execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Failed to get image data: " + e);
            Toast.makeText(ctx, "Task failed", Toast.LENGTH_SHORT).show();
        }
        if (bit != null) {
            Log.d(TAG, "Got image data.");
        } else {
            Log.e(TAG, "Failed to get image data: Bitmap is null.");
        }
        return bit;
    }

    private void writeFile(Bitmap bit, File file) {
        Log.d(TAG, "Writing file...");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bit.compress(Bitmap.CompressFormat.PNG, 80, out);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File error: " + e);
            return;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {}
            }
        }
    }

    private class ImgRef {
        public String url;
        public ImageView view;

        public ImgRef(String url, ImageView view) {
            this.url = url;
            this.view = view;
        }
    }

    private class ImgQueueManager implements Runnable{
        private ImgRef ref;

        public ImgQueueManager(ImgRef ref) {
            this.ref = ref;
        }

        @Override
        public void run() {
            Log.d(TAG, "Handling queued image: " + ref.url);
            ImgRef imgLoad = ref;
            Bitmap bit = getBitmap(ref.url);
            String key = ref.url;
            images.put(key, new SoftReference<Bitmap>(bit));
            Object tag = imgLoad.view.getTag();
            if (tag == key) {
                Displayer dis = new Displayer(bit, ref.view);
                Activity act = (Activity) imgLoad.view.getContext();
                act.runOnUiThread(dis);
            }
        }
    }

    private class Displayer implements Runnable {
        Bitmap bit;
        ImageView view;

        public Displayer(Bitmap bit, ImageView view) {
            this.bit = bit;
            this.view = view;
        }

        @Override
        public void run() {
            view.setImageBitmap(bit);
        }
    }

}
