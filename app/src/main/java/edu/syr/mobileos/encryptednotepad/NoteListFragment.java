package edu.syr.mobileos.encryptednotepad;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

/**
 * A list of all the notes currently stored in the app. Each entry contains the note title,
 * and (optionally) the date on which it was created.
 *
 * Clicking on an entry will trigger a callback, onNoteClicked(), to the MainActivity
 * with that note's ID. Long clicking on an entry will trigger a popup menu. The
 * popup menu provides two options: editing and deleting the selected note. Clicking either of
 * these will send an onNoteInteraction() callback to the MainActivity.
 *
 * There is one action bar button for adding a new note. This triggers a callback,
 * onNoteCreateClicked(), to the MainActivity.
 */
public class NoteListFragment extends NoteManipulatorFragment {

    private List<Long> mNotes;
    private OnNoteClickedListener mNoteClickedListener;
    private OnNoteCreateListener mNoteCreateListener;

    // TODO: Rename and change types and number of parameters
    public static NoteListFragment newInstance() {
        NoteListFragment fragment = new NoteListFragment();
        return fragment;
    }

    public NoteListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_view = inflater.inflate(R.layout.fragment_note_list, container, false);

        ListView listView = (ListView) fragment_view.findViewById(R.id.list);

        return fragment_view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mNoteClickedListener = (OnNoteClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNoteClickedListener");
        }
        try {
            mNoteCreateListener = (OnNoteCreateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNoteCreateListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mNoteClickedListener = null;
        mNoteCreateListener = null;
    }

    public interface OnNoteClickedListener {
        /**
         * Callback to parent activity when a note is clicked
         * @param note_id   SQL id of the note that was clicked
         */
        public void onNoteClicked(long note_id);
    }

    public interface OnNoteCreateListener {
        /**
         * Callback to parent activity when a new note should be created
         */
        public void onNoteCreateClicked();
    }

}
