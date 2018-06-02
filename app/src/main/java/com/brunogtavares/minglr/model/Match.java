package com.brunogtavares.minglr.model;

/**
 * Created by brunogtavares on 6/2/18.
 */

public class Match {

    private String userId;

    public Match(String userId) {
        this.userId = userId;
    }

    public String getUserId() { return userId; }
    public void setUserId(String id) { this.userId = id; }
}
