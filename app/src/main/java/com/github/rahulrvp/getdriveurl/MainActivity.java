package com.github.rahulrvp.getdriveurl;

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

public class MainActivity extends AppCompatActivity {

    private DriveListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

            // option 1: download to a file
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        writeToFile(sourceUri);
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
            item.setName(getFileNameFromUri(uri));
            item.setContentUri(uri);

            String mime = getContentResolver().getType(uri);
            item.setMime(mime);
        }

        return item;
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;

        if (uri != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                if (!cursor.isAfterLast()) {
                    cursor.moveToFirst();

                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }

                cursor.close();
            }
        }

        return fileName;
    }

    private Uri writeToFile(Uri sourceUri) throws IOException {
        Uri uri = null;

        InputStream inputStream = getContentResolver().openInputStream(sourceUri);

        if (inputStream != null) {
            String path = getLocalDirName();

            if (path != null) {
                File outputFile = new File(path + File.separator + getFileNameFromUri(sourceUri));

                FileOutputStream outputStream = new FileOutputStream(outputFile);

                int read;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

                uri = Uri.parse("file://" + outputFile.getAbsolutePath());
            }
        }

        return uri;
    }

    private String getLocalDirName() {
        String path = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name);

        File localDir = new File(path);
        if (!localDir.exists()) {
            boolean isSuccess = localDir.mkdirs();

            if (!isSuccess) {
                path = null;
            }
        }

        return path;
    }
}
