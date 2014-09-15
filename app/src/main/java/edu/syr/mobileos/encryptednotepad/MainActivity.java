package edu.syr.mobileos.encryptednotepad;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements
        NoteManipulatorFragment.OnNoteInteractionListener,
        NoteEditFragment.OnDoneClickedListener,
        NoteListFragment.OnNoteCreateListener,
        NoteListFragment.OnNoteClickedListener,
        PasswordDialogFragment.EditPasswordDialogListener
{

    private byte[] mKey;
    private ENDBManager mENDBManagerObject;

    @Override
    protected void onStart() {
        super.onStart();
        PasswordDialogFragment dialogFragment = new PasswordDialogFragment();
        dialogFragment.show(getFragmentManager(), null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (int i = 0; i < mKey.length; i++)
            mKey[i] = 0;
        mENDBManagerObject.closeDatabase();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new TestNotes();

        mENDBManagerObject = new ENDBManager(this);
        mENDBManagerObject.openDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onDoneClicked(Note note) {
        note = encryptNote(note);
        long noteId;
        if (!mENDBManagerObject.updateNoteThroughId(note)) {
            noteId = mENDBManagerObject.addNote(note);
        }
        else
        {
            noteId=note.getID();
        }

        Note new_note = getNoteThroughCursor(mENDBManagerObject.getNoteThroughId(noteId));
        getFragmentManager().beginTransaction()
                .replace(R.id.container, NoteDetailFragment.newInstance(new_note))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onNoteClicked(Note note) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, NoteDetailFragment.newInstance(note))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onNoteCreateClicked() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, NoteEditFragment.newInstance())
                .commit();
    }

    @Override
    public void onNoteInteraction(int action, Note note) {
        switch (action) {
            case Note.ACTION_DELETE:
                mENDBManagerObject.deleteNote(note.getID());
                ArrayList<Note> notes = new ArrayList<Note>();
                Cursor cursor = mENDBManagerObject.getAllNotes();
                for (long id : getAllNotesIdsFromCursor(cursor))
                    notes.add(getNoteThroughCursor(mENDBManagerObject.getNoteThroughId(id)));
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, NoteListFragment.newInstance(notes))
                        .addToBackStack(null)
                        .commit();
                break;
            case Note.ACTION_EDIT:
                long note_id;
                if (!mENDBManagerObject.updateNoteThroughId(note)) {
                    Log.d("MainActivity", "tried to update a note which doesn't exist in DB");
                }
                else {
                    note_id = note.getID();
                    Note new_note = getNoteThroughCursor(mENDBManagerObject.getNoteThroughId(note_id));
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, NoteEditFragment.newInstance(new_note))
                            .commit();
                }
                break;
        }
    }

    @Override
    public void onFinishEditDialog(String password) {
        mKey = Crypto.sha256(password);

        List<Note> test_notes = TestNotes.get();

        /* uncomment to make more notes */
        mENDBManagerObject.addNote(encryptNote(test_notes.get(0)));
        mENDBManagerObject.addNote(encryptNote(test_notes.get(1)));
        mENDBManagerObject.addNote(encryptNote(test_notes.get(2)));

        ArrayList<Note> notes = new ArrayList<Note>();
        for (long id : getAllNotesIdsFromCursor(mENDBManagerObject.getAllNotes()))
            notes.add(decryptNote(getNoteThroughCursor(mENDBManagerObject.getNoteThroughId(id))));

        getFragmentManager().beginTransaction()
                .add(R.id.container, NoteListFragment.newInstance(notes))
                .commit();
    }

    private Note getNoteThroughCursor(Cursor cursor)
    {
        Note note = new Note();
        long noteId = cursor.getLong(cursor.getColumnIndexOrThrow(ENDBManager.ENOTE_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(ENDBManager.ENOTE_TITLE));
        String contents = cursor.getString(cursor.getColumnIndexOrThrow(ENDBManager.ENOTE_CONTENTS));
        byte[] iv = StringUtility.string2Bin(cursor.getString(cursor.getColumnIndexOrThrow(ENDBManager.ENOTE_IVECTOR)));
        note.setID(noteId);
        note.setTitle(title);
        note.setText(contents);
        note.setIV(iv);
        return note;
    }

    private ArrayList<Long> getAllNotesIdsFromCursor(Cursor cursor)
    {
        ArrayList<Long> notesIdList = new ArrayList<Long>();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false)
        {
            notesIdList.add(cursor.getLong(cursor.getColumnIndexOrThrow(ENDBManager.ENOTE_ID)));
            cursor.moveToNext();
        }
        return notesIdList;
    }

    private Note encryptNote(Note note) {
        note.setIVText(Crypto.getIV());
        note.setIVTitle(Crypto.getIV());
        String encrypted_text = Crypto.aes256_enc(mKey, note.getText(), note.getIVText());
        String hmac = Crypto.hmac_sha256(mKey, encrypted_text);
        note.setText(hmac + encrypted_text);
        String encrypted_title = Crypto.aes256_enc(mKey, note.getTitle(), note.getIVText());
        hmac = Crypto.hmac_sha256(mKey, encrypted_title);
        note.setTitle(hmac + encrypted_title);

        return note;
    }

    private Note decryptNote(Note note) {
        String encrypted_blob = note.getText();
        String hmac = encrypted_blob.substring(0, 31);
        String encrypted_text = encrypted_blob.substring(32);
        if (!(hmac.equals(Crypto.hmac_sha256(mKey, encrypted_text)))) {
            note.setTitle("Title decryption failed!");
            note.setText("Password is incorrect. Try Again!");
            return note;
        }
        note.setText(Crypto.aes256_dec(mKey, encrypted_text, note.getIVText()));

        encrypted_blob = note.getTitle();
        hmac = encrypted_blob.substring(0, 31);
        encrypted_text = encrypted_blob.substring(32);
        if (!(hmac.equals(Crypto.hmac_sha256(mKey, encrypted_text)))) {
            note.setTitle("Note decryption failed!");
            note.setText("Note decryption failed!");
            return note;
        }
        note.setTitle(Crypto.aes256_dec(mKey, encrypted_text, note.getIVTitle()));

        return note;
    }

    // test function, please ignore
    private void testCrypto() {
        // Example demonstrating the Crypto class
        byte[] key = mKey;
        Log.d("debug", "key: " + StringUtility.bin2hex(key));
        String plaintext = "123456789101112131415161718192021222324252627282930313233343536";

        byte[] sIV = Crypto.getIV();
        Log.d("debug", "IV: " + sIV);

        Log.d("debug", "plaintext: " + plaintext);
        String ciphertext = Crypto.aes256_enc(key, plaintext,sIV);
        Log.d("debug", "ciphertext: " + ciphertext);

        String plaintext2 = Crypto.aes256_dec(key, ciphertext,sIV);
        Log.d("debug", "plaintext2: " + plaintext2);

        String hmacText = Crypto.hmac_sha256(key, ciphertext);
        Log.d("debug", "hmacText: "+hmacText);
        //Log.d("debug",Crypto)

        // Log.d("debug",Crypto.hm)
    }
}
