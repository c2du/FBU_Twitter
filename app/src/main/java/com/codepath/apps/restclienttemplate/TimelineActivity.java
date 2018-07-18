package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    ProgressBar pb;
    private SwipeRefreshLayout swipeContainer;
    private final int BUTTON_REPLY_CODE = 0;
    private final int BUTTON_RETWEET_CODE = 1;
    private final int BUTTON_FAVORITE_CODE = 2;
    private final int ITEMVIEW_CODE = 3;

    // REQUEST_COMPOSE_EMPTY can be any value we like, used to determine the result type later
    private final int REQUEST_COMPOSE_EMPTY = 20;
    private final int REQUEST_COMPOSE_REPLY = 40;

    private final String TAG = "debugdebugdebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApp.getRestClient(this);

        // find the RecyclerView
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);
        // find the progress bar
        pb = (ProgressBar) findViewById(R.id.pbLoading);
        // init the arraylist (data source)
        tweets = new ArrayList<>();
        // construct the adapter from this datasource
        tweetAdapter = new TweetAdapter(tweets, new TweetAdapter.ClickListener() {
            @Override
            public void onPositionClicked(int position, int code) {
//                Toast.makeText(TimelineActivity.this, String.format("Code: %d", code), Toast.LENGTH_LONG).show();
                Tweet t = tweets.get(position);
                switch (code) {
                    case BUTTON_REPLY_CODE:
                        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
                        i.putExtra("tweet", Parcels.wrap(t));
                        startActivityForResult(i, REQUEST_COMPOSE_REPLY);
                        break;
                    case BUTTON_RETWEET_CODE:
                        if (t.retweeted_local == true) {
                            t.toggleRetweetedLocal();
                            t.decrementRetweetCount();
                            tweetAdapter.notifyItemChanged(position);
                            unretweetTweet(t.uid);
                        } else {
                            t.toggleRetweetedLocal();
                            t.incrementRetweetCount();
                            tweetAdapter.notifyItemChanged(position);
                            retweetTweet(t.uid);
                        }
                        break;
                    case BUTTON_FAVORITE_CODE:
                        if (t.favorited_local == true) {
                            t.toggleFavoritedLocal();
                            t.decrementFavoriteCount();
                            tweetAdapter.notifyItemChanged(position);
                            unfavoriteTweet(t.uid);
                        } else {
                            t.toggleFavoritedLocal();
                            t.incrementFavoriteCount();
                            tweetAdapter.notifyItemChanged(position);
                            favoriteTweet(t.uid);
                        }
                        break;
                    case ITEMVIEW_CODE:
                        Intent b = new Intent(TimelineActivity.this, TweetDetailsActivity.class);
                        b.putExtra("tweet", Parcels.wrap(t));
                        startActivity(b);
                        break;
                }
//                Toast.makeText(TimelineActivity.this, String.format("Clicked %d position TimelineActivity", position), Toast.LENGTH_LONG).show();
            }

//            @Override
//            public void onLongClicked(int position) {
//
//            }
        });
        // RecyclerView setup (layout manager, user adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        // set the adapter
        rvTweets.setAdapter(tweetAdapter);
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        populateTimeline();
    }

    public void fetchTimelineAsync(int page) {
        pb.setVisibility(ProgressBar.VISIBLE);
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);// Remember to CLEAR OUT old items before appending in the new ones
                tweetAdapter.clear();
                tweets.clear();
                // ...the data has come back, add new items to your adapter...
                for (int i = 0; i < response.length(); i++) {
                    // convert each object to a Tweet model
                    // add that Tweet model to our data source
                    // notify the adapter that we've added an item
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "Adding all tweets");
                tweetAdapter.addAll(tweets);
                // Now we call setRefreshing(false) to signal refresh has finished
                Log.d(TAG, "Finished adding all tweets");
                swipeContainer.setRefreshing(false);
                pb.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "Fetch timeline error: " + throwable.toString());
                Log.d(TAG, responseString);
                pb.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d(TAG, "Fetch timeline error: " + throwable.toString());
                Log.d(TAG, errorResponse.toString());
                pb.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miCompose:
                composeMessage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_COMPOSE_EMPTY is defined above
        if (resultCode == RESULT_OK) {
            Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("tweet"));
            //Log.d("SendTweet", "Activity result: " + tweet.body);
            // Extract name value from result extras
            tweets.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        }
    }

    private void composeMessage() {
        Intent i = new Intent(this, ComposeActivity.class);
        //i.putExtra("tweet", Parcels.wrap(tweet));
        startActivityForResult(i, REQUEST_COMPOSE_EMPTY);
    }

    private void populateTimeline() {
        pb.setVisibility(ProgressBar.VISIBLE);
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Log.d("TwitterClient", response.toString());
                // iterate through the JSON array
                // for each entry, deserialize the JSON object
                for (int i = 0; i < response.length(); i++) {
                    // convert each object to a Tweet model
                    // add that Tweet model to our data source
                    // notify the adapter that we've added an item
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                pb.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
                pb.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
                pb.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
                pb.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
                pb.setVisibility(ProgressBar.INVISIBLE);
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
