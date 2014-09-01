package edu.syr.mobileos.encryptednotepad;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by scottconstable on 9/1/14.
 */
public class NoteDB {

    public NoteDB() {}

    // Tables
    private static final String TABLE_NOTES = "notes";

    // Note DB Contract
    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ENCRYPTED_TEXT = "text";

    public static class Agent {

        private static RecipeSQLiteOpenHelper sSQLHelper;
        private static SQLiteDatabase sDatabase;

        private Agent() {}

        public static void init(Context context){
            if (sSQLHelper == null)
                sSQLHelper = new RecipeSQLiteOpenHelper(context);
        }

        public static long getAllNotes() {
            return 0;
        }

        public static long addNote(Note note) {
            return 0;
        }

        public static Note getNote(long id) {
            return null;
        }

        public static void updateNote(Note note) {
        }

        public static void deleteNote(long id) {
        }

    }

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
