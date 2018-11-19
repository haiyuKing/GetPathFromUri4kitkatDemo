package com.why.project.getpathfromuri4kitkatdemo.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;

/**
 * @CreateBy HaiyuKing
 * @Used Android 4.4 kitkat以上及以下根据uri获取路径的方法
 * @参考资料 http://www.2cto.com/kf/201502/376975.html
 */
public class GetPathFromUri4kitkat {
	/**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     * @param uri - Android6.0 content://com.android.providers.media.documents/document/image%3A593410
     *            Android4.2.2 file:///storage/emulated/0/Pictures/Screenshots/Screenshot_2017-04-17-14-39-13.png
     * @return Android6.0 /storage/emulated/0/Pictures/Screenshots/Screenshot_20170706-113240.png
     *          Android4.2.2 /storage/emulated/0/Pictures/Screenshots/Screenshot_2017-04-17-14-39-13.png
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
 
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
 
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
 
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
 
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
 
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
 
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
 
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
 
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };
 
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
 
        return null;
    }
 
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     * 
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query. 构造筛选语句
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
            String[] selectionArgs) {
 
        Cursor cursor = null;

        //筛选列
        final String column = MediaStore.Files.FileColumns.DATA;//"_data"
        final String[] projection = { column };
 
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }catch (IllegalArgumentException e){
            //java.lang.IllegalArgumentException: column '_data' does not exist
            //华为的特殊处理：content://com.huawei.hidisk.fileprovider/root/storage/emulated/0/tencent/TIMfile_recv/xxx.doc
            String rootPre = File.separator + "root";// /root
            return uri.getPath().startsWith(rootPre) ? uri.getPath().replace(rootPre,"") : uri.getPath();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
 
    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
 
    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
 
    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
