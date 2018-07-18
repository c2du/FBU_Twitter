package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class TweetDetailsActivity extends AppCompatActivity {

    TwitterClient client;
    private Tweet tweet;
    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvScreenName;
    private TextView tvBody;
    private TextView tvRetweetCount;
    private TextView tvFavoriteCount;
    private ImageView ivReply;
    private ImageView ivRetweet;
    private ImageView ivFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        client = TwitterApp.getRestClient(this);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvName = findViewById(R.id.tvName);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvBody = findViewById(R.id.tvBody);
        tvRetweetCount = findViewById(R.id.tvRetweetCount);
        tvFavoriteCount = findViewById(R.id.tvFavoriteCount);
        ivReply = findViewById(R.id.ivReply);
        ivRetweet = findViewById(R.id.ivRetweet);
        ivFavorite = findViewById(R.id.ivFavorite);

        tvName.setText(tweet.user.name);
        tvScreenName.setText("@" + tweet.user.screenName);
        tvBody.setText(tweet.body);
        tvRetweetCount.setText(Integer.toString(tweet.retweetCount_local));
        tvFavoriteCount.setText(Integer.toString(tweet.favoriteCount_local));

        final RoundedCorners roundedCorners = new RoundedCorners(100);
        final RequestOptions requestOptions = RequestOptions.bitmapTransform(
                roundedCorners
        );
        Glide.with(this).load(tweet.user.profileImageUrl).apply(requestOptions).into(ivProfileImage);

        if (tweet.retweeted_local == true) {
            ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
            ivRetweet.setColorFilter(ContextCompat.getColor(this, R.color.retweetGreen), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            ivRetweet.setColorFilter(ContextCompat.getColor(this, R.color.greyText), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (tweet.favorited_local == true) {
            ivFavorite.setImageResource(R.drawable.ic_vector_heart);
            ivFavorite.setColorFilter(ContextCompat.getColor(this, R.color.favoriteRed), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            ivFavorite.setImageResource(R.drawable.ic_vector_heart_stroke);
            ivFavorite.setColorFilter(ContextCompat.getColor(this, R.color.greyText), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TweetDetailsActivity.this, ComposeActivity.class);
                i.putExtra("tweet", Parcels.wrap(tweet));
                startActivityForResult(i, 20);
            }
        });

        ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet.retweeted_local == true) {
                    tweet.toggleRetweetedLocal();
                    tweet.decrementRetweetCount();
                    unretweetTweet(tweet.uid);
                    ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                    ivRetweet.setColorFilter(ContextCompat.getColor(TweetDetailsActivity.this, R.color.greyText), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    tweet.toggleRetweetedLocal();
                    tweet.incrementRetweetCount();
                    retweetTweet(tweet.uid);
                    ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
                    ivRetweet.setColorFilter(ContextCompat.getColor(TweetDetailsActivity.this, R.color.retweetGreen), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                tvRetweetCount.setText(Integer.toString(tweet.retweetCount_local));
            }
        });

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet.favorited_local == true) {
                    tweet.toggleFavoritedLocal();
                    tweet.decrementFavoriteCount();
                    unfavoriteTweet(tweet.uid);
                    ivFavorite.setImageResource(R.drawable.ic_vector_heart_stroke);
                    ivFavorite.setColorFilter(ContextCompat.getColor(TweetDetailsActivity.this, R.color.greyText), android.graphics.PorterDuff.Mode.SRC_IN);

                } else {
                    tweet.toggleFavoritedLocal();
                    tweet.incrementFavoriteCount();
                    favoriteTweet(tweet.uid);
                    ivFavorite.setImageResource(R.drawable.ic_vector_heart);
                    ivFavorite.setColorFilter(ContextCompat.getColor(TweetDetailsActivity.this, R.color.favoriteRed), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                tvFavoriteCount.setText(Integer.toString(tweet.favoriteCount_local));
            }
        });
    }

    public void retweetTweet(long uid) {
        client.retweetTweet(uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet newTweet = Tweet.fromJSON(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("SendTweet", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("SendTweet", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("SendTweet", responseString);
            }
        });
    }

    public void unretweetTweet(long uid) {
        client.unretweetTweet(uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet newTweet = Tweet.fromJSON(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("SendTweet", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("SendTweet", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("SendTweet", responseString);
            }
        });
    }

    public void favoriteTweet(long uid) {
        client.favoriteTweet(uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet newTweet = Tweet.fromJSON(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("SendTweet", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("SendTweet", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("SendTweet", responseString);
            }
        });
    }

    public void unfavoriteTweet(long uid) {
        client.unfavoriteTweet(uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet newTweet = Tweet.fromJSON(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("SendTweet", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("SendTweet", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("SendTweet", responseString);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("tweet"));
            //Log.d("SendTweet", "Activity result: " + tweet.body);
            // Extract name value from result extras
            startActivity(new Intent(TweetDetailsActivity.this, TimelineActivity.class));
        }
    }
}
