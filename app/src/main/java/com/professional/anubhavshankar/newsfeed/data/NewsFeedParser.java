package com.professional.anubhavshankar.newsfeed.data;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anubhav Shankar on 8/28/2016.
 */
public class NewsFeedParser {
    //Not using namespace
    static final String LOG_TAG=NewsFeedParser.class.getSimpleName();
    private static final String ns=null;
    public List<NewsFeed> parse(BufferedReader in) throws XmlPullParserException,IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            String line;
            StringBuilder sb= new StringBuilder();
            /*
            while((line=in.readLine())!=null){
                sb.append(line+"\n");
            }
            Log.d(LOG_TAG,sb.toString());
            */
            parser.setInput(in);
            parser.nextTag();
            Log.d(LOG_TAG,"pullParser created");
            return readFeed(parser);
        } finally {
            in.close();
        }
    }
    public void skipParser(XmlPullParser parser) throws IOException, XmlPullParserException {
        if(parser.getEventType()!=XmlPullParser.START_TAG)
            throw new IllegalStateException("Parser needs to be on the start TAG");
        int depth=1;
        while (depth!=0) {
            switch (parser.next()) {
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
            }
        }
    }
    private List<NewsFeed> readFeed(XmlPullParser parser)throws XmlPullParserException,IOException{
        parser.require(XmlPullParser.START_TAG,ns,"rss");
        int count=0;
        List<NewsFeed> myFeed= new ArrayList<NewsFeed>();
        while((parser.next()!=XmlPullParser.END_TAG) && count<20){
            if(parser.getEventType()!=XmlPullParser.START_TAG){
                Log.d(LOG_TAG,"NOT a start tag. ");
                continue;
            }
            String name=parser.getName();
            Log.d(LOG_TAG,"GOT XML TAG: "+name);
            if(("channel").equals(name))
                continue;
            else if("item".equals(name)){
                NewsFeed newsFeed= new NewsFeed();
                newsFeed.readItem(parser);
                count++;
                myFeed.add(newsFeed);
                Log.d(LOG_TAG,"Storing in FEED: "+newsFeed.getTitle());
            }
            else
                skipParser(parser);
        }
        Log.d(LOG_TAG,"ending AT: "+parser.getName());
        return myFeed;
    }


}
