package com.brunogtavares.minglr.chat;

/**
 * Created by brunogtavares on 6/2/18.
 */

public class Chat {

    private String message;
    private Boolean isCurrentUser;
    private String imageUrl;

    public Chat(String message, Boolean isCurrentUser) {
        this.message = message;
        this.isCurrentUser = isCurrentUser;
    }

    public Chat(String message, Boolean isCurrentUser, String photo) {
        this.imageUrl = photo;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
