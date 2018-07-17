package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Tweet {

    // list out the attributes
    public String body;
    public long uid; // database ID for the tweet
    public User user;
    public String createdAt;
    public Boolean retweeted;
    public Boolean favorited;
    public int retweetCount;
    public int favoriteCount;

    public boolean retweeted_local;
    public int retweetCount_local;
    public boolean favorited_local;
    public int favoriteCount_local;
    // Only available to premium accounts
//    public int replyCount;

    public Tweet() {}

    // deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.createdAt = jsonObject.getString("created_at");
//        tweet.replyCount = jsonObject.getInt("reply_count");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.retweeted_local = tweet.retweeted;
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.retweetCount_local = tweet.retweetCount;

        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.favorited_local = tweet.favorited;
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        tweet.favoriteCount_local = tweet.favoriteCount;

        return tweet;
    }

    public void toggleRetweetedLocal() {
        if (retweeted_local == true)
            retweeted_local = false;
        else
            retweeted_local = true;
    }

    public void incrementRetweetCount() {
        this.retweetCount_local++;
    }

    public void decrementRetweetCount() {
        this.retweetCount_local--;
    }

    public void toggleFavoritedLocal() {
        if (favorited_local == true)
            favorited_local = false;
        else
            favorited_local = true;
    }

    public void incrementFavoriteCount() {
        this.favoriteCount_local++;
    }

    public void decrementFavoriteCount() {
        this.favoriteCount_local--;
    }

}
