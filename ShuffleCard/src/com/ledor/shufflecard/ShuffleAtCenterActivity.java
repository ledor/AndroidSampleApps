package com.ledor.shufflecard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class ShuffleAtCenterActivity extends Activity implements OnClickListener {
    /**
     * This class will shuffle cards at center. All card will move to center and then shuffle
     */

	/**
	 * Handle for GridView where the cards are being displayed
	 */
	GridView mGridView;

	/**
	 * Handle for Adapter
	 */
	ShuffleGridAdapter mGridAdapter;

	/**
	 * Handle for Buttons
	 */
	Button mShuffle;
	Button mFlip;

	/**
	 * Animation status
	 */
	boolean mIsAnimationOn = false;
	
	/**
	 * List of coordinates of all views as Center ImageView as reference point
	 */
	List<Point> mToCenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shuffle_at_center);
		
		mGridAdapter = new ShuffleGridAdapter(ShuffleAtCenterActivity.this);

		mGridView = (GridView) findViewById(R.id.myGrid2);
        mGridView.setAdapter(mGridAdapter);
               
        mShuffle = (Button) findViewById(R.id.shuffle2);
        mShuffle.setOnClickListener(this);

        mFlip = (Button) findViewById(R.id.flipcard2);
        mFlip.setOnClickListener(this);
        
        mToCenter = new ArrayList<Point>();
        
	}

	@Override
	public void onClick(View view) {
		switch (view.getId())
		{
			case R.id.shuffle2:
				if (mIsAnimationOn) {
					mIsAnimationOn = false;
					mShuffle.setText(R.string.app_name);
					mFlip.setEnabled(true);
				} else {
					mIsAnimationOn = true;
					mShuffle.setText(R.string.stop);
					mFlip.setEnabled(false);
					PrepareMoveCoordinates();
					ShuffleCard();
				}
				break;
			case R.id.flipcard2:
				mShuffle.setEnabled(false);
				mFlip.setEnabled(false);
				FlipCard();
				break;
			default:
				break;
		}
		
	}

	/**
	 * PrepareMoveCoordinates
	 *
	 * List up the coordinates where all the views will be moving into referenced at center view
	 *
	 * @param void
	 * @return void
	 */
	private void PrepareMoveCoordinates() {
		// Clear Coordinates
		if (!mToCenter.isEmpty()) {
			mToCenter.clear();
		}
		
		// Get the coordinate of View at Center
		int indexCenter = mGridAdapter.getCount()/2;
		ImageView iView = (ImageView) mGridView.getChildAt(indexCenter);
		int xCenterView = (int) iView.getX();
		int yCenterView = (int) iView.getY();
		
		// Create list of coordinates
		for (int i=0; i<mGridAdapter.getCount(); i++) {
			// Get ImageView Handler from GridView
			iView = (ImageView) mGridView.getChildAt(i);
			
			// Adding Point to our List
			Point inCenter = new Point();
			inCenter.x = xCenterView - (int) iView.getX();
			inCenter.y = yCenterView - (int) iView.getY();
			mToCenter.add(inCenter);
		}
		
	}

	/**
	 * ShuffleCard
	 *
	 * Shuffle the cards by moving it first to the center
	 *
	 * @param void
	 * @return void
	 */
	private void ShuffleCard() {
		AnimateToCenter();
	}

	/**
	 * AnimateToCenter
	 *
	 * Move all the cards to the center
	 *
	 * @param void
	 * @return void
	 */
	private void AnimateToCenter() {
		// Set TranslateAnimation to all views inside the grid and set listener to last view
		for (int i=0; i<mGridAdapter.getCount(); i++) {
			// Get ImageView Handler from GridView
			ImageView iView = (ImageView) mGridView.getChildAt(i);
			
			// Create and set animation 
			TranslateAnimation animation = new TranslateAnimation(0, mToCenter.get(i).x, 0, mToCenter.get(i).y);
			animation.setDuration(1000);
			animation.setFillAfter(false);
			animation.setRepeatCount(1);
			animation.setRepeatMode(Animation.REVERSE);
			iView.setAnimation(animation);			
		}
		
		// Get the last view and set the animation listener
		ImageView iView = (ImageView) mGridView.getChildAt(mGridAdapter.mResID.size()-1);
		TranslateAnimation animation = (TranslateAnimation) iView.getAnimation();
		animation.setAnimationListener(new AnimateToCenterListener());
		
		// Start animation now
		mGridView.startLayoutAnimation();
		
	}

	/**
	 * FlipCard
	 *
	 * Flip the cards
	 *
	 * @param void
	 * @return void
	 */
	private void FlipCard() {
		
		// Get the first View
		ImageView iView = (ImageView) mGridView.getChildAt(0);
			
		// Prepare the animation Listener
		FlipAnimationListener listener = new FlipAnimationListener();
		listener.setGridView(mGridView);
		listener.setIndex(0);
		listener.setShuffleButton(mShuffle);
		listener.setFlipButton(mFlip);
			
		// Prepare the animation
		ScaleAnimation animation = (ScaleAnimation) AnimationUtils.loadAnimation(this, R.anim.anim_flip);
			
		// Set listener to animation
		animation.setAnimationListener(listener);
		
		// Start the animation
		iView.startAnimation(animation);
	}

	private class AnimateToCenterListener implements AnimationListener {
	    /**
	     * Animation Listener for this activity
	     */

		@Override
		public void onAnimationEnd(Animation animation) {
			mGridAdapter.notifyDataSetChanged();
    		if (mIsAnimationOn) {
    			ShuffleCard();
    		}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// Set the shuffled images to GridView
			for (int i=0; i<mGridAdapter.getCount(); i++) {
				// Get ImageView Handler from GridView
				ImageView iView = (ImageView) mGridView.getChildAt(i);
				
				// Set Images 
				if (mGridAdapter.isFaceUp) {
					iView.setImageResource(mGridAdapter.mResID.get(i));							
				} else {
					iView.setImageResource(R.drawable.back);	
				}
			}
		}

		@Override
		public void onAnimationStart(Animation animation) {
			Collections.shuffle(mGridAdapter.mResID);
		}
	}
}
