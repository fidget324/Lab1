package edu.syr.mobileos.encryptednotepad;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment which allows a new note to be created, or an already existing note to be edited.
 * There are EditText fields for the title and content, along with a "Done" button. This "Done"
 * button triggers a callback to the MainActivity.
 */
public class NoteEditFragment extends Fragment {

    private static final String ARG_NOTE_ID = "note_id";

    private long mNoteID;

    /**
     * For editing an already existing note
     * @param note_id   the SQL ID for the already existing note
     * @return          the new fragment
     */
    public static NoteEditFragment newInstance(long note_id) {
        NoteEditFragment fragment = new NoteEditFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_NOTE_ID, note_id);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * For creating a new note
     * @return          the new fragment
     */
    public static NoteEditFragment newInstance() {
        NoteEditFragment fragment = new NoteEditFragment();
        return fragment;
    }

    public NoteEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { // editing an existing note
            mNoteID = getArguments().getLong(ARG_NOTE_ID);
        }
        else { // creating a new note

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_edit, container, false);
    }

    public interface OnDoneClickedListener {
        /**
         * Callback to parent activity when a note has been edited
         * @param note_id   SQL id of the note that was edited
         */
        public void onDoneClicked(long note_id);
    }

}
