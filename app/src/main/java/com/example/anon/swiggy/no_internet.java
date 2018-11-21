package com.example.anon.swiggy;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;

public class no_internet extends AppCompatActivity {
    private boolean internet = false;
    private Context ncontext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.internet);
        Button refresh = (Button) findViewById(R.id.button);
//        ImageView image = (ImageView) findViewById(R.id.noint);
//        Bitmap img = null;
//        try {
//            img = getBitmapFromAsset("no-internet.jpg");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        image.setImageBitmap(img);
        refresh.setOnClickListener(refreshListen);
        ncontext = this;
    }
//    private Bitmap getBitmapFromAsset(String strName) throws IOException{
//        try {
//            AssetManager assetManager = getAssets();
//            InputStream istr = assetManager.open(strName);
//            Bitmap bitmap = BitmapFactory.decodeStream(istr);
//            return bitmap;
//        }catch (IOException ioe){
//            ioe.printStackTrace();
//            return null;
//        }
//    }
    private void CheckInternet(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            internet = true;
        }else{
            internet = false;
        }
    }

    private final View.OnClickListener refreshListen = new View.OnClickListener() {
        @Override
        public void onClick(View refresh) {
            // do something when the button is clicked
            // Yes we will handle click here but which button clicked??? We don't know
            CheckInternet();
            if (!internet) {
                Toast.makeText(ncontext, "No Internet", Toast.LENGTH_SHORT).show();
            } else {
               Intent intent = new Intent(ncontext, MainActivity.class);
               ncontext.startActivity(intent);
            }
        }
    };
}
