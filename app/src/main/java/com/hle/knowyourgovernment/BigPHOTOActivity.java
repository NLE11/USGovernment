package com.hle.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class BigPHOTOActivity extends AppCompatActivity {

    private ScrollView mScrollView2;
    private ImageView photo2;
    private ImageView logo2;
    private TextView locationSet2;
    private TextView nameSet2;
    private TextView positionSet2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_photo);

        Intent intent = getIntent();

        String location2 = intent.getStringExtra("location");
        String name2 = intent.getStringExtra("name");
        String position2 = intent.getStringExtra("position");
        String party2 = intent.getStringExtra("party");
        String photoUrl2 = intent.getStringExtra("photoUrl");

        locationSet2 = findViewById(R.id.sublocation2);
        nameSet2 = findViewById(R.id.subname2);
        positionSet2 = findViewById(R.id.subposition2);


        photo2 = findViewById(R.id.photo2);
        logo2 = findViewById(R.id.logo2);

        locationSet2.setText(location2);
        nameSet2.setText(name2);
        positionSet2.setText(position2);

        mScrollView2 = (ScrollView) findViewById(R.id.scrollView2);

        if (party2.contains("Republican")) {
            mScrollView2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRep));
            logo2.setImageResource(R.drawable.rep_logo);
        } else if (party2.contains("Democratic")) {
            mScrollView2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDem));
            logo2.setImageResource(R.drawable.dem_logo);
        } else {
            mScrollView2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorNon));
            logo2.setImageResource(0);
        }

        if (!checkInternet()) {
            Picasso.get().load(R.drawable.brokenimage)
                    .into(photo2);
        } else {
            Picasso.get().load(photoUrl2)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(photo2);
        }
    }

    public boolean checkInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) return true;
        else return false;
    }
}