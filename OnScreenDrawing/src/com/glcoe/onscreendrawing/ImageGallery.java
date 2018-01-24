package com.glcoe.onscreendrawing;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * 
 * @author Moses.Kesavan
 * 
 */
public class ImageGallery extends Activity {

	private CustomImageGalery mGallery;
	private String[] mFileStrings;
	private File[] listFile;
	private ArrayList<String> mImageList;
	private Context mContext;
	private TextView emptyTextView;
	private MenuDialog customMenuDialog;
	private AsyncTask<Void, Void, Void> mOpenAsyncTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.image_gallery);
		getList();
		mGallery = (CustomImageGalery) findViewById(R.id.gallery);
		mGallery.setAdapter(new MyGalleryAdapter(this));
		mGallery.setOnItemLongClickListener(mLongClickListener);
		emptyTextView = (TextView) findViewById(R.id.empty_text);

	}

	private OnItemLongClickListener mLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			if (customMenuDialog == null) {
				customMenuDialog = new MenuDialog(ImageGallery.this);
			}
			customMenuDialog.show();
			return false;
		}
	};

	/**
	 * Adapter for the image list in the gallery.
	 * 
	 * @author Moses.Kesavan
	 * 
	 */
	public class MyGalleryAdapter extends BaseAdapter {

		public MyGalleryAdapter(Context c) {
			mContext = c;
		}

		@Override
		public int getCount() {
			return mImageList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(mContext);
			Bitmap bitmap = BitmapFactory.decodeFile(mImageList.get(position));
			imageView.setImageBitmap(bitmap);
			imageView.setAdjustViewBounds(true);
			imageView.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			return imageView;
		}
	}

	/**
	 * Method to get the list of images in the sd card.
	 */
	private void getList() {
		mImageList = new ArrayList<String>();
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + "CapturedImages");

		if (file.isDirectory()) {
			listFile = file.listFiles();
			mFileStrings = new String[listFile.length];
			if (listFile != null && listFile.length > 0) {
				for (int i = 0; i < listFile.length; i++) {
					if (listFile[i].getName().contains(".png")
							|| listFile[i].getName().contains(".jpg")
							|| listFile[i].getName().contains(".PNG")) {
						mFileStrings[i] = listFile[i].getAbsolutePath();
					}
				}
				createImageList(mFileStrings);
			} else {
				emptyTextView.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * method to build a list of available images.
	 * 
	 * @param strings
	 */
	private void createImageList(String[] strings) {
		for (int i = 0; i < strings.length; i++) {
			if (strings[i] != null) {
				mImageList.add(strings[i]);
			}

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mGallery = null;
		mFileStrings = null;
		listFile = null;
		mImageList = null;
		mContext = null;
	}

	/**
	 * Method to show option picker for the gallery.
	 * 
	 * @author Moses.Kesavan
	 * 
	 */
	private class MenuDialog extends AlertDialog {
		public MenuDialog(Context context) {
			super(context);
			setTitle(getString(R.string.multiple_share));
			View cus_menu = getLayoutInflater().inflate(
					R.layout.custom_menu_layout, null);
			ImageView shareButton = (ImageView) cus_menu
					.findViewById(R.id.shareBtn);
			shareButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mOpenAsyncTask != null
							&& mOpenAsyncTask.getStatus() == Status.RUNNING) {
						mOpenAsyncTask.cancel(true);
						mOpenAsyncTask = null;
					}
					mOpenAsyncTask = new OpenGalleryView();
					mOpenAsyncTask.execute();
				}
			});

			setView(cus_menu);
		}
	}

	/**
	 * task to open the sharing view .
	 * 
	 * @author Moses.Kesavan
	 * 
	 */
	private class OpenGalleryView extends AsyncTask<Void, Void, Void> {
		ProgressDialog progressBar = new ProgressDialog(ImageGallery.this);
		Intent mIntent;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setMessage(getString(R.string.progress));
			progressBar.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			mIntent = new Intent(ImageGallery.this, SharingGallery.class);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			customMenuDialog.dismiss();
			progressBar.dismiss();
			startActivity(mIntent);
		}
	}
}
