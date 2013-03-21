package com.ledor.listactivitysample;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlayerListAdapter extends BaseAdapter {
	
	public static final String RICHVIEW = "RICHVIEW";
	public static final String SIMPLEVIEW = "SIMPLEVIEW";

	/**
	 * Data of the list
	 */
    private ArrayList<PlayerListItem> mData;
    
    /**
     * Layout holder
     */
    private LayoutInflater mInflater=null;
    
    /**
     * List view mode: SimpleView or RichView
     */
    private String mViewMode=RICHVIEW;

    /**
     * Constructor
     */
    public PlayerListAdapter(Context context, ArrayList<PlayerListItem> data) {
    	mData=data;
    	mInflater = LayoutInflater.from(context);
    	mViewMode = RICHVIEW;
    }

    public int getCount() {
       return mData.size();
	}

	public Object getItem(int position) {
		return mData.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		View itemView = convertView;
		
        ViewHolder holder;
        if( itemView == null ) {
        	itemView = mInflater.inflate(R.layout.item_list, null);

        	holder = new ViewHolder();
        	
            holder.title = (TextView) itemView.findViewById(R.id.title);
            holder.artist = (TextView) itemView.findViewById(R.id.artist);
            holder.duration = (TextView) itemView.findViewById(R.id.duration);
            holder.thumbnail = (LinearLayout) itemView.findViewById(R.id.thumbnail);
            holder.icon = (ImageView) itemView.findViewById(R.id.icon);
            holder.arrow = (ImageView) itemView.findViewById(R.id.arrow);

            itemView.setTag(holder);
        } else {
        	holder = (ViewHolder) itemView.getTag();
        }

        PlayerListItem media = mData.get(position);
        
        if (mViewMode == RICHVIEW) {
        	
    		holder.artist.setVisibility(View.VISIBLE);
            holder.duration.setVisibility(View.VISIBLE);
            holder.thumbnail.setVisibility(View.VISIBLE);
            holder.icon.setVisibility(View.VISIBLE);
            holder.arrow.setVisibility(View.VISIBLE);
            
            holder.title.setText(media.getItemTitle());
            holder.artist.setText(media.getItemArtist());
            holder.duration.setText(media.getItemDuration());
            holder.icon.setImageBitmap(media.getItemBitmap());
            
        } else {
        	
    		holder.artist.setVisibility(View.GONE);
            holder.duration.setVisibility(View.GONE);
            holder.thumbnail.setVisibility(View.GONE);
            holder.icon.setVisibility(View.GONE);
            holder.arrow.setVisibility(View.GONE);
            
            int filetype = media.getItemFiletype();
            if ( filetype == PlayerListItem.TYPE_FOLDER ) {
            	holder.title.setText(media.getItemTitle() + "/");
            } else {
            	holder.title.setText(media.getItemTitle());
            }
        }

        return itemView;
	}
	
	/** 
	 * ViewHolder for those views inside the item in the list
	 */
    static class ViewHolder {
        TextView title;
        TextView artist;
        TextView duration;
        ImageView icon;
        ImageView arrow;
        LinearLayout thumbnail;
    }
    
    /**
     * Toggle view mode
     */
    public void toggleViewMode(){
    	mViewMode = (mViewMode == RICHVIEW) ? SIMPLEVIEW : RICHVIEW;
    }

    /**
     * Get the view mode
     */
    public String getViewMode(){
    	return mViewMode;
    }
    
}
