package com.example.anon.swiggy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.util.LogTime;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public ArrayList<String> ImageUrls = new ArrayList<>();
    public ArrayList<String> ImageNames = new ArrayList<>();
    public ArrayList<String> ImageDesc = new ArrayList<>();
    private int previousTotal = 0;
    private int page = 1;
    private int visibleItemCount = 1;
    private int totalItemCount = 10;
    private boolean loading = true;
    private int visibleThreshold = 1;
    private boolean internet = false;
    int firstVisibleItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckInternet();
        if(!internet){
            Intent intent = new Intent(this, no_internet.class);
            this.startActivity(intent);
        }else{
            initImages();
        }

        final RecyclerViewPager mRecyclerView = (RecyclerViewPager) findViewById(R.id.list);
        final LinearLayoutManager layout = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

    }

    private void CheckInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            internet = true;
        }else{
            internet = false;
        }
    }
   private void initImages(){


       final OkHttpClient client = new OkHttpClient();
       client.setConnectTimeout(15, TimeUnit.SECONDS);
       final Request request = new Request.Builder()
               .url("http://tech24.in/wp-json/wp/v2/posts?_embed&per_page=20")
               .build();

       @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
           private static final String TAG = "SlideFragment";

           @Override
           protected String doInBackground(Void... params) {

               try {
                   Response response = client.newCall(request).execute();

                   if (!response.isSuccessful()) {
                       Log.d(TAG, "doInBackground: REsponse Un Successfull - 56");
                       return null;

                   }
                   String Data =  response.body().string();
                   response.body().close();
                   return Data;
               } catch (Exception e) {
                   e.printStackTrace();
                   Log.d(TAG, "doInBackground: Exceptione on line63");
                   return null;
               }

           }

           @Override
           protected void onPostExecute(String Data) {

               super.onPostExecute(Data);
               if (Data != null) {
                   Log.d(TAG, "onPostExecute: line-72");
                   try {
                       JSONArray json = new JSONArray(Data);
                       for (int i = 0; i < json.length(); i++) {
                           JSONObject post = json.getJSONObject(i);
                           String title = post.getJSONObject("title").getString("rendered");
                           String description = post.getJSONObject("excerpt").getString("rendered");
                           String imgURL = post.getJSONObject("_embedded").getJSONArray("wp:featuredmedia").getJSONObject(0).getJSONObject("media_details").getString("file");
                           String imagUrl = "http://tech24.in/wp-content/uploads/" + imgURL;
                           String desc = Jsoup.parse(description).text();
                           String tit = Jsoup.parse(title).text();
                           ImageNames.add(tit);
                           ImageDesc.add(desc);
                           ImageUrls.add(imagUrl);
                           Log.d(TAG, "onPostExecute: arrayList Created" );
                           }
                        initRecycler();
                        cancel(true);
                   }catch(JSONException j){
                       j.printStackTrace();
                       Log.d(TAG, "onPostExecute: on line 121");
                   }
               }
           }
       };
       asyncTask.execute();


   }

   private void initRecycler(){

       final RecyclerViewPager mRecyclerView = (RecyclerViewPager) findViewById(R.id.list);

// setLayoutManager like normal RecyclerView, you do not need to change any thing.
       final LinearLayoutManager layout = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
       mRecyclerView.setLayoutManager(layout);

//set adapter
//You just need to implement ViewPageAdapter by yourself like a normal RecyclerView.Adpater.
       RecyclerViewAdapter adapter = new RecyclerViewAdapter(ImageUrls, ImageNames, ImageDesc, this);
       mRecyclerView.setAdapter(adapter);

       mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

           @Override
           public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
               super.onScrolled(recyclerView, dx, dy);

              visibleItemCount = layout.getChildCount();
              totalItemCount = layout.getItemCount();
               firstVisibleItem = layout.findFirstVisibleItemPosition();
//               int aaa = totalItemCount - visibleItemCount;
//               int bbb = firstVisibleItem + visibleThreshold;
//               Log.d("tag" ,"totalItemCount : " + totalItemCount + "|  visibleItemCount : " + visibleItemCount);
              Log.d("tag" ,"totalItemCount : " + totalItemCount + "|  previousTotal : " + previousTotal);
               Log.d("tag", "firstVisibleItem :" + firstVisibleItem + " | totalItemCount :" + totalItemCount);
               if (loading) {
                   if (totalItemCount > previousTotal) {
                       loading = false;
                       previousTotal = totalItemCount;
                   }
               }
               if (!loading && (firstVisibleItem + 12)
                       == (totalItemCount)) {

                   Log.i("Ya!", "end called");
                   page += 1;
                   String url = "http://tech24.in/wp-json/wp/v2/posts?page="+page+"&_embed&per_page=20";
                   Log.d( url ,": ");
                   // Do something
                   final OkHttpClient client2 = new OkHttpClient();
                   client2.setConnectTimeout(15, TimeUnit.SECONDS);
                   final Request request2 = new Request.Builder()
                           .url(url)
                           .build();
                   @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                       private static final String TAG = "SlideFragment";

                       @Override
                       protected String doInBackground(Void... params) {

                           try {

                               Response response2 = client2.newCall(request2).execute();

                               if (!response2.isSuccessful()) {
                                   Log.d(TAG, "doInBackground: REsponse Un Successfull - 56");
                                   cancel(true);
                                   response2.body().close();
                                   return null;

                               }
                               String Data2 =  response2.body().string();
                               response2.body().close();
                               if (Data2 != null) {
                                   Log.d(TAG, "onPostExecute: 188 LINE");
                                   try {
                                       JSONArray json = new JSONArray(Data2);
                                       for (int i = 0; i < json.length(); i++) {
                                           JSONObject post = json.getJSONObject(i);
                                           String title = post.getJSONObject("title").getString("rendered");
                                           String description = post.getJSONObject("excerpt").getString("rendered");
                                           String imgURL = post.getJSONObject("_embedded").getJSONArray("wp:featuredmedia").getJSONObject(0).getJSONObject("media_details").getString("file");
                                           String imagUrl = "http://tech24.in/wp-content/uploads/" + imgURL;
                                           String desc = Jsoup.parse(description).text();
                                           String tit = Jsoup.parse(title).text();
                                           ImageNames.add(tit);
                                           ImageDesc.add(desc);
                                           ImageUrls.add(imagUrl);
                                           Log.d(TAG, "onPostExecute: arrayList Created" );
                                       }


                                   }catch(JSONException j){
                                       j.printStackTrace();
                                       Log.d(TAG, "onPostExecute: on line 121");
                                   }
                               }
                               return null;
                           } catch (Exception e) {
                               e.printStackTrace();
                               Log.d(TAG, "doInBackground: Exceptione on line63");
                               return null;
                           }

                       }

                       @Override
                       protected void onPostExecute(String Data2) {

                           super.onPostExecute(Data2);

                       }
                   };
                   asyncTask.execute();
                   loading = true;
               }
           }
       });

   }
}
