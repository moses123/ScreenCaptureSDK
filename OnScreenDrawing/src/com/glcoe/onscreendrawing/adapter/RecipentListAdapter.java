package com.glcoe.onscreendrawing.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.glcoe.onscreendrawing.R;
import com.glcoe.onscreendrawing.db.DBHelper;
import com.glcoe.onscreendrawing.model.RecipentAddress;

/**
 * 
 * @author Moses.Kesavan
 * 
 */
public class RecipentListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	private ViewHolder holder;
	private ArrayList<RecipentAddress> arrayList;
	private ArrayList<RecipentAddress> mSelectedArrayList;
	private DBHelper dbHelper;

	public RecipentListAdapter(Context context) {
		mContext = context;
		dbHelper = new DBHelper(context);
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mSelectedArrayList = new ArrayList<RecipentAddress>();
	}

	@Override
	public int getCount() {
		if (arrayList != null) {
			return arrayList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public ArrayList<RecipentAddress> getArrayList() {
		return arrayList;
	}

	public void setArrayList(ArrayList<RecipentAddress> arrayList) {
		this.arrayList = arrayList;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final RecipentAddress model = arrayList.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item, null);
			holder.addrssText = (TextView) convertView
					.findViewById(R.id.recipent);

			holder.clearIcon = (ImageView) convertView
					.findViewById(R.id.clear_icon);
			holder.checkBox = (CheckBox) convertView
					.findViewById(R.id.check_box);
			convertView.setTag(holder);

		}
		holder = (ViewHolder) convertView.getTag();
		holder.addrssText.setText(model.text);
		holder.clearIcon.setTag(position);
		convertView.setTag(holder);
		holder.checkBox.setChecked(model.isSelected());
		holder.clearIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String id = arrayList.get(position).getText();
				dbHelper.deleteRow(id);
				arrayList.remove(position);
				setArrayList(arrayList);

			}
		});
		holder.checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					mSelectedArrayList.add(arrayList.get(position));
					model.setSelected(true);
				} else {
					mSelectedArrayList.remove(arrayList.get(position));
					model.setSelected(false);
				}

			}
		});
		return convertView;
	}

	public ArrayList<RecipentAddress> getSelectedReceipentAddresses() {
		return mSelectedArrayList;
	}

	private class ViewHolder {
		TextView addrssText;
		ImageView clearIcon;
		CheckBox checkBox;
	}

}
