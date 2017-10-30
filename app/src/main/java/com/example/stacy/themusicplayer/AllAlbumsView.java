package com.example.stacy.themusicplayer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class AllAlbumsView extends AppCompatActivity {

    GridView gv;

    String []ar={"A","b","c"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.albumgrid);        //remove activity_my_grid (default layout) and replace with custom layout
        gv=(GridView) findViewById(R.id.agv);
        ArrayAdapter<String> ob=new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,ar);
        gv.setAdapter(ob);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name=ar[position];
                //Toast.makeText(getApplicationContext(),""+name,Toast.LENGTH_LONG).show();
                try{
                    Class cr=Class.forName("com.sd.mahe.myandroidapp."+name);
                    Intent i=new Intent(AllAlbumsView.this,cr);
                    startActivity(i);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    String[] projection = new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums.NUMBER_OF_SONGS };
    String selection = null;
    String[] selectionArgs = null;
    String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC";
    Cursor cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder);

}
