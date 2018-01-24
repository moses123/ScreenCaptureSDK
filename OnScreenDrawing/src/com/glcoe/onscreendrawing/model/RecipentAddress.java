package com.glcoe.onscreendrawing.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RecipentAddress implements Parcelable{
	/**
	 * 
	 * @author Moses.Kesavan
	 * 
	 */
	public static enum RecipentAddressEnum {

		ADRRESS(1);

		private int mColumnIndex;

		private RecipentAddressEnum(int columnIndex) {
			mColumnIndex = columnIndex;
		}

		public int getColumnIndex() {
			return mColumnIndex;
		}
	}

	public String text;
	private Boolean isSelected = false;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isSelected() {
		return isSelected;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
}
