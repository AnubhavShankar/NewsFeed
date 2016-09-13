package com.professional.anubhavshankar.newsfeed.data;

import android.database.Cursor;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Anubhav Shankar on 8/27/2016.
 */
public class NewsFeed {
    private String title;
    Date date;
    private String imageUrl;
    private String category;
    private String detailLink;
    private static final String[] NEWS_FEED_PROJECTIONS={
            NewsFeedContract.NewsEntry.TABLE_NAME+"."+ NewsFeedContract.NewsEntry._ID,
            NewsFeedContract.NewsEntry.COLUMN_TITLE,
            NewsFeedContract.NewsEntry.COLUMN_STORY_IMAGE,
            NewsFeedContract.NewsEntry.COLUMN_Category,
            NewsFeedContract.NewsEntry.COLUMN_DETAIL,
            NewsFeedContract.NewsEntry.COLUMN_DATE
    };
    public static final int COL_NEWS_ID=0;
    public static final int COL_NEWS_TITLE=1;
    public static final int COL_NEWS_STORY_IMG=2;
    public static final int COL_NEWS_CATEGORY=3;
    public static final int COL_NEWS_DETAIL=4;
    public static final int COL_NEWS_DATE=5;
    public NewsFeed(){
        title="NO TITLE";
        date=new Date();
        detailLink="NO_LINK";
        imageUrl="http://www.onetouchpayroll.com/blog/wp-content/uploads/2012/12/error-free-reports-290x300.jpg";
        category="No Category";
    };
    public static NewsFeed fromCursor(Cursor cursorFeed ){
        NewsFeed temp= new NewsFeed();
        temp.setTitle(cursorFeed.getString(COL_NEWS_TITLE));
        temp.setCategory(cursorFeed.getString(COL_NEWS_CATEGORY));
        temp.setImageUrl(cursorFeed.getString(COL_NEWS_STORY_IMG));
        temp.setDetailLink(cursorFeed.getString(COL_NEWS_DETAIL));
        temp.setFromDbDate(cursorFeed.getString(COL_NEWS_DATE));
        return temp;
    }
    public NewsFeed(String title,String date,String imageUrl,String category,String detailLink)
    {
        this.setTitle(title);
        this.setDate(date);
        this.setImageUrl(imageUrl);
        this.setDetailLink(detailLink);
        this.setCategory(category);
    }
    public String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result="";
        if(parser.next()==XmlPullParser.TEXT){
            result=parser.getText();
            parser.nextTag();
        }
        return result;
    }
    public void setFromDbDate(String dateString){
        SimpleDateFormat sqlDateFormat=new SimpleDateFormat("yyyyMMddkkmm");
        try {
            this.date=sqlDateFormat.parse(dateString);
        } catch (ParseException e) {
            this.date=new Date();
        }

    }
    public static Long getDBFormatDate(Date date){
        SimpleDateFormat sqlDateFormat=new SimpleDateFormat("yyyyMMddkkmm");
        String sqlDbDate=sqlDateFormat.format(date);
        return Long.parseLong(sqlDbDate);
    }
    public String getDBDateString(){
        SimpleDateFormat sqlDateFormat=new SimpleDateFormat("yyyyMMddkkmm");
        String sqldbDate=sqlDateFormat.format(this.date);
        return sqldbDate;

    };
    public String removeCDATA(String data){
        String newData=data.replace("![CDATA[", "");
        data=newData.replace("]]", "");
        return data;
    }
    public String readTag(XmlPullParser parser,String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG,null,tag);
        String title=readText(parser);
        parser.require(XmlPullParser.END_TAG,null,tag);
        title=removeCDATA(title);
        return title;
    }

    public NewsFeed readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG,null,"item");
        while(parser.next()!=XmlPullParser.END_TAG){
            if(parser.getEventType()!=XmlPullParser.START_TAG){
                continue;
            }
            String name=parser.getName();
            if ("title".equals(name))
                setTitle(readTag(parser, "title"));
            else if (("updatedAt").equals(name))
                setDate(readTag(parser,"updatedAt"));
            else if(("link").equals(name))
                setDetailLink(readTag(parser,"link"));
            else if(("StoryImage").equals(name))
                setImageUrl(readTag(parser,"StoryImage"));
            else if(("category").equals(name))
                setCategory(readTag(parser,"category"));
            else
                skip(parser);

        }
        return this;
    }
    public void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if(parser.getEventType()!=XmlPullParser.START_TAG)
            throw new IllegalStateException("Requlre Start tag to perform skip on");
        int depth=1;
        while(depth!=0){
            switch (parser.next()){
                case XmlPullParser.START_TAG:
                    depth++; break;
                case XmlPullParser.END_TAG:
                    depth--;break;
            }
        }
    }
    public String getDateString(){
        SimpleDateFormat displayFormat=new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
        String strDate=displayFormat.format(this.date);
        return strDate;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(String date){
        SimpleDateFormat feedFormat=new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
        try {
            this.date = feedFormat.parse(date);
        } catch (ParseException e) {
            this.date=new Date();
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public void setDetailLink(String detailLink) {
        this.detailLink = detailLink;
    }
}
