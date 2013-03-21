/**
 * ListActivity                                       
 * Activity名： MainActivity                                  
 * 内容：　音楽ファイルを閲覧する画面の処理
 * @author	R.Casimina
 * @version	1.01
 * @since	2012.11.18    
 */

package com.ledor.listactivitysample;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class MainActivity extends ListActivity {

	/**
	 * Root Directory
	 */
	private String mRoot;

	/**
	 * Current Directory
	 */
	private String mCurrentDir;
	
	/**
	 * TextView to handle current folder name
	 */
	private TextView mTvFileDir;

	/**
	 * List of the folders and files
	 */
	private ArrayList<PlayerListItem> mFileList;

	/**
	 * List Adapter
	 */
	private PlayerListAdapter mPlayerAdapter;

	/**
	 * TextView to handle current folder name
	 */
	private ImageView mToggleViewIcon;

	/**
	 * List of the folder and file paths
	 */
	private ArrayList<String> mPathList;

	/**
	 * onCreate
	 *
	 * Initialize and create all class member
	 *
	 * @param	savedInstanceState Bundle
	 * @return	void
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		mRoot = Utils.ROOT_DIR;

		mCurrentDir = mRoot;
		
		mTvFileDir = (TextView)  findViewById(R.id.current_dir);

		mFileList = new ArrayList<PlayerListItem>();
		
		mPathList = new ArrayList<String>();

		mPlayerAdapter = new PlayerListAdapter(this, mFileList);
		
		mToggleViewIcon = (ImageView) findViewById(R.id.view_mode);

		mToggleViewIcon.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				onToggleViewMode();
			}
		});
		
		setListAdapter(mPlayerAdapter);

    }
    
	@Override
	public void onResume() {
		super.onResume();
		
		updateList(mCurrentDir);
				
	}
	
	@SuppressLint("ShowToast")
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		PlayerListItem item = mFileList.get(position);
		
		switch (item.getItemFiletype()) {
			case PlayerListItem.TYPE_FILE:
				Toast.makeText(this, item.getItemTitle(), Toast.LENGTH_SHORT).show();
				break;
			case PlayerListItem.TYPE_FOLDER:
			default:
				String dir = item.getItemFilePath();
				mCurrentDir = dir;
				updateList(dir);
				break;
		}
	}


	/**
	 * UpdateList
	 *
	 * Update the list on Browse Screen
	 * Use {@link #UpdateList(String)} update the list inside the Browse Screen
	 *<p>
	 * Set local variable dir to TextView for Current directory
	 * Clear the current content of the list
	 * Get the list of file from the current directory and assign it to class member mmp3List
	 * Notify the List adapter that the list has been updated, so that it will display our latest file list 
	 *
	 * @param dir String current directory to be displayed
	 * @return            void
	 */
	private void updateList(String dir){
	
		mTvFileDir.setText(dir);
		
		File currentDir = new File(dir);
		int fileindex = 0;
		
		if ( !currentDir.exists() ) {
			currentDir = new File(mRoot);
			dir = mRoot;
		}
		
		File[] list = currentDir.listFiles();

		if (!mFileList.isEmpty()) {
			mFileList.clear();
			mPathList.clear();
		}
		
		if(!dir.equals(mRoot)) {
			PlayerListItem item = new PlayerListItem(getApplicationContext(), dir, PlayerListItem.TYPE_PARENT);
			mFileList.add(item);
		}
		
		if ( list != null ) {
			Arrays.sort(list);
			for (int i=0; i < list.length; i++) {
				File file = list[i];
				
				if(file.isDirectory()) {
					PlayerListItem item = new PlayerListItem(getApplicationContext(), file.getPath(), PlayerListItem.TYPE_FOLDER);
					mFileList.add(item);
				} else {
					String name = file.getName();
					if ( name.toLowerCase(Locale.getDefault()).endsWith(Utils.MP3_EXTENSION) ) {
						PlayerListItem item = new PlayerListItem(getApplicationContext(), file.getAbsolutePath(), PlayerListItem.TYPE_FILE);
						item.setItemFileIndex(fileindex);
						fileindex++;
						mFileList.add(item);
						mPathList.add(item.getItemFilePath());
					}
				}
			}
		}
		mPlayerAdapter.notifyDataSetChanged();
	}
	
	/**
	 * onToggleViewMode
	 *
	 * Toggle the view mode of the ListView from RichView to SimpleView and vice versa 
	 * Called when Toggle View icon is clicked
	 *<p>
	 * Toggle the ViewMode between RichView and SimpleView
	 * Get the latest ViewMode and display the appropriate view icon
	 * if current mode is RichView, display simpleview icon
	 * if current mode is SimpleView, display richview icon
	 * Notify the List adapter that the view has been updated, so that it will display the latest view 
	 *
	 * @param	void 
	 * @return	void
	 */
	public void onToggleViewMode() {
		
		mPlayerAdapter.toggleViewMode();
		
		if (mPlayerAdapter.getViewMode() == PlayerListAdapter.RICHVIEW ) {
			mToggleViewIcon.setImageResource(R.drawable.simpleview);
		} else {
			mToggleViewIcon.setImageResource(R.drawable.richview);			
		}
		mPlayerAdapter.notifyDataSetChanged();
	}
	

}
