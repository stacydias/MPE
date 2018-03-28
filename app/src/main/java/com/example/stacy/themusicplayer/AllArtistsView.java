package com.example.stacy.themusicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM;
import static android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST;
import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;
import static android.media.MediaMetadataRetriever.METADATA_KEY_TITLE;

public class AllArtistsView extends AppCompatActivity {
    private Cursor audiocursor;
    private int song_column_index;
    ListView songlist;
    String WHERE = MediaStore.Audio.Media.ARTIST + "=?";
    String orderby = null;
    int count;
    MediaPlayer mp;
    Button back2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_artists_view);
        init_phone_music_grid();

        back2=findViewById(R.id.back_button2);
        Toolbar myToolbar = findViewById(R.id.artist_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.artists_act);

        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ib=new Intent(AllArtistsView.this,MainPlayerList.class);
                startActivity(ib);
            }
        });
    }
    private void init_phone_music_grid(){
        String []proj = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Artists.ARTIST_KEY
        };
        //String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        try {
            audiocursor = managedQuery(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, proj, null, null, null);
            count = audiocursor.getCount();

        }
        catch(Exception e){
            Log.e("Database Helper", "query:" +e);
        }

        songlist = (ListView)findViewById(R.id.listView1);
        songlist.setAdapter(new AllArtistsView.MusicAdapter(getApplicationContext()));
        songlist.setOnItemClickListener(musicgridlistener);
        mp= new MediaPlayer();
    }

    AdapterView.OnItemClickListener musicgridlistener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
            Intent i= new Intent(AllArtistsView.this,AllSongsView.class);
            i.putExtra("where", WHERE);
            i.putExtra("whereVal", whereVal);
            startActivity(i);
        }
    };

    public class MusicAdapter extends BaseAdapter {

        private Context mContext;
        ArrayList<String> album_list = new ArrayList<>();
        public MusicAdapter(Context c){
            mContext=c;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int position) {
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.artistlist,parent,false);

                holder = new ViewHolder();
                holder.song_artist =(TextView) convertView.findViewById(R.id.textView7);

                song_column_index= audiocursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
                audiocursor.moveToPosition(position);
                id=audiocursor.getString(song_column_index);
                holder.song_artist.setText(id);
                album_list.add(id);
            }

            return convertView;
        }
    }
    static class ViewHolder{

        //TextView song_title;
        TextView song_artist;
        //TextView song_album;

    }
}
