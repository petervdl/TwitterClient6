package com.codepath.apps.basictwitter;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

// This is where most of the work takes place - displaying your
// tweet timeline, and transitioning to ComposeActivity, when ActionBar icon pressed
public class TimelineActivity extends Activity {

	private static final int REQCODE =1;
	
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private ArrayAdapter<Tweet> aTweets;
	private ListView lvTweets;

	OnScrollListener esl = new EndlessScrollListener() {
		@Override
		public void onLoadMore(int page, int totalItemsCount) {
			// Triggered only when new data needs to be appended to the list
			// invokes code to append new items to my AdapterView
            Log.d("simpletwitter", "totalItemsCount=" + totalItemsCount);
            Log.d("simpletwitter", "tweets ArrayList size=" + tweets.size());

			if (totalItemsCount >= tweets.size()) {
				getNextPageOfTweets( MAX_TWEETS_PER_CALL, -1, getOldestSeen());   
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterApplication.getRestClient();
		lvTweets = (ListView) findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		// the layout on the TweetArrayAdapter defines how a single tweet looks in list
		aTweets = new TweetArrayAdapter(this, tweets);
		lvTweets.setAdapter(aTweets);
		lvTweets.setOnScrollListener( esl ); // MUST be done in onCreate().  Check.
		populateTimeline();

	}

	@Override
    public boolean onOptionsItemSelected (MenuItem item){
		// compose is the only item on the timeline
		doComposeAction(item);
        return true;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.compose, menu);
		return true;
	}
	
	public void doComposeAction(MenuItem mi) {
		// the "Compose" action in Action bar has been pressed
		// go to the ComposeTweet activity
		Intent i = new Intent(getApplicationContext(), ComposeActivity.class);
		startActivityForResult(i, REQCODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String result;
        if(requestCode == REQCODE && resultCode == RESULT_OK){
            // now add this to Timeline
            Tweet justPosted = data.getParcelableExtra("tweet");
            
            // add tweet to top of arrayList
            tweets.add(0, justPosted);

            aTweets.notifyDataSetChanged();
            
            // display beginning of list
            lvTweets.setSelection(0);

        }
	}

	public boolean isNetworkAvailable() {
		// from http://developer.android.com/training/basics/network-ops/managing.html
		ConnectivityManager connMgr = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	public static final int MAX_TWEETS_PER_CALL = 10;
	
	public void populateTimeline() {
		// this is the method we call to get more data into our scrolling list.

		// It simply calls getHomeTimeline on the client, and passes a couple of callbacks.

		getNextPageOfTweets(MAX_TWEETS_PER_CALL, getNewestSeen(), getOldestSeen() );
	}

	private long getOldestSeen() {
		long oldestInCurrentGroup = 0;
		if (aTweets==null || aTweets.getCount()==0 ) {
			oldestInCurrentGroup = 0;
		} else {
			oldestInCurrentGroup = aTweets.getItem(tweets.size() - 1).getUid() - 1;
		}
		//Log.d("simpletwitter","entered getNextPageOfTweets: oldest id="+oldestInCurrentGroup);
		return oldestInCurrentGroup;
	}
	
	private long getNewestSeen() {
		long newestInCurrentGroup = 0;
		if (aTweets==null || aTweets.getCount()==0 ) {
			newestInCurrentGroup= 0; 
		} else {
			newestInCurrentGroup = aTweets.getItem(0).getUid();
		}
		//Log.d("simpletwitter","entered getNextPageOfTweets: newest id="+newestInCurrentGroup);
		return newestInCurrentGroup;
	}
	
	public void getNextPageOfTweets(int count, long sinceId, long maxId) {
		// this method asks for the next page of results.

		if ( ! isNetworkAvailable()) {
			Toast.makeText(this, getString(R.string.no_net_connection), Toast.LENGTH_LONG).show();
			return;
		}

		long newestInCurrentGroup = getNewestSeen();
		long oldestInCurrentGroup = getOldestSeen();
		Log.d("simpletwitter","START OF getNextPageOfTweets: newest id="+newestInCurrentGroup);
		Log.d("simpletwitter","START OF getNextPageOfTweets: oldest id="+oldestInCurrentGroup);

		client.getHomeTimeline(MAX_TWEETS_PER_CALL, 
				getNewestSeen(), 
				getOldestSeen(),    
				new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				super.onSuccess(json);
				aTweets.addAll(Tweet.fromJSONArray(json) );

				long newestInCurrentGroup = getNewestSeen();
				long oldestInCurrentGroup = getOldestSeen();

				Log.d("simpletwitter", "in getNextPageofTweets, read " +json.toString());
				newestInCurrentGroup = getNewestSeen();
				oldestInCurrentGroup = getOldestSeen();
				Log.d("simpletwitter","END OF getNextPageOfTweets: newest id="+newestInCurrentGroup);
				Log.d("simpletwitter","END OF getNextPageOfTweets: oldest id="+oldestInCurrentGroup);

			}

			@Override
			public void onFailure(Throwable e, String s) {
				super.onFailure(e, s);
				Log.d("debug", "excn in populateTimeline - " +e.getMessage());
				Log.d("debug", "excn in populateTimeline - " +s);
			}
		});
	}
}
