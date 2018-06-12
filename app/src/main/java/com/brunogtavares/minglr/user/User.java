package com.brunogtavares.minglr.user;

/**
 * Created by brunogtavares on 6/11/18.
 */

public class User {
    private String userId;
    private String userName;
    private int userAge;
    private String userProfileImage;
    private String userDescription;

    public User(String userId, String userName, int userAge, String userProfileImage, String userDescription) {
        this.userId = userId;
        this.userName = userName;
        this.userAge = userAge;
        this.userProfileImage = userProfileImage;
        this.userDescription = userDescription;
    }

    public User(String userName, int userAge, String userProfileImage, String userDescription) {
        this.userName = userName;
        this.userAge = userAge;
        this.userProfileImage = userProfileImage;
        this.userDescription = userDescription;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }
}
