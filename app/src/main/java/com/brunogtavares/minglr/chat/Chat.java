package com.brunogtavares.minglr.chat;

/**
 * Created by brunogtavares on 6/2/18.
 */

public class Chat {

    private String message;
    private Boolean isCurrentUser;

    public Chat(String message, Boolean isCurrentUser) {
        this.message = message;
        this.isCurrentUser = isCurrentUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getCurrentUser() {
        return isCurrentUser;
    }

    public void setCurrentUser(Boolean currentUser) {
        isCurrentUser = currentUser;
    }
}
