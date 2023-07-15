package com.example.PdfCreator.imagePicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.PdfCreator.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class image_picker_adapter extends RecyclerView.Adapter<image_picker_adapter.viewHolder>{

    private Context c;
    private List<Uri> images;
    public static int screenHeight;
    public final static ArrayList<images> selectedImages = new ArrayList<>();

    public image_picker_adapter(Context c, ArrayList<Uri> images) {
        this.c = c;
        this.images = images;//.addAll(images);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.gallery_layout, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull image_picker_adapter.viewHolder holder, final int position) {

            //inflate custom layout
            //view = LayoutInflater.from(c).inflate(R.layout.gallery_layout, parent, false);

            final CheckBox checkBox = holder.checkBox;
            final ImageView image = holder.image;

            final int pos = position;

            //load images with uri in image view using picasso
            Picasso.Builder builder = new Picasso.Builder(c);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                    try {

                    Bitmap bit = null;

                    bit = BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri));

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bit.compress(Bitmap.CompressFormat.JPEG, 1, out);
                    Bitmap finalImage = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                    image.setImageBitmap(finalImage);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                    image.setImageURI(uri);
                }
                    //e.getMessage();
                    e.printStackTrace();
                }
            });

            Log.d("custom_adapter", "Uri : " + images.get(position).toString());

            Uri u = Uri.fromFile(new File(String.valueOf(images.get(position))));

        try {
            Bitmap bitmap = builder.build().load(u).resize(image.getWidth(), image.getHeight()).get();
            image.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //builder.build().load(new File((images.get(position)))).error(R.drawable.load_failed)
            builder.build().load(u).error(R.drawable.load_failed)
                    //.placeholder(R.drawable.load_failed)
                      //.noPlaceholder()
                    .fit().into(image);
            //image.setImageURI(images.get(position));

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {

                        Iterator<images> itr = selectedImages.iterator();
                        while (itr.hasNext()) {

                            images img = itr.next();
                            if(img.id == checkBox.getId()){
                                //boolean removed = selectedImages.remove(img);
                                itr.remove();
                                //Log.d("Custom adapter", "Image Uri removed from selected images list : " + removed);
                            }
                        }

                        checkBox.setChecked(false);
                    } else {
                        boolean added = selectedImages.add(new images(checkBox.getId(), images.get(pos)));
                        checkBox.setChecked(true);
                        Log.d("Custom adapter", "Image Uri added to selected images list " + added);
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;
        public ImageView image;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkBox);
            image = itemView.findViewById(R.id.image);

            //int screenHeight = displayMetrics.heightPixels;
            //image.setMinimumHeight(screenHeight/3);
            //image.setMaxHeight(screenHeight/3);
        }
    }
}
