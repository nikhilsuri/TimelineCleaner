package com.example.nikhilsuri.fbtinder;

import java.util.HashMap;

/**
 * Created by nikhil.suri on 15/01/18.
 */

public class SwipedOutPosts {
    private HashMap<String, SwipedOutPost> swipedOutPostHashMap;

    public SwipedOutPosts() {
        swipedOutPostHashMap = new HashMap<>();

    }


    public HashMap<String, SwipedOutPost> getSwipedOutPostHashMap() {
        return swipedOutPostHashMap;
    }

    public void setSwipedOutPostHashMap(HashMap<String, SwipedOutPost> swipedOutPostHashMap) {
        this.swipedOutPostHashMap = swipedOutPostHashMap;
    }
}
