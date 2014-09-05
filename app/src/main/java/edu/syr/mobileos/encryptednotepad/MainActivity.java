package edu.syr.mobileos.encryptednotepad;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;



public class MainActivity extends Activity implements
        NoteManipulatorFragment.OnNoteInteractionListener,
        NoteEditFragment.OnDoneClickedListener,
        NoteListFragment.OnNoteCreateListener,
        NoteListFragment.OnNoteClickedListener
{

    public static final String sHardcodedKey = "mykey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new TestNotes();
        TestNotes.get();

        // Example demonstrating the Crypto class
        byte[] key = Crypto.sha256(sHardcodedKey);
        Log.d("debug", "key: " + Crypto.bin2hex(key));
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

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    @Override
    public void onDoneClicked(long note_id) {

    }

    @Override
    public void onNoteClicked(long note_id) {

    }

    @Override
    public void onNoteCreateClicked() {

    }

    @Override
    public void onNoteInteraction(int action, long note_id) {

    }
}
