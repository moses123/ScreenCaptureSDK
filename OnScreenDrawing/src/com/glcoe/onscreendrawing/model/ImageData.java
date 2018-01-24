package com.glcoe.onscreendrawing.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageData implements Parcelable {
	private String imagePath;
	private String imageName;
	private boolean selectedImage = false;

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public boolean isSelectedImage() {
		return selectedImage;
	}

	public void setSelectedImage(boolean selectedImage) {
		this.selectedImage = selectedImage;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
	}

}
