package com.glcoe.onscreendrawing.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.glcoe.onscreendrawing.R;
import com.glcoe.onscreendrawing.model.ImageData;

/**
 * 
 * @author Moses.Kesavan
 * 
 */
public class ImageShareAdapter extends BaseAdapter {
	private ViewHolder holder;
	private ArrayList<ImageData> mImageDataList;
	private LayoutInflater inflater;
	private ArrayList<ImageData> mSelectedImage;
	private Bitmap bitmap;
	private BitmapFactory.Options mOptions;

	public ArrayList<ImageData> getmSelectedImage() {
		return mSelectedImage;
	}

	public void setmSelectedImage(ArrayList<ImageData> mSelectedImage) {
		this.mSelectedImage = mSelectedImage;
	}

	public ImageShareAdapter(Context context) {
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mOptions = new BitmapFactory.Options();
		mOptions.inPreferredConfig = Bitmap.Config.ARGB_4444;
		mSelectedImage = new ArrayList<ImageData>();

	}

	public ArrayList<ImageData> getmImageDataList() {
		return mImageDataList;
	}

	public void setmImageDataList(ArrayList<ImageData> mImageDataList) {
		this.mImageDataList = mImageDataList;
	}

	@Override
	public int getCount() {
		return getmImageDataList().size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ImageData imageData = mImageDataList.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.sharing_items, null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.image);

			holder.textView = (TextView) convertView
					.findViewById(R.id.imageText);

			holder.checkedItem = (CheckBox) convertView
					.findViewById(R.id.checked_image);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		bitmap = BitmapFactory.decodeFile(imageData.getImagePath(), mOptions);

		holder.imageView.setImageBitmap(bitmap);
		holder.textView.setText(imageData.getImageName());
		convertView.setTag(holder);
		holder.checkedItem.setChecked(imageData.isSelectedImage());

		holder.checkedItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageData data = new ImageData();
				if (((CheckBox) v).isChecked()) {
					data.setImagePath(mImageDataList.get(position)
							.getImagePath().toString());
					mSelectedImage.add(data);
					imageData.setSelectedImage(true);
				} else {
					mSelectedImage.remove(mImageDataList.get(position));
					imageData.setSelectedImage(false);
				}
				setmSelectedImage(mSelectedImage);
			}
		});
		return convertView;
	}

	public class ViewHolder {
		ImageView imageView;
		TextView textView;
		CheckBox checkedItem;
	}

}
