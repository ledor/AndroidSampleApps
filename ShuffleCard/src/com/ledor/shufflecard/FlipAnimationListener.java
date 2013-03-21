package com.ledor.shufflecard;

import android.view.animation.Animation.AnimationListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class FlipAnimationListener implements AnimationListener {
    /**
     * Animation Listener for flipping cards
     */
	
	/**
	 * Index of grid
	 */
	int mIndex;

	/**
	 * Handler for Grid Adapter
	 */
	GridView mGridView;

	/**
	 * Handler for Grid Adapter
	 */
	ShuffleGridAdapter mGridAdapter;

	/**
	 * Handler for ImageView
	 */
	ImageView mImageView;

	/**
	 * Handler for shuffle button
	 */
	Button mShuffle;

	/**
	 * Handler for flip button
	 */
	Button mFlip;

	@Override
	public void onAnimationStart(Animation animation) {
		mGridAdapter = (ShuffleGridAdapter) mGridView.getAdapter();
		mImageView = (ImageView) mGridView.getChildAt(mIndex);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		int resID;
		if (mGridAdapter.isFaceUp) {
			resID = R.drawable.back;
		} else {
			resID = mGridAdapter.mResID.get(mIndex);
		}
		mImageView.setImageResource(resID);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		mImageView.clearAnimation();
		int index = mIndex + 1;
		
		if (index >= mGridAdapter.getCount()) {
			mGridAdapter.isFaceUp = mGridAdapter.isFaceUp ? false : true;
			mGridAdapter.notifyDataSetChanged();
			
			mShuffle.setEnabled(true);
			mFlip.setEnabled(true);
			return;
		}
		ImageView iView = (ImageView) mGridView.getChildAt(index);
		
		// Prepare the animation Listener
		FlipAnimationListener listener = new FlipAnimationListener();
		listener.setGridView(mGridView);
		listener.setIndex(index);
		listener.setShuffleButton(mShuffle);
		listener.setFlipButton(mFlip);
		
		// Set listener to animation
		animation.setAnimationListener(listener);
		
		// Start the animation to next View
		iView.startAnimation(animation);
	}
	
	public void setGridView(GridView view) {
		mGridView = view;
	}

	public void setIndex(int index) {
		mIndex = index;
	}

	public void setShuffleButton(Button btn) {
		mShuffle = btn;
	}

	public void setFlipButton(Button btn) {
		mFlip = btn;
	}

}
