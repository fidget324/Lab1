package edu.syr.mobileos.encryptednotepad;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

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

    private static final String ARG_NOTES =
            "edu.syr.mobileos.encryptednotepad.NoteListFragment.notes";

    private ArrayList<Note> mNotes;
    private OnNoteClickedListener mNoteClickedListener;
    private OnNoteCreateListener mNoteCreateListener;
    private ArrayAdapter<String> mAdapter;

    /**
     * Display a set of notes in a ListView
     * @param notes     notes to display
     * @return          a new instance of NoteListFragment
     */
    public static NoteListFragment newInstance(ArrayList<Note> notes) {
        NoteListFragment fragment = new NoteListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTES, notes);
        fragment.setArguments(args);
        return fragment;
    }

    public NoteListFragment() {
        // Required empty public constructor
    }

    /**
     * Retrieve the list of notes from the fragment argument, and set up the adapter
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNotes = (ArrayList<Note>) getArguments().getSerializable(ARG_NOTES);
        }

        setHasOptionsMenu(true);

        ArrayList<String> titles = new ArrayList<String>();
        for (Note n : mNotes)
            titles.add(n.getTitle());

        mAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.fragment_note_list_entry,
                R.id.fragment_note_list_entry_title,
                titles);
    }

    /**
     * Inflate the view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_view = inflater.inflate(R.layout.fragment_note_list, container, false);

        ListView listView = (ListView) fragment_view.findViewById(R.id.list);

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mNoteClickedListener.onNoteClicked(mNotes.get(i));
            }
        });

        return fragment_view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_note_list, menu);
    }

    /**
     * Listen on the Edit and Delete Action Bar buttons. Callback whenever one is clicked
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_new_note:
                mNoteCreateListener.onNoteCreateClicked();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
         * @param note   the note that was clicked
         */
        public void onNoteClicked(Note note);
    }

    public interface OnNoteCreateListener {
        /**
         * Callback to parent activity when a new note should be created
         */
        public void onNoteCreateClicked();
    }

}
