package com.example.PdfCreator.ui.create;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.FileProvider;

import com.example.PdfCreator.PDF_Activity;
import com.example.PdfCreator.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class name_bottom_sheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private EditText fileName;
    private String tag = "Bottom_sheet";
    private Context c;
    private ArrayList<Uri> list;
    private PdfDocument document;
    private ProgressBar progress;
    private Button save;
    private createDoc docThread;
    private View topView;
    static private ConstraintLayout fragmentCreateLayout;

    name_bottom_sheet(ArrayList<Uri> list) {
        this.list = list;

        Log.d(tag, "number of images returned from multi image picker are " + list.size());

        //this.document = document;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.name_bottom_sheet, container, false);
        c = this.getContext();
        topView = v;

        fragmentCreateLayout = v.findViewById(R.id.bottom_sheet);
        save = v.findViewById(R.id.saveButton);
        fileName = v.findViewById(R.id.fileName);
        progress = v.findViewById(R.id.progress);
        ImageView close = v.findViewById(R.id.close);
        //locationName = v.findViewById(R.id.locationName);

        close.setOnClickListener(this);
        save.setOnClickListener(this);
        docThread = new createDoc();
        docThread.execute(90);

        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close) {
            dismiss();
        } else if (v.getId() == R.id.saveButton)
            if (fileName.getText() != null && fileName.getText().length() > 0) {
                save.setEnabled(false);
                new createFile().execute(fileName.getText().toString());
            } else {
                fileName.setError("filename required!");
                dismiss();
            }
    }

    @SuppressLint("StaticFieldLeak")
    private class createDoc extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... quality) {
            Log.d(tag, "Now creating pdf file");
            int pageNumber = 1;
            document = new PdfDocument();
            int reqH, reqW = 714, height = 1010, width = 714;
            if (list != null && !list.isEmpty()) {
                for (Uri uri : list) {
                    //Uri uri = parcel;
                    if (this.isCancelled()) {
                        return false;
                    }

                    try {
                                    /*ByteBuffer buffer = img.getPlanes()[0].getBuffer();
                                      byte[] bytes = new byte[buffer.capacity()];
                                      buffer.get(bytes);
                                      Bitmap bitFImg = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);*/
                        Bitmap bit = BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri));
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        bit.compress(Bitmap.CompressFormat.JPEG, quality[0], out);
                        Bitmap finalImage = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

                        //Bitmap mutableImage = finalImage.copy(finalImage.getConfig(), true);
                        //mutableImage.setHeight(1000);
                        //mutableImage.setWidth(700);
                        //finalImage.reconfigure(700, 1000, finalImage.getConfig());
                        reqH = width * finalImage.getHeight() / finalImage.getWidth();
                        Log.d(tag, pageNumber + ": reqH = " + reqH);

                        if (reqH >= height) {
                            reqH = height;
                            reqW = height * finalImage.getWidth() / finalImage.getHeight();
                            Log.d(tag, pageNumber + ": reqW = " + reqW);
                        }

                        finalImage = Bitmap.createScaledBitmap(finalImage, reqW, reqH, true);

                        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(reqW, reqH, pageNumber).create();

                        pageNumber++;

                        PdfDocument.Page page = document.startPage(pageInfo);

                        Canvas canvas = page.getCanvas();
                        Matrix matrix = new Matrix();
                        matrix.reset();

                        canvas.drawBitmap(finalImage, matrix, null);

                        document.finishPage(page);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        dismiss();
                    }
                }
            } else {
                return false;
            }
            Log.d(tag, "Now pdf file created successfully");
            return true;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Boolean successful) {
            if (!successful) {
                dismiss();
                Toast.makeText(c, "Try again!", Toast.LENGTH_SHORT).show();
            } else {
                progress.setVisibility(View.GONE);
                save.setText("SAVE");
                save.setEnabled(true);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class createFile extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            enable_progressBar();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            disable_progressBar();
            dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            disable_progressBar();
        }

        @Override
        protected Void doInBackground(String... names) {
            Log.d(tag, "Now saving this pdf file");
            File loc;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                loc = c.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            else
                loc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            if (loc == null) {
                Log.e(tag, "Unable to get document directory path");
                Toast.makeText(c, "Unable to get document directory path", Toast.LENGTH_SHORT).show();
                return null;
            } else if (!loc.exists()) {
                boolean created = loc.mkdir();
                if (!created) {
                    Toast.makeText(c, "Unable to create document folder", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }
            File file = new File(loc, names[0] + ".pdf");

            //Log.d(tag, "file location = "+file.toString());
            try (FileOutputStream fos = new FileOutputStream(file)) {
                try {
                    document.writeTo(fos);
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.publishProgress();

            Uri data = FileProvider.getUriForFile(c, c.getApplicationContext().getPackageName() + ".provider", file);

            //call media scanner class to scan newly created file
            new MyMediaScannerConnectionClient(c, file, "application/pdf");

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(data, "application/pdf");
            i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (c.getPackageManager().queryIntentActivities(i, 0).size() > 0) {
                c.startActivity(Intent.createChooser(i, "Open pdf with"));
            } else {
                Intent pdf = new Intent(c, PDF_Activity.class);
                pdf.setData(data);
                c.startActivity(pdf);
            }

            Log.d(tag, "pdf file is saved successfully");
            return null;
        }

        private void enable_progressBar() {
            final ProgressBar progressBar = new ProgressBar(c);
            progressBar.setVisibility(View.VISIBLE);
            int progressBarID = 2;
            progressBar.setId(progressBarID);

            fragmentCreateLayout.addView(progressBar);
            ConstraintSet set = new ConstraintSet();
            set.clone(fragmentCreateLayout);
            set.connect(progressBar.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            set.connect(progressBar.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            set.connect(progressBar.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            set.connect(progressBar.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            set.applyTo(fragmentCreateLayout);
        }

        private void disable_progressBar() {
            int progressBarID = 2;
            ProgressBar progressBar = topView.findViewById(progressBarID);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        Log.d(tag, "inside onStop method");
        if (docThread.getStatus() != AsyncTask.Status.FINISHED) {
            docThread.cancel(true);
            Log.d(tag, "docThread is canceled");
        }
        super.onStop();
    }
}



