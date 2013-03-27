package com.ledor.asyncprocess.commonclasses;

import java.util.ArrayList;

import com.ledor.asyncprocess.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * ContentListAdapter
 * List Adapter to handle the content downloaded from url
 * 
 * @author R.Casimina
 * @param context
 * @param data
 * @param hasProgressDialog
 */
public class ContentListAdapter extends BaseAdapter {

	/**
     * Handle for ArrayList<ItemContent>
     */
    public ArrayList<ItemContent> mContentList;

    /**
     * Context
     */
    private Context mContext;
        
    /**
     * Flag if progrss bar is needed or not
     */
    private ImageDownloadManager mImageDownloader;
    
    /**
     * Flag if progress bar is needed or not
     */
    private boolean mHasProgressBar;

    public ContentListAdapter(Context context, ArrayList<ItemContent> data, Boolean hasProgressDialog, ImageDownloadManager imageDownloader) {
    	mContext = context;
    	mContentList = data;
    	mHasProgressBar = hasProgressDialog;
    	mImageDownloader = imageDownloader;
    }

	@Override
	public int getCount() {
		return mContentList.size();
	}

	@Override
	public Object getItem(int pos) {
		return mContentList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View view, ViewGroup viewgrp) {
		
		View itemView = view;
		
		ViewHolder vholder;
		
		if (itemView == null) {
			itemView = LayoutInflater.from(mContext).inflate(R.layout.item_list2, null);
			
			vholder = new ViewHolder();
			
			vholder.avatar = (ImageView) itemView.findViewById(R.id.avatar);
			vholder.username = (TextView) itemView.findViewById(R.id.username);
			vholder.message = (TextView) itemView.findViewById(R.id.message);
			vholder.imageurl = (TextView) itemView.findViewById(R.id.imageurl);
			vholder.progressbar = (ProgressBar) itemView.findViewById(R.id.progress);
			
			itemView.setTag(vholder);
		} else {
			vholder = (ViewHolder) itemView.getTag();
		}
		
		ItemContent item = mContentList.get(pos);
		
		vholder.username.setText(item.mUserName);
		vholder.message.setText(item.mMessage);
		vholder.imageurl.setText(item.mImageUrl);
		if (mHasProgressBar) {
			vholder.progressbar.setVisibility(View.VISIBLE);
			mImageDownloader.displayImage(item.mImageUrl, vholder.avatar, vholder.progressbar);
		} else {
			vholder.progressbar.setVisibility(View.GONE);			
		}
		
		return itemView;
	}

	/** 
	 * ViewHolder for those views inside the item in the list
	 */
    static class ViewHolder {
    	ImageView avatar;
    	ProgressBar progressbar;
        TextView username;
        TextView message;
        TextView imageurl;
    }
}
