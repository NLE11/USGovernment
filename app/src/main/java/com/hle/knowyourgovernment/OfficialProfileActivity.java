package com.hle.knowyourgovernment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class OfficialProfileActivity extends AppCompatActivity {

    private static final String TAG = "OfficialProfileActivity";
    private ScrollView mScrollView;
    private ImageView photo;
    private ImageView logo;
    private ImageView Facebook;
    private ImageView Twitter;
    private ImageView YouTube;
    private TextView locationSet;
    private TextView nameSet;
    private TextView positionSet;
    private TextView partySet;
    private TextView addressSet;
    private TextView phonesSet;
    private TextView urlsSet;
    private TextView emailsSet;
    private TextView addTitle;
    private TextView emailTitle;
    private TextView phoneTitle;
    private TextView webTitle;

    private String FacebookID;
    private String TwitterID;
    private String YouTubeID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_profile);

        Intent intent = getIntent();

        final String location = intent.getStringExtra("location");
        final String name = intent.getStringExtra("name");
        final String position = intent.getStringExtra("position");
        final String party = intent.getStringExtra("party");
        String address = intent.getStringExtra("address");
        String phones = intent.getStringExtra("phones");
        String urls = intent.getStringExtra("urls");
        String emails = intent.getStringExtra("emails");
        final String photoUrl = intent.getStringExtra("photoUrl");
        FacebookID = intent.getStringExtra("FacebookID");
        TwitterID = intent.getStringExtra("Twitter");
        YouTubeID = intent.getStringExtra("YouTube");

        locationSet = findViewById(R.id.sublocation);
        nameSet = findViewById(R.id.subname);
        positionSet = findViewById(R.id.subposition);
        partySet = findViewById(R.id.subparty);
        addressSet = findViewById(R.id.add);
        phonesSet = findViewById(R.id.phone);
        emailsSet = findViewById(R.id.email);
        urlsSet = findViewById(R.id.web);

        Facebook = findViewById(R.id.facebook);
        Twitter = findViewById(R.id.twitter);
        YouTube = findViewById(R.id.youtube);

        if (FacebookID.equals("")) {
            Facebook.setVisibility(View.INVISIBLE);
        }
        if (TwitterID.equals("")) {
            Twitter.setVisibility(View.INVISIBLE);
        }
        if (YouTubeID.equals("")) {
            YouTube.setVisibility(View.INVISIBLE);
        }

        addTitle = findViewById(R.id.addtitle);
        phoneTitle = findViewById(R.id.phonetitle);
        emailTitle = findViewById(R.id.emailtitle);
        webTitle = findViewById(R.id.webtitle);

        if (address.equals("")) {
            addTitle.setVisibility(View.INVISIBLE);
        }
        if (emails.equals("")) {
            emailTitle.setVisibility(View.INVISIBLE);
        }
        if (phones.equals("")) {
            phoneTitle.setVisibility(View.INVISIBLE);
        }
        if (urls.equals("")) {
            webTitle.setVisibility(View.INVISIBLE);
        }

        photo = findViewById(R.id.photo);
        logo = findViewById(R.id.logo);

        locationSet.setText(location);
        nameSet.setText(name);
        positionSet.setText(position);
        partySet.setText(party);
        addressSet.setText(address);
        phonesSet.setText(phones);
        emailsSet.setText(emails);
        urlsSet.setText(urls);

        Linkify.addLinks(phonesSet, Linkify.ALL);
        phonesSet.setLinkTextColor(Color.parseColor("#ffffff"));
        Linkify.addLinks(emailsSet, Linkify.ALL);
        emailsSet.setLinkTextColor(Color.parseColor("#ffffff"));
        Linkify.addLinks(addressSet, Linkify.ALL);
        addressSet.setLinkTextColor(Color.parseColor("#ffffff"));
        Linkify.addLinks(urlsSet, Linkify.ALL);
        urlsSet.setLinkTextColor(Color.parseColor("#ffffff"));


        mScrollView = (ScrollView) findViewById(R.id.scrollView);

        if (party.contains("Republican")) {
            mScrollView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRep));
            logo.setImageResource(R.drawable.rep_logo);
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkInternet()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        String link = "https://www.gop.com/";
                        intent.setData(Uri.parse(link));
                        startActivity(intent);
                    }
                }
            });
        } else if (party.contains("Democratic")) {
            mScrollView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDem));
            logo.setImageResource(R.drawable.dem_logo);
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkInternet()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        String link = "https://democrats.org/";
                        intent.setData(Uri.parse(link));
                        startActivity(intent);
                    }
                }
            });
        } else {
            mScrollView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorNon));
            logo.setImageResource(0);
        }


        if (!checkInternet()) {
            Picasso.get().load(R.drawable.brokenimage)
                         .into(photo);
        }
        else {
            if(!photoUrl.equals("")) {
                Picasso.get().load(photoUrl)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(photo);
            }
        }

        if(!photoUrl.equals("")) {
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OfficialProfileActivity.this, BigPHOTOActivity.class);
                    intent.putExtra("location", location);
                    intent.putExtra("name", name);
                    intent.putExtra("position", position);
                    intent.putExtra("party", party);
                    intent.putExtra("photoUrl", photoUrl);
                    startActivity(intent);
                }
            });
        }
    }

    public boolean checkInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) return true;
        else return false;
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String name = TwitterID;
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + FacebookID;
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            }
            else { //older versions of fb app
                urlToUse = "fb://page/" + FacebookID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void youTubeClicked(View v) {
        String name = YouTubeID;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }

}


