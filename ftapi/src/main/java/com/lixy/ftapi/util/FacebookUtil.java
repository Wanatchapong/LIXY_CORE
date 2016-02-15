package com.lixy.ftapi.util;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.RawAPIResponse;
import facebook4j.Reading;
import facebook4j.User;
import facebook4j.auth.AccessToken;
import facebook4j.internal.org.json.JSONException;

public class FacebookUtil {

	private static Facebook facebook;
	public static String APP_ID;
	public static String APP_SECRET;

	public static Facebook getFacebook() {
		return facebook;
	}

	public static void init() throws FacebookException, JSONException{
		facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId(APP_ID, APP_SECRET);
		
		facebook.getOAuthAppAccessToken();
		RawAPIResponse response = facebook.callGetAPI("999892543415229");
		if(!response.asJSONObject().getString("id").equals(APP_ID)){
			throw new FacebookException("APP ID MISMATCH!");
		}
	}
	
	public static User getUser(Long profileId, String accessToken) throws FacebookException {
		AccessToken accToken = new AccessToken(accessToken);
		facebook.setOAuthAccessToken(accToken);
		
		User user = facebook.getUser(profileId.toString(), new Reading().fields("id","email","first_name", "middle_name", "last_name", "picture"));
		
		if(!user.getId().equals(profileId.toString())){
			throw new FacebookException("Profile id missmatch!");
		}
		
		return user;
	}

}
