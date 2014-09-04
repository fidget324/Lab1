package edu.syr.mobileos.encryptednotepad;

import java.io.Serializable;

/**
 * Structure for an individual note
 */
public class Note implements Serializable {

    private static final long serialVersionUID = 464897646;

    public static final int ACTION_DELETE       = 0;
    public static final int ACTION_EDIT         = 1;

    private long mID;
    private String mTitle;
    private String mText;

    public Note(String title, String text) {
        mID = 0;
        mTitle = title;
        mText = text;
    }

    // this constructor should only be used for testing purposes, SQL provides
    // the ID automatically
    public Note(long ID, String title, String text) {
        mID = ID;
        mTitle = title;
        mText = text;
    }

    public long getID() {
        return mID;
    }

    public void setID(long ID) {
        mID = ID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }
}
