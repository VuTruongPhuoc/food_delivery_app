package com.example.app2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Contacts extends AppCompatActivity {
    private static final int PLACE_PICKER_REQUEST = 1;
    ImageView ivMail,ivLocation,ivPhone, ivFacebook, ivInstagram;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ivMail = findViewById(R.id.ivMail);
        ivLocation = findViewById(R.id.ivLocation);
        ivPhone = findViewById(R.id.ivPhone);
        ivFacebook = findViewById(R.id.ivFacebook);
        ivInstagram = findViewById(R.id.ivInstagram);
        ivMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"vananh999@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message text.");
                startActivity(Intent.createChooser(emailIntent, "Send email using..."));
            }
        });
        ivLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ivPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + "0123456789"));
                startActivity(in);
            }
        });
        ivInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sWeblink = "https://www.instagram.com/ngovananh_ins";
                openlink(sWeblink);
            }
        });
        ivFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sWeblink ="https://www.facebook.com/anhss.ngovan/";
                openlink(sWeblink);
            }
        });

    }
    private void openlink(String sWeblink) {
        Uri uri = Uri.parse(sWeblink);

        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setData(uri);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }
}