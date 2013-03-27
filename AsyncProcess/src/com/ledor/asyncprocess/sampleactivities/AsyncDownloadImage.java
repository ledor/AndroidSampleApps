package com.ledor.asyncprocess.sampleactivities;

import java.util.ArrayList;

import com.ledor.asyncprocess.commonclasses.ContentListAdapter;
import com.ledor.asyncprocess.commonclasses.ImageDownloadManager;
import com.ledor.asyncprocess.commonclasses.ItemContent;
import com.ledor.asyncprocess.threadclass.HttpRequestThread;
import com.ledor.asyncprocess.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Sample Activity for asynchronous HTTP Request and Downloading images
 * @author R. Casimina
 *
 */
public class AsyncDownloadImage extends Activity implements OnClickListener {

	/**
	 * ListView Handler
	 */
	ListView mListView;
	
	/**
	 * ListView Handler
	 */
	ContentListAdapter mListAdapter;
	
	/**
	 * Item List
	 */
	ArrayList<ItemContent> mItemList;
	
	/**
	 * Image Download Manager
	 */
	ImageDownloadManager mDownloadManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_async_http_request);
		
		Button button = (Button) findViewById(R.id.searchButton);
		button.setOnClickListener(this);
		
		// Create our data handler for our list 
		mItemList = new ArrayList<ItemContent>();
		
		// Create our Download Manager
		mDownloadManager = new ImageDownloadManager(this);
		
		// Create our customize adapter to be used by our list 
		mListAdapter = new ContentListAdapter(this, mItemList, true, mDownloadManager);
		
		mListView = (ListView) findViewById(R.id.searchResult);
		mListView.setAdapter(mListAdapter);
		
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.searchButton:
			TextView edittext = (TextView) findViewById(R.id.searchBox);
			if (edittext.getText().length() <= 0) {
				Toast.makeText(this, "Please input some text", Toast.LENGTH_LONG).show();
			} else {
				// Request Http response asynchronously
				new HttpRequestThread(this, mListAdapter).execute(edittext.getText().toString());
			}
			break;
		default:
			break;
		}		
	}

}
