package com.example.nikhilsuri.fbtinder;

import java.util.Date;

/**
 * Created by nikhil.suri on 16/01/18.
 */

public class SwipedOutPost {

    String postId;
    Date swipedOutTime;

    public SwipedOutPost(String postId, Date swipedOutTime) {
        this.postId = postId;
        this.swipedOutTime = swipedOutTime;
    }

    public SwipedOutPost() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Date getSwipedOutTime() {
        return swipedOutTime;
    }

    public void setSwipedOutTime(Date swipedOutTime) {
        this.swipedOutTime = swipedOutTime;
    }
}
