# Technical Remarks and Issues

1. "General" error on sign up

**Solution**: On Signup activity, at `mAuth.createUserWithEmailAndPassword`, if task is not successful, add these lines:

```java
    if(!task.isSuccessful()) {
 
        Toast.makeText(SignupActivity.this,
            "Sign up error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
        Log.i(SignupActivity.this, "Failed to create user: " + task.getException().getMessage());
    }
```

2. App crashes if users try to login leaving password field in blank

**Solution**:

3. App crashes on launch

```java
E/dalvikvm: Could not find class 'android.support.v4.graphics.drawable.DrawableWrapper', 
referenced from method android.support.v7.widget.DrawableUtils.canSafelyMutateDrawable
```
Error on inflate ChooseLoginRegistration Activitiy 
on `setContentView(R.layout.activity_choose_login_registration);`

**Solution:** Update all the dependencies from 26.0.1 to 27.1.1

4. taskSnapshot.getDownload() can't be recognized.

Replace:

```java
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

```

By 

```java
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

```

More info: https://firebase.google.com/docs/storage/android/upload-files#get_a_download_url