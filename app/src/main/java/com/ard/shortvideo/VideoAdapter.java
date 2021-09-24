package com.ard.shortvideo;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.media2.exoplayer.external.source.ExtractorMediaSource;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import com.google.android.exoplayer2.extractor.ts.H262Reader;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import com.google.android.exoplayer2.util.Util;
import com.vincan.medialoader.DefaultConfigFactory;
import com.vincan.medialoader.MediaLoader;
import com.vincan.medialoader.MediaLoaderConfig;
import com.vincan.medialoader.data.file.naming.HashCodeFileNameCreator;

import java.io.IOException;
import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {

    private ArrayList<VideoModel> videoList;
    private Context context;
    VideoHolder videoHolder;


    public VideoAdapter(ArrayList<VideoModel> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.short_video_layout,parent,false);
        return new VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoHolder holder, int position) {

        videoHolder = holder;

        if(videoList.size() != position+1)
        {
            MediaLoaderConfig mediaLoaderConfig = new MediaLoaderConfig.Builder(context)
                    .cacheRootDir(DefaultConfigFactory.createCacheRootDir(context, "ache_dir"))
                    .cacheFileNameGenerator(new HashCodeFileNameCreator())
                    .maxCacheFilesCount(100)
                    .maxCacheFilesSize(100 * 1024 * 1024)
                    .maxCacheFileTimeLimit(10)
                    .downloadThreadPoolSize(3)
                    .downloadThreadPriority(Thread.NORM_PRIORITY)
                    .build();
            MediaLoader.getInstance(context).init(mediaLoaderConfig);
        }

        String proxyUrl = MediaLoader.getInstance(context).getProxyUrl(videoList.get(position).getVideoUrl());



        if (holder.isPlaying()) {

            holder.player.play();
            Log.e("TAG1", "playing");

        } else {
            Log.e("TAG1", "empty");
            holder.execute(proxyUrl);
        }


    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder{

        private SurfaceView videoSurface;
        private   SimpleExoPlayer player;
        private long playbackPosition;
        private int currentWindow;
        private boolean playWhenReady;

        public VideoHolder(@NonNull View v) {
            super(v);
            videoSurface = v.findViewById(R.id.video_surface);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            player = new SimpleExoPlayer.Builder(context).setTrackSelector(trackSelector).build();
        }


        public void execute(String vPath)
        {


            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            player = new SimpleExoPlayer.Builder(context).setTrackSelector(trackSelector).build();
            player.setVideoSurfaceView(videoSurface);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, "shortVideo"));
            DefaultExtractorsFactory defaultExtractorsFactory = new DefaultExtractorsFactory();
            defaultExtractorsFactory.setFragmentedMp4ExtractorFlags(FragmentedMp4Extractor.FLAG_WORKAROUND_IGNORE_EDIT_LISTS);
            defaultExtractorsFactory.setTsExtractorFlags(DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES);
            Uri uri = Uri.parse(vPath);
            MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory,defaultExtractorsFactory).createMediaSource(uri);



            player.prepare(videoSource);
            player.setRepeatMode(Player.REPEAT_MODE_ONE);
            player.setPlayWhenReady(true);
            player.pause();


        }


        public void play()
        {
            player.play();
        }
        public void pause()
        {
           player.pause();
        }

        public boolean isPlaying() {
            return player != null
                    && player.getPlaybackState() != Player.STATE_ENDED
                    && player.getPlaybackState() != Player.STATE_IDLE
                    && player.getPlayWhenReady();
        }

       /* public void releasePlayer() {
            if (player != null) {
                playbackPosition = player.getCurrentPosition();
                currentWindow = player.getCurrentWindowIndex();
                playWhenReady = player.getPlayWhenReady();
                player.release();
                player = null;
            }
        }*/

    }

    @Override
    public void onViewAttachedToWindow(@NonNull VideoHolder holder) {
        super.onViewAttachedToWindow(holder);
         holder.play();


    }

    @Override
    public void onViewDetachedFromWindow(@NonNull VideoHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.pause();
        Log.e("VideoAdapter","deAttach");

    }






}
