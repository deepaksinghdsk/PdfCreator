package com.example.PdfCreator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class about extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void visitGithub(View view) {
        Intent youtube = new Intent(Intent.ACTION_VIEW);
        youtube.setData(Uri.parse("https://github.com/deepaksinghdsk"));

        if (youtube.resolveActivity(getPackageManager()) != null) {
            startActivity(youtube);
        }
    }

    public void visitYoutube(View view) {

        Intent youtube = new Intent(Intent.ACTION_VIEW);
        youtube.setData(Uri.parse("https://youtube.com/channel/UCBEMDJvH1-7Tg7_qmFYkYnQ"));

        if (youtube.resolveActivity(getPackageManager()) != null) {
            startActivity(youtube);
        }
    }

    public void visitFacebook(View view) {

        Intent facebook = new Intent(Intent.ACTION_VIEW);
        facebook.setData(Uri.parse("https://www.facebook.com/deepaksinghkainturaDSK.1/"));

        if (facebook.resolveActivity(getPackageManager()) != null) {
            startActivity(facebook);
        }
    }
}
