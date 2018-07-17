package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    public interface ClickListener {

        void onPositionClicked(int position, int button_code);

//        void onLongClicked(int position);
    }

    private List<Tweet> mTweets;
    Context context;
    private final ClickListener listener;

    // pass in the Tweets array into the Constructor
    public TweetAdapter(List<Tweet> tweets, ClickListener listener) {
        mTweets = tweets;
        this.listener = listener;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView, listener);
        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the data according to position
        Tweet tweet = mTweets.get(position);

        // populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        Glide.with(context).load(tweet.user.profileImageUrl).into(holder.ivProfileImage);
        holder.tvRelativeTimestamp.setText("\u2022 " + getRelativeTimeAgo(tweet.createdAt));
        if (tweet.retweeted_local == true) {
            holder.ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
            holder.ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.retweetGreen), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            holder.ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            holder.ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.greyText), android.graphics.PorterDuff.Mode.SRC_IN);
        }
//        if (tweet.replyCount > 0)
//            holder.tvReplyCount.setText(tweet.replyCount);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    // Create ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvRelativeTimestamp;
        public ImageView ivReply;
        public ImageView ivRetweet;
//        public TextView tvReplyCount;

        private final int BUTTON_REPLY_CODE = 0;
        private final int BUTTON_RETWEET_CODE = 1;
        private WeakReference<ClickListener> listenerRef;

        public ViewHolder(View itemView, ClickListener listener) {
            super(itemView);

            listenerRef = new WeakReference<>(listener);

            // perform findViewById lookups

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvRelativeTimestamp = (TextView) itemView.findViewById(R.id.tvRelativeTimestamp);
            ivReply = itemView.findViewById(R.id.ivReply);
            ivRetweet = itemView.findViewById(R.id.ivRetweet);
//            tvReplyCount = itemView.findViewById(R.id.tvReplyCount);

            ivReply.setOnClickListener(this);
            ivRetweet.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == ivReply.getId()) {
                listenerRef.get().onPositionClicked(getAdapterPosition(), BUTTON_REPLY_CODE);
            } else if (v.getId() == ivRetweet.getId()) {
                listenerRef.get().onPositionClicked(getAdapterPosition(), BUTTON_RETWEET_CODE);
            }
        }
    }
}
