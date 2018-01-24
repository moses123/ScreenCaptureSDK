package com.glcoe.onscreendrawing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.glcoe.onscreendrawing.service.ShakeListenerService;

public class ScreenCapture {

	private static final String TAG = ScreenCapture.class.getSimpleName();

	private static Context mContext;

	private static int count = 1;

	private static volatile TimerTask mTimeTask;
	private static volatile Timer timer;

	private static final int INITIAL_DELAY = 2000;
	private static final int REPEATING_INTERVAL = 10000;

	public static void allowCapture(Context context) {
		mContext = context;
	}

	public static void init(Context context) {
		mContext = context;
		getFileCount();
		Intent intent = new Intent(context, ShakeListenerService.class);
		context.startService(intent);

	}

	private static void getFileCount() {

		File dir = new File(Environment.getExternalStorageDirectory().getPath()
				+ File.separator + "CapturedImages");
		File[] files = dir.listFiles();
		if (files != null && files.length > 0) {
			count = files.length;
		} else {
			count = 1;
		}

	}

	public static void clean() {

		Intent intent = new Intent(mContext, ShakeListenerService.class);
		mContext.stopService(intent);

	}

	public static byte[] screenCapture(Context context) {

		Bitmap bitmap;
		View v1 = ((Activity) context).findViewById(android.R.id.content)
				.getRootView(); // Not working
		v1.setDrawingCacheEnabled(true);
		// v1.layout(0, 0, v1.getMeasuredWidth(), v1.getMeasuredHeight());
		v1.buildDrawingCache();
		bitmap = Bitmap.createBitmap(v1.getDrawingCache());
		v1.setDrawingCacheEnabled(false);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;

	}

	public static String saveCapture(Context context) {

		Bitmap bitmap;

		View v1 = ((Activity) context).findViewById(android.R.id.content)
				.getRootView(); // Not working
		v1.setDrawingCacheEnabled(true);
		bitmap = Bitmap.createBitmap(v1.getDrawingCache());
		v1.setDrawingCacheEnabled(false);

		OutputStream fout = null;
		File imageFileDir = new File(Environment.getExternalStorageDirectory()
				.getPath() + File.separator + "CapturedImages");
		if (!imageFileDir.exists()) {
			imageFileDir.mkdir();
		}

		String path = "screen" + count++ + ".jpg";
		File imageFile = new File(imageFileDir, path);
		try {
			fout = new FileOutputStream(imageFile);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
			fout.flush();
			fout.close();
			Log.i(TAG, "Captured at :" + path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Environment.getExternalStorageDirectory().getPath()
				+ File.separator + "CapturedImages" + File.separator + path;

	}

	public static byte[] takeCurrentScreenShot() {
		return screenCapture(mContext);
	}

	public static void schedulePeriodicCapture(Context context) {
		mContext = context;
		if (mTimeTask == null) {
			mTimeTask = new TimerTask() {
				@Override
				public void run() {

					if (mContext != null) {

						screenCapture(mContext);
					}
				}
			};
			timer = new Timer();
			timer.schedule(mTimeTask, INITIAL_DELAY, REPEATING_INTERVAL);
		}
	}
}
