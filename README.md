#Minglr - Dating App


## Problems faced 

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

