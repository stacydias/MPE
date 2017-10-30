package com.example.stacy.themusicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;

/**
 * Created by d1ndra on 10/30/17.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private final String TAG = MusicService.class.getSimpleName();
    private MediaPlayer player;
    private String filepath;
    private int position;
    private final IBinder musicBind = new MusicBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        initPlayer();
    }

    public void playSong() {
        player.reset();
        try {
            player.setDataSource(filepath);
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
        player.prepareAsync();

    }

    public void initPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

    }

    public void setSong(String fp, int pos) {
        filepath = fp;
        position = pos;
    }

    public void pauseSong() {
        player.pause();
    }

    public boolean sameSong(int pos) {
        return position==pos;
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void onDestroy() {
        if (player != null) player.release();
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
}

