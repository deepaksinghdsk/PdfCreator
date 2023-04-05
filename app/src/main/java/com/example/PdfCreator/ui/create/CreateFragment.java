package com.example.PdfCreator.ui.create;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.PdfCreator.R;
import com.example.PdfCreator.about;
import com.example.PdfCreator.imagePicker.image_picker;
import com.vlk.multimager.activities.MultiCameraActivity;
import com.vlk.multimager.utils.Constants;
import com.vlk.multimager.utils.Params;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class CreateFragment extends Fragment implements View.OnClickListener {

    private String tag = "CreateFragment";
    final private int imageFromGalleryCode = 101, fileExplorerCode = 102, getImageFromImagePickerClass = 103;
    private Context c;
    //private Handler handler = new Handler(Looper.getMainLooper());
    private FragmentManager fragmentManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create, container, false);
        setHasOptionsMenu(true);
        c = this.getContext();

        ImageButton camera, gallery, explorer;

        fragmentManager = getParentFragmentManager();
        camera = root.findViewById(R.id.camera);
        gallery = root.findViewById(R.id.gallery);
        explorer = root.findViewById(R.id.attach);

       /* ConstraintLayout bottomSheetLayout = root.findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        sheetBehavior.setPeekHeight(0, true);
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED)
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);*/

        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);
        explorer.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.attach) {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("image/*");
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(i, fileExplorerCode);
        } else if (v.getId() == R.id.gallery) {
            Log.d(tag, "Gallery is clicked");

            //use image_picker.java class
            Intent imagePicker = new Intent(c, image_picker.class);
            startActivityForResult(imagePicker, getImageFromImagePickerClass);

            //use phones default gallery
            /*Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setType("image/*");
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

            if (galleryIntent.resolveActivity(c.getPackageManager()) != null) {
                startActivityForResult(Intent.createChooser(galleryIntent, "Select images with"), imageFromGalleryCode);
            }*/

            //use gallery from library
            /*Intent intent = new Intent(c, GalleryActivity.class);
            Params params = new Params();
            params.setCaptureLimit(10);
            params.setPickerLimit(10);
            params.setToolbarColor(R.color.colorPrimary);
            params.setActionButtonColor(R.color.colorPrimary);
            params.setButtonTextColor(R.color.colorPrimary);
            intent.putExtra(Constants.KEY_PARAMS, params);
            startActivityForResult(intent, Constants.TYPE_MULTI_PICKER);*/
        } else if (v.getId() == R.id.camera) {
            Log.d(tag, "Camera is clicked");

            Intent intent = new Intent(c, MultiCameraActivity.class);
            Params params = new Params();
            params.setCaptureLimit(10);
            params.setPickerLimit(10);
            params.setToolbarColor(R.color.colorPrimary);
            params.setActionButtonColor(R.color.colorPrimary);
            params.setButtonTextColor(R.color.colorPrimary);
            intent.putExtra(Constants.KEY_PARAMS, params);
            startActivityForResult(intent, Constants.TYPE_MULTI_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<Uri> mArrayUri = new ArrayList<>();

        if (resultCode != RESULT_OK || data == null) {
            Toast.makeText(c, "Unable to select images try again", Toast.LENGTH_SHORT).show();
            return;
        }

        mArrayUri.clear();
        switch (requestCode) {
            case Constants.TYPE_MULTI_CAPTURE:
            case Constants.TYPE_MULTI_PICKER:
                ArrayList<com.vlk.multimager.utils.Image> imagesList = data.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
                if (imagesList != null) {
                    Log.d(tag, "number of images returned from multi image picker are " + imagesList.size());
                    for (com.vlk.multimager.utils.Image img : imagesList) {
                        mArrayUri.add(img.uri);
                    }
                    createPdf(mArrayUri);
                }
                break;
            case fileExplorerCode:
            case getImageFromImagePickerClass:
            case imageFromGalleryCode:
                if (data.getData() != null) {

                    Uri uri = data.getData();
                    mArrayUri.add(uri);
                    createPdf(mArrayUri);
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            mArrayUri.add(item.getUri());
                        }
                        createPdf(mArrayUri);
                    } else
                        Toast.makeText(c, "Unable to select images try again", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment2menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.shareCreate) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "Check out this Indian PDF creator, I use it to create and view PDF files. Get it for free at https://github.com/deepaksinghdsk/AllChat/releases/download/v1.0/AllChat.apk");
            intent.setType("text/plain");

            if (intent.resolveActivity(c.getPackageManager()) != null) {
                c.startActivity(intent);
            }
        } else if (item.getItemId() == R.id.aboutCreate) {
            Intent i2 = new Intent(c, about.class);
            startActivity(i2);
        }
        return true;
    }

    private void createPdf(final ArrayList<Uri> list) {
        Log.i(tag, "bottom navigation view is started");

        final name_bottom_sheet bottom_sheet = new name_bottom_sheet(list);
        bottom_sheet.show(fragmentManager, "ModalBottomSheet");
        bottom_sheet.setCancelable(false);
    }
}
