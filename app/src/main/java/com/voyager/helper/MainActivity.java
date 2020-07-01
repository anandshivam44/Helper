package com.voyager.helper;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.voyager.helper.Adapters.GalleryAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView_grid)
    RecyclerView recyclerViewGrid;

    private GalleryAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recyclerViewGrid.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        adapter=new GalleryAdapter(MainActivity.this);
        recyclerViewGrid.setAdapter(adapter);



    }


}