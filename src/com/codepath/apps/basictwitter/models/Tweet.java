package com.codepath.apps.basictwitter.models;

//package com.mho.mytwitter.models;
//import com.activeandroid.Model;
//import com.activeandroid.annotation.Column;
//import com.activeandroid.annotation.Table;
//import com.activeandroid.query.Select;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.List;
//
///*
// * This is a temporary, sample model that demonstrates the basic structure
// * of a SQLite persisted Model object. Check out the ActiveAndroid wiki for more details:
// * https://github.com/pardom/ActiveAndroid/wiki/Creating-your-database-model
// * 
// */
//@Table(name = "items")
//public class SampleModel extends Model {
//
//    // Define table fields
//    @Column(name = "name")
//    private String name;
//
//    public SampleModel() {
//        super();
//    }
//
//    // Parse model from JSON
//    public SampleModel(JSONObject object) {
//        super();
//
//        try {
//            this.name = object.getString("title");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Getters
//    public String getName() {
//        return name;
//    }
//
//    // Record Finders
//    public static SampleModel byId(long id) {
//        return new Select().from(SampleModel.class).where("id = ?", id).executeSingle();
//    }
//
//    public static List<SampleModel> recentItems() {
//        return new Select().from(SampleModel.class).orderBy("id DESC").limit("300").execute();
//    }
//}

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


@Table(name="Tweet")
public class Tweet extends com.activeandroid.Model implements Parcelable {

    public Tweet() {
        super();
    }

	//  See http://guides.codepath.com/android/ActiveAndroid-Guide
	//  See also https://github.com/pardom/ActiveAndroid/wiki/Creating-your-database-model
	//  for info about persisting this model

	//  remember to specify an empty argument constructor for db? See helpful hints for more suggestions.
	// https://groups.google.com/forum/?hl=en#!topic/june-codepath-android-sf-sny/XkOnSR2zHtk

	@Column(name = "body")
	private String body;

	@Column(name = "uid")
	private long uid;  // uid is the 64-bit "tweet id"

	@Column(name = "user")
	private User user;

	@Column(name = "timeSince") // how old this tweet is (1 min, 1 hour, 7 hours, 2 days etc)
	private String timeSince;

	private String createdAt;  // no need to persist this, timeSince is another slice on it.

	@Column(name = "sinceUid")      // these 2 don't change when offline (no network), but we need to persist their values
	private static long sinceUid =-1; // ID of the most recent Tweet application has already seen

	@Column(name = "maxUid")
	private static long maxUid =-1;  // max_id is the *lowest* id of tweets already processed (badly named, be consistent with docs)

	@Override
	public String toString() {
		if (user == null) {
			return getBody() + " - null screen name";
		} else {
			return getBody() + " - " + getUser().getScreenName();
		}
	}
	
	public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());
		for (int i=0; i<jsonArray.length(); i++) {
			JSONObject tweetJson = null;
			try {
				tweetJson = jsonArray.getJSONObject(i);
			} catch (Exception e) {
				Log.e("simpletwitter", "FAILURE parsing tweet from json ArrayList. "+ e.getMessage());
				e.printStackTrace();
				continue;
			}

			Tweet tweet = Tweet.fromJSON(tweetJson);
			if (tweet!=null) {
				tweets.add(tweet);
			}
		}
		return tweets;
	}

	// getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
	public static String getRelativeTimeAgo(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
		sf.setLenient(true);
	 
		String relativeDate = "";
		try {
			long dateMillis = sf.parse(rawJsonDate).getTime();
			// "24 minutes ago", "2 hours ago", "Yesterday" "2 days ago"
			relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
					System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	 
		return relativeDate;
	}

	public static Tweet fromJSON(JSONObject jsonObject) {
		Tweet tweet = new Tweet();
		// extract values from json to populate the tweet object

		try {
			tweet.body = jsonObject.getString("text");
			tweet.uid = jsonObject.getLong("id");
			String s = tweet.body;
			if (s.length()> 25) {
				s = s.substring(0,25);
			}
			Log.d("simpletwitter", "read in tweet id "+ tweet.uid +" " +s);
			tweet.createdAt = jsonObject.getString("created_at");
			tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
			tweet.timeSince = getRelativeTimeAgo(tweet.createdAt);

		} catch (JSONException e) {
			Log.e("simpletwitter", "FAILURE parsing 1 tweet from json. "+ e.getMessage());
			e.printStackTrace();
		}
		return tweet;
	}
	public String getBody() {
		return body;
	}

	public long getUid() {
		return uid;
	}

	public String getCreatedAt() {
		
		return createdAt;
	}

	public User getUser() {
		return user;
	}

	public String getTimeSince() {
		return timeSince;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int flags) {
        p.writeLong(this.uid);
        p.writeString(this.body);
        p.writeString(this.timeSince);
        p.writeParcelable(this.user, 0);
	}
	
    private Tweet(Parcel in) {
        this.uid = in.readLong();
        this.body = in.readString();
        this.timeSince = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
	
}
