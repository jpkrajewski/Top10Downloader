package com.timbuchalka.top10downloader;

import android.support.annotation.LongDef;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseApplications {
    private static final String TAG = "ParseApplications";
    private ArrayList<FeedEntry> applications;
    private String title;

    public ParseApplications() {
        this.applications = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public String getTitle() { return title; }

    public boolean parse(String xmlData) {
        boolean status = true;
        FeedEntry currentRecord = null;
        boolean inEntry = false;
        String textValue = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();

            String tagName;

            while(eventType != XmlPullParser.END_DOCUMENT) {

                tagName = xmlPullParser.getName();
                if(XmlPullParser.START_TAG == eventType && "title".equalsIgnoreCase(tagName)) {
                    Log.d(TAG, "parse: Złapano");
                        eventType = xmlPullParser.next();
                        title = xmlPullParser.getText();
                        Log.d(TAG, "parse: przetworzono");
                    break;
                }

                eventType = xmlPullParser.next();
            }

            title = xmlPullParser.getText();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                tagName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
//                        Log.d(TAG, "parse: Starting tag for: " + tagName);
                        if("entry".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = xmlPullParser.getText();
                        break;
                        
                    case XmlPullParser.END_TAG:
//                        Log.d(TAG, "parse: Ending tag for " + tagName);
                        if(inEntry) {
                            if("entry".equalsIgnoreCase(tagName)) {
                                applications.add(currentRecord);
                                inEntry = false;
                            } else if("name".equalsIgnoreCase(tagName)) {
                                currentRecord.setName(textValue);
                            } else if("artist".equalsIgnoreCase(tagName)) {
                                currentRecord.setArtist(textValue);
                            } else if("releaseDate".equalsIgnoreCase(tagName)) {
                                currentRecord.setReleaseDate(textValue);
                            } else if("summary".equalsIgnoreCase(tagName)) {
                                currentRecord.setSummary(textValue);
                            } else if("image".equalsIgnoreCase(tagName)) {
                                currentRecord.setImageURL(textValue);
                            }
                        }
                        break;
                    default:
                }
                eventType = xmlPullParser.next();
            }
//            for(FeedEntry app: applications) {
//                Log.d(TAG, "*****************");
//                Log.d(TAG, app.toString());
//            }
        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }

        return status;
    }
}
