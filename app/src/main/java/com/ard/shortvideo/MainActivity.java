package com.ard.shortvideo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import com.google.android.exoplayer2.extractor.ts.H262Reader;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private  String TAG = "MainActivity";
    private ArrayList<VideoModel> videoList;
    private ProgressDialog progressDialog;

    private ViewPager2 viewPager2;
    private  VideoAdapter videoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        videoList = new ArrayList<>();
        viewPager2 = findViewById(R.id.view_pager2);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        getVideos();


    }


    public void getVideos()
    {

        AndroidNetworking.get("http://fatema.takatakind.com/app_api/index.php?p=showAllVideos")
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        Log.e(TAG,response);

                        try {
                            JSONArray jsonArray = new JSONObject(response).getJSONArray("msg");

                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                 String desc=jsonObject.getString("description");
                                 String videoUrl=jsonObject.getString("video");
                                 String thumbUrl=jsonObject.getString("thum");
                                 String gifUrl=jsonObject.getString("gif");

                                 videoList.add(new VideoModel(desc,videoUrl,thumbUrl,gifUrl));

                            }

                            videoAdapter = new VideoAdapter(videoList,MainActivity.this);
                            viewPager2.setAdapter(videoAdapter);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.e(TAG,error.getErrorBody());
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    protected void onPause() {
        super.onPause();
        android.os.Process.killProcess(android.os.Process.myPid());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}