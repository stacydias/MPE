package com.example.stacy.themusicplayer;

import android.app.ListActivity;
import android.content.Intent;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainPlayerList extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,ar));
        //myProgress();
    }

    String []ar={"All Songs", "Artists", "Albums" };

    Class []c={AllSongsView.class, AllArtistsView.class, AllAlbumsView.class };

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //String name=ar[position];
        //Toast.makeText(getApplicationContext(),""+name,Toast.LENGTH_LONG).show();

        Intent i=new Intent(MainPlayerList.this,c[position]);
        startActivity(i);

    }

}
