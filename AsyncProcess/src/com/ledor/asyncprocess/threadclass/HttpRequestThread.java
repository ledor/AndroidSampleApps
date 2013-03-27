package com.ledor.asyncprocess.threadclass;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ledor.asyncprocess.commonclasses.ContentListAdapter;
import com.ledor.asyncprocess.commonclasses.ItemContent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * HttpRequestThread
 * TO be used by Main UI when requesting http response asynchronously
 * 
 * @author R.Casimina
 *
 */
public class HttpRequestThread extends AsyncTask<String, Integer, Void> {
	
	/**
	 * Context
	 */
	Context mContext;

	/**
	 * ListAdapter Handler
	 */
	ContentListAdapter mListAdapter;
	
	/**
	 * ProgressDialog
	 * This will be display while waiting the response from requested URL
	 */
	private ProgressDialog mProgressDialog;
	
	public HttpRequestThread(Context context, ContentListAdapter adapter) {
		// Save the context. We need the context to create ProgessDialog
		mContext = context;
		
		// Save the Adapter so that we can notify the ListView to update
		mListAdapter = adapter;
	}
	
	@Override
	protected Void doInBackground(String... keyword) {
		
		InputStream stream = null;
		try {
			// Create search string. This time we will use the Twitter Search API
			String query = URLEncoder.encode(keyword[0].toString(), "utf-8");
			String searchUrl = "http://search.twitter.com/search.json?q=" + query;
			
			// Create Http Client
			HttpClient client = new  DefaultHttpClient();
			
			// Http has the following method. GET, POST. This time we will just use GET method
			// Create HTTP GET method
			HttpGet get = new HttpGet(searchUrl);
			
			// Execute our query and save the response from the server
			HttpResponse responseGet = client.execute(get);
			
			// Create stream data from the response we received from the server
			stream = responseGet.getEntity().getContent();
			
		} catch (Exception e) {
			Log.e("HttpRequestManager", "Connection Error", e);
		}
		String result = null;
		
		try {
			// Start streaming the response and save it to String class
			// We need the String Class to create JSONObject
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream , "iso-8859-1"), 8);
			StringBuilder buildStr = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				buildStr.append(line + "\n");
			}
			stream.close();
			result = buildStr.toString();
		} catch (Exception e) {
			Log.e("HttpRequestManager", "Error converting response to String", e);
		}
		
		JSONObject jObj = null;
		try {
			// Create JSONOBject from the string we created from the response
			jObj = new JSONObject(result);
		} catch (Exception e) {
			Log.e("HttpRequestManager", "Error creating JSON Object from String", e);
		}
		
		JSONArray jArray = null;
		try {
			// JSONObject constents the array of the web content, so we need to get the JSONArray
			jArray = jObj.getJSONArray("results");
			if(!mListAdapter.mContentList.isEmpty()) {
				mListAdapter.mContentList.clear();
			}
			for (int i=0; i < jArray.length(); i++) {
				JSONObject obj = jArray.getJSONObject(i);
				ItemContent itemContent = new ItemContent(
						obj.get("from_user").toString(),
						obj.get("text").toString(),
						obj.get("profile_image_url").toString()
					);
				// Add the web content to our list
				mListAdapter.mContentList.add(itemContent);
				
			}
		} catch (Exception e) {
			Log.e("HttpRequestManager", "Error getting JSON Array", e);
		}
		return null;
	}
		
	@Override
	protected void onPostExecute(Void result) {
		mProgressDialog.dismiss();
		mListAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onPreExecute() {
		mProgressDialog = ProgressDialog.show(mContext, "", "Loading messages. Please wait...", true);		
	}

}
