package com.ledor.shufflecard;

import java.util.Collections;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.GridLayoutAnimationController;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class BlinkingShuffleActivity extends Activity implements OnClickListener {
    /**
     * This class will shuffle all the cards by randomly selecting images in one View 
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
	 * Animation status
	 */
	boolean mIsListenerSet = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blinking_shuffle);

		mGridAdapter = new ShuffleGridAdapter(BlinkingShuffleActivity.this);

		mGridView = (GridView) findViewById(R.id.myGrid4);
        mGridView.setAdapter(mGridAdapter);
                       
        mShuffle = (Button) findViewById(R.id.shuffle4);
        mShuffle.setOnClickListener(this);

        mFlip = (Button) findViewById(R.id.flipcard4);
        mFlip.setOnClickListener(this);

        // Set listener to our animation
        BlinkingAnimationListener listener = new BlinkingAnimationListener();
		GridLayoutAnimationController animLayout = (GridLayoutAnimationController) mGridView.getLayoutAnimation();
		AlphaAnimation animation = (AlphaAnimation) animLayout.getAnimation();
		animation.setAnimationListener(listener);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId())
		{
			case R.id.shuffle4:
				if (mIsAnimationOn) {
					mIsAnimationOn = false;
					mShuffle.setText(R.string.app_name);
					mFlip.setEnabled(true);
				} else {
					mIsAnimationOn = true;
					mShuffle.setText(R.string.stop);
					mFlip.setEnabled(false);
					ShuffleCard();
				}
				break;
			case R.id.flipcard4:
				mShuffle.setEnabled(false);
				mFlip.setEnabled(false);
				FlipCard();
				break;
			default:
				break;
		}
		
	}
	
	/**
	 * ShuffleCard
	 *
	 * Shuffles the cards selecting images in each view
	 *
	 * @param void
	 * @return void
	 */
	private void ShuffleCard() {
		
		mIsListenerSet = true;
		// Since animation is already embedded in its xml, we just need to call the start animation
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

	private class BlinkingAnimationListener implements AnimationListener {
	    /**
	     * Animation Listener for this activity
	     */
		
		/**
		 * Counter
		 */
		int mCounter = 0;
	    
	    @Override
		public void onAnimationStart(Animation animation) {
	    	// Cancel the animation on first load
			if (!mIsListenerSet) {
				animation.cancel();
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			
			// Shuffle the images only when the alpha is zero
	    	if ( mCounter%2 == 0 ) {
	    		Collections.shuffle(mGridAdapter.mResID);
	    		mGridAdapter.notifyDataSetChanged();
	    	}
	    	mCounter = mCounter==1 ? 2 : 1;
	    	
			if (!mIsAnimationOn) {
				animation.cancel();
			}
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			mGridAdapter.notifyDataSetChanged();			
		}
		
	}

}

