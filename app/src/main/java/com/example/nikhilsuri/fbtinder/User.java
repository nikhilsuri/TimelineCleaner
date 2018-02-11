package com.example.nikhilsuri.fbtinder;

import java.util.HashMap;
import java.util.List;

/**
 * Created by nikhil.suri on 14/06/17.
 */

public class User {

    String profilePicUrl;
    String displayName;
    String lastName;
    String checkoutOutPosts;

    public String getCheckoutOutPosts() {
        return checkoutOutPosts;
    }

    public void setCheckoutOutPosts(String checkoutOutPosts) {
        this.checkoutOutPosts = checkoutOutPosts;
    }

    public User() {
    }

    public List<String> getUsedPosts() {
        return usedPosts;
    }

    public void setUsedPosts(List<String> usedPosts) {
        this.usedPosts = usedPosts;
    }

    List<String> usedPosts;

    public User(String profilePicUrl, String displayName, String lastName) {
        this.profilePicUrl = profilePicUrl;
        this.displayName = displayName;
        this.lastName = lastName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
