package com.marat.apps.numarket.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Post implements Parcelable {

    private String PostId;
    private String authorName;
    private String authorId;
    private long time;
    private String bodyText;
    private String numberOfLikes;
    private String numberOfReposts;
    private String numberOfComments;
    private ArrayList<String> comments;

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        this.PostId = postId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(String numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public String getNumberOfReposts() {
        return numberOfReposts;
    }

    public void setNumberOfReposts(String numberOfReposts) {
        this.numberOfReposts = numberOfReposts;
    }

    public String getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(String numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    public Post() {}

    protected Post(Parcel in) {
        authorName = in.readString();
        time = in.readLong();
        bodyText = in.readString();
        numberOfLikes = in.readString();
        numberOfReposts = in.readString();
        numberOfComments = in.readString();
        comments = in.createStringArrayList();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authorName);
        dest.writeLong(time);
        dest.writeString(bodyText);
        dest.writeString(numberOfLikes);
        dest.writeString(numberOfReposts);
        dest.writeString(numberOfComments);
        dest.writeList(comments);
    }
}
