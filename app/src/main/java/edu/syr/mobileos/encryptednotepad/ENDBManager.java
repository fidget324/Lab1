package edu.syr.mobileos.encryptednotepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;
import android.util.Log;

/**
 * The entirety of the SQLite BACK-END
 * The rest of the App interacts with EncryptedNotesDBManager via the six public methods define in the nested Agent class.
 * DOES NOT USE RAW QUERIES.
 */
public class ENDBManager {

    // Declaring database level details
    private static final String DATABASE_NAME = "Notepad";
    private static final String DATABASE_TABLE_NAME = "EncryptedNote";
    private static final String DATABASE_CREATE_QUERY="CREATE TABLE "+DATABASE_TABLE_NAME+ " (NoteID INTEGER PRIMARY KEY AUTOINCREMENT, IVectorTitle TEXT NOT NULL, IVectorContent TEXT NOT NULL, NoteTitle TEXT NOT NULL, NoteContents TEXT NOT NULL, LastModifiedDate DATE DEFAULT CURRENT_DATE);";
    private static final int DATABASE_VERSION = 2;

    // Note level attributes
    public static final String ENOTE_IVECTORTITLE = "IVectorTitle";
    public static final String ENOTE_IVECTORCONTENT = "IVectorContent";
    public static final String ENOTE_ID = "NoteID";
    public static final String ENOTE_TITLE = "NoteTitle";
    public static final String ENOTE_CONTENTS = "NoteContents";
    public static final String ENOTE_LM_DATE="LastModifiedDate";

    // Declaring TAG for logging purpose
    private static final String TAG = "ENDBManager";

    private DatabaseAgent mDatabaseAgent;
    private SQLiteDatabase mDatabase;

    // Declaring the Context
    private final Context mContext;

    /*
      Defining a nested class DatabaseAgent that extends the class SQLiteOpenHelper
      which is provided by Android for data base creation and version management
      We could have defined it as a separate class but defining it as a nested class because
      it will be used by only this class.
    */

    private static class DatabaseAgent extends SQLiteOpenHelper
    {
        DatabaseAgent(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        // Creating a table in the created database 
        @Override
        public void onCreate(SQLiteDatabase database)
        {
            database.execSQL(DATABASE_CREATE_QUERY);
        }

        //This method will upgrade the database
        //@Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            Log.w(TAG, "Database being upgraded from " + oldVersion + " to " + newVersion + ", destroying all the data in the table note");
            database.execSQL("DROP TABLE IF EXISTS EncryptedNote");
            onCreate(database);
        }
    }

    ENDBManager(Context context)
    {
        mContext=context;
    }

    public ENDBManager openDatabase() throws SQLException
    {
        mDatabaseAgent = new DatabaseAgent(mContext);
        // getWritableDatabase is used to create and/or open a database that will be used for reading and writing.
        // The first time this is called, the database will be opened and
        // onCreate(SQLiteDatabase), onUpgrade(SQLiteDatabase, int, int) and/or onOpen(SQLiteDatabase) will be called.
        // getWritableDatabase returns a read/write database object valid until close() is called
        mDatabase = mDatabaseAgent.getWritableDatabase();
        return this;
    }

    public void closeDatabase() {
        mDatabaseAgent.close();
    }


    /**
     * Create a new note supplied by the MainActivity class. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param note the whole note supplied by the MainActivity class
     * @return rowId or -1 if failed
     */
    public long addNote(Note note) {
        String eTitle=note.getTitle();
        String eContents=note.getText();
        String iVectorTitle= StringUtility.bin2String(note.getIVTitle());
        String iVectorContent= StringUtility.bin2String(note.getIVText());
        ContentValues initialValues = new ContentValues();
        Time currentTime = new Time();
        currentTime.setToNow();
        initialValues.put(ENOTE_IVECTORTITLE, iVectorTitle);
        initialValues.put(ENOTE_IVECTORCONTENT, iVectorContent);
        initialValues.put(ENOTE_TITLE, eTitle);
        initialValues.put(ENOTE_CONTENTS, eContents);
        initialValues.put(ENOTE_LM_DATE, currentTime.toString());
        // Inserting the newly created note (title, encryptedContents and currentDate) to the table.
        return mDatabase.insert(DATABASE_TABLE_NAME, null, initialValues); // returns the RowId by default on successful

    }

    /**
     * Delete a single note, given that note's ID
     * @param NoteId    SQL ID of the note to be deleted
     * @return          true if the delete was successful
     */
    public boolean deleteNote(long NoteId) {
        // Deleting the note with the given ID from the table EncryptedNote.
        return mDatabase.delete(DATABASE_TABLE_NAME, ENOTE_ID + "=" + NoteId, null) > 0;
    }

    /**
     * This method will retrieve all the notes from the database ordered by the Last Modified Date
     * @return      cursor containing all notes
     */
    public Cursor getAllNotes() {
        String ColumnList[]= {ENOTE_ID, ENOTE_IVECTORTITLE, ENOTE_IVECTORCONTENT, ENOTE_TITLE, ENOTE_CONTENTS, ENOTE_LM_DATE};
        return mDatabase.query(DATABASE_TABLE_NAME, ColumnList , null, null, null, null, ENOTE_LM_DATE+" DESC");
    }
    /*
		This method will return a Cursor positioned at the note that matched the requested NoteId.
    */

    /**
     * This method will return a Cursor positioned at the note that matched the requested NoteId.
     * @param noteId    SQL ID of the note to look up
     * @return          cursor positioned at the note that matched
     * @throws SQLException
     */
    public Cursor getNoteThroughId(long noteId) throws SQLException {

        String ColumnList[]= {ENOTE_ID, ENOTE_IVECTORTITLE, ENOTE_IVECTORCONTENT, ENOTE_TITLE, ENOTE_CONTENTS, ENOTE_LM_DATE};

        Cursor cursorAtGivenNoteId =mDatabase.query(true, DATABASE_TABLE_NAME, ColumnList, ENOTE_ID + "=" + noteId, null,
                null, null, null, null);
        if (cursorAtGivenNoteId != null) {
            cursorAtGivenNoteId.moveToFirst();
        }
        return cursorAtGivenNoteId;

    }

    /**
     * This function will take the following arguments to update the desired note
     1. NoteId,
     2. Updated Title of the note,
     3. Updated Content of the note
     In case the systems fails to update the database for any reason, it will return false otherwise true;
     * @param note      The updated note
     * @return          true on a successful update, false otherwise
     */
    public boolean updateNoteThroughId(Note note) {
        long noteID= note.getID();
        String iVectorTitle= StringUtility.bin2String(note.getIVTitle()); // We many not need it as we do not need to update the initialization vector
        String iVectorContent= StringUtility.bin2String(note.getIVText());
        String title=note.getTitle();
        String eContents=note.getText();
        ContentValues updatedValues = new ContentValues();
        Time currentTime = new Time();
        currentTime.setToNow();
        updatedValues.put(ENOTE_IVECTORTITLE, iVectorTitle); // We many not need it as we do not need to update the initialization vector
        updatedValues.put(ENOTE_IVECTORCONTENT, iVectorContent);
        updatedValues.put(ENOTE_TITLE, title);
        updatedValues.put(ENOTE_CONTENTS, eContents);
        updatedValues.put(ENOTE_LM_DATE, currentTime.toString());
        return mDatabase.update(DATABASE_TABLE_NAME, updatedValues, ENOTE_ID + "=" + noteID, null) > 0;
    }

}