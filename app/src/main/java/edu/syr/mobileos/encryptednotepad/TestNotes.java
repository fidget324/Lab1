package edu.syr.mobileos.encryptednotepad;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This is for testing purposes only. Will not be included in the final version.
 */
public class TestNotes {

    private static List<Note> sNoteList = null;

    public static List<Note> get() {
        if (sNoteList == null)
            createNoteList();
        return sNoteList;
    }

    private static void createNoteList() {
        Log.d("debug", "createNoteList()");
        String title;
        String text;

        sNoteList = new ArrayList<Note>();

        title = "Note 1";
        text = "This is a note";
        sNoteList.add(new Note(title, text));

        title = "Note 2";
        text = "This is another note";
        sNoteList.add(new Note(title, text));

        title = "Note 3";
        text = "This is yet another note";
        sNoteList.add(new Note(title, text));
    }

    public static void clear() {
        sNoteList = null;
    }
}
