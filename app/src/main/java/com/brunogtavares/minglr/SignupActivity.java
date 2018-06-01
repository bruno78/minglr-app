package com.brunogtavares.minglr;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.brunogtavares.minglr.FirebaseData.FirebaseContract.FirebaseEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText mEmail, mPassword, mName;
    private Button mSignup;

    private RadioGroup mSexRadioGroup;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mName = (EditText) findViewById(R.id.et_name);
        mEmail = (EditText) findViewById(R.id.et_signup_email);
        mPassword = (EditText) findViewById(R.id.et_signup_password);
        mSexRadioGroup = (RadioGroup) findViewById(R.id.rg_sex);

        mSignup = (Button) findViewById(R.id.bt_signup);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null) {
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mSignup.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                // Getting the selected Radio Group option id
                int selectedId = mSexRadioGroup.getCheckedRadioButtonId();

                // Use the selected Radio Group option id to find which button was selected to pass to Firebase
                final RadioButton radioButton = (RadioButton) findViewById(selectedId);

                // Check if the user has NOT chosen something it quits before trigger the rest of operation
                if(radioButton.getText() == null) {
                    return;
                }

                final String name = mName.getText().toString();
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                // If signup fails
                                if(!task.isSuccessful()) {

                                    Toast.makeText(SignupActivity.this,
                                            "Sign up error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                   // Log.i("Response","Failed to create user: "+task.getException().getMessage());
                                }
                                else {


                                    String userId = mAuth.getCurrentUser().getUid();
                                    DatabaseReference currentUserDb = FirebaseDatabase.getInstance()
                                            .getReference().child(FirebaseEntry.TABLE_NAME).child(radioButton.getText().toString())
                                            .child(userId);

                                    Map userInfo = new HashMap();
                                    userInfo.put(FirebaseEntry.COLUMN_NAME, name);
                                    userInfo.put(FirebaseEntry.COLUMN_PROFILE_IMAGE_URL, "default");

                                    currentUserDb.updateChildren(userInfo);
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
