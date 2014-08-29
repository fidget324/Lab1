package edu.syr.mobileos.encryptednotepad;

/**
 * Created by scottconstable on 8/29/14.
 */
public class Note {

    private long mID;
    private String mTitle;
    private String mEncryptedText;

    public Note(long ID, String title, String encryptedText) {
        mID = ID;
        mTitle = title;
        mEncryptedText = encryptedText;
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

    public String getEncryptedText() {
        return mEncryptedText;
    }

    public void setEncryptedText(String encryptedText) {
        mEncryptedText = encryptedText;
    }
}
