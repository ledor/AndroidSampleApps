package com.ledor.asyncprocess;

import com.ledor.asyncprocess.sampleactivities.*;
import com.ledor.asyncprocess.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ListActivity {

    private static final String[][] TEXT = {	{"Simple Async Process","Use Thread, Handler and ProcessDialog to show the async process."},
    											{"Async Http Request","Use AsyncTask, and Tweeter Search API(JSON)."},
    											{"Async Image Download","Use AysncTask, Thread, and Tweeter Seach API. This time with User Avatar downloaded asynchronously."},
    										};
    
    ListView mListView;
    private static AsyncProcessSample[] mSamples;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setListAdapter(new SimpleListAdapter(this));
		
		
        mSamples = new AsyncProcessSample[]{
        		new AsyncProcessSample(R.string.title_activity_simple_async_proceess, SimpleAsyncProceess.class),
        		new AsyncProcessSample(R.string.title_activity_async_http_request, AsyncHttpRequest.class),
        		new AsyncProcessSample(R.string.title_activity_async_download_image, AsyncDownloadImage.class),
            };
	}
	
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        // Launch the sample associated with this list position.
        startActivity(new Intent(MainActivity.this, mSamples[position].activityClass));
    }

    private static class SimpleListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public SimpleListAdapter(Context context) {
             mInflater = LayoutInflater.from(context);
        }

		@Override
        public int getCount() {
            return TEXT.length;
        }

 		@Override
        public Object getItem(int position) {
            return position;
        }

		@Override
        public long getItemId(int position) {
            return position;
        }


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_list, null);

                holder = new ViewHolder();
                holder.text1 = (TextView) convertView.findViewById(R.id.text1);
                holder.text2 = (TextView) convertView.findViewById(R.id.text2);

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.text1.setText(TEXT[position][0]);
            holder.text2.setText(TEXT[position][1]);

            return convertView;
		}
		
        static class ViewHolder {
            TextView text1;
            TextView text2;
        }
    }
    
    private class AsyncProcessSample {
        private CharSequence title;
        private Class<? extends Activity> activityClass;

        public AsyncProcessSample(int titleResId, Class<? extends Activity> activityClass) {
            this.activityClass = activityClass;
            this.title = getResources().getString(titleResId);
        }

        @Override
        public String toString() {
            return title.toString();
        }
    }

}
