package edu.syr.mobileos.encryptednotepad;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
    private ArrayList<Note> mNotes;

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
        TestNotes.clear();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, NoteListFragment.newInstance(mNotes))
                        .addToBackStack(null)
                        .commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            noteId = note.getID();
        }

        Note new_note = getNoteThroughCursor(mENDBManagerObject.getNoteThroughId(noteId));
        getFragmentManager().beginTransaction()
                .replace(R.id.container, NoteDetailFragment.newInstance(decryptNote(new_note)))
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
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onNoteInteraction(int action, Note note) {
        switch (action) {
            case Note.ACTION_DELETE:
                mENDBManagerObject.deleteNote(note.getID());
                mNotes = new ArrayList<Note>();
                Cursor cursor = mENDBManagerObject.getAllNotes();
                for (long id : getAllNotesIdsFromCursor(cursor))
                    mNotes.add(getNoteThroughCursor(mENDBManagerObject.getNoteThroughId(id)));
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, NoteListFragment.newInstance(mNotes))
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
                            .addToBackStack(null)
                            .commit();
                }
                break;
        }
    }

    @Override
    public void onFinishEditDialog(String password) {
        mKey = Crypto.sha256(password);

        List<Note> test_notes = TestNotes.get();
        Log.d("debug", test_notes.get(0).getText());
        Log.d("debug", test_notes.get(1).getText());
        Log.d("debug", test_notes.get(2).getText());

        /* uncomment to make more notes */
        mENDBManagerObject.addNote(encryptNote(test_notes.get(0)));
        mENDBManagerObject.addNote(encryptNote(test_notes.get(1)));
        mENDBManagerObject.addNote(encryptNote(test_notes.get(2)));

        mNotes = new ArrayList<Note>();
        Note note;
        for (long id : getAllNotesIdsFromCursor(mENDBManagerObject.getAllNotes())) {
            note = decryptNote(getNoteThroughCursor(mENDBManagerObject.getNoteThroughId(id)));
            if (note != null)
                mNotes.add(note);
        }

        getFragmentManager().beginTransaction()
                .add(R.id.container, NoteListFragment.newInstance(mNotes))
                .commit();
    }

    private Note getNoteThroughCursor(Cursor cursor)
    {
        Note note = new Note();
        long noteId = cursor.getLong(cursor.getColumnIndexOrThrow(ENDBManager.ENOTE_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(ENDBManager.ENOTE_TITLE));
        String contents = cursor.getString(cursor.getColumnIndexOrThrow(ENDBManager.ENOTE_CONTENTS));
        byte[] ivTitle = StringUtility.string2Bin(cursor.getString(cursor.getColumnIndexOrThrow(ENDBManager.ENOTE_IVECTORTITLE)));
        byte[] ivContent = StringUtility.string2Bin(cursor.getString(cursor.getColumnIndexOrThrow(ENDBManager.ENOTE_IVECTORCONTENT)));
        note.setID(noteId);
        note.setTitle(title);
        note.setText(contents);
        note.setIVTitle(ivTitle);
        note.setIVText(ivContent);
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
        note.setIVTitle(Crypto.getIV());
        note.setIVText(Crypto.getIV());
        String encrypted_text = Crypto.aes256_enc(mKey, note.getText(), note.getIVText());
        String hmac = Crypto.hmac_sha256(mKey, encrypted_text);
        note.setText(hmac + encrypted_text);
        String encrypted_title = Crypto.aes256_enc(mKey, note.getTitle(), note.getIVTitle());
        hmac = Crypto.hmac_sha256(mKey, encrypted_title);
        note.setTitle(hmac + encrypted_title);

        return note;
    }

    private Note decryptNote(Note note) {
        String encrypted_blob = note.getText();
        String hmac = encrypted_blob.substring(0, 32);
        String encrypted_text = encrypted_blob.substring(32);
        if (!(hmac.equals(Crypto.hmac_sha256(mKey, encrypted_text)))) {
            return null;
        }
        byte[] iv = note.getIVText();
        String decrypted_text = Crypto.aes256_dec(mKey, encrypted_text, iv);
        note.setText(decrypted_text);

        encrypted_blob = note.getTitle();
        hmac = encrypted_blob.substring(0, 32);
        encrypted_text = encrypted_blob.substring(32);
        if (!(hmac.equals(Crypto.hmac_sha256(mKey, encrypted_text)))) {
            return null;
        }
        iv = note.getIVTitle();
        String decrypted_title = Crypto.aes256_dec(mKey, encrypted_text, iv);
        note.setTitle(decrypted_title);

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
