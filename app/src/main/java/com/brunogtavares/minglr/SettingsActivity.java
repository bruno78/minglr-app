package com.brunogtavares.minglr;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.brunogtavares.minglr.FirebaseData.FirebaseContract.FirebaseEntry;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_CODE = 1;

    private ImageView mProfileImage;
    private EditText mNameField, mBirthdayField;
    private Button mConfirmButton, mBackButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDb;

    private String mUserId, mName, mBirthday, mProfileImageUrl, mSex, mDescription;

    private Calendar mUserCalendar;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private Uri mResultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mProfileImage = (ImageView) findViewById(R.id.iv_profile_image);
        mNameField = (EditText) findViewById(R.id.et_set_user_name);
        mBirthdayField = (EditText) findViewById(R.id.et_set_user_birthday);
        mConfirmButton = (Button) findViewById(R.id.bt_confirm);
        mBackButton = (Button) findViewById(R.id.bt_back);

        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();

        mUserDb = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEntry.TABLE_USERS).child(mUserId);

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

        // Getting the Calendar pop up to get user's birthday
        mBirthdayField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SettingsActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                String date = month + "/" + dayOfMonth + "/" + year;
                mBirthdayField.setText(date);
                Log.d("Settings Activity", "onDateSet: mm/dd/yyyy " + month + "/" + dayOfMonth + "/" + year);
            }
        };

        // Saving customer info to the database
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });

        // Back to the Main activity
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        mBirthdayField.setText(sdf.format(mUserCalendar.getTime()));
    }

    private void getUserInfo() {

        mUserDb.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    Map<String, Object> userInfo = (Map<String, Object>) dataSnapshot.getValue();

                    if(userInfo.get(FirebaseEntry.COLUMN_NAME) != null) {
                        mNameField.setText(userInfo.get(FirebaseEntry.COLUMN_NAME).toString());
                    }
                    if(userInfo.get(FirebaseEntry.COLUMN_SEX) != null) {
                        mSex = userInfo.get(FirebaseEntry.COLUMN_SEX).toString();
                    }
                    if(userInfo.get(FirebaseEntry.COLUMN_BIRTHDAY) != null) {
                        mBirthdayField.setText(userInfo.get(FirebaseEntry.COLUMN_BIRTHDAY).toString());
                    }
                    if(userInfo.get(FirebaseEntry.COLUMN_PROFILE_IMAGE_URL) != null) {
                        mProfileImageUrl = userInfo.get(FirebaseEntry.COLUMN_PROFILE_IMAGE_URL).toString();

                        // If user has assigned an image on registration, assign it to profileImageUrl
                        Glide.clear(mProfileImage);
                        if(!mProfileImageUrl.equals("default")) {
                            Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                        }
                        else {
                            Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(mProfileImage);
                        }


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
        mBirthday = mBirthdayField.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put(FirebaseEntry.COLUMN_NAME, mName);
        userInfo.put(FirebaseEntry.COLUMN_BIRTHDAY, mBirthday);

        mUserDb.updateChildren(userInfo);

        // Saving image to Firebase storage
        if (mResultUri != null) {

            final StorageReference filepath = FirebaseStorage.getInstance().getReference()
                    .child(FirebaseEntry.COLUMN_PROFILE_IMAGE_URL).child(mUserId);

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

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL.
                    return filepath.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {

                        Uri downloadUrl = task.getResult();
                        Map userInfo = new HashMap();
                        userInfo.put(FirebaseEntry.COLUMN_PROFILE_IMAGE_URL, downloadUrl.toString());
                        mUserDb.updateChildren(userInfo);
                        Toast.makeText(SettingsActivity.this, "Message saved successfully!", Toast.LENGTH_SHORT).show();

                        finish();
                        return;
                    }
                    else {
                        Toast.makeText(SettingsActivity.this, "Unable to download file!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
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
