package edu.syr.mobileos.encryptednotepad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment which displays the details for a given note. These include the note's title,
 * decrypted text, and (optionally) the date of creation.
 *
 * There are two action bar buttons: one for editing the current note, and one for deleting
 * it. These two buttons trigger callbacks to the MainActivity via the interface defined in
 * NoteManipulatorFragment.
 */
public class NoteDetailFragment extends NoteManipulatorFragment {

    private static final String ARG_NOTE =
            "edu.syr.mobileos.encryptednotepad.NoteDetailFragment.note";

    private Note mNote;
    private TextView mTitleView;
    private TextView mTextView;

    public static NoteDetailFragment newInstance(Note note) {
        NoteDetailFragment fragment = new NoteDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE, note);
        fragment.setArguments(args);
        return fragment;
    }

    public NoteDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mNote = (Note) getArguments().getSerializable(ARG_NOTE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_view = inflater.inflate(R.layout.fragment_note_detail, container, false);

        mTitleView = (TextView) fragment_view.findViewById(R.id.fragment_note_detail_title);
        mTextView = (TextView) fragment_view.findViewById(R.id.fragment_note_detail_text);

        mTitleView.setText(mNote.getTitle());
        mTextView.setText(mNote.getText());

        return fragment_view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_note_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit_note:
                mInteractionListener.onNoteInteraction(Note.ACTION_EDIT, mNote);
                break;
            case R.id.action_delete_note:
                mInteractionListener.onNoteInteraction(Note.ACTION_DELETE, mNote);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
