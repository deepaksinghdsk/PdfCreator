package com.example.PdfCreator;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.PdfCreator.ui.home.PDFDoc;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class custom_adapter extends RecyclerView.Adapter<custom_adapter.viewHolder1> implements Filterable {


    private static List<PDFDoc> allPdfDocs;
    private Context c;
    private int type;
    private List<PDFDoc> pdfDoc;
    private List<Uri> images;
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<PDFDoc> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0)
                filteredList.addAll(allPdfDocs);
            else {
                String filterString = constraint.toString().trim().toLowerCase();
                for (PDFDoc item : allPdfDocs) {
                    if (item.getName().trim().contains(filterString)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            pdfDoc.clear();
            pdfDoc.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public custom_adapter(Context c, ArrayList<PDFDoc> pdfDocs, int layoutType) {
        this.c = c;
        this.pdfDoc = pdfDocs;
        allPdfDocs = pdfDocs;
        this.type = layoutType;
    }


    public custom_adapter(Context c, int layoutType, ArrayList<Uri> images) {
        this.c = c;
        this.type = layoutType;
        this.images = images;
    }

    @NonNull
    @Override
    public viewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.grid_view_model, parent, false);

        return new viewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder1 holder, int position) {
        if (type == 1) {

            //inflate custom layout
            //view = LayoutInflater.from(c).inflate(R.layout.grid_view_model, parent, false);

            final PDFDoc pdf = pdfDoc.get(position);

            TextView pdfName = holder.pdfName;
            ImageView imageView = holder.imageView;

            pdfName.setText(pdf.getName());
            pdfName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPdfView(pdf.getPath());
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(pdf.getPath()));
                    intent.setType("application/pdf");

                    if (intent.resolveActivity(c.getPackageManager()) != null)
                        c.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return type;
    }

    @Override
    public int getItemCount() {
        return type == 1 ? pdfDoc.size() : images.size();
    }

    private void openPdfView(String path) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(FileProvider.getUriForFile(c, c.getApplicationContext().getPackageName() + ".provider", new File(path)), "application/pdf");
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //i.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        List<ResolveInfo> activities = c.getPackageManager().queryIntentActivities(i, 0);
        if (activities.size() > 0) {
            c.startActivity(Intent.createChooser(i, "Open pdf with"));
        } else {
            Intent pdf = new Intent(c, PDF_Activity.class);
            pdf.setData(Uri.parse(path));
            c.startActivity(pdf);
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

   /* @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (type == 1) {

            //inflate custom layout
            view = LayoutInflater.from(c).inflate(R.layout.grid_view_model, parent, false);

            final PDFDoc pdf = pdfDoc.get(position);

            TextView pdfName = view.findViewById(R.id.pdfName);
            ImageView imageView = view.findViewById(R.id.sharePDF);

            pdfName.setText(pdf.getName());
            pdfName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPdfView(pdf.getPath());
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(pdf.getPath()));
                    intent.setType("application/pdf");

                    if (intent.resolveActivity(c.getPackageManager()) != null)
                        c.startActivity(intent);
                }
            });
        }
        else {
            //inflate custom layout
            view = LayoutInflater.from(c).inflate(R.layout.gallery_layout, parent, false);

            final CheckBox checkBox = view.findViewById(R.id.checkBox);
            ImageView image = view.findViewById(R.id.image);

            //load images with uri in image view using picasso
            Picasso.Builder builder = new Picasso.Builder(c);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                    Log.e("Picasso", Objects.requireNonNull(e.getMessage()));
                    e.printStackTrace();
                }
            });

            Log.d("custom_adapter", "Uri : " + images.get(position));

            builder.build().load(new File(String.valueOf(images.get(position)))).error(R.drawable.load_failed)
                    .placeholder(R.drawable.load_failed).into(image);
            //image.setImageURI(images.get(position));

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        checkBox.setChecked(false);
                        boolean removed = selectedImages.remove(images.get(position));
                        Log.d("Custom adapter", "Image Uri removed from selected images list : " + removed);
                    } else {
                        checkBox.setChecked(true);
                        boolean added = selectedImages.add(images.get(position));
                        Log.d("Custom adapter", "Image Uri added to selected images list " + added);
                    }
                }
            });
        }

        return view;
    }*/

   /* @Override
    public Object getItem(int position) {
        return pdfDoc.get(position);
    }
*/
    /* @Override
    public int getCount() {
        return type == 1 ? pdfDoc.size() : images.size();
    }*/

    class viewHolder1 extends RecyclerView.ViewHolder {

        public TextView pdfName;
        public ImageView imageView;

        public viewHolder1(@NonNull View itemView) {
            super(itemView);

            pdfName = itemView.findViewById(R.id.pdfName);
            imageView = itemView.findViewById(R.id.sharePDF);
        }
    }
}
