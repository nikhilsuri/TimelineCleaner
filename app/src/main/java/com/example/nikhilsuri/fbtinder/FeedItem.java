package com.example.nikhilsuri.fbtinder;

import android.nfc.Tag;
import android.util.Log;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nikhilsuri on 5/14/17.
 */

public class FeedItem {
    private String id;
    private String name, status, image, profilePic, timeStamp, url,message,story,locationUrl,postDescription,postName;

    public FeedItem() {
    }

    public FeedItem(String id, String name, String image, String status,
                    String profilePic, String timeStamp, String url,String message,String story,String locationUrl,String postDescription,String postName) {
        super();
        this.id = id;
        this.postDescription=postDescription;
        this.postName=postName;
        this.name = name;
        this.image = image;
        this.status = status;
        this.profilePic = profilePic;
        this.timeStamp = timeStamp;
        this.url = url;
        this.message=message;
        this.story=story;
        this.locationUrl=locationUrl;
    }

    public String getLocationUrl(){
        return locationUrl;
    }
    public void setLocationUrl(String url){
        locationUrl=url;
    }
    public String getStory(){
        return story;
    }
    public void setStory(String story){
        this.story=story;
    }
    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message=message;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImge() {
        return image;
    }

    public void setImge(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }
    public String getPostDescription(){
        return postDescription;
    }

    public void setpostName(String postName) {
        this.postName = postName;
    }
    public String getpostName(){
        return postName;
    }

    @Override
    public String toString() {
        return "FeedItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", image='" + image + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", url='" + url + '\'' +
                ", message='" + message + '\'' +
                ", story='" + story + '\'' +
                ", locationUrl='" + locationUrl + '\'' +
                ", postDescription='" + postDescription + '\'' +
                ", postName='" + postName + '\'' +
                '}';
    }
    public  FeedItem(JSONObject post, String displayName,String profilePic) {

        try {
            //profile name
            String story = "";
            if (!post.has("story"))
                story = displayName;
            else {
                story = story + "  " + post.getString("story");
            }
            this.setStory(story);
            //profile pic
            if (profilePic != null && !profilePic.isEmpty())
                this.setProfilePic(profilePic);
            //heading ,message
            if (post.has("message"))
                this.setStatus(post.getString("message"));
            // post image
            String image = post.isNull("picture") ? null : post
                    .getString("picture");

            this.setImge(image);
            //post message and description
            if (post.has("description")) {
                this.setPostDescription(post.getString("description"));
            }
            if (post.has("name")) {
                this.setpostName(post.getString("name"));
            }


            //created time
            String dateTime[] = post.getString("created_time").split("T");
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
            Date date = inputFormat.parse(dateTime[0]);
            String dateString = outputFormat.format(date);
            this.setTimeStamp(dateString);
            boolean isLocationPost = post.has("place");
            //location url if exits
            if (isLocationPost) {
                String latitude = post.getJSONObject("place").getJSONObject("location").getString("latitude");
                String longitude = post.getJSONObject("place").getJSONObject("location").getString("longitude");
                String locationUrl = "http://maps.google.com/maps/api/staticmap?center=" +
                        latitude + "," + longitude +
                        "&zoom=15&size=200x200&sensor=false";
                this.setLocationUrl(locationUrl);


            }
            this.setId(post.getString("id"));
        } catch (Exception e) {

        }


    }
}