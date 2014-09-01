package edu.syr.mobileos.encryptednotepad;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * The entirety of the SQLite backend
 *
 * The rest of the app interacts with NoteDB via the six public methods defined
 * in the nested Agent class.
 */
public class NoteDB {

    public NoteDB() {}

    // Tables
    private static final String TABLE_NOTES = "notes";

    // Note DB Contract
    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ENCRYPTED_TEXT = "text";

    /**
     * This defines the external interface to the SQL database
     */
    public static class Agent {

        private static RecipeSQLiteOpenHelper sSQLHelper;
        private static SQLiteDatabase sDatabase;

        // required empty constructor
        private Agent() {}

        // initialize the Agent
        public static void init(Context context){
            if (sSQLHelper == null)
                sSQLHelper = new RecipeSQLiteOpenHelper(context);
        }

        /**
         * Gets all the notes in the database
         * @return      list containing the id's of all notes in the database
         */
        public static List<Long> getAllNotes() {
            return null;
        }

        /**
         * Adds a note to the database
         * @param note  note to add
         * @return      id of the newly added note
         */
        public static long addNote(Note note) {
            return 0;
        }

        /**
         * Retrieves a particular note from the database
         * @param id    id of the note to look up
         * @return      the note
         */
        public static Note getNote(long id) {
            return null;
        }

        /**
         * Updates an already existing note in the database
         * @param note  the updated note
         * @return      the id of the updated note in the database
         */
        public static long updateNote(Note note) {
            return 0;
        }

        /**
         * Deletes a note from the database
         * @param id    the id of the note to delete
         */
        public static void deleteNote(long id) {
        }

    }

    /**
     * The very backend. This creates the SQLite database when the app is first run
     */
    private static class RecipeSQLiteOpenHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 1;

        private static final String DATABASE_NAME = "noteManager";

        public RecipeSQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Creating tables
        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_NOTES_TABLE = "CREATE TABLE "
                    + TABLE_NOTES + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_TITLE + " TEXT,"
                    + KEY_ENCRYPTED_TEXT + " TEXT)";
            db.execSQL(CREATE_NOTES_TABLE);
        }

        // Upgrading the database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop older TABLE if it existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);

            // Create TABLEs again
            onCreate(db);
        }

    }
}
