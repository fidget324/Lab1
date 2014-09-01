package edu.syr.mobileos.encryptednotepad;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link edu.syr.mobileos.encryptednotepad.NoteDetailFragment.OnNoteInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NoteDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class NoteDetailFragment extends Fragment {

    private static final String ARG_NOTE_ID = "note_id";

    private long mNoteID;

    private OnNoteInteractionListener mListener;

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnNoteInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNoteInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnNoteInteractionListener {
        /**
         * Callback to parent activity when a note manipulation is required
         * @param action    can be Note.ACTION_DELETE or Note.ACTION_EDIT
         * @param note_id   SQL id of the note to manipulate
         */
        public void onNoteInteraction(int action, long note_id);
    }

}
