package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private EditText etCompose;
    private TextView tvCharCount;
    private TwitterClient client;
    private Tweet tweet;
    private Tweet replyToTweet;
    private ProgressBar pb;
    private ImageView ivProfileImage;
    private Button btCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client = TwitterApp.getRestClient(this);
        etCompose = (EditText) findViewById(R.id.etCompose);
        tvCharCount = (TextView) findViewById(R.id.tvCharCount);
        pb = (ProgressBar) findViewById(R.id.pbLoading);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        btCompose = findViewById(R.id.btCompose);

        replyToTweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        if (replyToTweet != null) {
            etCompose.setText("@" + replyToTweet.user.screenName + " ");
            etCompose.setSelection(etCompose.getText().length());
        }

        btCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (replyToTweet != null)
                    replyTweet(v);
                else
                    composeTweet(v);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_close:
                closeCompose();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void closeCompose() {
        Intent data = new Intent();
        setResult(RESULT_CANCELED, data); // set result code and bundle data for response
        finish(); // closes the activity
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        String charsLeft = String.format(Locale.US, "%d", 140 - etCompose.getText().toString().length());
        tvCharCount.setText(charsLeft);
        return true;
    }

    public void composeTweet(View view) {
        pb.setVisibility(ProgressBar.VISIBLE);

        client.sendTweet(etCompose.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    tweet = Tweet.fromJSON(response);
                    //Log.d("SendTweet", tweet.body);

                    // Prepare data intent
                    Intent data = new Intent();
                    // Pass relevant data back as a result
                    data.putExtra("tweet", Parcels.wrap(tweet));
                    //data.putExtra("code", 200); // ints work too
                    // Activity finished ok, return the data
                    setResult(RESULT_OK, data); // set result code and bundle data for response
                    pb.setVisibility(ProgressBar.INVISIBLE);
                    finish(); // closes the activity, pass data to parent
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("SendTweet", errorResponse.toString());
                pb.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("SendTweet", errorResponse.toString());
                pb.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("SendTweet", responseString);
                pb.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }

    public void replyTweet(View view) {
        pb.setVisibility(ProgressBar.VISIBLE);

        client.replyTweet(etCompose.getText().toString(), replyToTweet.uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    tweet = Tweet.fromJSON(response);
                    //Log.d("SendTweet", tweet.body);

                    // Prepare data intent
                    Intent data = new Intent();
                    // Pass relevant data back as a result
                    data.putExtra("tweet", Parcels.wrap(tweet));
                    //data.putExtra("code", 200); // ints work too
                    // Activity finished ok, return the data
                    setResult(RESULT_OK, data); // set result code and bundle data for response
                    pb.setVisibility(ProgressBar.INVISIBLE);
                    finish(); // closes the activity, pass data to parent
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("SendTweet", errorResponse.toString());
                pb.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("SendTweet", errorResponse.toString());
                pb.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("SendTweet", responseString);
                pb.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }
}
