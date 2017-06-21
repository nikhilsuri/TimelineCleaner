package com.example.nikhilsuri.fbtinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
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


        getPosts();

    }

    private void fillCards() {

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

    private void getPosts() {
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(), "/me/posts", null, HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            Log.e(TAG, response.toString());
                            postsArray = response.getJSONObject().getJSONArray("data");

                            Log.e(TAG, postsArray.toString());
                            fillCards();
                        } catch (Exception q) {
                            Log.e(TAG, q.getStackTrace().toString());
                        }
                    }
                });
        Bundle params = new Bundle();
        params.putString("fields", "id,caption,created_time,description,icon,link,message,name,permalink_url,picture,place,shares,story");
        request.setParameters(params);
        request.executeAsync();
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
