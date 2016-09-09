package io.github.rahulrvp.drive_file_downloader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rahul on 9/9/16.
 */
public class DriveFileDownloader {

    /**
     * Downloads a file to given destination folder when provided with a content Uri.
     *
     * @param context               valid context object
     * @param sourceUri             source uri, generally the content uri of file in Google drive.
     * @param destinationFolderPath the folder to which the file should be downloaded
     * @return a valid uri if download was success, else null
     * @throws IOException throws exception when file is not found
     */
    public static Uri downloadFile(Context context, Uri sourceUri, String destinationFolderPath) throws IOException {
        Uri uri = null;

        InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);

        if (inputStream != null && !TextUtils.isEmpty(destinationFolderPath)) {

            File outputFile = new File(destinationFolderPath + File.separator + getFileNameFromUri(context, sourceUri));

            FileOutputStream outputStream = new FileOutputStream(outputFile);

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            uri = Uri.parse("file://" + outputFile.getAbsolutePath());
        }

        return uri;
    }

    /**
     * Extracts file name from a given Uri
     *
     * @param context valid context object
     * @param uri     uri from which the file name has to be extracted
     * @return file name (with extension if available)
     */
    public static String getFileNameFromUri(Context context, Uri uri) {
        String fileName = null;

        if (context != null && uri != null) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
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

    /**
     * Creates a folder with name provided in appName inside external storage root dir.
     *
     * @param appName folder name
     * @return path to the folder, null if folder creation fails
     */
    public static String getPublicAppFolderPath(String appName) {
        String path = Environment.getExternalStorageDirectory() + File.separator + appName;

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
