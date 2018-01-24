package com.glcoe.onscreendrawing;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.glcoe.onscreendrawing.adapter.ImageShareAdapter;
import com.glcoe.onscreendrawing.model.ImageData;

/**
 * 
 * @author Moses.Kesavan
 * 
 */
public class SharingGallery extends Activity {

	private ListView mImageListView;
	private Button mShareButton;
	private File[] listFile;
	private ArrayList<ImageData> mSelectedImage;
	private ArrayList<ImageData> mImageDataList;
	private ImageShareAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sharing_gallery_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (savedInstanceState != null) {
			mImageDataList = savedInstanceState
					.getParcelableArrayList("NAME_LIST");
			mSelectedImage = savedInstanceState
					.getParcelableArrayList("PATH_LIST");
		}
		mImageListView = (ListView) findViewById(R.id.image_list);
		mShareButton = (Button) findViewById(R.id.button_share);
		mShareButton.setOnClickListener(mClickListener);
		mImageListView.setItemsCanFocus(true);
		adapter = new ImageShareAdapter(this);
		getList();
		mImageListView.setAdapter(adapter);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("PATH_LIST", mSelectedImage);
		outState.putParcelableArrayList("NAME_LIST", mImageDataList);
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mSelectedImage = adapter.getmSelectedImage();
			if (mSelectedImage == null || mSelectedImage.size() <= 0) {
				Toast.makeText(SharingGallery.this,
						"Please select an image to share", Toast.LENGTH_SHORT)
						.show();
			} else {
				ArrayList<Uri> uris = new ArrayList<Uri>();
				for (int i = 0; i < mSelectedImage.size(); i++) {
					String file = mSelectedImage.get(i).getImagePath();
					File fileIn = new File(file);
					Uri u = Uri.fromFile(fileIn);
					uris.add(u);
				}
				Intent shareIntent = new Intent(SharingGallery.this,
						MailingActivity.class);
				shareIntent.putExtra("Activity", "SharingGallery");
				shareIntent.putParcelableArrayListExtra("ImageList", uris);
				startActivity(shareIntent);
			}
		}
	};

	/**
	 * Method to get the list of image path and name from sd card.
	 */
	private void getList() {
		mImageDataList = new ArrayList<ImageData>();
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + "CapturedImages");

		if (file.isDirectory()) {
			ImageData imageData;
			listFile = file.listFiles();
			for (int i = 0; i < listFile.length; i++) {
				if (listFile[i].getName().contains(".png")
						|| listFile[i].getName().contains(".jpg")
						|| listFile[i].getName().contains(".PNG")) {
					imageData = new ImageData();
					imageData.setImageName(listFile[i].getName());
					imageData.setImagePath(listFile[i].getAbsolutePath());
					mImageDataList.add(imageData);
					adapter.setmImageDataList(mImageDataList);
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mImageListView = null;
		mShareButton = null;
		listFile = null;
		mSelectedImage = null;
	}

}
