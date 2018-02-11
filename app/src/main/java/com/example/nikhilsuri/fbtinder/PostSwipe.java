package com.example.nikhilsuri.fbtinder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikhilsuri on 5/15/17.
 */

@Layout(R.layout.feed_item)
public class PostSwipe {


    @com.mindorks.placeholderview.annotations.View(R.id.feed_item_id)
    private View viewPlaceHolder;
    @com.mindorks.placeholderview.annotations.View(R.id.profilePic)
    private NetworkImageView profilePic;
    @com.mindorks.placeholderview.annotations.View(R.id.postImage)
    private FeedImageView postImage;
    @com.mindorks.placeholderview.annotations.View(R.id.status)
    private TextView status;
    @com.mindorks.placeholderview.annotations.View(R.id.timestamp)
    private TextView timeStamp;
    @com.mindorks.placeholderview.annotations.View(R.id.story)
    private TextView story;
    @com.mindorks.placeholderview.annotations.View(R.id.locationPic)
    private NetworkImageView locationPic;
    //    @com.mindorks.placeholderview.annotations.View(R.id.link_description)
//    private TextView linkDescription;

    private String TAG = "PostActivity";

    private FeedItem feedItem;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private SwipeActivity swipeActivity;

    public PostSwipe() {
        super();
    }

    public PostSwipe(final Context context, FeedItem feed, SwipePlaceHolderView swipeView, SwipeActivity swipeActivity) {
        mContext = context;
        feedItem = feed;
        mSwipeView = swipeView;
        this.swipeActivity = swipeActivity;

    }


    @Resolve
    private void onResolved() {


        viewPlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOpenFacebookIntent(mContext, feedItem.getId());

            }
        });
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();


        //message
        if (!TextUtils.isEmpty(feedItem.getStatus())) {
            status.setText(feedItem.getStatus());
            status.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(feedItem.getStory())) {
            story.setText(feedItem.getStory());
            story.setVisibility(View.VISIBLE);
        }


        // Feed image
        if (feedItem.getImge() != null) {
            postImage.setImageUrl(feedItem.getImge(), imageLoader);
            postImage.setVisibility(View.VISIBLE);
            postImage
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        }
        // user profile pic

        profilePic.setImageUrl(feedItem.getProfilePic(), imageLoader);

        //time stamp

        timeStamp.setText(feedItem.getTimeStamp());

        //location on maps only if there is no picture

        try {
            if (feedItem.getLocationUrl() != null && feedItem.getImge() == null) {
                locationPic.setImageUrl(feedItem.getLocationUrl(), imageLoader);
            }
        } catch (Exception e) {

        }

    }

    @SwipeInState
    private void onSwipeInState() {
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOut
    private void onSwipedOut() {
        Log.d("EVENT", "onSwipedOut");
        Log.d("FeedItems", swipeActivity.feedItems.size() + ":" + swipeActivity.feedItems.get(0).toString());
        getOpenFacebookIntent(mContext,feedItem.getId());
        swipeActivity.feedItems.remove(0);
        swipeActivity.swipedOutPosts.getSwipedOutPostHashMap().put(feedItem.getId(), new SwipedOutPost(feedItem.getId(), new Date()));
        swipeActivity.deletedPosts.getSwipedOutPostHashMap().put(feedItem.getId(), new SwipedOutPost(feedItem.getId(), new Date()));
        swipeActivity.checkForLastItems();
    }

    @SwipeCancelState
    private void onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn() {
        Log.d("EVENT", "onSwipedIn");
        Log.d("FeedItems", swipeActivity.feedItems.size() + ":" + swipeActivity.feedItems.get(0).toString());
        swipeActivity.feedItems.remove(0);
        swipeActivity.swipedOutPosts.getSwipedOutPostHashMap().put(feedItem.getId(), new SwipedOutPost(feedItem.getId(), new Date()));
        swipeActivity.checkForLastItems();

    }


    @SwipeOutState
    private void onSwipeOutState() {
        Log.d("EVENT", "onSwipeOutState");

    }


    public void getOpenFacebookIntent(Context context, String id) {
        String userId = id.split("_")[0];
        String postId = id.split("_")[1];
        try {

            Intent intent
                    = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + userId + "/posts/" + postId));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //TODO open in fb app
            mContext.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    }

}
