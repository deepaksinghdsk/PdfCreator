package com.example.PdfCreator.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.PdfCreator.R;
import com.example.PdfCreator.about;
import com.example.PdfCreator.custom_adapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private Context c;
    private String tag = "HomeFragment";
    private ArrayList<PDFDoc> docs = new ArrayList<>();
    private ProgressBar bar;
    private RecyclerView listView;
    //private PDFDoc docs = new PDFDoc();
    private Handler handler = new Handler(Looper.getMainLooper());
    private custom_adapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        c = this.getContext();
        bar = new ProgressBar(this.getContext());

        listView = root.findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(c));

        final ConstraintLayout mainContainer = root.findViewById(R.id.mainContainer);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //if (docs.getDocs().isEmpty()) {
                ConstraintLayout.LayoutParams constraintLayout = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                bar.setId(View.generateViewId());
                bar.setLayoutParams(constraintLayout);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mainContainer.addView(bar, 1);

                        ConstraintSet set = new ConstraintSet();
                        set.clone(mainContainer);
                        set.connect(bar.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                        set.connect(bar.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                        set.connect(bar.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
                        set.connect(bar.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                        set.applyTo(mainContainer);

                        bar.setVisibility(View.VISIBLE);
                        Log.d(tag, "permission checking started");
                        checkPermissions();
                    }
                });
            }
        }).start();

        return root;
    }

    public void onBackPressed() {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment1menu, menu);

        MenuItem item = menu.findItem(R.id.search);
        final SearchView sv = (SearchView) item.getActionView();

        sv.setQueryHint("Search pdf");


        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //adapter.getFilter().filter(query);
                sv.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                //sv.clearFocus();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "Check out this Indian PDF creator, I use it to create and view PDF files. Get it for free at https://github.com/deepaksinghdsk/AllChat/releases/download/v1.0/AllChat.apk");
            intent.setType("text/plain");

            if (intent.resolveActivity(c.getPackageManager()) != null) {
                c.startActivity(intent);
            }
        } else if (item.getItemId() == R.id.about) {
            Intent i2 = new Intent(c, about.class);
            startActivity(i2);
        }
        return true;
    }

    private void getPDFs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(tag, "inside getPDFs");
                final ArrayList<PDFDoc> pdfDocs = new ArrayList<>();
                PDFDoc pdfDoc;

                Uri externalUri;// = MediaStore.Files.getContentUri("external");
                /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
                    externalUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
                else*/
                externalUri = MediaStore.Files.getContentUri("external");

                Uri internalUri;// = MediaStore.Files.getContentUri("internal");
                /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
                    internalUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_INTERNAL);
                else*/
                internalUri = MediaStore.Files.getContentUri("internal");


                String[] column = {MediaStore.Files.FileColumns.DATA,
                        MediaStore.Files.FileColumns.TITLE};

                String name, where = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
                String[] args = {MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")};

                Cursor cursor = c.getContentResolver().query(externalUri, column, where, args, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        pdfDoc = new PDFDoc();
                        pdfDoc.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
                        name = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE)) + ".pdf";
                        Log.i(tag, "pdf file name = " + name + " and pdf path = " + pdfDoc.getPath());
                        pdfDoc.setName(name);
                        pdfDocs.add(pdfDoc);
                    }
                    cursor.close();
                } else
                    Log.e(tag, "cursor is null in else block of code for android block for 10");

                Cursor cursor2 = c.getContentResolver().query(internalUri, column, where, args, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
                if (cursor2 != null) {
                    while (cursor2.moveToNext()) {
                        pdfDoc = new PDFDoc();
                        pdfDoc.setPath(cursor2.getString(cursor2.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
                        name = cursor2.getString(cursor2.getColumnIndex(MediaStore.Files.FileColumns.TITLE)) + ".pdf";
                        Log.i(tag, "pdf file name = " + name + " and pdf path = " + pdfDoc.getPath());
                        pdfDoc.setName(name);
                        pdfDocs.add(pdfDoc);
                    }
                    cursor2.close();
                } else
                    Log.i(tag, "cursor is null in else block of code for android block for 10");

                Log.i(tag, "getting PDFs first time");
                Log.i(tag, "All pdfs fetched, no: " + pdfDocs.size());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        bar.setVisibility(View.GONE);
                        //custom_adapter.allPdfDocs = pdfDocs;
                        //adapter = custom_adapter.instance(c, pdfDocs, 1);
                        adapter = new custom_adapter(c, pdfDocs, 1);
                        listView.setAdapter(adapter);
                        Log.i(tag, "Grid view is configured with adapter");
                    }
                });
            }
        }).start();
    }

    private void checkPermissions() {
        Log.d(tag, "Asking permission");

        if (ContextCompat.checkSelfPermission(c, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            } else {
                ActivityCompat.requestPermissions(this.requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the contacts

                }

                this.requestPermissions(new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 101);

            }
        } else {
            getPDFs();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(tag, "inside permission result " + requestCode);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(c, "Permission is required to show available PDF files.", Toast.LENGTH_LONG).show();
                Log.i(tag, "permission is not granted");
            } else {
                getPDFs();
                Log.i(tag, "permission is granted");
            }
        }
    }
}
