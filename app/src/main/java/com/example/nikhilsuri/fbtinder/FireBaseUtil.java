package com.example.nikhilsuri.fbtinder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikhil.suri on 20/09/17.
 */

//make signleton
public class FireBaseUtil {


    private FirebaseAuth fireAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public FireBaseUtil() {
        fireAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    public void makePostInvisible(){
        final FirebaseUser user = fireAuth.getCurrentUser();
        Map<String, Object> userValues = new HashMap<>();
        userValues.put("displayName", user.getDisplayName());
        userValues.put("feedItemCheck", new HashMap<String, Boolean>());
        userValues.put("profileUrl", user.getPhotoUrl().toString());
        userValues.put("checkoutOutPosts",null);
        myRef.child("users").child(user.getUid()).setValue(userValues);
    }
}
