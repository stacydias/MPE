package com.example.stacy.themusicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import static android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM;
import static android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST;
import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;
import static android.media.MediaMetadataRetriever.METADATA_KEY_TITLE;

public class AllAlbumsView extends AppCompatActivity {
    private Cursor audiocursor;
    private int song_column_index;
    ListView songlist;
    int count;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_albums_view);


        init_phone_music_grid();
    }
    private void init_phone_music_grid(){
        String []proj={MediaStore.Audio.Artists._ID, MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS, MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
                ,MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Albums.ALBUM};
        //String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        try {
            audiocursor = managedQuery(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, proj, null, null, null);
            count = audiocursor.getCount();

        }
        catch(Exception e){
            Log.e("Database Helper", "query:" +e);
        }

        songlist = (ListView) findViewById(R.id.agv);
        songlist.setAdapter(new AllAlbumsView.MusicAdapter(getApplicationContext()));
        songlist.setOnItemClickListener(musicgridlistener);
        mp= new MediaPlayer();
    }

    AdapterView.OnItemClickListener musicgridlistener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            song_column_index=audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
            audiocursor.moveToPosition(position);

            String filename=audiocursor.getString(song_column_index);
            String artist_name=MediaStore.Audio.Media.ALBUM_ID;

            /*try{
                if (mp.isPlaying()){
                    mp.reset();
                }
                mp.setDataSource(filename);
                mp.prepare();
                mp.start();
            }catch (Exception e){
                e.printStackTrace();
            }*/
            Intent i= new Intent(AllAlbumsView.this,ArtistsSongs.class);
            getIntent().putExtra(artist_name,MediaStore.Audio.Albums.ALBUM);
            startActivity(i);
        }
    };

    public class MusicAdapter extends BaseAdapter {

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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.albumgrid,parent,false);

                holder = new ViewHolder();
                holder.song_album =(ImageView) convertView.findViewById(R.id.imageView4);

                song_column_index= audiocursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
                audiocursor.moveToPosition(position);
                id=audiocursor.getString(song_column_index);
                //holder.song_album.setText(id);

                int dataIndex = audiocursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                String filepath = audiocursor.getString(dataIndex);
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(filepath);
                byte[] art = mmr.getEmbeddedPicture();
                if (art != null) {
                    Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                    holder.song_album.setImageBitmap(songImage);
                }
                else {
                    holder.song_album.setImageDrawable(getDrawable(R.drawable.blank_song));
                }
            }

            return convertView;
        }
    }
    static class ViewHolder{

        //TextView song_title;
        ImageView song_album;

    }
}
