/**
 * 
 */
package com.glcoe.onscreendrawing.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * @author ravi.gu
 * 
 */
public class CopyDataBaseIntoSDCard {

	private static final String TAG = CopyDataBaseIntoSDCard.class
			.getSimpleName();
	private static String DATABASE_NAME;
	private Context mContext;

	/**
	 * 
	 */
	public CopyDataBaseIntoSDCard(Context context) {
		DATABASE_NAME = "MAILADDRESS";
		mContext = context;
		copy(DATABASE_NAME);
	}

	/**
	 * It copies database file from internal storage to external storage(SD
	 * card).
	 */
	public boolean copy() {
		return copy(DATABASE_NAME);
	}

	/**
	 * It copies database file from internal storage to external storage(SD
	 * card).
	 */
	@SuppressWarnings("resource")
	public boolean copy(String DATABASE_NAME) {
		boolean flage = false;
		FileChannel src = null;
		FileChannel dst = null;
		try {
			File dbFile = mContext.getDatabasePath(DATABASE_NAME);

			Log.i("Test", dbFile.getAbsolutePath());

			if (dbFile.exists()) {

				File sd = Environment.getExternalStorageDirectory();
				String state = Environment.getExternalStorageState();

				if (Environment.MEDIA_MOUNTED.equals(state)) {

					if (sd.canWrite()) {

						File backupDB = new File(sd, DATABASE_NAME);
						src = new FileInputStream(dbFile).getChannel();
						dst = new FileOutputStream(backupDB).getChannel();

						StatFs stats = new StatFs(sd.getAbsolutePath());
						int availableBytes = stats.getAvailableBlocks()
								* stats.getBlockSize();

						if (availableBytes < src.size()) {
							throw new Exception(
									"SD Card does not have sufficient space.");
						}

						dst.transferFrom(src, 0, src.size());
						flage = true;

					} else {
						throw new Exception("Can't write on SD Card");
					}
				} else {
					throw new Exception(
							"SD Card is not Mounted or in read only mode");
				}
			}
		} catch (Exception e) {
			flage = false;
			Log.e(TAG, e.getMessage());

		} finally {
			if (src != null) {
				try {
					src.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (dst != null) {
				try {
					dst.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return flage;
	}
}
