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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
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
        holder.tvUsername.setText(tweet.user.screenName);
        holder.tvBody.setText(tweet.body);
        holder.tvRelativeTimestamp.setText("\u2022 " + getRelativeTimeAgo(tweet.createdAt));

        if (tweet.retweetCount_local > 0)
            holder.tvRetweetCount.setText(Integer.toString(tweet.retweetCount_local));
        else
            holder.tvRetweetCount.setText("");

        if (tweet.favoriteCount_local > 0)
            holder.tvFavoriteCount.setText(Integer.toString(tweet.favoriteCount_local));
        else
            holder.tvFavoriteCount.setText("");

        if (tweet.retweeted_local == true) {
            holder.ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
            holder.ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.retweetGreen), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            holder.ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            holder.ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.greyText), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (tweet.favorited_local == true) {
            holder.ivFavorite.setImageResource(R.drawable.ic_vector_heart);
            holder.ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.favoriteRed), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            holder.ivFavorite.setImageResource(R.drawable.ic_vector_heart_stroke);
            holder.ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.greyText), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        final RoundedCorners roundedCorners = new RoundedCorners(100);
        final RequestOptions requestOptions = RequestOptions.bitmapTransform(
                roundedCorners
        );
        Glide.with(context).load(tweet.user.profileImageUrl).apply(requestOptions).into(holder.ivProfileImage);
//        if (tweet.replyCount > 0)
//            holder.tvReplyCount.setText(tweet.replyCount);

        if (tweet.mediaUrl != null) {
            Glide.with(context).load(tweet.mediaUrl+":small").into(holder.ivMedia);
        }
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
        public TextView tvRetweetCount;
        public ImageView ivFavorite;
        public TextView tvFavoriteCount;
        public ImageView ivMedia;
//        public TextView tvReplyCount;

        private final int BUTTON_REPLY_CODE = 0;
        private final int BUTTON_RETWEET_CODE = 1;
        private final int BUTTON_FAVORITE_CODE = 2;
        private final int ITEMVIEW_CODE = 3;
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
            tvRetweetCount = itemView.findViewById(R.id.tvRetweetCount);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvFavoriteCount = itemView.findViewById(R.id.tvFavoriteCount);
            ivMedia = itemView.findViewById(R.id.ivMedia);
//            tvReplyCount = itemView.findViewById(R.id.tvReplyCount);

            ivReply.setOnClickListener(this);
            ivRetweet.setOnClickListener(this);
            ivFavorite.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == ivReply.getId()) {
                listenerRef.get().onPositionClicked(getAdapterPosition(), BUTTON_REPLY_CODE);
            } else if (v.getId() == ivRetweet.getId()) {
                listenerRef.get().onPositionClicked(getAdapterPosition(), BUTTON_RETWEET_CODE);
            } else if (v.getId() == ivFavorite.getId()) {
                listenerRef.get().onPositionClicked(getAdapterPosition(), BUTTON_FAVORITE_CODE);
            } else {
                listenerRef.get().onPositionClicked(getAdapterPosition(), ITEMVIEW_CODE);
            }
        }
    }
}
