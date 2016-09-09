package com.github.rahulrvp.getdriveurl;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.github.rahulrvp.drive_file_downloader.DriveFileDownloader;

public class MainActivity extends AppCompatActivity {

    private DriveListAdapter mAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        ListView listView = (ListView) findViewById(R.id.drive_item_list);
        if (listView != null) {
            mAdapter = new DriveListAdapter();
            listView.setAdapter(mAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DriveItem driveItem = mAdapter.getItem(position);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setType(driveItem.getMime());
                    intent.setData(driveItem.contentUri);

                    startActivity(intent);
                }
            });
        }
    }

    public void onGetFromDriveClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    0);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            final Uri sourceUri = data.getData();

            String fileName = DriveFileDownloader.getFileNameFromUri(this, data.getData());

            String externalDirPath = DriveFileDownloader.getPublicAppFolderPath(getString(R.string.app_name));

            final String destinationPath = externalDirPath + File.separator + fileName;

            // option 1: download to a file
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DriveFileDownloader.downloadFile(mContext, sourceUri, destinationPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).run();

            // option 2: add a preview list item
            mAdapter.addItem(getDriveItem(sourceUri));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private DriveItem getDriveItem(Uri uri) {
        DriveItem item = new DriveItem();

        if (uri != null) {
            item.setName(DriveFileDownloader.getFileNameFromUri(this, uri));
            item.setContentUri(uri);

            String mime = getContentResolver().getType(uri);
            item.setMime(mime);
        }

        return item;
    }
}
