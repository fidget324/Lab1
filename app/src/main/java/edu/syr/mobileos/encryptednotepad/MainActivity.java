package edu.syr.mobileos.encryptednotepad;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity implements
        NoteManipulatorFragment.OnNoteInteractionListener,
        NoteEditFragment.OnDoneClickedListener,
        NoteListFragment.OnNoteCreateListener,
        NoteListFragment.OnNoteClickedListener,
        PasswordDialogFragment.EditPasswordDialogListener
{

    private byte[] mKey;

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new TestNotes();
        TestNotes.get();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, NoteListFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDoneClicked(Note note) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, NoteDetailFragment.newInstance(note))
                .commit();
    }

    @Override
    public void onNoteClicked(Note note) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, NoteDetailFragment.newInstance(note))
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
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, NoteListFragment.newInstance())
                        .commit();
                break;
            case Note.ACTION_EDIT:
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, NoteEditFragment.newInstance(note))
                        .commit();
                break;
        }
    }

    @Override
    public void onFinishEditDialog(String password) {
        mKey = Crypto.sha256(password);
    }

    // test function, please ignore
    private void testCrypto() {
        // Example demonstrating the Crypto class
        Log.d("debug", "key: " + Crypto.bin2hex(mKey));
        String plaintext = "hello kitty";
        Log.d("debug", "plaintext: " + plaintext);
        String ciphertext = Crypto.aes256_enc(mKey, plaintext);
        Log.d("debug", "ciphertext: " + ciphertext);
        plaintext = Crypto.aes256_dec(mKey, ciphertext);
        Log.d("debug", "plaintext: " + plaintext);
    }
}
