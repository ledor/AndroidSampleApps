package com.ledor.shufflecard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.graphics.Point;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CubeLikeShuffleActivity extends Activity implements OnClickListener {
    /**
     * This class will shuffle cards like a Rubic's Cube.
     * This will randomly rotate the cards one row or one column at a time
     */

	public static final int ROTATEROW = 0;
	public static final int ROTATECOLUMN = 1;
	
	public static final int ROTATERIGHT = 1;
	public static final int ROTATELEFT = -1;
	public static final int ROTATEDOWN = 1;
	public static final int ROTATEUP = -1;

	int mRowDirection[] = {ROTATERIGHT,ROTATELEFT};
	int mColumnDirection[] = {ROTATEDOWN,ROTATEUP};
	
	/**
	 * Handle for GridView where the cards are being displayed
	 */
	GridView mGridView;
	
	/**
	 * Handle for invisible LinearLayout that represents one row or column of the GridView.
	 * This will be shown during animation
	 */
	LinearLayout mLinearRow;
	LinearLayout mLinearCol;

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
	 * Rotate Row
	 * If true row has been rotated
	 * If false column has been rotated
	 */
	boolean mIsRow;

	/**
	 * Row or Column Index
	 */
	int mIndex;
	
	/**
	 * Row or Column Index
	 */
	int mDirection;

	/**
	 * Animation status
	 */
	boolean mIsAnimationOn = false;
	
	/**
	 * List of coordinates of all ImageViews inside GridViews
	 */
	List<Point> mViewCoord;

	/**
	 * Handles for 4 invisible views
	 */
	private int mIViewsIDs[] = {
		R.id.image1,
		R.id.image2,
		R.id.image3,
		R.id.image4,
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cube_like_shuffle);
		
		mGridAdapter = new ShuffleGridAdapter(CubeLikeShuffleActivity.this);

		mGridView = (GridView) findViewById(R.id.myGrid);
        mGridView.setAdapter(mGridAdapter);
                
        mShuffle = (Button) findViewById(R.id.shuffle);
        mShuffle.setOnClickListener(this);

        mFlip = (Button) findViewById(R.id.flipcard);
        mFlip.setOnClickListener(this);
        
        mViewCoord = new ArrayList<Point>();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId())
		{
			case R.id.shuffle:
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
			case R.id.flipcard:
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
	 * Shuffles the cards like rotating the Rubic's Cube randomly
	 *
	 * @param void
	 * @return void
	 */
	private void ShuffleCard() {
		// Random row(0) or col(1)
		Random rand=new Random();
		int rowOrCol = rand.nextInt(2);
		mIndex = rand.nextInt(3);
		if (rowOrCol == ROTATEROW) {
			mIsRow = true;
			mDirection = mRowDirection[rand.nextInt(2)];;
			RotateRow(mIndex, mDirection);
		} else { //If Column
			mIsRow = false;
			mDirection = mColumnDirection[rand.nextInt(2)];;
			RotateColumn(mIndex, mDirection);
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
	 * RotateRow
	 *
	 * Shuffles the cards like rotating the Rubic's Cube randomly
	 *
	 * @param rowNum row index to be rotate
	 * @param direction direction where the images move
	 * @return void
	 */
	private void RotateRow(int rowNum, int direction) {
		
		// Prepare the params layout patterned to first view of GridView
		ImageView iView = (ImageView) mGridView.getChildAt(0);
		RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(iView.getWidth(),iView.getHeight());

		// Get the Horizontal distance between two Views in Grid
		int xDistance = mViewCoord.get(1).x - mViewCoord.get(0).x;

		// Prepare the View to be animated
		int gridIndex = rowNum * 3;
		int index = 0;
		if (direction == ROTATERIGHT) {
			// First View should be beyond the GridView's boundaries
			iView = (ImageView) findViewById(mIViewsIDs[index]);
			iView.setAdjustViewBounds(false);
			iView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			iView.setLayoutParams(lparams);
			if (mGridAdapter.isFaceUp) {
				iView.setImageResource(mGridAdapter.mResID.get(gridIndex+2));				
			} else {
				iView.setImageResource(R.drawable.back);								
			}
			iView.setX(mViewCoord.get(gridIndex).x - xDistance);
			iView.setY(mViewCoord.get(gridIndex).y);
			iView.setVisibility(View.VISIBLE);
			iView.bringToFront();
			
			TranslateAnimation animation = new TranslateAnimation(0,xDistance*direction, 0, 0);
			animation.setDuration(1000);
			animation.setFillAfter(false);
			animation.setAnimationListener( new CubeLikeAnimationListener() );
			
			iView.setAnimation(animation);
			index++;
		}
		
		for (int i=gridIndex; i<gridIndex+3; i++) {
			iView = (ImageView) findViewById(mIViewsIDs[index]);
			iView.setAdjustViewBounds(false);
			iView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			iView.setLayoutParams(lparams);
			if (mGridAdapter.isFaceUp) {
				iView.setImageResource(mGridAdapter.mResID.get(i));
			} else {
				iView.setImageResource(R.drawable.back);								
			}
			iView.setX(mViewCoord.get(i).x);
			iView.setY(mViewCoord.get(i).y);
			iView.setVisibility(View.VISIBLE);
			mGridView.getChildAt(i).setVisibility(View.INVISIBLE);
			iView.bringToFront();
			
			TranslateAnimation animation = new TranslateAnimation(0,xDistance*direction, 0, 0);
			animation.setDuration(1000);
			animation.setFillAfter(false);
			
			iView.setAnimation(animation);
			index++;
		}
		
		if (direction == ROTATELEFT) {
			// Last View should be beyond the GridView's boundaries
			iView = (ImageView) findViewById(mIViewsIDs[index]);
			iView.setAdjustViewBounds(false);
			iView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			iView.setLayoutParams(lparams);
			if (mGridAdapter.isFaceUp) {
				iView.setImageResource(mGridAdapter.mResID.get(gridIndex));
			} else {
				iView.setImageResource(R.drawable.back);								
			}
			iView.setX(mViewCoord.get(gridIndex+2).x + xDistance);
			iView.setY(mViewCoord.get(gridIndex+2).y);
			iView.setVisibility(View.VISIBLE);
			iView.bringToFront();
			
			TranslateAnimation animation = new TranslateAnimation(0,xDistance*direction, 0, 0);
			animation.setDuration(1000);
			animation.setFillAfter(false);
			animation.setAnimationListener( new CubeLikeAnimationListener() );
			
			iView.setAnimation(animation);
		}
		
		mGridView.startLayoutAnimation();
		
	}

	/**
	 * RotateColum
	 *
	 * Shuffles the cards like rotating the Rubic's Cube randomly
	 *
	 * @param colNum column index to be rotate
	 * @param direction direction where the images move
	 * @return void
	 */
	private void RotateColumn(int colNum, int direction) {
		// Prepare the params layout patterned to first view of GridView
		ImageView iView = (ImageView) mGridView.getChildAt(0);
		RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(iView.getWidth(),iView.getHeight());

		// Get the Horizontal distance between two Views in Grid
		int yDistance = mViewCoord.get(3).y - mViewCoord.get(0).y;

		// Prepare the View to be animated
		int gridIndex = colNum;
		int index = 0;
		
		if (direction == ROTATEDOWN) {
			// First View should be beyond the GridView's boundaries
			iView = (ImageView) findViewById(mIViewsIDs[index]);
			iView.setAdjustViewBounds(false);
			iView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			iView.setLayoutParams(lparams);
			if (mGridAdapter.isFaceUp) {
				iView.setImageResource(mGridAdapter.mResID.get(gridIndex+6));				
			} else {
				iView.setImageResource(R.drawable.back);								
			}
			iView.setX(mViewCoord.get(gridIndex).x);
			iView.setY(mViewCoord.get(gridIndex).y - yDistance);
			iView.setVisibility(View.VISIBLE);
			iView.bringToFront();
			
			TranslateAnimation animation = new TranslateAnimation(0,0, 0, yDistance*direction);
			animation.setDuration(1000);
			animation.setFillAfter(false);
			animation.setAnimationListener( new CubeLikeAnimationListener() );
			
			iView.setAnimation(animation);
			index++;
		}
		
		for (int i=0; i<0+3; i++) {
			gridIndex = colNum+(i*3);
			iView = (ImageView) findViewById(mIViewsIDs[index]);
			iView.setAdjustViewBounds(false);
			iView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			iView.setLayoutParams(lparams);
			if (mGridAdapter.isFaceUp) {
				iView.setImageResource(mGridAdapter.mResID.get(gridIndex));
			} else {
				iView.setImageResource(R.drawable.back);								
			}
			iView.setX(mViewCoord.get(gridIndex).x);
			iView.setY(mViewCoord.get(gridIndex).y);
			iView.setVisibility(View.VISIBLE);
			mGridView.getChildAt(gridIndex).setVisibility(View.INVISIBLE);
			iView.bringToFront();
			
			TranslateAnimation animation = new TranslateAnimation(0,0, 0, yDistance*direction);
			animation.setDuration(1000);
			animation.setFillAfter(false);
			
			iView.setAnimation(animation);
			index++;
		}
		
		if (direction == ROTATEUP) {
			// Last View should be beyond the GridView's boundaries
			iView = (ImageView) findViewById(mIViewsIDs[index]);
			iView.setAdjustViewBounds(false);
			iView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			iView.setLayoutParams(lparams);
			if (mGridAdapter.isFaceUp) {
				iView.setImageResource(mGridAdapter.mResID.get(colNum));
			} else {
				iView.setImageResource(R.drawable.back);								
			}
			iView.setX(mViewCoord.get(colNum+6).x);
			iView.setY(mViewCoord.get(colNum+6).y + yDistance);
			iView.setVisibility(View.VISIBLE);
			iView.bringToFront();
			
			TranslateAnimation animation = new TranslateAnimation(0,0, 0, yDistance*direction);
			animation.setDuration(1000);
			animation.setFillAfter(false);
			animation.setAnimationListener( new CubeLikeAnimationListener() );
			
			iView.setAnimation(animation);
		}
		
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

	private class CubeLikeAnimationListener implements AnimationListener {
	    /**
	     * Animation Listener for this activity
	     */

        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationRepeat(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
    	
    	    if (mIsRow) {
            	// Start switching images
            	int gridIndex = mIndex * 3;
            	if (mDirection == ROTATELEFT) {
        			// Set the Image ResIDs to its proper places
        	        int firstCardResID = mGridAdapter.mResID.get(gridIndex);
        	        int secondCardResID = mGridAdapter.mResID.get(gridIndex+1);
        	        int thirdCardResID = mGridAdapter.mResID.get(gridIndex+2);
        			mGridAdapter.mResID.set(gridIndex,secondCardResID);			
        			mGridAdapter.mResID.set(gridIndex+1,thirdCardResID);
        			mGridAdapter.mResID.set(gridIndex+2,firstCardResID);          		
            	} else {
        	        int firstCardResID = mGridAdapter.mResID.get(gridIndex);
        	        int secondCardResID = mGridAdapter.mResID.get(gridIndex+1);
        	        int thirdCardResID = mGridAdapter.mResID.get(gridIndex+2);
        			mGridAdapter.mResID.set(gridIndex,thirdCardResID);			
        			mGridAdapter.mResID.set(gridIndex+1,firstCardResID);
        			mGridAdapter.mResID.set(gridIndex+2,secondCardResID);
            	}

            	// Start showing images
            	int index = 0;
        		for (int x=gridIndex; x<gridIndex+3; x++) {
        			//Show items of our GridView
        			ImageView iView = (ImageView) mGridView.getChildAt(x);
        			iView.setVisibility(View.VISIBLE);
        			
        			iView = (ImageView) findViewById(mIViewsIDs[index]);
        			iView.setVisibility(View.GONE);
        			index++;	
        		}
        		
        		ImageView iView = (ImageView) findViewById(mIViewsIDs[index]);
    			iView.setVisibility(View.GONE);        		
    	    } else {          	            	
	        	// Start switching images
	        	int gridIndex = mIndex;
	        	if (mDirection == ROTATEDOWN) {
	    			// Set the Image ResIDs to its proper places
	    	        int firstCardResID = mGridAdapter.mResID.get(gridIndex);
	    	        int secondCardResID = mGridAdapter.mResID.get(gridIndex+3);
	    	        int thirdCardResID = mGridAdapter.mResID.get(gridIndex+6);
	    			mGridAdapter.mResID.set(gridIndex,thirdCardResID);			
	    			mGridAdapter.mResID.set(gridIndex+3,firstCardResID);
	    			mGridAdapter.mResID.set(gridIndex+6,secondCardResID);          		
	        	} else {
	    	        int firstCardResID = mGridAdapter.mResID.get(gridIndex);
	    	        int secondCardResID = mGridAdapter.mResID.get(gridIndex+3);
	    	        int thirdCardResID = mGridAdapter.mResID.get(gridIndex+6);
	    			mGridAdapter.mResID.set(gridIndex,secondCardResID);			
	    			mGridAdapter.mResID.set(gridIndex+3,thirdCardResID);
	    			mGridAdapter.mResID.set(gridIndex+6,firstCardResID);
	        	}
	
	        	// Start showing images
	    		int index = 0;
	    		for (int i=0; i<3; i++) {
	    			gridIndex = mIndex+(i*3);
	
	    			//Show items of our GridView
	    			ImageView iView = (ImageView) mGridView.getChildAt(gridIndex);
	    			iView.setVisibility(View.VISIBLE); 
	    			
	    			iView = (ImageView) findViewById(mIViewsIDs[i]);
	    			iView.setVisibility(View.GONE);
	    			index++;
	    		}
	    		
	    		ImageView iView = (ImageView) findViewById(mIViewsIDs[index]);
				iView.setVisibility(View.GONE);        		
    	    }
        	
    	    mGridAdapter.notifyDataSetChanged();

    	    if (mIsAnimationOn) {
				int secondsDelayed = 1;
	            new Handler().postDelayed(new Runnable() {
	                    public void run() {
	                    	ShuffleCard();
	                    }
	            }, secondsDelayed * 250);    			
    		}
    		
    	}
    }
}

