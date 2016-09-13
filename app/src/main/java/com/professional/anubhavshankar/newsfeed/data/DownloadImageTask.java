package com.professional.anubhavshankar.newsfeed.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.professional.anubhavshankar.newsfeed.R;

import java.beans.IndexedPropertyChangeEvent;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Anubhav Shankar on 8/28/2016.
 */
public class DownloadImageTask extends AsyncTask<String,Void,Bitmap>{

    private static WeakReference<ImageView> imageView;
    public DownloadImageTask(ImageView ivImage){
        imageView=new WeakReference<ImageView>(ivImage);
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        //int width = image.getWidth();
        //int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    @Override
    protected Bitmap doInBackground(String... params) {
        return downloadBitMap(params[0]);
    }
    public Bitmap downloadBitMap(String url){
        HttpURLConnection urlConnection=null;
        try{
            URL uri= new URL(url);
            Bitmap bitmap= BitmapFactory.decodeStream(uri.openStream());
            return bitmap;

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
        }
        Log.w("DownloadImageTask","Error DOWNLOADING IMAGES");
        return null;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(isCancelled()){
            bitmap=null;
        }
        if(imageView!=null){
            ImageView ivImage=imageView.get();
            if(ivImage!=null){
                if(bitmap!=null)
                {
                    ivImage.setImageBitmap(bitmap);
                }
                else {
                    Drawable placeHolder=ivImage.getContext().getResources().getDrawable(R.mipmap.ic_test_image);
                    ivImage.setImageDrawable(placeHolder);
                }
            }
        }
    }
}
