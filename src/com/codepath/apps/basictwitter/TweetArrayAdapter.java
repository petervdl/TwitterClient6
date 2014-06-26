package com.codepath.apps.basictwitter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {

	public TweetArrayAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);
		
	}
		
    public static Drawable savedProfileDrawable;
    public static Spanned savedUserName;
    public static String savedScreenName;
    
    private static boolean firstTime= true;
    
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		
		// get the tweet data for item at position pos
		Tweet tweet = getItem(pos);
		// find or inflate the template
		View v;
		if (convertView==null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			v = inflater.inflate(R.layout.tweet_item, parent, false);
		} else {
			v = convertView;
		}
		
		// find the 3 views within the template
		TextView tvUserName = (TextView) v.findViewById(R.id.tvUserName);
		TextView tvUserBody = (TextView) v.findViewById(R.id.tvBody);
		TextView tvSince = (TextView) v.findViewById(R.id.tvSince);
		TextView tvScreenName = (TextView) v.findViewById(R.id.tvScreenName);
		
		ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
		ivProfileImage.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		
		//populate the views with the data from model
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImage);
		Spanned boldedName = Html.fromHtml("<b>" + tweet.getUser().getUserName() + "</b>");
		tvScreenName.setText(" @"+tweet.getUser().getScreenName());
		
		if (firstTime) {
			savedProfileDrawable = ivProfileImage.getDrawable();    // save this for later
			savedUserName = boldedName;
			savedScreenName = tweet.getUser().getScreenName();
			firstTime=false;
		}
		
		tvUserName.setText( boldedName );
		tvUserBody.setText(tweet.getBody());
		tvSince.setText(tweet.getTimeSince());
		return v; 
	}

}
