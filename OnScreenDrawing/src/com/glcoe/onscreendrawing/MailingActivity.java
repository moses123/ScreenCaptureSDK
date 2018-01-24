package com.glcoe.onscreendrawing;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.glcoe.onscreendrawing.adapter.RecipentListAdapter;
import com.glcoe.onscreendrawing.db.DBHelper;
import com.glcoe.onscreendrawing.model.RecipentAddress;
import com.glcoe.onscreendrawing.utils.AppConstants;

/**
 * 
 * @author Moses.Kesavan
 * 
 */
public class MailingActivity extends Activity {

	private RecipentListAdapter adapter;
	private Button mAddButton;
	private Button mSendButton;
	private ListView recipentList;
	private ArrayList<RecipentAddress> mModelsList;
	private EditText mAddressEditText;
	private DBHelper dbHelper;
	private InputMethodManager mImputManager;
	private Context mContext;
	private Intent mIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipents_list_layout);
		if (savedInstanceState != null) {
			mModelsList = savedInstanceState.getParcelableArrayList("MODEL_LIST");
		}

		mContext = this;
		mImputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		dbHelper = new DBHelper(this);

		mAddButton = (Button) findViewById(R.id.add_button);
		mSendButton = (Button) findViewById(R.id.send_button);
		recipentList = (ListView) findViewById(R.id.reciever_list);
		mAddressEditText = (EditText) findViewById(R.id.edit);

		mIntent = getIntent();
		mAddButton.setOnClickListener(mClickListener);
		mSendButton.setOnClickListener(mClickListener);
		recipentList.setItemsCanFocus(true);
		adapter = new RecipentListAdapter(MailingActivity.this);
		recipentList.setAdapter(adapter);
		mModelsList = new ArrayList<RecipentAddress>();

		initData();

	}

	/**
	 * initialise data from db if present.
	 */
	private void initData() {
		String query = "Select * From RecipentAddress";
		mModelsList = dbHelper.list(query);
		if (mModelsList != null && mModelsList.size() > 0) {
			Log.d("Test", "retrived recipentAddresses :: "
					+ mModelsList.get(0).getText());
			adapter.setArrayList(mModelsList);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("MODEL_LIST", mModelsList);
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == mAddButton) {
				mImputManager.hideSoftInputFromWindow(
						mAddressEditText.getWindowToken(), 0);
				if (!TextUtils.isEmpty(mAddressEditText.getText())) {
					RecipentAddress model = new RecipentAddress();
					model.setText(mAddressEditText.getText().toString());
					mModelsList.add(model);
					adapter.setArrayList(mModelsList);
					mAddressEditText.setText("");
				} else {
					Toast.makeText(MailingActivity.this,
							getString(R.string.receipent_msg),
							Toast.LENGTH_SHORT).show();
				}

			} else if (v == mSendButton) {
				// saving the data in db.
				ArrayList<RecipentAddress> list = adapter.getArrayList();

				ArrayList<RecipentAddress> selectedAddressList = adapter
						.getSelectedReceipentAddresses();
				if (selectedAddressList.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						dbHelper.save(list.get(i));
					}
					ArrayList<String> arrayList = new ArrayList<String>();
					for (int i = 0; i < selectedAddressList.size(); i++) {
						arrayList.add(selectedAddressList.get(i).getText());
					}
					Intent intent = new Intent();

					if (!TextUtils.isEmpty(mIntent.getStringExtra("Activity"))) {
						ArrayList<Uri> uris = mIntent
								.getParcelableArrayListExtra("ImageList");
						shareImage(uris, arrayList);
						finish();
					} else {
						intent.putStringArrayListExtra("LIST", arrayList);
						setResult(RESULT_OK, intent);
						finish();
					}

				} else {
					Toast.makeText(mContext,
							getString(R.string.select_receipent_address),
							Toast.LENGTH_SHORT).show();
				}

			}
		}

	};

	/**
	 * method to send multiple images to the intent.
	 * 
	 * @param uris
	 * @param arrayList
	 */
	private void shareImage(ArrayList<Uri> uris, ArrayList<String> arrayList) {
		StringBuffer buffer = new StringBuffer();
		int size = arrayList.size();
		for (int i = 0; i < size; i++) {
			if (i == (size) - 1) {
				buffer.append(arrayList.get(i));
			} else {
				buffer.append(arrayList.get(i));
				buffer.append(",");
			}
		}
		String[] mAddress = { buffer.toString() };

		Intent share = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
		share.setType("image/jpeg");
		share.putExtra(android.content.Intent.EXTRA_EMAIL, mAddress);
		share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		startActivity(Intent
				.createChooser(share, "Select your option to share"));
	}

	protected void onDestroy() {
		super.onDestroy();
		mSendButton = null;
		recipentList = null;
		mModelsList = null;
		mAddressEditText = null;
		dbHelper = null;
		mImputManager = null;
		adapter = null;
		AppConstants.count = 0;
	};

}