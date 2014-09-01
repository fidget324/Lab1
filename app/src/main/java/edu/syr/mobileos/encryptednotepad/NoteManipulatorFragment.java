package edu.syr.mobileos.encryptednotepad;

import android.app.Activity;
import android.app.Fragment;

/**
 * Defines the interface for fragments which can manipulate (ie. edit, delete) notes
 */
public abstract class NoteManipulatorFragment extends Fragment {

    protected OnNoteInteractionListener mListener;

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
