package com.example.mealbuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class CreateAccountDialogFragment extends DialogFragment {

    public interface DialogFragmentListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    private DialogFragmentListener mListener;
    private String mUserEmail;
    private String mPassword;

    private final String TAG = "CreateAccountDialog";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (DialogFragmentListener) activity;
        } catch (ClassCastException cce) {
            throw new ClassCastException(activity.toString()
                    + " must implement CreateAccountDialogFragment.DialogFragmentListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.create_user, null);
        final EditText usernameEdit = (EditText) v.findViewById(R.id.create_email_edit);
        final EditText passwordEdit = (EditText) v.findViewById(R.id.create_password_edit);

        builder.setTitle(R.string.create_account)
                .setView(v)
                .setPositiveButton(R.string.create_account, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "Confirm Create");
                        mUserEmail = usernameEdit.getText().toString();
                        mPassword = passwordEdit.getText().toString();
                        mListener.onDialogPositiveClick(CreateAccountDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Cancel Create");
                        mListener.onDialogNegativeClick(CreateAccountDialogFragment.this);
                    }
                });
        return builder.create();
    }

    public String getUserEmail() { return mUserEmail; }
    public String getPassword() { return mPassword; }

}
