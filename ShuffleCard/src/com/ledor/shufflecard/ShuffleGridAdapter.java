package com.ledor.shufflecard;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ShuffleGridAdapter extends BaseAdapter {
    /**
     * Common GridAdapter.
     * This will display the cards in GridView
     */

	/* *
	 * List of resource ID of Images
	 */
	List<Integer> mResID;

	/* *
	 * Context
	 */
    private Context mContext;

	/* *
	 * Array of Resource ID of Images
	 */
    private Integer[] mThumbIds = {
            R.drawable.dia1, R.drawable.dia2, R.drawable.dia3,
            R.drawable.dia4, R.drawable.dia5, R.drawable.dia6,
            R.drawable.dia7, R.drawable.dia8, R.drawable.dia9,
    };

	/* *
	 * List of resource ID of Images
	 */
	boolean isFaceUp = false;

	public ShuffleGridAdapter(Context c) {
        mContext = c;
        mResID = new ArrayList<Integer>();
        for (int i=0;i<mThumbIds.length;i++) {
        	mResID.add(mThumbIds[i]);
        }
    }

    @Override
	public int getCount() {
		return mResID.size();
	}

	@Override
	public Object getItem(int position) {
		return (int) mResID.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
            imageView.setAdjustViewBounds(false);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            imageView = (ImageView) convertView;
        }

        if (isFaceUp) {
        	imageView.setImageResource(mResID.get(position));
        } else {
        	imageView.setImageResource(R.drawable.back);
        }

        return imageView;
	}

}

