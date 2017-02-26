package com.example.mealbuddy;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity
        implements CreateAccountDialogFragment.DialogFragmentListener {

    private final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase login
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null) {
                    // User is signed out
                    Log.i(TAG, "User is signed out");
                } else {
                    // User is signed in
                    Log.i(TAG, "User is signed in:" + user.getUid());

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        };

        final Button login_button = (Button) findViewById(R.id.login_button);
        login_button.setEnabled(false);

        final EditText username_text = (EditText) findViewById(R.id.login_username_text);
        final EditText password_text = (EditText) findViewById(R.id.login_password_text);
        TextWatcher credential_watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (username_text.length() == 0 || password_text.length() == 0) {
                    login_button.setEnabled(false);
                } else {
                    login_button.setEnabled(true);
                }
            }
        };
        username_text.addTextChangedListener(credential_watcher);
        password_text.addTextChangedListener(credential_watcher);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Login button clicked");
                mAuth.signInWithEmailAndPassword(
                        username_text.getText().toString(),
                        password_text.getText().toString())
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete" + task.isSuccessful());

                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(LoginActivity.this, "Sign In Failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        });

        TextView createUserText = (TextView) findViewById(R.id.create_acct_text);
        createUserText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Create user text clicked");

                DialogFragment createAccountDialog = new CreateAccountDialogFragment();
                createAccountDialog.show(getFragmentManager(), "createAccount");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.i(TAG, "onDialogNegativeClick");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.i(TAG, "onDialogPositiveClick");

        if (dialog instanceof CreateAccountDialogFragment) {
            CreateAccountDialogFragment d = (CreateAccountDialogFragment) dialog;
            String userEmail = d.getUserEmail();
            String password = d.getPassword();
            Log.d(TAG, "New - username: " + d.getUserEmail()
                    + " password: " + d.getPassword());

            if (userEmail != null && !userEmail.isEmpty() &&
                    password != null && !password.isEmpty()) {
                mAuth.createUserWithEmailAndPassword(userEmail, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Failed to Create Account",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }
}
