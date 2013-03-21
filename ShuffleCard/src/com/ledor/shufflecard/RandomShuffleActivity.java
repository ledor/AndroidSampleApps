package com.ledor.shufflecard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
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
import android.widget.RelativeLayout;

public class RandomShuffleActivity extends Activity  implements OnClickListener {
    /**
     * This class will shuffle cards switching two random cards
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
	 * Handle for invisible ImageViews that represents the two cards to be swap.
	 * This will be shown during animation
	 */
	ImageView mFirstCardView;
	ImageView mSecondCardView;

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
	 * List of coordinates of all ImageViews inside GridViews
	 */
	List<Point> mViewCoord;

	/**
	 * List of index of all ImageViews to be used to shuffle the views
	 */
	List<Integer> mIndexes;
	
	/**
	 * Indexes of Views to be switch
	 */
	int mFirstCardIndex;
	int mSecondCardIndex;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_random_shuffle);

		mGridAdapter = new ShuffleGridAdapter(RandomShuffleActivity.this);

		mGridView = (GridView) findViewById(R.id.myGrid3);
        mGridView.setAdapter(mGridAdapter);
               
        mFirstCardView = (ImageView) findViewById(R.id.imageView1);
        mSecondCardView = (ImageView) findViewById(R.id.imageView2);

        mShuffle = (Button) findViewById(R.id.shuffle3);
        mShuffle.setOnClickListener(this);

        mFlip = (Button) findViewById(R.id.flipcard3);
        mFlip.setOnClickListener(this);
        
        mViewCoord = new ArrayList<Point>();
        
		// Prepare list to get two random number
        mIndexes = new ArrayList<Integer>();
		for(int num = 0; num<9; num++) {
			mIndexes.add(num);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId())
		{
			case R.id.shuffle3:
				if (mIsAnimationOn) {
					mIsAnimationOn = false;
					mShuffle.setText(R.string.app_name);
					mFlip.setEnabled(true);
				} else {
					mIsAnimationOn = true;
					mShuffle.setText(R.string.stop);
					mFlip.setEnabled(false);
					PrepareCoordinates();
					ShuffleCard();
				}
				break;
			case R.id.flipcard3:
				mShuffle.setEnabled(false);
				mFlip.setEnabled(false);
				FlipCard();
				break;
			default:
				break;
		}
		
	}

	/**
	 * PrepareCoordinates
	 *
	 * List up the coordinates of all views inside the GridView
	 *
	 * @param void
	 * @return void
	 */
	private void PrepareCoordinates() {
		// Clear Coordinates
		if (!mViewCoord.isEmpty()) {
			mViewCoord.clear();
		}
			
		// Create list of coordinates
		for (int i=0; i<mGridAdapter.getCount(); i++) {
			// Get ImageView Handler from GridView
			ImageView iView = (ImageView) mGridView.getChildAt(i);
			
			// Adding Point to our Coming In List
			Point viewPt = new Point();
			viewPt.x = (int) iView.getX();
			viewPt.y = (int) iView.getY();
			mViewCoord.add(viewPt);
		}
		
	}

	/**
	 * ShuffleCard
	 *
	 * Shuffling the card by swapping two cards' position randomly
	 *
	 * @param void
	 * @return void
	 */
	private void ShuffleCard() {		
		// Shuffle the list
		Collections.shuffle(mIndexes);
		
		// Get view of first card
		mFirstCardIndex = mIndexes.get(0);
		ImageView iView1 = (ImageView) mGridView.getChildAt(mFirstCardIndex);
		RelativeLayout.LayoutParams lparams1 = new RelativeLayout.LayoutParams(iView1.getWidth(),iView1.getHeight());
		
		// Prepare the first invisible ImageView
		mFirstCardView.setAdjustViewBounds(false);
		mFirstCardView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		mFirstCardView.setLayoutParams(lparams1);
		if (mGridAdapter.isFaceUp) {
			mFirstCardView.setImageResource(mGridAdapter.mResID.get(mFirstCardIndex));			
		} else {
			mFirstCardView.setImageResource(R.drawable.back);			
		}
		
		
		// Get views of second card
		mSecondCardIndex = mIndexes.get(1);
		ImageView iView2 = (ImageView) mGridView.getChildAt(mSecondCardIndex);
		RelativeLayout.LayoutParams lparams2 = new RelativeLayout.LayoutParams(iView1.getWidth(),iView1.getHeight());

		// Prepare the second invisible ImageView
		mSecondCardView.setAdjustViewBounds(false);
		mSecondCardView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		mSecondCardView.setLayoutParams(lparams2);
		if (mGridAdapter.isFaceUp) {
			mSecondCardView.setImageResource(mGridAdapter.mResID.get(mSecondCardIndex));
		} else {
			mSecondCardView.setImageResource(R.drawable.back);			
		}

		// Preparing the movements
		Point firstPt = mViewCoord.get(mFirstCardIndex);
		mFirstCardView.setX(firstPt.x);
		mFirstCardView.setY(firstPt.y);

		Point secondPt = mViewCoord.get(mSecondCardIndex);
		mSecondCardView.setX(secondPt.x);
		mSecondCardView.setY(secondPt.y);
		
		int firstX = secondPt.x - firstPt.x;
		int secondX = firstPt.x - secondPt.x;
		
		int firstY = secondPt.y - firstPt.y;
		int secondY = firstPt.y - secondPt.y;
		
		// Create animation for first View
		TranslateAnimation animation1 = new TranslateAnimation(0, firstX, 0, firstY);
		animation1.setDuration(1000);
		animation1.setFillAfter(false);
		mFirstCardView.setAnimation(animation1);
		mFirstCardView.setVisibility(View.VISIBLE);
		mFirstCardView.bringToFront();
		iView1.setVisibility(View.INVISIBLE);
		
		// Create animation for second View
		TranslateAnimation animation2 = new TranslateAnimation(0, secondX, 0, secondY);
		animation2.setDuration(1000);
		animation2.setFillAfter(false);
		animation2.setAnimationListener( new RandomShuffleAnimListener());
		mSecondCardView.setAnimation(animation2);
		mSecondCardView.setVisibility(View.VISIBLE);
		mSecondCardView.bringToFront();
		iView2.setVisibility(View.INVISIBLE);
		
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

	private class RandomShuffleAnimListener implements AnimationListener {
	    /**
	     * Animation Listener for this activity
	     */

		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			
			// Set the Image ResIDs to its proper places after shuffle
	        int firstCardResID = mGridAdapter.mResID.get(mFirstCardIndex);
	        int secondCardResID = mGridAdapter.mResID.get(mSecondCardIndex);
			mGridAdapter.mResID.set(mFirstCardIndex,secondCardResID);			
			mGridAdapter.mResID.set(mSecondCardIndex,firstCardResID);
			
			ImageView iView1 = (ImageView) mGridView.getChildAt(mFirstCardIndex);			
			ImageView iView2 = (ImageView) mGridView.getChildAt(mSecondCardIndex);
			
			iView1.setVisibility(View.VISIBLE);
			iView2.setVisibility(View.VISIBLE);

			mFirstCardView.setVisibility(View.GONE);
			mSecondCardView.setVisibility(View.GONE);
			
			mGridAdapter.notifyDataSetChanged();
			if (mIsAnimationOn) {
				int secondsDelayed = 1;
	            new Handler().postDelayed(new Runnable() {
	                    public void run() {
	                    	ShuffleCard();
	                    }
	            }, secondsDelayed * 500);    			
    		}
			
		}
		
	}

}