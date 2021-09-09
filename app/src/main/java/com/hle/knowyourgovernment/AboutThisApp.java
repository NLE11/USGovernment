package com.hle.knowyourgovernment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class AboutThisApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_this_app);
    }

    public void click(View v) {
        boolean check = checkNetworkConnection();
        if (check == true) {
            Intent i = new Intent(Intent.ACTION_VIEW); //ACTION_VIEW in this case is the browser
            i.setData(Uri.parse("https://developers.google.com/civic-information/"));
            startActivity(i);
        }
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) return true;
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Cannot open this link without an internet connection.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }
    }

}