package edu.syr.mobileos.encryptednotepad;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * A fragment which allows a new note to be created, or an already existing note to be edited.
 * There are EditText fields for the title and content, along with a "Done" button. This "Done"
 * button triggers a callback to the MainActivity.
 */
public class NoteEditFragment extends Fragment {

    private static final String ARG_NOTE =
            "edu.syr.mobileos.encryptednotepad.NoteEditFragment.note";

    private OnDoneClickedListener mDoneListener;
    private Note mNote;
    private EditText mEditTitle;
    private EditText mEditText;
    private Button mDoneButton;
    private boolean mCreateNewNote;

    /**
     * For editing an already existing note
     * @param note      the already existing note
     * @return          the new fragment
     */
    public static NoteEditFragment newInstance(Note note) {
        NoteEditFragment fragment = new NoteEditFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE, note);
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
            mNote = (Note) getArguments().getSerializable(ARG_NOTE);
            mCreateNewNote = false;
        }
        else { // creating a new note
            mNote = new Note();
            mCreateNewNote = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_view = inflater.inflate(R.layout.fragment_note_edit, container, false);

        mEditTitle = (EditText) fragment_view.findViewById(R.id.fragment_note_edit_title);
        mEditText = (EditText) fragment_view.findViewById(R.id.fragment_note_edit_text);
        mDoneButton = (Button) fragment_view.findViewById(R.id.fragment_note_edit_done_button);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNote.setTitle(mEditTitle.getText().toString());
                mNote.setText(mEditText.getText().toString());

/*                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0); */

                mDoneListener.onDoneClicked(mNote);
            }
        });
        mEditTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (mCreateNewNote)
                        mEditTitle.setText("");
                }
            }
        });
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (mCreateNewNote)
                        mEditText.setText("");
                }
            }
        });

        if (!mCreateNewNote) {
            mEditTitle.setText(mNote.getTitle());
            mEditText.setText(mNote.getText());
        }
        else {
            mEditTitle.setText("Enter title");
            mEditText.setText("Enter text");
        }

        return fragment_view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mDoneListener = (OnDoneClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDoneClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDoneListener = null;
    }

    public interface OnDoneClickedListener {
        /**
         * Callback to parent activity when a note has been edited
         * @param note   the note that was edited
         */
        public void onDoneClicked(Note note);
    }

}
