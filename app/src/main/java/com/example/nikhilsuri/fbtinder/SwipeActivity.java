package com.example.nikhilsuri.fbtinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by nikhilsuri on 5/6/17.
 */


public class SwipeActivity extends Activity {


    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private JSONArray postsArray;

    public String displayName;
    public String profilePic;
    private String TAG = "SwipeActivity";

    private FirebaseDatabase database;
    private FirebaseAuth fireAuth;
    private DatabaseReference myRef;
    private FirebaseUser user;
    public GraphRequest nextRequestUrl;
    public SwipedOutPosts swipedOutPosts;
    public SwipedOutPosts deletedPosts;
    public List<FeedItem> feedItems = new ArrayList<>();
    public Boolean backupDone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        database = FirebaseDatabase.getInstance();
        fireAuth = FirebaseAuth.getInstance();
        myRef = database.getReference();
        mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);
        mContext = getApplicationContext();
        mSwipeView.getBuilder()
                .setDisplayViewCount(20)
                .setSwipeDecor(new SwipeDecor()
                        //.setPaddingTop(5)
                        //.setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.swipe_right)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_left));

        user = fireAuth.getCurrentUser();


        setAcceptListner();
        setRejectListner();
        setLogoutListner();
        feedItemFirebaseReferenceListner();
        getPosts();
    }


    private void feedItemFirebaseReferenceListner() {
        ((DatabaseReference) myRef.child(Constants.USERSTAG).child(user.getUid()).child(Constants.CHECKED_OUT_POSTS)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SwipeActivity.this.swipedOutPosts = dataSnapshot.getValue(SwipedOutPosts.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ((DatabaseReference) myRef.child(Constants.USERSTAG).child(user.getUid()).child(Constants.DELETED_POSTS)).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        SwipeActivity.this.deletedPosts = dataSnapshot.getValue(SwipedOutPosts.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        ((DatabaseReference) myRef.child(Constants.USERSTAG).child(user.getUid()).child(Constants.DISPLAY_NAME)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SwipeActivity.this.displayName = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ((DatabaseReference) myRef.child(Constants.USERSTAG).child(user.getUid()).child(Constants.PROFILE_PIC)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SwipeActivity.this.profilePic = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public boolean isPostSwiped(String postId) {
        return swipedOutPosts.getSwipedOutPostHashMap().containsKey(postId);
    }

    public void setRejectListner() {
        findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });

    }

    public void setAcceptListner() {
        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
                Log.d("FeedItems", feedItems.size() + ":" + feedItems.toString());
            }
        });

    }


    private void setLogoutListner() {
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(-1);
                addSwipedPostsInFirebase();
                finish();
            }
        });
    }

    public void checkForLastItems() {

        if (feedItems.size() < 5) {
            getPosts();
        }

    }


    private void getPosts() {
        try {
            if (nextRequestUrl == null) {
                GraphRequest request = new GraphRequest(
                        AccessToken.getCurrentAccessToken(), "me/posts", null, HttpMethod.GET,
                        graphCallback);
                Bundle params = new Bundle();
                params.putString("fields", "id,caption,created_time,description,icon,link,message,name,permalink_url,picture,place,shares,story");
                request.setParameters(params);
                request.executeAsync();
            } else
                nextRequestUrl.executeAsync();
        } catch (Exception e) {
            Log.e(TAG, e.toString());

        }
    }


    final GraphRequest.Callback graphCallback = new GraphRequest.Callback() {
        @Override
        public void onCompleted(GraphResponse response) {
            try {
                Log.e(TAG, response.toString());
                postsArray = response.getJSONObject().getJSONArray("data");
                Log.e(TAG, postsArray.toString());
                for (int i = 0; i < postsArray.length(); ++i) {
                    FeedItem feedItem;
                    try {
                        if (isPostSwiped(postsArray.getJSONObject(i).getString("id")))
                            continue;
                        feedItem = new FeedItem(postsArray.getJSONObject(i), displayName, profilePic);
                        PostSwipe singlePost = getSinglePost(feedItem);
                        mSwipeView.addView(singlePost);
                        feedItems.add(feedItem);
                        backupDone = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                nextRequestUrl = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
                if (nextRequestUrl != null) {
                    nextRequestUrl.setCallback(this);
                }
                checkForLastItems();
                // fillCards();
            } catch (Exception q) {
                Log.e(TAG, q.getStackTrace().toString());
            }
        }
    };

    private PostSwipe getSinglePost(FeedItem feedItem) {
        return new PostSwipe(mContext, feedItem, mSwipeView, this);
    }


    public void addSwipedPostsInFirebase() {
        if (backupDone == null || backupDone == false) {
            myRef.child(Constants.USERSTAG).child(user.getUid()).child(Constants.CHECKED_OUT_POSTS).setValue(swipedOutPosts);
            myRef.child(Constants.USERSTAG).child(user.getUid()).child(Constants.DELETED_POSTS).setValue(deletedPosts);
            backupDone = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        addSwipedPostsInFirebase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        addSwipedPostsInFirebase();
    }

    @Override
    protected void onStop() {
        super.onStop();
        addSwipedPostsInFirebase();
    }

    public void startActivity() {
        Intent main = new Intent(SwipeActivity.this, MainActivity.class);
        startActivity(main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        backupDone = false;

    }
}
