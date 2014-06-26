package com.codepath.apps.basictwitter.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class User extends com.activeandroid.Model implements Parcelable {
	//  See https://github.com/pardom/ActiveAndroid/wiki/Creating-your-database-model
	private String name;  // shown in bold
	private long uid;
	private String screenName;  // shown with "@" prefix, and in gray
	private String profileImageUrl;

	public User() {   //no arg constructor
		super();
	}
	
	// Factory method, creates Java model from Json, User.fromJSON(...); then get fields
	public static User fromJSON(JSONObject json) {
		User u = new User();
		// extract values from json to populate the tweet field
		try {
			u.name = json.getString("name");
			u.uid = json.getLong("id");
			u.screenName = json.getString("screen_name");
			u.profileImageUrl = json.getString("profile_image_url");

			// Log.e("simpletwitter", "screenname="+u.screenName);
			// Log.e("simpletwitter", "name="+u.name);

		} catch (JSONException e) {
			Log.e("simpletwitter", "FAILURE parsing a User from json. "+ e.getMessage() + ", json="+json);
			e.printStackTrace();
		}
		return u;
	}

	public String getName() {
		return getUserName();
	}

	public String getUserName() {
		return name;
	}

	public long getUid() {
		return uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.uid);
        dest.writeString(this.name);
        dest.writeString(this.screenName);
        dest.writeString(this.profileImageUrl);
	}
	
    private User(Parcel in) {
        this.uid = in.readLong();
        this.name = in.readString();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
    }

    public static Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
    };

}
