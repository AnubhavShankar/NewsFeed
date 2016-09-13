package com.professional.anubhavshankar.newsfeed.data;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.professional.anubhavshankar.newsfeed.NewsFragment;
import com.professional.anubhavshankar.newsfeed.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Anubhav Shankar on 8/28/2016.
 */
public class NewsCardAdapter extends CursorRecyclerViewAdapter<NewsCardAdapter.NewCardViewHolder> {
    private final String LOG_TAG=NewsCardAdapter.class.getSimpleName();
    public NewsCardAdapter(Context context, Cursor cursor){
        super(context,cursor);
    }
    @Override
    public NewCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View cardView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_card,viewGroup,false);
        NewCardViewHolder newsCardView=new NewCardViewHolder(cardView);
        return newsCardView;
    }
    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }
    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    public Bitmap downloadBitMap(String url){
        HttpURLConnection urlConnection=null;
        try{
            URL uri= new URL(url);
            urlConnection=(HttpURLConnection)uri.openConnection();
            int statusCode=urlConnection.getResponseCode();
            if(statusCode!=200)
                return null;
            urlConnection.setRequestMethod("GET");
            InputStream inputStream=urlConnection.getInputStream();
            if(inputStream!=null){
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
        }
        Log.w("DownloadImageTask", "Error DOWNLOADING IMAGES");
        return null;
    }
    @Override
    public void onBindViewHolder(NewCardViewHolder viewHolder, Cursor cursor) {
            NewsFeed newsPiece = NewsFeed.fromCursor(cursor);
            Log.d(LOG_TAG,newsPiece.getTitle());
            final int DETAIL_LINK = 1;
            viewHolder.tvPubDate.setText(newsPiece.getDateString());
            viewHolder.tvTitle.setText(
                    Html.fromHtml(
                            "<a href=\"" + newsPiece.getDetailLink() + "\">" + newsPiece.getTitle() + "</a>"));
            viewHolder.tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
            stripUnderlines(viewHolder.tvTitle);
            Log.d("NewCardAdapter", "trying to load image: " + newsPiece.getImageUrl());
            //Bitmap storyImage=downloadBitMap(newsPiece.getImageUrl());
            // new DownloadImageTask(viewHolder.ivPicture).execute(newsPiece.getImageUrl());
            Picasso.with(viewHolder.ivPicture.getContext()).load(newsPiece.getImageUrl()).resize(270, 135).into(viewHolder.ivPicture);
            viewHolder.ivPicture.setTag(newsPiece.getDetailLink());
            viewHolder.ivPicture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse((String) v.getTag()));
                    v.getContext().startActivity(intent);
                }
            });
    }

    public static class NewCardViewHolder extends RecyclerView.ViewHolder {
        TextView tvPubDate;
        TextView tvTitle;
        ImageView ivPicture;
        public NewCardViewHolder(View itemView) {
            super(itemView);
            tvPubDate=(TextView)itemView.findViewById(R.id.pub_date);
            tvTitle=(TextView)itemView.findViewById(R.id.title);
            ivPicture=(ImageView)itemView.findViewById(R.id.story_image);
        }
    }
}
