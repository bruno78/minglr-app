# Minglr - Dating App

<p align="center">
 <ima src="https://cdn.rawgit.com/bruno78/minglr-app/250502ce/app/src/main/ic_launcher-web.png" />
</p>

It's a clone of Tinder dating App where users can see cards of other users and swipe left for "nope" or right for "yep".
If two users swipe "yep" for each other, a match is made and they can chat to each other. 

## Pre-requisites

* Android SDK v27
* Android min SDK v15

## Tools used

* Firebase core v16.0.0
    * auth v16.0.1
    * database v16.0.1
    * storage v16.0.1
* Lorentzo's Swipe Cards Library v1.0.9
* Glide v3.7.0
* Cardview 27.1.1

## Getting Started 

This app uses the Gradle build system and [Google's Firebase](https://firebase.google.com/). 

To add Firebase, go to [Firebase console](https://console.firebase.google.com/):

1. Create a project, and follow the instructions to get google-services.json file. Once 
the file is downloaded, copy and paste to the app's folder. 

2. Go to the newly created project and choose Authentication. On the Sing-in method tab, choose Email/Password
provider

3. Go to the database and activate "Realtime Database". On the rules tab make sure only authenticated
users have access by setting up the configuration to this:

```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}

```
4. Go to Storage and activate storage. On the rules tab make sure it's configured to this:

```
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

NOTE: This app won't work without Firebase. 

Once Firebase is properly setup, build this project by using the "gradlew build" command or use "Import Project" Android Studio. 

## To do:

- [ ] Polish UI
- [ ] Add user profiles
- [ ] Add favorites feature
- [ ] Add user preferences
- [ ] Add AdMob
- [ ] Bring nopes back 
- [x] Allow users to send images to chat

## Issues 

The list of open issues and solutions can be found at the github repo or click [here](https://github.com/bruno78/minglr-app/blob/master/TECHNICAL.md)