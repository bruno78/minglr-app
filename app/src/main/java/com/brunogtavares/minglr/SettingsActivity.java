package com.brunogtavares.minglr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.brunogtavares.minglr.FirebaseData.FirebaseContract.FirebaseEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_CODE = 1;

    private ImageView mProfileImage;
    private EditText mNameField, mPhoneField;
    private Button mConfirmButton, mBackButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDb;

    private String mUserId, mName, mPhone, mProfileImageUrl;

    private Uri mResultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mProfileImage = (ImageView) findViewById(R.id.iv_profile_image);
        mNameField = (EditText) findViewById(R.id.et_set_user_name);
        mPhoneField = (EditText) findViewById(R.id.et_set_phone);
        mConfirmButton = (Button) findViewById(R.id.bt_confirm);
        mBackButton = (Button) findViewById(R.id.bt_back);

        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();

        mCustomerDb = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEntry.TABLE_NAME).child(FirebaseEntry.COLUMN_CUSTOMERS).child(mUserId);

        // Retrieve user's information to populate the settings text form.
        getUserInfo();

        // Choosing a profile picture
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_CODE);
            }
        });

        // Saving customer info to the database
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
    }

    private void getUserInfo() {

        mCustomerDb.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    Map<String, Object> userInfo = (Map<String, Object>) dataSnapshot.getValue();

                    if(userInfo.get(FirebaseEntry.COLUMN_NAME) != null) {
                        mNameField.setText(userInfo.get(FirebaseEntry.COLUMN_NAME).toString());
                    }

                    if(userInfo.get(FirebaseEntry.COLUMN_PHONE) != null) {
                        mPhoneField.setText(userInfo.get(FirebaseEntry.COLUMN_PHONE).toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInformation() {
        mName = mNameField.getText().toString();
        mPhone = mPhoneField.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put(FirebaseEntry.COLUMN_NAME, mName);
        userInfo.put(FirebaseEntry.COLUMN_PHONE, mPhone);

        mCustomerDb.updateChildren(userInfo);

        // Saving image to Firebase storage
        if (mResultUri != null) {

            StorageReference filepath = FirebaseStorage.getInstance().getReference()
                    .child(FirebaseEntry.COLUMN_PROFILE_IMAGE).child(mUserId);

            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), mResultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            })
        }
        else {
            finish();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            mResultUri = imageUri;
            mProfileImage.setImageURI(mResultUri);
        }
    }
}
