package com.example.stacy.themusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.io.File;
import static android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM;
import static android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST;
import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;
import static android.media.MediaMetadataRetriever.METADATA_KEY_TITLE;

public class IndSongView extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnSeekCompleteListener {

    TextView songname, duration, artistname;
    Button rewind_b, fastforward_b, stop_b, repeat_b, back_b;
    ImageView albumart;
    ToggleButton play_b;
    MediaPlayer mp;
    SeekBar sb;
    public MusicService musicService;
    private boolean isBound = false;
    private ServiceConnection serviceConnection;
    private Intent playIntent;

    private Cursor audiocursor;
    private int song_column_index;


    public String convertDuration(long duration) {
        String out = null;
        long hours=00;
        try {
            hours = (duration / 3600000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return out;
        }
        long remaining_minutes = (duration - (hours * 3600000)) / 60000;
        String minutes = String.valueOf(remaining_minutes);
        if (minutes.equals(0)) {
            minutes = "00";
        }
        long remaining_seconds = (duration - (hours * 3600000) - (remaining_minutes * 60000));
        String seconds = String.valueOf(remaining_seconds);
        if (seconds.length() < 2) {
            seconds = "00";
        } else {
            seconds = seconds.substring(0, 2);
        }

        if (hours > 0) {
            out = hours + ":" + minutes + ":" + seconds;
        } else {
            out = minutes + ":" + seconds;
        }

        return out;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ind_song_view);
        songname = (TextView) findViewById(R.id.song_name);
        artistname = (TextView) findViewById(R.id.artist_name);
        duration = (TextView) findViewById(R.id.duration);
        play_b = (ToggleButton) findViewById(R.id.play_button);
        rewind_b = findViewById(R.id.rewind_button);
        fastforward_b = findViewById(R.id.fastforward_button);
        stop_b = findViewById(R.id.stop_button);
        repeat_b = findViewById(R.id.repeat_button);
        back_b=findViewById(R.id.back_button);
        albumart=findViewById(R.id.album_art_view);
        sb = (SeekBar) findViewById(R.id.seekBar);

        //rewind_b.setOnClickListener(this);
        String path_ex = getIntent().getStringExtra("path");
        //Bitmap bitmap = getIntent().getParcelableExtra("image");
        //Log.d("OUTPUTS", "path_id ka output"+path_ex);


        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                MusicService.MusicBinder binder = (MusicService.MusicBinder) iBinder;
                musicService = binder.getService();
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                isBound = false;

            }
        };
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path_ex);

        songname.setText(mmr.extractMetadata(METADATA_KEY_TITLE));

        final long dura=Long.parseLong(mmr.extractMetadata(METADATA_KEY_DURATION));
        //String du= convertDuration(dura);
        //duration.setText(du);

        artistname.setText(mmr.extractMetadata(METADATA_KEY_ARTIST));
        byte[] art = mmr.getEmbeddedPicture();
        if (art != null) {
            Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
            albumart.setImageBitmap(songImage);
        }
        else {
            albumart.setImageDrawable(getDrawable(R.drawable.blank_song));
        }
        mmr.release();


        mp=new MediaPlayer();
        try{
        mp.setDataSource(path_ex);
        //mp.reset();
        mp.prepare();
        mp.start();}
        catch (Exception e){
            e.printStackTrace();
        }

        //sb.setMax(mp.getDuration());
/**
        Thread t=new Thread(){
            public void run(){
                while(true){
                    sb.setProgress(mp.getCurrentPosition());
                }
            }
        };
        t.start();**/

        back_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ib=new Intent(IndSongView.this,AllSongsView.class);
                startActivity(ib);
            }
        });

        play_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mp.isPlaying() == false) {
                    mp.start();
                    duration.setText("00:00");
                    long c = mp.getCurrentPosition();
                    String dura=convertDuration(c);
                    duration.setText(dura);
                    play_b.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause));
                    Log.d("PLAY", "duration =  "+dura);
                }
                else{
                    mp.pause();
                    //long c = mp.getCurrentPosition();
                    long c = mp.getCurrentPosition();
                    String dura=convertDuration(c);
                    duration.setText(dura);
                    Log.d("PLAY", "duration =  "+dura);
                    play_b.setBackgroundDrawable(getResources().getDrawable(R.drawable.play));
                }

            }
        });

        rewind_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mp.getDuration() > (mp.getCurrentPosition() - 5000)) {
                    mp.seekTo(mp.getCurrentPosition() - 5000);    //rewinds by 5 seconds
                    long c = mp.getCurrentPosition();
                    String dura=convertDuration(c);
                    duration.setText(dura);
                }

            }
        });

        fastforward_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mp.getDuration() > (mp.getCurrentPosition() + 5000)) {
                    mp.seekTo(mp.getCurrentPosition() + 5000);    //forwards by 5 seconds
                    long c = mp.getCurrentPosition();
                    String dura=convertDuration(c);
                    duration.setText(dura);

                    /**
                    double c = mp.getCurrentPosition();
                    double d = c / 1000;
                    double e = d / 60;
                    double f = d % 60;
                    duration.setText((int) e + ":" + (int) f);**/
                }
            }
        });

        repeat_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.seekTo(0);
                duration.setText("00:00");
            }
        });

        stop_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.stop();
            }
        });



    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent=new Intent(this,MusicService.class);
            bindService(playIntent,serviceConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);

        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        long c = mp.getCurrentPosition();
        String dura=convertDuration(c);
        duration.setText(dura);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long c = mp.getCurrentPosition();
        String dura=convertDuration(c);
        duration.setText(dura);
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
    mp.seekTo(0);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        if(b==true){
            mp.seekTo(i);
        }
        long c = mp.getCurrentPosition();
        String dura=convertDuration(c);
        duration.setText(dura);

    }
}
