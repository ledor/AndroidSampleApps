package com.ledor.shufflecard;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends ListActivity {
    /**
     * This class describes an individual sample (the sample title, and the activity class that
     * demonstrates shuffling of cards).
     */
    private class ShuffleSample {
        private CharSequence title;
        private Class<? extends Activity> activityClass;

        public ShuffleSample(int titleResId, Class<? extends Activity> activityClass) {
            this.activityClass = activityClass;
            this.title = getResources().getString(titleResId);
        }

        @Override
        public String toString() {
            return title.toString();
        }
    }

    /**
     * The collection of all samples in the app. This gets instantiated in {@link
     * #onCreate(android.os.Bundle)} because the {@link ShuffleSample} constructor needs access to {@link
     * android.content.res.Resources}.
     */
    private static ShuffleSample[] mSamples;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Instantiate the list of samples.
        mSamples = new ShuffleSample[]{
    		new ShuffleSample(R.string.title_activity_cube_like_shuffle, CubeLikeShuffleActivity.class),
    		new ShuffleSample(R.string.title_shuffle_at_center, ShuffleAtCenterActivity.class),
    		new ShuffleSample(R.string.title_random_shuffle, RandomShuffleActivity.class),
    		new ShuffleSample(R.string.title_blinking_shuffle, BlinkingShuffleActivity.class),
        };

	}
	
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        // Launch the sample associated with this list position.
        startActivity(new Intent(MainActivity.this, mSamples[position].activityClass));
    }
	

}