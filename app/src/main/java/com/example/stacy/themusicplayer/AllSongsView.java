package com.example.stacy.themusicplayer;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

public class AllSongsView extends Activity {

    private Cursor videocursor;
    private int video_column_index;
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

        videocursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,proj,null,null,null);
        count = videocursor.getCount();

        videolist = (ListView)findViewById(R.id.listView);
        videolist.setAdapter(new MusicAdapter(getApplicationContext()));
        videolist.setOnItemClickListener(musicgridlistener);
        mp= new MediaPlayer();
    }

    AdapterView.OnItemClickListener musicgridlistener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            video_column_index=videocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            videocursor.moveToPosition(position);

            String filename=videocursor.getString(video_column_index);

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
                holder.txtTitle =(TextView) convertView.findViewById(R.id.textView11);
                holder.txtSize=(TextView) convertView.findViewById(R.id.textView10);
                holder.thumbImage=(ImageView) convertView.findViewById(R.id.imageView2);

                video_column_index= videocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                videocursor.moveToPosition(position);
                id=videocursor.getString(video_column_index);
                video_column_index=videocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
                videocursor.moveToPosition(position);

                holder.txtTitle.setText(id);
                holder.txtSize.setText("Size (KB): "+videocursor.getString(video_column_index));


            }

            return convertView;
        }
    }
    static class ViewHolder{

        TextView txtTitle;
        TextView txtSize;
        ImageView thumbImage;
    }
}

