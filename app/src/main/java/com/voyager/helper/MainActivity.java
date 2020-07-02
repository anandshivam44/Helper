package com.voyager.helper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.voyager.helper.Adapters.GalleryAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyTag";
    @BindView(R.id.recyclerView_grid)
    RecyclerView recyclerViewGrid;
    //    Fab fab;
    FloatingActionsMenu fabView;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;

    List<String> images = new ArrayList<>();
    List<String> imageName = new ArrayList<>();
//    2020-07-01 22:48:45.255 31476-31476/com.example.workingwithimages D/MyTag: URI = content://media/external/images/media/3606
//    2020-07-01 22:49:05.841 31476-31476/com.example.workingwithimages D/MyTag: URI = file:///storage/emulated/0/Android/data/com.example.workingwithimages/cache/pickImageResult.jpeg
    ///storage/emulated/0/testfolder

    private GalleryAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fabView = findViewById(R.id.expanded_image);
        fab1 = findViewById(R.id.fab_item_1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Item_1 Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        fab2 = findViewById(R.id.fab_item_2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Item_2 Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Item_3 Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        handlePermission();


    }

    private void handlePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        102);
            } else {
                loadImages();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 102) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Storage Granted", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Read Storage Granted");
                loadImages();
            } else {
                Toast.makeText(this, "Read Storage DECLINED", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Read Storage DECLINED");
            }
        }

    }

    private void loadImages() {

        getURIofImages();

        recyclerViewGrid.hasFixedSize();
        recyclerViewGrid.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));//pending work
        recyclerViewGrid.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        adapter = new GalleryAdapter(MainActivity.this, images, imageName, new GalleryAdapter.PhotoListner() {
            @Override
            public void onShareButtonClicked(String path) {
                Toast.makeText(MainActivity.this, "Clicked " + path, Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewGrid.setAdapter(adapter);
    }

    private void getURIofImages() {

        String parentDir = Environment.getExternalStorageDirectory().toString() + File.separator + "testfolder";
        List<File> trying = getListFiles(new File(parentDir));
        if (trying.size() > 0) {
            for (int i = 0; i < trying.size(); i++) {
                Log.d(TAG, "Name " + trying.get(i).getName() + " Absolute Path " + trying.get(i).getAbsolutePath() + " URI= " + trying.get(i).toURI());
                images.add(trying.get(i).toURI().toString());
                imageName.add(trying.get(i).getName());
            }
        }

    }

    List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                inFiles.add(file);
            }
        }
        return inFiles;
    }

//    private void getImages() {
//        Log.d(TAG, "getImages: ");
//        String uri = MediaStore.Images.Media.DATA;
//        // if GetImageFromThisDirectory is the name of the directory from which image will be retrieved
//        String condition = uri + "/testfolder";
//        String[] projection = {uri, MediaStore.Images.Media.DATE_ADDED,
//                MediaStore.Images.Media.SIZE};
//        try {
//            Cursor cursor = getContentResolver().query(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
//                    condition, null, null);
//            if (cursor != null) {
//                boolean isDataPresent = cursor.moveToFirst();
//                if (isDataPresent) {
//                    do {
//                        Log.e(TAG, cursor.getString(cursor.getColumnIndex(uri)));
//                    } while (cursor.moveToNext());
//                }
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    //    public String getRealPathFromURI(Context context, Uri contentUri) {
//        Cursor cursor = null;
//        try {
//            String[] proj = { MediaStore.Images.Media.DATA };
//            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//    }
//    private String getRealPathFromURI(Uri contentUri) {
//        String[] proj = { MediaStore.Images.Media.DATA };
//        CursorLoader loader = new CursorLoader(mContext, contentUri, proj, null, null, null);
//        Cursor cursor = loader.loadInBackground();
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        String result = cursor.getString(column_index);
//        cursor.close();
//        return result;
//    }

    void initFab() {
//        fab = new Fab(fabView, list, prefs.getBoolean(PREF_FAB_EXPANSION_BEHAVIOR, false));
//        fab.setOnFabItemClickedListener(id -> {
//            View v = mainActivity.findViewById(id);
//            switch (id) {
//                case R.id.fab_expand_menu_button:
//                    editNote(new Note(), v);
//                    break;
//                case R.id.fab_note:
//                    editNote(new Note(), v);
//                    break;
//                case R.id.fab_camera:
//                    Intent i = mainActivity.getIntent();
//                    i.setAction(ACTION_FAB_TAKE_PHOTO);
//                    mainActivity.setIntent(i);
//                    editNote(new Note(), v);
//                    break;
//                case R.id.fab_checklist:
//                    Note note = new Note();
//                    note.setChecklist(true);
//                    editNote(note, v);
//                    break;
//            }
//        });

//        fabView.setOn
    }


}