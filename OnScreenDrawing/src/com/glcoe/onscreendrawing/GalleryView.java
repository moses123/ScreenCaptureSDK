package com.glcoe.onscreendrawing;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

/**
 * 
 * @author Moses.Kesavan
 * 
 */
public class GalleryView extends Activity implements
		AdapterView.OnItemSelectedListener, ViewFactory {
	private ImageSwitcher mSwitcher;
	private String[] mFileStrings;
	private File[] listFile;
	private ArrayList<String> mImageList;
	private TextView mNoImageText;
	private Gallery g;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gallery);
		mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
		mSwitcher.setFactory(this);
		mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));
		g = (Gallery) findViewById(R.id.gallery);
		mNoImageText = (TextView) findViewById(R.id.no_image_text);
		Button button = (Button) findViewById(R.id.back_button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		getList();
		g.setAdapter(new ImageAdapter(this));
		g.setOnItemSelectedListener(this);
	}

	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		Bitmap bitmap = BitmapFactory.decodeFile(mImageList.get(position));
		Drawable drawable = new BitmapDrawable(bitmap);
		mSwitcher.setImageDrawable(drawable);
	}

	public void onNothingSelected(AdapterView<?> parent) {
	}

	public View makeView() {
		ImageView i = new ImageView(this);
		i.setBackgroundColor(0xFF000000);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return i;
	}

	public class ImageAdapter extends BaseAdapter {
		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			if (mImageList != null && mImageList.size() > 0) {
				return mImageList.size();
			}
			return 0;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		private Context mContext;

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			Bitmap bitmap = BitmapFactory.decodeFile(mImageList.get(position));
			i.setImageBitmap(bitmap);
			i.setAdjustViewBounds(true);
			i.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			return i;
		}

	}

	private void getList() {
		mImageList = new ArrayList<String>();
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + "CapturedImages");

		if (file.isDirectory()) {
			listFile = file.listFiles();
			mFileStrings = new String[listFile.length];
			for (int i = 0; i < listFile.length; i++) {
				if (listFile[i].getName().contains(".png")
						|| listFile[i].getName().contains(".jpg")
						|| listFile[i].getName().contains(".PNG")) {
					mFileStrings[i] = listFile[i].getAbsolutePath();
				}
			}
		}
		createImageList(mFileStrings);
	}

	private void createImageList(String[] strings) {
		if (strings != null) {
			for (int i = 0; i < strings.length; i++) {
				if (strings[i] != null) {
					mImageList.add(strings[i]);
				}

			}
		} else {
			mNoImageText.setVisibility(View.VISIBLE);
			g.setVisibility(View.GONE);
			mSwitcher.setVisibility(View.GONE);

		}
	}

}
