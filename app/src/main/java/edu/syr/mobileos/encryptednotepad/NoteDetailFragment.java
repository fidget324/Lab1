package edu.syr.mobileos.encryptednotepad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment which displays the details for a given note. These include the note's title,
 * decrypted text, and (optionally) the date of creation.
 *
 * There are two action bar buttons: one for editing the current note, and one for deleting
 * it. These two buttons trigger callbacks to the MainActivity via the interface defined in
 * NoteManipulatorFragment.
 */
public class NoteDetailFragment extends NoteManipulatorFragment {

    private static final String ARG_NOTE_ID = "note_id";

    private long mNoteID;

    public static NoteDetailFragment newInstance(long note_id) {
        NoteDetailFragment fragment = new NoteDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_NOTE_ID, note_id);
        fragment.setArguments(args);
        return fragment;
    }

    public NoteDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNoteID = getArguments().getLong(ARG_NOTE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_detail, container, false);
    }

}
