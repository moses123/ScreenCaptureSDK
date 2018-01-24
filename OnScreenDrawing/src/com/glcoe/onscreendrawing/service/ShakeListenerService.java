package com.glcoe.onscreendrawing.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.glcoe.onscreendrawing.FingerPaint;
import com.glcoe.onscreendrawing.ScreenCapture;
import com.glcoe.onscreendrawing.ShakeListener;

public class ShakeListenerService extends Service implements OnTouchListener{

	private ShakeListener mShaker;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {

		super.onCreate();

		Toast.makeText(ShakeListenerService.this, "I am Listening Shake ... ",
				Toast.LENGTH_SHORT).show();

		final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		mShaker = new ShakeListener(this);
		mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
			public void onShake() {
				Log.v("ShakeListenerService","onShake ........");
				vibe.vibrate(100);
				Intent intent = new Intent(ShakeListenerService.this,
						FingerPaint.class);
				intent.putExtra("BitmapImage",
						ScreenCapture.takeCurrentScreenShot());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(ShakeListenerService.this, "Bye-Bye... ",
				Toast.LENGTH_SHORT).show();
		if (mShaker != null) {
			mShaker.setOnShakeListener(null);
			mShaker = null;
		}

	}

	public void stopService() {
		Toast.makeText(ShakeListenerService.this, "Bye-Bye... ",
				Toast.LENGTH_SHORT).show();
		if (mShaker != null) {
			mShaker.setOnShakeListener(null);
			mShaker = null;
		}
		stopSelf();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
