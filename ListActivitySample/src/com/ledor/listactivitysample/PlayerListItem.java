/**
 * PlayerListItem Class                                       
 *                                   
 * List Item for our Customized Adapter PlayerListAdapter
 * @author	R.Casimina
 * @version	1.01
 * @since	2012.11.18    
 */

package com.ledor.listactivitysample;

import java.io.File;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PlayerListItem {
	public static final int TYPE_PARENT = 0;
	public static final int TYPE_FOLDER = 1;
	public static final int TYPE_FILE = 2;

	private Bitmap mIcon;
	private String mTitle ;
	private String mArtist;
	private String mDuration;
	private int mFileType;
	private String mFilePath;
	private int mFileIndex;

	public PlayerListItem(Context context, String path, int filetype) {

		File mediaFile = new File(path);
		mFileType = filetype;
		
		switch (filetype) {
		case TYPE_FILE: // if file
			// We should use the MetadataUtil here to get the MP3 file information
			// MetadataUtil mediafile = new MetadataUtil(path);
			// mTitle = mediafile.getTitle();
			// mArtist = mediafile.getArtist();
			// mDuration = mediafile.getDuration();
			// mIcon = mediafile.getBitmap();
			mTitle = mediaFile.getName().substring(0, 20);
			mArtist = mediaFile.getName();
			//mDuration = mediaFile.getName();
			mDuration = "00:00:00";
			mIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.music_icon);
			mFilePath = path;
			break;
		case TYPE_FOLDER: // if folder
			mTitle = mediaFile.getName();
			mArtist = "";
			mDuration = "";
			mIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder);
			mFilePath = path;
			break;
		default: // Not file nor folder
			mTitle = Utils.GOTO_PARENT;
			mArtist = "";
			mDuration = "";
			mIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.folderup);
			mFilePath = mediaFile.getParent();
			break;
		}	
	}
	
	public String getItemTitle() {
		return mTitle;
	}

	public String getItemArtist() {
		return mArtist;
	}

	public String getItemDuration() {
		return mDuration;
	}

	public Bitmap getItemBitmap() {
		return mIcon;
	}
	
	public int getItemFiletype() {
		return mFileType;
	}

	public String getItemFilePath() {
		return mFilePath;
	}

	public int getItemFileIndex() {
		return mFileIndex;
	}

	public void setItemFileIndex(int pos) {
		mFileIndex = pos;
	}

}
