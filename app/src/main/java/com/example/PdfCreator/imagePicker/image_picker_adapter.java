package com.example.PdfCreator.imagePicker;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.PdfCreator.R;
import com.example.PdfCreator.custom_adapter;
import com.example.PdfCreator.ui.home.PDFDoc;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class image_picker_adapter extends RecyclerView.Adapter<image_picker_adapter.viewHolder>{

    private Context c;
    private List<Uri> images;
    public static List<Uri> selectedImages = new ArrayList<>();

    public image_picker_adapter(Context c, ArrayList<Uri> images) {
        this.c = c;
        this.images = images;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.grid_view_model, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull image_picker_adapter.viewHolder holder, final int position) {

            //inflate custom layout
            //view = LayoutInflater.from(c).inflate(R.layout.gallery_layout, parent, false);

            final CheckBox checkBox = holder.checkBox;
            ImageView image = holder.image;

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

    @Override
    public int getItemCount() {
        return 0;
    }

    class viewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;
        public ImageView image;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkBox);
            image = itemView.findViewById(R.id.image);
        }
    }
}
