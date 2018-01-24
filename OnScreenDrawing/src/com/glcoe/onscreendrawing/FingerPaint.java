/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.glcoe.onscreendrawing;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FingerPaint extends GraphicsActivity implements
		ColorPickerDialog.OnColorChangedListener {

	private RelativeLayout controlsLayout;
	private Bitmap mMainBitmap;
	private String mSavedPath;
	private Animation animationSlideInLeft;
	private Animation animationSlideOutRight;
	private ArrayList<String> mRecipentList;
	private AsyncTask<Void, Void, Void> mSaveAsyncTask;
	private Paint mPaint;
	private MaskFilter mEmboss;
	private MaskFilter mBlur;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.paint_layout);
		FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
		byte[] fileName = getIntent().getByteArrayExtra("BitmapImage");
		mMainBitmap = BitmapFactory.decodeByteArray(fileName, 0,
				fileName.length);
		Drawable d = new BitmapDrawable(mMainBitmap);
		frame.setBackgroundDrawable(d);

		controlsLayout = (RelativeLayout) findViewById(R.id.actionControls);
		animationSlideInLeft = AnimationUtils.loadAnimation(this,
				R.anim.anim_right_to_left);
		animationSlideOutRight = AnimationUtils.loadAnimation(this,
				R.anim.anim_left_to_right);
		showActionButtonBar();
		ImageView chooseColorIcon = (ImageView) findViewById(R.id.choose_color);
		ImageView embossIcon = (ImageView) findViewById(R.id.emboss);
		ImageView blurIcon = (ImageView) findViewById(R.id.blur);
		ImageView eraseIcon = (ImageView) findViewById(R.id.erase);
		ImageView captureIcon = (ImageView) findViewById(R.id.capture);
		ImageView closeIcon = (ImageView) findViewById(R.id.closeBtn);
		ImageView share = (ImageView) findViewById(R.id.share);
		ImageView gallery = (ImageView) findViewById(R.id.gallery);

		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSaveAsyncTask != null
						&& mSaveAsyncTask.getStatus() == Status.RUNNING) {
					mSaveAsyncTask.cancel(true);
					mSaveAsyncTask = null;
				}
				mSaveAsyncTask = new ScreenSaveTask();
				mSaveAsyncTask.execute();

			}
		});

		gallery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideActionButtonBar();
				Intent LaunchIntent = new Intent(FingerPaint.this,
						ImageGallery.class);
				LaunchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(LaunchIntent);

			}
		});

		chooseColorIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
				new ColorPickerDialog(FingerPaint.this, FingerPaint.this,
						mPaint.getColor()).show();
				hideActionButtonBar();
			}
		});

		embossIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPaint.getMaskFilter() != mEmboss) {
					mPaint.setMaskFilter(mEmboss);
				} else {
					mPaint.setMaskFilter(null);
				}
				hideActionButtonBar();
			}
		});

		blurIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPaint.getMaskFilter() != mBlur) {
					mPaint.setMaskFilter(mBlur);
				} else {
					mPaint.setMaskFilter(null);
				}
				hideActionButtonBar();
			}
		});

		eraseIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				hideActionButtonBar();
			}
		});

		captureIcon.setOnClickListener(new OnClickListener() {
			Boolean isSDPresent = android.os.Environment
					.getExternalStorageState().equals(
							android.os.Environment.MEDIA_MOUNTED);

			@Override
			public void onClick(View v) {
				if (isSDPresent) {
					controlsLayout.setVisibility(View.GONE);
					mSavedPath = ScreenCapture.saveCapture(FingerPaint.this);
					Toast.makeText(FingerPaint.this,
							"Saved at : " + mSavedPath, Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(FingerPaint.this,
							"No SD Card to save the image", Toast.LENGTH_SHORT)
							.show();
				}
			}

		});

		closeIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				// ScreenCapture.clean();
			}
		});

		frame.addView(new MyView(this));

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		// mPaint.setColor(0xFFFF0000);
		mPaint.setColor(0xFF00FF00);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);

		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);

		mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public void colorChanged(int color) {
		mPaint.setColor(color);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1) {

			if (resultCode == RESULT_OK) {
				mRecipentList = data.getStringArrayListExtra("LIST");
				System.out.println("FingerPaint.onActivityResult()"
						+ mRecipentList.toString());
				sendMail("gmail");
			}
			if (resultCode == RESULT_CANCELED) {
			}
		}
	}

	/**
	 * Method to launch mail sending module.
	 * 
	 * @param type
	 */
	private void sendMail(String type) {
		int size = mRecipentList.size();
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("image/jpeg");
		List<ResolveInfo> resolveInfo = getPackageManager()
				.queryIntentActivities(share, 0);
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < size; i++) {
			if (i == (size) - 1) {
				buffer.append(mRecipentList.get(i));
			} else {
				buffer.append(mRecipentList.get(i));
				buffer.append(",");
			}
		}
		String[] mAddress = { buffer.toString() };
		if (!resolveInfo.isEmpty()) {
			for (ResolveInfo info : resolveInfo) {
				if (info.activityInfo.packageName.toLowerCase().contains(type)
						|| info.activityInfo.name.toLowerCase().contains(type)) {
					share.putExtra(android.content.Intent.EXTRA_EMAIL, mAddress);
					share.putExtra(Intent.EXTRA_STREAM,
							Uri.parse("file://" + mSavedPath));
					share.setPackage(info.activityInfo.packageName);
					break;
				}

			}
		}

		startActivity(Intent.createChooser(share, "Select"));
	}

	public class MyView extends View {

		// private static final float MINP = 0.25f;
		// private static final float MAXP = 0.75f;
		// private static final int ICON_WIDTH = 32;
		// private static final int ICON_HEIGHT = 32;

		private Bitmap mBitmap;
		private Canvas mCanvas;
		private Path mPath;
		private Paint mBitmapPaint;

		// private Bitmap mCloseButton;
		// private Bitmap mCaptureButton;

		public MyView(Context c) {
			super(c);

			mPath = new Path();
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);
			// mCloseButton = BitmapFactory.decodeResource(getResources(),
			// R.drawable.close);
			// mCaptureButton = BitmapFactory.decodeResource(getResources(),
			// R.drawable.capture);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

			// Bitmap tempBitmap = mMainBitmap.copy(Bitmap.Config.ARGB_8888,
			// true);
			// mBitmap = tempBitmap;
			mCanvas = new Canvas(mBitmap);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// canvas.drawColor(0xFFAAAAAA);
			canvas.drawColor(0x00000000);

			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

			canvas.drawPath(mPath, mPaint);

			// canvas.drawBitmap(mCloseButton, getWidth() - ICON_WIDTH ,
			// getHeight() - ICON_HEIGHT , null);
			//
			// canvas.drawBitmap(mCaptureButton, getWidth() - ICON_WIDTH ,
			// getHeight() - ICON_HEIGHT * 2 - 10, null);

		}

		private float mX, mY;
		private static final float TOUCH_TOLERANCE = 4;

		private void touch_start(float x, float y) {
			mPath.reset();
			mPath.moveTo(x, y);
			mX = x;
			mY = y;
		}

		private void touch_move(float x, float y) {
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;
			}
		}

		private void touch_up() {
			mPath.lineTo(mX, mY);
			// commit the path to our offscreen
			mCanvas.drawPath(mPath, mPaint);
			// kill this so we don't double draw
			mPath.reset();
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				// if ( x > getWidth() - ICON_WIDTH && x < getWidth() &&
				// y > getHeight() - ICON_WIDTH && y < getHeight()) {
				// //Close icon
				// finish();
				// }

				if (Math.abs(mX - x) < 5 && Math.abs(mY - y) < 5) {
					if (controlsLayout.getVisibility() == View.VISIBLE) {
						hideActionButtonBar();
					} else {
						showActionButtonBar();
						controlsLayout.requestFocus();
						controlsLayout.bringToFront();
					}
				}

				touch_up();
				invalidate();
				break;
			}
			return true;
		}
	}

	private void hideActionButtonBar() {
		controlsLayout.startAnimation(animationSlideOutRight);
		controlsLayout.setVisibility(View.GONE);
	}

	private void showActionButtonBar() {
		controlsLayout.setVisibility(View.VISIBLE);
		controlsLayout.startAnimation(animationSlideInLeft);
	}

	private static final int COLOR_MENU_ID = Menu.FIRST;
	private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
	private static final int BLUR_MENU_ID = Menu.FIRST + 2;
	private static final int ERASE_MENU_ID = Menu.FIRST + 3;
	private static final int SRCATOP_MENU_ID = Menu.FIRST + 4;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
		menu.add(0, EMBOSS_MENU_ID, 0, "Emboss").setShortcut('4', 's');
		menu.add(0, BLUR_MENU_ID, 0, "Blur").setShortcut('5', 'z');
		menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
		menu.add(0, SRCATOP_MENU_ID, 0, "SrcATop").setShortcut('5', 'z');

		/****
		 * Is this the mechanism to extend with filter effects? Intent intent =
		 * new Intent(null, getIntent().getData());
		 * intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		 * menu.addIntentOptions( Menu.ALTERNATIVE, 0, new ComponentName(this,
		 * NotesList.class), null, intent, 0, null);
		 *****/
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mPaint.setXfermode(null);
		mPaint.setAlpha(0xFF);

		switch (item.getItemId()) {
		case COLOR_MENU_ID:
			new ColorPickerDialog(this, this, mPaint.getColor()).show();
			return true;
		case EMBOSS_MENU_ID:
			if (mPaint.getMaskFilter() != mEmboss) {
				mPaint.setMaskFilter(mEmboss);
			} else {
				mPaint.setMaskFilter(null);
			}
			return true;
		case BLUR_MENU_ID:
			if (mPaint.getMaskFilter() != mBlur) {
				mPaint.setMaskFilter(mBlur);
			} else {
				mPaint.setMaskFilter(null);
			}
			return true;
		case ERASE_MENU_ID:
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			return true;
		case SRCATOP_MENU_ID:
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
			mPaint.setAlpha(0x80);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mSaveAsyncTask != null
				&& mSaveAsyncTask.getStatus() == Status.RUNNING) {
			mSaveAsyncTask.cancel(true);
			mSaveAsyncTask = null;
		}
	}

	/**
	 * task to save screen and provide with recipient option at the same time.
	 * 
	 * @author Moses.Kesavan
	 * 
	 */
	private class ScreenSaveTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog progressBar = new ProgressDialog(FingerPaint.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setMessage(getString(R.string.saving_image));
			progressBar.show();
			hideActionButtonBar();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mSavedPath = ScreenCapture.saveCapture(FingerPaint.this);

				}
			});
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressBar.dismiss();
			if (mSavedPath != null && !mSavedPath.equals("")) {
				Intent intent = new Intent(FingerPaint.this,
						MailingActivity.class);
				startActivityForResult(intent, 1);
			} else {
				Toast.makeText(FingerPaint.this, getString(R.string.fail_msg),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
