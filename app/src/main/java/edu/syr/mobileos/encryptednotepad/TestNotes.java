package edu.syr.mobileos.encryptednotepad;

import java.util.List;

/**
 * Created by scottconstable on 8/29/14.
 */
public class TestNotes {

    private static List<Note> sNoteList;

    public static List<Note> get() {
        if (sNoteList == null)
            createNoteList();
        return sNoteList;
    }

    private static void createNoteList() {

    }

}
