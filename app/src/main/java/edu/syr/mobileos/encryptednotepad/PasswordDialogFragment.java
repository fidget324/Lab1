package edu.syr.mobileos.encryptednotepad;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Presents a dialog to the user, prompting him/her for a password. When "OK" is clicked, a callback
 * to MainActivity is triggered, containing the entered password as a string. When "Cancel is clicked,
 * a callback to MainActivity provides the empty string.
 */
public class PasswordDialogFragment extends DialogFragment {

    private EditPasswordDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (EditPasswordDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement EditPasswordDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.password_dialog, null);

        final EditText passwordEntry = (EditText) dialogView.findViewById(R.id.password_dialog_EditText);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Please enter the master password: ")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onFinishEditDialog(passwordEntry.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onFinishEditDialog("");
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface EditPasswordDialogListener {
        void onFinishEditDialog(String password);
    }
}
