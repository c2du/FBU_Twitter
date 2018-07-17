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
    public boolean retweeted_local;
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

        return tweet;
    }

    public void toggleRetweetedLocal() {
        if (retweeted_local == true)
            retweeted_local = false;
        else
            retweeted_local = true;
    }

}
