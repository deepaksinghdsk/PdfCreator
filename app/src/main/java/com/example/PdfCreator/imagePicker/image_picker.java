package com.example.PdfCreator.imagePicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.PdfCreator.R;
import com.example.PdfCreator.custom_adapter;

import java.util.ArrayList;

public class image_picker extends AppCompatActivity {

    private Context c;
    RecyclerView imageGrid;
    private final String tag = "image_picker";
    //private image_picker_adapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        c = this;

        imageGrid = findViewById(R.id.gallery);
        imageGrid.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        new backgroundThread().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class backgroundThread extends AsyncTask<Void, Void, ArrayList<Uri>> {

        @Override
        protected ArrayList<Uri> doInBackground(Void... voids) {
            ArrayList<Uri> images = new ArrayList<>();
            Uri externalUri, internalUri;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
                externalUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            else
                externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
                internalUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_INTERNAL);
            else
                internalUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;

            //projection
            String[] columns = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DATE_ADDED};

            // Log.d(tag, "ExternalUri: "+externalUri+"\ninternalUri: "+internalUri);

            String sort = MediaStore.Images.Media.DATE_ADDED + " DESC";
            Cursor cursor = c.getContentResolver().query(externalUri, columns, null, null, sort);

            if (cursor != null) {
                int dataColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                while (cursor.moveToNext()) {
                    images.add(Uri.parse(cursor.getString(dataColumn)));
                    //Log.d("Image_picker", "Uri : " + cursor.getString(dataColumn));
                }
                cursor.close();
            }

            Cursor cursor2 = c.getContentResolver().query(internalUri, columns, null, null, sort);

            if (cursor2 != null) {
                int dataColumn = cursor2.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                while (cursor2.moveToNext()) {
                    images.add(Uri.parse(cursor2.getString(dataColumn)));
                }
                cursor2.close();
            }

            Log.i(tag, "Total images: "+images.size());
            return images;
        }

        @Override
        protected void onPostExecute(ArrayList<Uri> imagesList) {
            image_picker_adapter adapter = new image_picker_adapter(c, imagesList);
            imageGrid.setAdapter(adapter);
            super.onPostExecute(imagesList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_picker_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.send) {
            Intent returnIntent = new Intent();

            returnIntent.putParcelableArrayListExtra("imageList",
                    image_picker_adapter.selectedImages);

            //ClipData clipData = (ClipData) image_picker_adapter.selectedImages;
            //returnIntent.setClipData(clipData);

            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        return true;
    }
}
