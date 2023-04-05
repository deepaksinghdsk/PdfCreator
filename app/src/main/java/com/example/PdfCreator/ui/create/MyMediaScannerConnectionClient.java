package com.example.PdfCreator.ui.create;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import java.io.File;

public class MyMediaScannerConnectionClient implements MediaScannerConnection.MediaScannerConnectionClient {

    private String mFilePath, mMimeType;
    private MediaScannerConnection mConn;

    MyMediaScannerConnectionClient(Context c, File mFile, String mMimeType) {
        this.mFilePath = mFile.getAbsolutePath();
        this.mMimeType = mMimeType;
        mConn = new MediaScannerConnection(c, this);
        mConn.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mConn.scanFile(mFilePath, mMimeType);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mConn.disconnect();
        Log.d(this.getClass().getSimpleName(), "Path = " + path + ", Uri = " + uri);
    }
}
