package com.ledor.asyncprocess.commonclasses;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import com.ledor.asyncprocess.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageDownloadManager {
    /**
	 * Context
	 */
    private Context mContext;

    /**
	 * The list of downloaded image.
	 */
    private HashMap<String, Bitmap> mDownloadedImages=new HashMap<String, Bitmap>();

    /**
	 * Directory where we saved dowloaded images
	 * so that we will not download this images again
	 */
    private File mCacheDir;
    
    /**
	 * Handle for our content loader Thread
	 * This is asynchronous process run in background of our Main UI
	 */
    ImageDownlaodThread mImageCacheThread = new ImageDownlaodThread();
    

    /**
     * Handle for our ImageStack class
     */
    ImageDownloadQueue mDownloadQueue = new ImageDownloadQueue();

    /**
     * ImageManager
     * 
     * Constructor
     * 
     * @param context Context to get our directory to save our cache images
     */
    public ImageDownloadManager (Context context) {
    	
    	mContext = context;
    	
    	// Set our thread to minimum for not to affect our Main UI
    	mImageCacheThread.setPriority(Thread.MIN_PRIORITY);
    	
    	// Create directory to SD card to save our cached images
    	String sdState = Environment.getExternalStorageState();
    	if (sdState.equals(Environment.MEDIA_MOUNTED)) {
    		mCacheDir = new File(Environment.getExternalStorageDirectory(),"ImageManager");
    	} else {
    		mCacheDir = mContext.getCacheDir();
    	}
    	
    	// If "ImageManager" doesn't exists, create the folder
    	if (!mCacheDir.exists()) {
    		// Create parent directories as well if parent directories doesn't exist 
    		mCacheDir.mkdirs();
    	}
    }

    /**
     * displayImage
     * 
     * This will be the entry point to start downloading the images
     * 
     * @param url String - the url of the image
     * @param view ImageView where we need to display the downloaded image
     * @param progressBar ProgressBar willbe displayed while downloading the image
     */
    public void displayImage(String url, ImageView view, ProgressBar progressBar) {
    	
    	// Since we are downloading the images asynchronously
    	// set tag to ImageView so that we can identify the correct ImageView for downloaded images
    	view.setTag(url);
    	
    	// Check if we already downloaded the images by comparing the url link
    	if (mDownloadedImages.containsKey(url)) {
    		// if yes set the downloaded image to ImageView
    		view.setImageBitmap(mDownloadedImages.get(url));
    		
    		// Hide the progressBar, we don't need this since we already downlaoded the image
    		progressBar.setVisibility(View.GONE);
    		
    		// then show the ImageView
    		view.setVisibility(View.VISIBLE);
    	} else {
    		// if not yet downloaded, add it to our download queue
    		QueueImage(url, view, progressBar);
    		
    		view.setVisibility(View.INVISIBLE);
    		progressBar.setVisibility(View.VISIBLE);
    	}
    }
    
    
    /**
     * QueueImage
     * 
     * This will be the list of the images to be downloaded
     * 
     * @param url String - the url of the image
     * @param view ImageView where we need to display the downloaded image
     * @param progressBar ProgressBar willbe displayed while downloading the image
     */
    private void QueueImage(String url, ImageView view, ProgressBar progressBar) {
		// Removed previous ImageInfo if any
    	mDownloadQueue.Clean(view);
    	
    	// Create new ImageInfo to be inserted to our mDownloadQueue
    	ImageInfo newImage = new ImageInfo(url, view, progressBar);
    	
    	synchronized(mDownloadQueue.mImagesInfo) {
    		mDownloadQueue.mImagesInfo.push(newImage);
    		mDownloadQueue.mImagesInfo.notifyAll();
    	}
    	
    	// Here's the asynchronous process
    	// We will download the content on background
    	if (mImageCacheThread.getState() == Thread.State.NEW) {
    		mImageCacheThread.start();
    	}
		
	}

	/**
     * ImageDownlaod Thread Class
     * This is the thread that will actually downloaded image and save it to our directory.
     */
    class ImageDownlaodThread extends Thread {
    	public void run() {
    		try {
    			while(true) {
    				// Check if we have images to download if non, tell the thread to wait
    				if(mDownloadQueue.mImagesInfo.size() == 0) {
    					synchronized(mDownloadQueue.mImagesInfo) {
    						// Hold the thread and wait until Main UI added new Image to be downloaded in our ImageStack
    						mDownloadQueue.mImagesInfo.wait();
    					}
    				}
    				
    				// Continue when Main UI added image to our ImageStack
    				if(mDownloadQueue.mImagesInfo.size() != 0) {
    					ImageInfo imgInfo;
    					
    					synchronized(mDownloadQueue.mImagesInfo) {
    						// Get and removed the top of our ImageStack data 
    						imgInfo = mDownloadQueue.mImagesInfo.pop();
    					}
    					
    					// Get the bitmap image from url
    					Bitmap bitmap = GetBitmap(imgInfo.mImageUrl);
    					
    					// Update our ImageList
    					mDownloadedImages.put(imgInfo.mImageUrl, bitmap);

    					// Display the image in our Main UI and ensure that we get the correct View handler
    					Object tag = imgInfo.mImageView.getTag();
    					if(tag != null && tag.toString().equals(imgInfo.mImageUrl)) {
    						ImageDisplayThread displayThread = new ImageDisplayThread(bitmap,imgInfo.mImageView,imgInfo.mProgressBar);
    						
    						// Get the activity where our ImageView is being displayed
    						//Activity a = (Activity) imgInfo.mImageView.getContext();
    						Activity a = (Activity) mContext;
    						
    						// Display the image to Main UI
    						a.runOnUiThread(displayThread);
   					}
    				}
        			if(Thread.interrupted()) {
        				break;
        			}
    			}	
    		} catch (InterruptedException e) {
    			Log.e("ImageDownlaodThread","Download Thread was interrupted");
    		}
    	}

        /**
         * GetBitmap
         * 
         * This is the actual image downloader
         * 
         * @param url String - the url of the image
         */
		private Bitmap GetBitmap(String mImageUrl) {
			try {
			    URL url = new URL(mImageUrl);
			    return BitmapFactory.decodeStream(url.openConnection().getInputStream()); 
			  }
			  catch(Exception ex) {
				  Log.e("GetBitmap", "Failed to download the image");
				  return null;
			  }
		}
    }
    
    /**
     * ImageDownloadQueue Class
     * This class manage all images to be downloaded.
     */
    class ImageDownloadQueue {
    	/**
    	 * Handle for the list of all images t be downloaded
    	 */
    	private Stack<ImageInfo> mImagesInfo=new Stack<ImageInfo>();

    	/**
    	 * Clean
         *
         * This will release all loaded images in our ImageViews
		 * @param view ViewHolder
		 * @return     void
         */
    	public void Clean(ImageView view)
    	{
    		for(int j=0 ;j<mImagesInfo.size();) {
    			if(mImagesInfo.get(j).mImageView == view) {
    				mImagesInfo.remove(j);
    			}
        		j+=1;
    		}
    	}
    }
    
    /**
     * ImageInfo Class
     * This class contains the image information
     */
    private class ImageInfo {
    	
    	/**
    	 * Url of the image
    	 */
    	public String mImageUrl;
    	
    	/**
    	 * ImageView handler where we need to setImage into
    	 */
    	public ImageView mImageView;
    	
    	/**
    	 * ProgressBar handler
    	 * This will be displayed while we are downloading the image from url
    	 */
    	public ProgressBar mProgressBar;
    	
    	public ImageInfo( String url, ImageView view, ProgressBar progressBar) {
    		mImageUrl = url;
    		mImageView = view;
    		mProgressBar = progressBar;
    	}
    }
    
    /**
     * ImageDisplayThread Class
     * This class will display our image in Main UI
     * This should be run on MainUI thread
     */
    private class ImageDisplayThread implements Runnable {
    	/**
    	 * Bitmap
    	 */
    	Bitmap mBitmap;
    	
    	/** 
    	 * ImageView handler
    	 */
    	ImageView mImageView;
    	
    	/** 
    	 * ImageView handler
    	 */
    	ProgressBar mProgressBar;

    	public ImageDisplayThread(Bitmap bitmap, ImageView view, ProgressBar progressBar) {
    		mBitmap = bitmap;
    		mImageView = view;
    		mProgressBar = progressBar;
    	}

		@Override
		public void run() {
			if (mBitmap != null) {
				mImageView.setImageBitmap(mBitmap);
			} else {
				mImageView.setImageResource(R.drawable.icon);
			}
			mProgressBar.setVisibility(View.GONE);
			mImageView.setVisibility(View.VISIBLE);
		}
    }
}
