package com.example.myapplicationfinal;

public class UserProfile {
    private String name;
    private String profilePicUrl;
    private String userId;

    public UserProfile(String name, String profilePicUrl,String userId) {
        this.name = name;
        this.profilePicUrl = profilePicUrl;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }
}
