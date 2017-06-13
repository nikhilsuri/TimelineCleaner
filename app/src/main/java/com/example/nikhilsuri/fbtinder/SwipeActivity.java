package com.example.nikhilsuri.fbtinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.login.LoginManager;
import com.facebook.share.widget.ShareDialog;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nikhilsuri on 5/6/17.
 */


public class SwipeActivity extends Activity {


    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private JSONArray postsArray;
    private ShareDialog shareDialog;

    private String name, surname, profilePic;
    private String TAG = "SwipeActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);
        mContext = getApplicationContext();

        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.swipe_right)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_left));


        Bundle inBundle = getIntent().getExtras();
        try {
            name = inBundle.getString("name");
            surname = inBundle.getString("surname");
            profilePic = inBundle.getString("imageUrl");
            postsArray = new JSONArray(inBundle.getString("postsArray"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < postsArray.length(); ++i) {

            FeedItem feedItem = null;
            try {
                feedItem = convertToFeedItem(postsArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PostSwipe singlePost = new PostSwipe(mContext, feedItem, mSwipeView);
            SwipePlaceHolderView addedView = mSwipeView.addView(singlePost);

        }
        findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });

        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });

    }

    private void logout() {
        LoginManager.getInstance().logOut();
        Intent login = new Intent(SwipeActivity.this, MainActivity.class);
        startActivity(login);
        finish();
    }


    private FeedItem convertToFeedItem(JSONObject post) {

        FeedItem item = new FeedItem();
        try {
            //profile name
            String story = "";
            if (!post.has("story"))
                story = name + "  " + surname;
            else {
                //name is there in the story
                //story
                story = story + "  " + post.getString("story");
            }
            item.setStory(story);
            //profile pic
            if (profilePic != null && !profilePic.isEmpty())
                item.setProfilePic(profilePic);
            //heading ,message
            if (post.has("message"))
                item.setStatus(post.getString("message"));
            // post image
            String image = post.isNull("picture") ? null : post
                    .getString("picture");

            item.setImge(image);
            //post message and description
            if (post.has("description")) {
                item.setPostDescription(post.getString("description"));
            }
            if (post.has("name")) {
                item.setpostName(post.getString("name"));
            }


            //created time
            String dateTime[] = post.getString("created_time").split("T");
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
            String inputDateStr = "2013-06-24";
            Date date = inputFormat.parse(dateTime[0]);
            String dateString = outputFormat.format(date);
            item.setTimeStamp(dateString);
            boolean isLocationPost = post.has("place");
            //location url if exits
            if (isLocationPost) {
                String latitude = post.getJSONObject("place").getJSONObject("location").getString("latitude");
                String longitude = post.getJSONObject("place").getJSONObject("location").getString("longitude");
                String locationUrl = "http://maps.google.com/maps/api/staticmap?center=" +
                        latitude + "," + longitude +
                        "&zoom=15&size=200x200&sensor=false";
                item.setLocationUrl(locationUrl);


            }
            item.setId(post.getString("id"));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return item;

    }


}
