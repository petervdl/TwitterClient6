package com.codepath.apps.basictwitter;

import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 * 
 *     TwitterClient client = TwitterApplication.getRestClient();
 *     // use client to send requests to API
 *     
 */
public class TwitterApplication extends com.activeandroid.app.Application {
	private static Context context;
	
    @Override
    public void onCreate() {
        super.onCreate();
        TwitterApplication.context = this;
        
        // Create global configuration and initialize ImageLoader with this configuration
        // what we configure here, will apply uniformly to all activities in the app
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
        		cacheInMemory().cacheOnDisc().build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .defaultDisplayImageOptions(defaultOptions)
            .build();
        ImageLoader.getInstance().init(config);
    }
    
    private static TwitterClient tc = null;
    
    public static TwitterClient getRestClient() {
    	if (tc == null) {
    		tc = (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TwitterApplication.context);
    	}
    	return tc;
    }
}