package com.glcoe.onscreendrawing.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.glcoe.onscreendrawing.model.RecipentAddress;

/**
 * 
 * @author Moses.Kesavan
 * 
 */
public class DBHelper {

	private DatabaseHelper myDbHelper;

	private static final String DATABASE_NAME = "MAILADDRESS";

	private static final String TABLE_NAME = "RecipentAddress";

	private static final String DATABASE_CREATE = "create table " + TABLE_NAME
			+ "(_id integer primary key autoincrement, "
			+ "EmailID text not null" + ");";

	private static final int DATABASE_VERSION = 1;

	private SQLiteDatabase mDb;

	public DBHelper(Context context) {
		myDbHelper = new DatabaseHelper(context);
		mDb = myDbHelper.getWritableDatabase();
	}

	/**
	 * save the data in db.
	 * 
	 * @param model
	 */
	public void save(RecipentAddress model) {
		mDb = myDbHelper.getWritableDatabase();
		long id = 0;
		ContentValues contentValues = new ContentValues();
		contentValues.put("EmailID", model.getText());
		id = mDb.update(TABLE_NAME, contentValues, "EmailID=?",
				new String[] { model.getText() });
		if (id == 0) {
			id = mDb.insert(TABLE_NAME, null, contentValues);

		}
		mDb.close();
	}

	/**
	 * retrieve list from db.
	 * 
	 * 
	 */
	public ArrayList<RecipentAddress> list(String query) {
		try {
			RecipentAddress address;
			ArrayList<RecipentAddress> results = new ArrayList<RecipentAddress>();

			Cursor cursor = mDb.rawQuery(query, null);
			while (cursor.moveToNext()) {
				int columIndex = cursor.getColumnIndex("EmailID");
				String value = cursor.getString(columIndex);
				address = new RecipentAddress();
				address.setText(value);
				results.add(address);
			}

			cursor.close();

			return results;

		} catch (SQLiteConstraintException e) {
			return null;
		}
	}

	/**
	 * Method to delete row from db.
	 * 
	 * @param emailId
	 */
	public void deleteRow(String emailId) {
		mDb.delete(TABLE_NAME, "EmailID=?", new String[] { emailId });
	}

	public class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}

	}
}
