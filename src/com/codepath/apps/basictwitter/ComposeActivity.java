package com.codepath.apps.basictwitter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

public class ComposeActivity extends Activity {
	private TwitterClient client;
	private Button bPost;
	private EditText etBody;
	private TextView tvCount;
		
	private void setupViews() {
		etBody = (EditText) findViewById(R.id.etBody);
		etBody.addTextChangedListener(new TextWatcher(){
			int i=0;
	        public void afterTextChanged(Editable s) {
	            i = s.length();
	            tvCount.setText(String.valueOf(i) + " / " + String.valueOf(140) + " char");
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
	    }); 
		bPost = (Button) findViewById(R.id.bPost);
		tvCount = (TextView) findViewById(R.id.tvCount);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
		setupViews();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		client = TwitterApplication.getRestClient();
	}
	
	AsyncHttpResponseHandler handler = new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				super.onSuccess(json);
				Log.d("simpletwitter", "success returned from postTweet request - " +json.toString());
				
                Tweet t = Tweet.fromJSON(json);

                Intent i = new Intent();
                i.putExtra("tweet", t);
                setResult(RESULT_OK, i);

                // pass tweet t to Timeline activity
                finish();

			}

			@Override
			public void onFailure(Throwable e, String s) {
				super.onFailure(e, s);
				Log.e("simpletwitter", "excn from postTweet - " +e.getMessage());
				Log.d("simpletwitter", "excn from postTweet - " +s);
			}
		};

	public void onClick(View v) {
		// get contents of tweet field, and post it
		String status = etBody.getText().toString();
		postTweet(status);
		Intent returnIntent = new Intent();
		returnIntent.putExtra("result",status);
		setResult(RESULT_OK,returnIntent);
	}
	
	public void postTweet(String status) {
		// This method posts the tweet.		
		client.postStatus(status, handler); 
	}
	
}
