package com.example.stacy.themusicplayer;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import static android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM;
import static android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST;
import static android.media.MediaMetadataRetriever.METADATA_KEY_TITLE;

public class AllSongsView extends Activity {

    private Cursor audiocursor;
    private int song_column_index;
    ListView videolist;
    int count;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_songs_view);


        init_phone_music_grid();
    }
    private void init_phone_music_grid(){
        String []proj={MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE};

        audiocursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,proj,null,null,null);
        count = audiocursor.getCount();

        videolist = (ListView)findViewById(R.id.listView);
        videolist.setAdapter(new MusicAdapter(getApplicationContext()));
        videolist.setOnItemClickListener(musicgridlistener);
        mp= new MediaPlayer();
    }

    AdapterView.OnItemClickListener musicgridlistener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            song_column_index=audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            audiocursor.moveToPosition(position);

            String filename=audiocursor.getString(song_column_index);

            try{
                if (mp.isPlaying()){
                    mp.reset();
                }
                mp.setDataSource(filename);
                mp.prepare();
                mp.start();
            }catch (Exception e){

            }

        }
    };

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
                holder.song_size=(TextView) convertView.findViewById(R.id.textView10);
                holder.song_artist=(TextView) convertView.findViewById(R.id.textView9);
                holder.song_album=(TextView) convertView.findViewById(R.id.textView8);
                holder.thumbImage=(ImageView) convertView.findViewById(R.id.imageView2);



                song_column_index = audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
                audiocursor.moveToPosition(position);
                holder.song_size.setText("Size: " + audiocursor.getString(song_column_index));

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
                holder.song_album.setText(mmr.extractMetadata(METADATA_KEY_ALBUM));
                holder.song_artist.setText(mmr.extractMetadata(METADATA_KEY_ARTIST));
                byte[] art = mmr.getEmbeddedPicture();
                if (art != null) {
                    Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                    holder.thumbImage.setImageBitmap(songImage);
                }
                else {
                    holder.thumbImage.setImageDrawable(getDrawable(R.drawable.music_player_icon));
                }
                mmr.release();
                //convertView.setTag(holder);
            }

            return convertView;
        }
    }
    static class ViewHolder{

        TextView song_title;
        TextView song_size;
        TextView song_artist;
        TextView song_album;
        ImageView thumbImage;

        int col1,col2,col3;

    }
}

