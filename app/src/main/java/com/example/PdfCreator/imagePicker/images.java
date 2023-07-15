package com.example.PdfCreator.imagePicker;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class images implements Parcelable {

    int id;
    public Uri uri;

    public images(int id, Uri uri){
        this.id = id;
        this.uri = uri;
    }

    protected images(Parcel in) {
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(this.uri, flags);
        dest.writeInt(id);
    }

    public static final Creator<images> CREATOR = new Creator<images>() {
        @Override
        public images createFromParcel(Parcel in) {
            return new images(in);
        }

        @Override
        public images[] newArray(int size) {
            return new images[size];
        }
    };
}
