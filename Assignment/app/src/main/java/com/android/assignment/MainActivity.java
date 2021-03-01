package com.android.assignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.RoomDatabase;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import Interface.OnLoadMoreListener;
import Interface.UserListDao;
import Model.ProfileDetails;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public RecyclerView recycler;

    ListAdapter adapter;
    AppDatabase mDb;
    UserListDao userList_Dao;
    UpdateData fetch_data;
    int total_Pages, pageNo;
    FloatingActionButton fabs,fab2;
    LinearLayout fabLayout1;
    boolean isFABOpen = false;
    String picturePath;
    Uri selectedImage;
    int serialNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        init();

    }

    private void init() {
        requestGallery();
        fetch_data = new UpdateData(MainActivity.this);

        mDb = AppDatabase.getDatabase(this);
        userList_Dao = mDb.userDao();
        recycler = findViewById(R.id.recycler);
        fabs = findViewById(R.id.fabs);
        fabs.setOnClickListener(this);
        fab2 = findViewById(R.id.fab2);
        fabLayout1 = findViewById(R.id.fabLayout1);
        fabLayout1.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());

        adapter = new ListAdapter(MainActivity.this, new ArrayList<ProfileDetails>(), recycler);
        recycler.setAdapter(adapter);
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (pageNo < total_Pages) {
                    pageNo = pageNo + 1;
                    fetch_data.fetchData(pageNo);
                }

            }
        });




    }


    @Override
    protected void onResume() {
        super.onResume();
        pageNo = 0;
        fetch_data.new fetchData(pageNo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabs:
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
                break;
            case R.id.fabLayout1:{
                fetch_data.showDialog();
                break;
            }
        }
    }

    private void showFABMenu() {
        isFABOpen = true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab2.setVisibility(View.VISIBLE);
    }


    private void closeFABMenu() {
        isFABOpen = false;
        fabLayout1.animate().translationY(0);
        fab2.setVisibility(View.GONE);
        fabLayout1.setVisibility(View.GONE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101&& null != data) {
            selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            String idStr = picturePath.substring(picturePath.lastIndexOf('/') + 1);
            fetch_data.btn_image.setText(idStr);
            //Savefile(picturePath);
            cursor.close();
        }
    }



    /*
     * App Permissions
     */
    private static String[] PERMISSIONS_GALLERY = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int REQUEST_GALLERY_PERMISSION = 5;

    private boolean requestGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_GALLERY, REQUEST_GALLERY_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS_GALLERY, REQUEST_GALLERY_PERMISSION);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_GALLERY_PERMISSION) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}