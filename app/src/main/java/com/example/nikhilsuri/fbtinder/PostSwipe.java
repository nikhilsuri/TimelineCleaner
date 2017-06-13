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

    public PostSwipe() {
        super();
    }

    public PostSwipe(final Context context, FeedItem feed, SwipePlaceHolderView swipeView) {
        mContext = context;
        feedItem = feed;
        mSwipeView = swipeView;

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

//        //image or link description
//        String description = "";
//        boolean descriptionAvailable = false;
//        if (feedItem.getpostName() != null) {
//            description = feedItem.getpostName();
//            descriptionAvailable = true;
//        }
////        if (feedItem.getPostDescription() != null) {
////            description = description + feedItem.getPostDescription();
////            descriptionAvailable = true;
////        }
//        if (descriptionAvailable) {
//            linkDescription.setText(description);
//        } else {
//            postImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//
//        }


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

    @SwipeOut
    private void onSwipedOut() {
        Log.d("EVENT", "onSwipedOut");
        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn() {
        Log.d("EVENT", "onSwipedIn");
    }

    @SwipeInState
    private void onSwipeInState() {
        Log.d("EVENT", "onSwipeInState");
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
                    = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/" + userId + "/posts/" + postId));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           //TODO open in fb app
           mContext.startActivity(intent);
            // startActivity(intent);
            /*context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://" + userId + "/posts/" + postId));*/
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            //return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + userId + "/posts/" + postId));

        }

    }

}
