package com.codepath.apps.basictwitter;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;

import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 *     There will be a REST client for each API you are going to consume
 *        and a method for each endpoint you will consume/use.
 * See a full list of supported API classes:  (e.g. TwitterAPI)
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // base API URL for twitter
	public static final String REST_UPDATE = "statuses/update.json";
	public static final String REST_VERIFY_CREDENTIALS = "account/verify_credentials.json";
	public static final String REST_HOME_TIMELINE = "statuses/home_timeline.json";
	public static final String REST_USER_TIMELINE = "statuses/user_timeline.json";

	public static final String REST_CONSUMER_KEY = "WQjqApThfceUQk4BBZ5yhlkWr";       // Given by twitter
	public static final String REST_CONSUMER_SECRET = "0Sqcmv2UZKpA8ErFPalaHjUBDp2sLmIncYhVRoHTXrATZmorNZ"; // from Twit
	public static final String REST_CALLBACK_URL = "oauth://cpbasictweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	static boolean firstRequest = true;
	
	public void postStatus(String status, AsyncHttpResponseHandler handler) {
		final String apiUrl = getApiUrl(REST_UPDATE);
		RequestParams params = new RequestParams();
		params.put("status", status); 
		client.post(apiUrl, params, handler);   
		// Log.d("simpletwitter", "have posted status update.  Look for result.");
	}

	public void getHomeTimeline(int count, long sinceId, long maxId, AsyncHttpResponseHandler handler) {
		final String apiUrl = getApiUrl(REST_HOME_TIMELINE);
		RequestParams params = new RequestParams();
		params.put("count", ""+count);  
		params.put("include_rts", "1"); // Twitter docs recommend using this, when using count

		if (firstRequest) {
			firstRequest = false;
		} else {
			// these params are only used on second and subsequent attempts
			// params.put("since_id", ""+sinceId);
			params.put("max_id", ""+maxId);
			
		}
        Log.d("simpletwitter", "sending get, oldest(max)="+maxId+", since(newest)="+sinceId);
		client.get(apiUrl, params, handler);   // warning, if NO params, pass null!
	}

	// DEFINE METHODS for different API endpoints here

	//    public void getInterestingnessList(AsyncHttpResponseHandler handler) {
	//        String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
	//        // Can specify query string params directly or through RequestParams.
	//        RequestParams params = new RequestParams();
	//        params.put("format", "json");
	//        client.get(apiUrl, params, handler);
	//    }

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}