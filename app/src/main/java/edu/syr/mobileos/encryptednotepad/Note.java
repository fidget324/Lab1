package edu.syr.mobileos.encryptednotepad;

/**
 * Structure for an individual note
 */
public class Note {

    public static final int ACTION_DELETE       = 0;
    public static final int ACTION_CREATE       = 1;
    public static final int ACTION_EDIT         = 2;

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
