package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailsActivity extends AppCompatActivity {

    private Tweet tweet;
    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvScreenName;
    private TextView tvBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvName = findViewById(R.id.tvName);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvBody = findViewById(R.id.tvBody);

        tvName.setText(tweet.user.name);
        tvScreenName.setText("@" + tweet.user.screenName);
        tvBody.setText(tweet.body);

        final RoundedCorners roundedCorners = new RoundedCorners(100);
        final RequestOptions requestOptions = RequestOptions.bitmapTransform(
                roundedCorners
        );
        Glide.with(this).load(tweet.user.profileImageUrl).apply(requestOptions).into(ivProfileImage);
    }
}
