package com.brunogtavares.minglr.model;

/**
 * Created by brunogtavares on 6/2/18.
 */

public class Match {

    private String userId;
    private String name;
    private String profileImageUrl;

    public Match(String userId, String name, String profileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserId() { return userId; }
    public void setUserId(String id) { this.userId = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
