package com.ledor.asyncprocess.commonclasses;

public class ItemContent {
	/**
	 * Username
	 */
	public String mUserName;
	
	/**
	 * Message
	 */
	public String mMessage;
	
	/**
	 * Url of the image
	 */
	public String mImageUrl;
	
	public ItemContent(String userName, String message, String url) {
		mUserName = userName;
		mMessage = message;
		mImageUrl = url;
	}
}
