package com.example.stacy.themusicplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import static android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM;
import static android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST;
import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;
import static android.media.MediaMetadataRetriever.METADATA_KEY_LOCATION;
import static android.media.MediaMetadataRetriever.METADATA_KEY_TITLE;

public class AllSongsView extends AppCompatActivity {

    private Cursor audiocursor;
    private int song_column_index;
    ListView videolist;
    int count;
    MediaPlayer mp;
    public MusicService musicService;
    private Intent playIntent;
    private boolean isBound = false;
    private ServiceConnection serviceConnection;
    Button back1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_songs_view);
        back1= findViewById(R.id.back_button1);
        Toolbar myToolbar = findViewById(R.id.songs_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.all_songs_act);
        Intent i = getIntent();
        if(i==null){
            init_phone_music_grid();
        } else {
            String where;
            String [] whereVal;
            where = i.getStringExtra("where");
            whereVal = i.getStringArrayExtra("whereVal");
            init_phone_music_grid(where, whereVal);
        }
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
                musicService = binder.getService();
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };

        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ib=new Intent(AllSongsView.this,MainPlayerList.class);
                startActivity(ib);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    private void init_phone_music_grid(String where, String[] whereVal){
        String []proj = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE
        };

        audiocursor = managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                proj,
                where,
                whereVal,
                MediaStore.Audio.Media.DISPLAY_NAME
        );
        count = audiocursor.getCount();

        videolist = (ListView)findViewById(R.id.listView);
        videolist.setAdapter(new MusicAdapter(getApplicationContext()));
        videolist.setOnItemClickListener(musicgridlistener);
        mp= new MediaPlayer();
    }

    private void init_phone_music_grid() {
        init_phone_music_grid(null, null);
    }

    AdapterView.OnItemClickListener musicgridlistener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            song_column_index=audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            audiocursor.moveToPosition(position);
            String filename=audiocursor.getString(song_column_index);
            Log.d("ALLSONGVIEW", "filename val : "+filename);

            String id_im=null;
            int dataIndex = audiocursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            audiocursor.moveToPosition(position);
            String filepath = audiocursor.getString(dataIndex);
            Log.d("ALLSONGSView", "filepath val :  "+filepath);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(filepath);

            if (mmr.extractMetadata(METADATA_KEY_TITLE) == null) {
                song_column_index = audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                audiocursor.moveToPosition(position);
                id_im = audiocursor.getString(song_column_index);
            }
            String song_im=mmr.extractMetadata(METADATA_KEY_TITLE);
/**
            if(musicService.isPlaying() && musicService.sameSong(position)) {
                musicService.pauseSong();
            }
            else {
                musicService.setSong(filename, position);
                musicService.playSong();
            }**/


            //mmr.release();


            Intent ii= new Intent(AllSongsView.this, IndSongView.class);
            ii.putExtra("path",filepath);
            startActivity(ii);
        }
    };

    @Override
    protected void onPause() {
        unbindService(serviceConnection);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicService = null;
        super.onDestroy();
    }

    public class MusicAdapter extends BaseAdapter{

        private Context mContext;
        public MusicAdapter(Context c){
            mContext=c;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            String id=null;
            convertView = null;
            if(convertView ==null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem,parent,false);

                holder = new ViewHolder();
                holder.song_title =(TextView) convertView.findViewById(R.id.textView11);
                //holder.song_duration=(TextView) convertView.findViewById(R.id.textView10);
                holder.song_artist=(TextView) convertView.findViewById(R.id.textView9);
                holder.song_album=(TextView) convertView.findViewById(R.id.textView8);
                holder.thumbImage=(ImageView) convertView.findViewById(R.id.imageView2);


                /*
                song_column_index = audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                audiocursor.moveToPosition(position);
                holder.song_size.setText("Size: " + audiocursor.getString(song_column_index));
                */
                //Extracting metadata from file
                int dataIndex = audiocursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                audiocursor.moveToPosition(position);
                String filepath = audiocursor.getString(dataIndex);
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(filepath);

                if (mmr.extractMetadata(METADATA_KEY_TITLE) != null) {
                    holder.song_title.setText(mmr.extractMetadata(METADATA_KEY_TITLE));
                }
                else {
                    song_column_index = audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                    audiocursor.moveToPosition(position);
                    id = audiocursor.getString(song_column_index);
                    holder.song_title.setText(id);
                }

                //long dura=Long.parseLong(mmr.extractMetadata(METADATA_KEY_DURATION));

                //holder.song_duration.setText(du);
                holder.song_album.setText(mmr.extractMetadata(METADATA_KEY_ALBUM));
                holder.song_artist.setText(mmr.extractMetadata(METADATA_KEY_ARTIST));
                byte[] art = mmr.getEmbeddedPicture();
                if (art != null) {
                    Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                    holder.thumbImage.setImageBitmap(songImage);
                }
                else {
                    holder.thumbImage.setImageDrawable(getDrawable(R.drawable.blank_song));
                }
                mmr.release();
                //convertView.setTag(holder);
            }

            return convertView;
        }
    }
    static class ViewHolder{

        TextView song_title;
        TextView song_duration;
        TextView song_artist;
        TextView song_album;
        ImageView thumbImage;

        int col1,col2,col3;

    }
}

