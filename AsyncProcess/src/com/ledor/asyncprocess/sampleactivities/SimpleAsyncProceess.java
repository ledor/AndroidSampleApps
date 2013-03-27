package com.ledor.asyncprocess.sampleactivities;

import com.ledor.asyncprocess.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SimpleAsyncProceess extends Activity {

	/**
	 * Handler for our ProgressDialog
	 */
	ProgressDialog mProgressDialog;
	
	/**
	 * Message Handler
	 */
	Handler mActivityHandler;
	
	/**
	 * Hanlder for our Thread
	 */
	UpdateTriggerThread mThread;
	
	/**
	 * Progress of our progressbar
	 */
	int mProgress = 0;
	
	/**
	 * Here's the maximum value for our progress bar
	 */
	final static int PROGRESS_MAX_VALUE = 1000;
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_async_proceess);

		// Create our messageHanlder
		mActivityHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int progress = msg.getData().getInt("progress");
				mProgressDialog.setProgress(progress);
				
				// Check if we reach the maximum progress value
				if ( progress >= PROGRESS_MAX_VALUE) {
					// If yes, dismiss the progress dialog
					mProgressDialog.dismiss();
					
					// set again the progress member to zero
					mProgress = 0;
					
					// At the same time stop our thread
					mThread.setFlag(false);
				}
			}
		};
		
		
		// Process button to start progress dialog that will update asynchronously
		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// Let's create Progress Dialog
				mProgressDialog = new ProgressDialog(SimpleAsyncProceess.this);
				
				mProgressDialog.setOnCancelListener(new OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						mProgress = mProgressDialog.getProgress();
						mThread.setFlag(false);
					}
				}
				);
				
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				mProgressDialog.setMax(PROGRESS_MAX_VALUE);
				mProgressDialog.setMessage("Updating...");
				
				// Let's create our Thread and start it
				mThread = new UpdateTriggerThread(mActivityHandler,mProgress);
				mThread.start();
				
				// Let show our progress dialog
				mProgressDialog.show();
			}
		});

	}
	
	/**
	 * This is our thread that will run on background to trigger our progress bar to update
	 * @param h Handler
	 */
	private class UpdateTriggerThread extends Thread {
		
		/**
		 * Handler
		 * This will force our Main UI to invalidate the update of progress bar
		 */
		Handler mHandler;
		int mProgress;
		boolean mFlag = true;
		
		public UpdateTriggerThread(Handler h, int initialValue) {
			mHandler = h;
			mProgress = initialValue;
		}
		
		@Override
		public void run() {
			while(mFlag) {
				// Let put some delay so that we can verify that our progress bar is updating
	            try {
	                Thread.sleep(50);
	            } catch (InterruptedException e) {
	                Log.e("UpdateTriggerThread Error", "Thread was Interrupted");
	            }
	            
	            // Create message to be sent to our main UI to update the progress bar
	            Message msg = mHandler.obtainMessage();
	            Bundle bundle = new Bundle();
	            bundle.putInt("progress", mProgress);
	            msg.setData(bundle);
	            
	            // Send the message to Main UI
	            mHandler.sendMessage(msg);
	            
	            // increment our progress
	            mProgress = mProgress + 1;
			}
		}
		
		public void setFlag(boolean flag) {
			mFlag = flag;
		}
	}
}
