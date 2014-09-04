package edu.syr.mobileos.encryptednotepad;

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
        long id;
        String title;
        String text;

        sNoteList = new ArrayList<Note>();

        id = 0;
        title = "Note 1";
        text = encryptText("This is a note");
        sNoteList.add(new Note(id, title, text));

        id = 1;
        title = "Note 2";
        text = encryptText("This is another note");
        sNoteList.add(new Note(id, title, text));

        id = 2;
        title = "Note 3";
        text = encryptText("This is yet another note");
        sNoteList.add(new Note(id, title, text));
    }

    private static String encryptText(String text) {
        byte[] key = Crypto.sha256("");
        return Crypto.aes256_enc(key, text);
    }

}
