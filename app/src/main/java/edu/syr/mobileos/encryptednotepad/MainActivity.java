package edu.syr.mobileos.encryptednotepad;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

/**
 * This serves as the Controller in the MVC design pattern.
 */
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

    /**
     * Prompt the user to enter the password
     */
    @Override
    protected void onStart() {
        super.onStart();
        PasswordDialogFragment dialogFragment = new PasswordDialogFragment();
        dialogFragment.show(getFragmentManager(), null);
    }

    /**
     * Wipe the password from memory (as best we can!)
     */
    @Override
    protected void onStop() {
        super.onStop();
        for (int i = 0; i < mKey.length; i++)
            mKey[i] = 0;
        mENDBManagerObject.closeDatabase();
        finish();
    }

    /**
     * Initialize the action bar, database, and fragment container
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mENDBManagerObject = new ENDBManager(this);
        mENDBManagerObject.openDatabase();
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Called whenever an Action Bar item controlled by MainActivity is clicked.
     * In our case this is only the home button.
     * @param item
     * @return
     */
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

    /**
     * User clicked "Done" after editing/creating a note. Add the note to the database, and
     * display the updated/new note
     * @param note   the note that was edited
     */
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

        updateNotes();
        Note new_note = getNoteThroughCursor(mENDBManagerObject.getNoteThroughId(noteId));
        getFragmentManager().beginTransaction()
                .replace(R.id.container, NoteDetailFragment.newInstance(decryptNote(new_note)))
                .addToBackStack(null)
                .commit();
    }

    /**
     * User selected a note from a list of notes. Display that note
     * @param note   the note that was clicked
     */
    @Override
    public void onNoteClicked(Note note) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, NoteDetailFragment.newInstance(note))
                .addToBackStack(null)
                .commit();
    }

    /**
     * User wishes to create a new note.
     */
    @Override
    public void onNoteCreateClicked() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, NoteEditFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    /**
     * User wishes to manipulate a note
     * @param action    can be Note.ACTION_DELETE or Note.ACTION_EDIT
     * @param note      the note to manipulate
     */
    @Override
    public void onNoteInteraction(int action, Note note) {
        switch (action) {
            case Note.ACTION_DELETE:
                mENDBManagerObject.deleteNote(note.getID());
                updateNotes();
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

    /**
     * User has entered the password, so we must hash it to generate the Key
     * @param password
     */
    @Override
    public void onFinishEditDialog(String password) {
        mKey = Crypto.sha256(password);
        updateNotes();

        getFragmentManager().beginTransaction()
                .add(R.id.container, NoteListFragment.newInstance(mNotes))
                .commit();
    }

    /**
     * Convert a Cursor object to a Note
     * @param cursor    A cursor set to a single note to convert
     * @return          The converted note
     */
    private Note getNoteThroughCursor(Cursor cursor)
    {
        Note note = new Note();
        long noteId = cursor.getLong(cursor.getColumnIndexOrThrow(ENDBManager.ENOTE_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(ENDBManager.ENOTE_TITLE));
        String contents = cursor.getString(cursor.getColumnIndexOrThrow(ENDBManager.ENOTE_CONTENTS));
        note.setID(noteId);
        note.setTitle(title);
        note.setText(contents);
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
        byte[] ivTitle = Crypto.getIV();
        byte[] ivText = Crypto.getIV();
        String encrypted_text = StringUtility.bin2String(ivText) +
                Crypto.aes256_enc(mKey, note.getText(), ivText);
        String hmac = Crypto.hmac_sha256(mKey, encrypted_text);
        note.setText(hmac + encrypted_text);
        String encrypted_title = StringUtility.bin2String(ivTitle) +
                Crypto.aes256_enc(mKey, note.getTitle(), ivTitle);
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
        byte[] ivText = StringUtility.string2Bin(encrypted_text.substring(0, 16));
        String decrypted_text = Crypto.aes256_dec(mKey, encrypted_text.substring(16), ivText);
        note.setText(decrypted_text);

        encrypted_blob = note.getTitle();
        hmac = encrypted_blob.substring(0, 32);
        String encrypted_title = encrypted_blob.substring(32);
        if (!(hmac.equals(Crypto.hmac_sha256(mKey, encrypted_title)))) {
            return null;
        }
        byte[] ivTitle = StringUtility.string2Bin(encrypted_title.substring(0, 16));
        String decrypted_title = Crypto.aes256_dec(mKey, encrypted_title.substring(16), ivTitle);
        note.setTitle(decrypted_title);

        return note;
    }

    private void updateNotes() {
        mNotes = new ArrayList<Note>();
        Note note;
        for (long id : getAllNotesIdsFromCursor(mENDBManagerObject.getAllNotes())) {
            note = decryptNote(getNoteThroughCursor(mENDBManagerObject.getNoteThroughId(id)));
            if (note != null)
                mNotes.add(note);
        }
    }
}
