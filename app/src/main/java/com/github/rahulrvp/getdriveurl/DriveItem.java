package com.github.rahulrvp.getdriveurl;

import android.net.Uri;

/**
 * Created by rahul on 9/9/16.
 */
public class DriveItem {
    String imageUrl;
    String name;
    String mime;
    Uri contentUri;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public Uri getContentUri() {
        return contentUri;
    }

    public void setContentUri(Uri contentUri) {
        this.contentUri = contentUri;
    }
}
