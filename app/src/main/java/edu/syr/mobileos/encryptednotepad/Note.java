package edu.syr.mobileos.encryptednotepad;

/**
 * Structure for an individual note
 */
public class Note {

    public static final int ACTION_DELETE       = 0;
    public static final int ACTION_EDIT         = 1;

    private long mID;
    private String mTitle;
    private String mEncryptedText;

    public Note(String title, String encryptedText) {
        mID = 0;
        mTitle = title;
        mEncryptedText = encryptedText;
    }

    // this constructor should only be used for testing purposes, SQL provides
    // the ID automatically
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
