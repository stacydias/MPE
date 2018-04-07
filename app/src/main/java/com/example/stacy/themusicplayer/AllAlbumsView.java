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

import java.util.ArrayList;
import java.util.List;


public class AllAlbumsView extends AppCompatActivity {
    private Cursor audiocursor;
    private int song_column_index;
    GridView songlist;
    GridView gridView;
    int count;
    MediaPlayer mp;
    String WHERE = android.provider.MediaStore.Audio.Media.ALBUM + "=?";
    String orderby = null;
    MusicAdapter musicAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_albums_view);

        init_phone_music_grid();
    }


    private void init_phone_music_grid(){
        String []proj = {
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.FIRST_YEAR,
                MediaStore.Audio.Albums.LAST_YEAR,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS
        };
        //String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        try {
            audiocursor = managedQuery(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, proj, null, null, null);
            count = audiocursor.getCount();

        }
        catch(Exception e){
            Log.e("Database Helper", "query:" +e);
        }
        songlist = findViewById(R.id.albumlist);
        musicAdapter = new AllAlbumsView.MusicAdapter(getApplicationContext());
        songlist.setAdapter(musicAdapter);
        songlist.setOnItemClickListener(musicgridlistener);
        mp= new MediaPlayer();
    }

    AdapterView.OnItemClickListener musicgridlistener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            song_column_index=audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
//            audiocursor.moveToPosition(position);
//
//            String filename=audiocursor.getString(song_column_index);
//            String albumname=MediaStore.Audio.Media.ALBUM_ID;
            String query = (String)  songlist.getAdapter().getItem(position);
            String whereVal[] = { query};
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
            Intent i= new Intent(AllAlbumsView.this,AllSongsView.class);
            i.putExtra("where", WHERE);
            i.putExtra("whereVal", whereVal);
            startActivity(i);
        }
    };

    public class MusicAdapter extends BaseAdapter {

        private Context mContext;
        ArrayList<String> album_list = new ArrayList<String>();
        public MusicAdapter(Context c){
            mContext=c;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public String getItem(int position) {
            return album_list.get(position);
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
                //holder.album_name = (TextView)convertView.findViewById(R.id.albumname);

                holder.album_art =(ImageView) convertView.findViewById(R.id.imageView4);

                song_column_index= audiocursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
                audiocursor.moveToPosition(position);
                id=audiocursor.getString(song_column_index);
                holder.album_name.setText(id);

                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                int albumartIndex = audiocursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
                String filepath  = audiocursor.getString(albumartIndex);
                if (filepath != null) {
                    Bitmap songImage = BitmapFactory.decodeFile(filepath);
                    holder.album_art.setImageBitmap(songImage);
                }
                else {
                    holder.album_art.setImageDrawable(getDrawable(R.drawable.blank_song));
                }
                album_list.add(id);

            }
            return convertView;
        }
    }
    static class ViewHolder{

        TextView album_name;
        ImageView album_art;

    }
}
