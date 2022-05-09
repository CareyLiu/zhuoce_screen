package com.sample;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.zhuoce.screen.MyApp;
import com.zhuoce.screen.R;

import java.io.File;

public class Video_BaseFragment extends Fragment implements CacheListener {

    private static final String LOG_TAG = "VideoFragment";

    String url;
    ImageView cacheStatusImageView;
    VideoView videoView;
    ProgressBar progressBar;

    public static Fragment newInstance(String url) {
        Video_BaseFragment fragment = new Video_BaseFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

    private final VideoProgressUpdater updater = new VideoProgressUpdater();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_video, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString("url");
        }
        cacheStatusImageView = view.findViewById(R.id.cacheStatusImageView);
        videoView = view.findViewById(R.id.videoView);
        progressBar = view.findViewById(R.id.progressBar);
        view.findViewById(R.id.btStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.start();
            }
        });

        view.findViewById(R.id.btStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
            }
        });
        checkCachedState();
        startVideo();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });
    }

    private void checkCachedState() {
        HttpProxyCacheServer proxy = MyApp.getProxy(getActivity());
        boolean fullyCached = proxy.isCached(url);
        setCachedState(fullyCached);
        if (fullyCached) {
            progressBar.setSecondaryProgress(100);
        }
    }

    private void startVideo() {
        HttpProxyCacheServer proxy = MyApp.getProxy(getActivity());
        proxy.registerCacheListener(this, url);
        String proxyUrl = proxy.getProxyUrl(url);
        Log.d(LOG_TAG, "Use proxy url " + proxyUrl + " instead of original url " + url);
        videoView.setVideoPath(proxyUrl);
        videoView.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        updater.start();

        videoView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        updater.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        videoView.stopPlayback();
        MyApp.getProxy(getActivity()).unregisterCacheListener(this);
    }

    @Override
    public void onCacheAvailable(File file, String url, int percentsAvailable) {
        progressBar.setSecondaryProgress(percentsAvailable);
        setCachedState(percentsAvailable == 100);
        Log.d(LOG_TAG, String.format("onCacheAvailable. percents: %d, file: %s, url: %s", percentsAvailable, file, url));
    }

    private void updateVideoProgress() {
        int videoProgress = videoView.getCurrentPosition() * 100 / videoView.getDuration();
        progressBar.setProgress(videoProgress);
    }


    private void setCachedState(boolean cached) {
        int statusIconId = cached ? R.drawable.ic_cloud_done : R.drawable.ic_cloud_download;
        cacheStatusImageView.setImageResource(statusIconId);
    }

    private final class VideoProgressUpdater extends Handler {

        public void start() {
            sendEmptyMessage(0);
        }

        public void stop() {
            removeMessages(0);
        }

        @Override
        public void handleMessage(Message msg) {
            updateVideoProgress();
            sendEmptyMessageDelayed(0, 500);
        }
    }
}
