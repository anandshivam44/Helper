package com.voyager.helper;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.voyager.helper.Adapters.GalleryAdapter;
import com.yalantis.myCustom_ucrop.UCrop;
import com.yalantis.myCustom_ucrop.UCropFragment;
import com.yalantis.myCustom_ucrop.UCropFragmentCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements UCropFragmentCallback {

    private static final String TAG = "MyTag";
    @BindView(R.id.recyclerView_grid)
    RecyclerView recyclerViewGrid;
    final int CAPTURE_IMAGE = 0, CHOOSE_FROM_GALLERY = 101;

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
    Uri selected_image_uri;
    Uri imageUri;
    public static String mCurrentPhotoPath;


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
                selectImage(MainActivity.this);
            }
        });

        fab2 = findViewById(R.id.fab_item_2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Item_2 Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        fab3 = findViewById(R.id.fab_item_3);
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
//                Log.d(TAG, "Name " + trying.get(i).getName() + " Absolute Path " + trying.get(i).getAbsolutePath() + " URI= " + trying.get(i).toURI());
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


    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {


                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, CAPTURE_IMAGE);

                } else if (options[item].equals("Choose from Gallery")) {
//                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(pickPhoto, CHOOSE_FROM_GALLERY);//one can be replaced with any action code

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                            .setType("image/*")
                            .addCategory(Intent.CATEGORY_OPENABLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        String[] mimeTypes = {"image/jpeg", "image/png"};
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    }

                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_FROM_GALLERY);


                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: +Requestcode :" + requestCode + " resultcode :" + resultCode + " data=" + String.valueOf(data) + " -- " + imageUri + " " + data.getData());

        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case CAPTURE_IMAGE:
                    if (resultCode == RESULT_OK && data != null) {
//                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
////                        imageView.setImageBitmap(selectedImage);
//                        selected_image_uri = data.getData();
//                        Log.d(TAG, "onActivityResult: After Camera URI = " + data.getData());


                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        selected_image_uri = data.getData();
                        Log.d(TAG, "onActivityResult: After Camera URI = " + data.getData());


                        startcrop(selected_image_uri);
                    }

                    break;
                case CHOOSE_FROM_GALLERY:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        selected_image_uri = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
//                                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                                startcrop(selected_image_uri);
                            }
                        }

                    }
                    break;
                case UCrop.REQUEST_CROP:
                    handleCropResult(data);
                    break;
                case UCrop.RESULT_ERROR:
                    handleCropError(data);
            }
        }

    }

    private void handleCropError(Intent data) {
        final Throwable cropError = UCrop.getError(data);
        if (cropError != null) {
            Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "unexpected error", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCropResult(Intent data) {
        final Uri resultUri = UCrop.getOutput(data);
        if (resultUri != null) {
//            imageView.setImageURI(resultUri);
            Log.d(TAG, "handleCropResult: CROP SUCCESSFULL bale bale URI=" + resultUri);
        } else {
            Toast.makeText(this, "can not retrieve crop image", Toast.LENGTH_SHORT).show();
        }
    }

    private void startcrop(Uri uri) {
        String destinationFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();

        UCrop ucrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        ucrop.start(MainActivity.this);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    public void loadingProgress(boolean showLoader) {

    }

    @Override
    public void onCropFinish(UCropFragment.UCropResult result) {
        switch (result.mResultCode) {
            case RESULT_OK:
                handleCropResult(result.mResultData);
                break;
            case UCrop.RESULT_ERROR:
                handleCropError(result.mResultData);
                break;
        }
//        removeFragmentFromScreen();
    }


//    public void removeFragmentFromScreen() {
//        getSupportFragmentManager().beginTransaction()
//                .remove(fragment)
//                .commit();
//        toolbar.setVisibility(View.GONE);
//        settingsView.setVisibility(View.VISIBLE);
//    }
}