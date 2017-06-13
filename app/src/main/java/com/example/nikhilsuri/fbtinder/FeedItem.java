package com.example.nikhilsuri.fbtinder;

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
}