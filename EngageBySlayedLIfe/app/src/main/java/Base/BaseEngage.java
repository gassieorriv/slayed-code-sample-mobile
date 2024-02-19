package Base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import androidx.annotation.RequiresApi;

import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.auth.AWSCredentials;
import com.slayed.life.engage.BuildConfig;
import com.slayed.life.engage.R;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import models.Authorization;
import models.shop.StateTax;
import models.social.GoogleAuth;
import models.users.User;
import sql.auth;

public class BaseEngage extends Application {
    public static final String baseAWSUrl = "https://s3.amazonaws.com/";
    public static String[] facebookLoginPermssions = new String[]
    {
      "public_profile",
      "email",
      "instagram_content_publish",
      "instagram_manage_comments",
      "instagram_manage_insights",
      "instagram_basic",
      "pages_read_engagement",
      "business_management",
      "ads_management",
      "pages_show_list"
    };

    public static StateTax[] StateTaxes;

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static User user;

    public static GoogleAuth googleAuth = new GoogleAuth();

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        createTables();
        user = new User();
    }

    private void createTables() {
        auth.Create((ContextWrapper) context);
    }

    public static Authorization getAuthroizedUser() {
        return  auth.Get((ContextWrapper)context);
    }

    public String getGoogleClient(Context context) {
        if(BuildConfig.DEBUG) {
            return context.getResources().getString(R.string.server_client_id_debug);
        } else {
            return context.getResources().getString(R.string.server_client_id_release);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public InputStream getClientSecret(Context context) {
        InputStream inputStream;
        if(BuildConfig.DEBUG) {
            inputStream = context.getResources().openRawResource(R.raw.client_secret_web);
        } else {
            inputStream = context.getResources().openRawResource(R.raw.client_secret_dev);
        }
        return  inputStream;
    }

    public static String getAwsBucket(Context context) {
        String bucket;
        if(BuildConfig.DEBUG) {
            bucket = context.getResources().getString(R.string.awsdevBucket);
        } else {
            bucket = context.getResources().getString(R.string.awsprodBucket);
        }
        return bucket;
    }

    public static void uploadWithTransferUtility(final Context requestingContext, String path, String FileName, File file,String Type) {
        AWSCredentials awsCredentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return         requestingContext.getResources().getString(R.string.awsS3AccessKey);
            }

            @Override
            public String getAWSSecretKey() {
                return requestingContext.getResources().getString(R.string.awsS3AppSecret);
            }
        };
        AWSConfiguration awsConfiguration = AWSMobileClient.getInstance().getConfiguration();
        AWSMobileClient.getInstance().initialize(requestingContext, awsConfiguration, new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {

            }

            @Override
            public void onError(Exception e) {

            }
        });

        AmazonS3 s3 = new AmazonS3Client(awsCredentials);
        s3.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).disableChunkedEncoding().build());
        TransferUtility transferUtility = TransferUtility.builder().context(requestingContext).awsConfiguration(awsConfiguration).s3Client(s3).build();
        switch (Type.toUpperCase()) {
            case "PRODUCT":
            case "SERVICE":
                try {
                    file = resizeImage(file, (Activity)requestingContext, FileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        file = checkImageOrientation(FileName,(Activity)requestingContext, file);
        String bucket = getAwsBucket(requestingContext);
        final TransferObserver uploadObserver = transferUtility.upload(bucket, path + FileName, file, CannedAccessControlList.PublicReadWrite);
        final File fileToDelete = file;
        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                   fileToDelete.delete();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }
        });

        Log.d("YourActivity", "Bytes Transferrred: " + uploadObserver.getBytesTransferred());
        Log.d("YourActivity", "Bytes Total: " + uploadObserver.getBytesTotal());
    }

    public static void setGoogleAuth(String clientId, String clientSecret, String authCode) {
        googleAuth.authCode = authCode;
        googleAuth.clientId = clientId;
        googleAuth.clientSecret = clientSecret;
    }

    public static File checkImageOrientation(String FileName, Activity context, File file) {
        try {
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), bounds);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG,100 , outStream);

            File f = new java.io.File(context.getApplicationContext().getFileStreamPath(FileName).getPath());
            f.createNewFile();
            f.setWritable(true);
            f.setReadable(true);
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(outStream.toByteArray());
            fo.close();
            file = f;
        }
        catch (Exception ignore) { }
        return file;
    }

    static File resizeImage(File file, Activity context, String FileName) throws IOException {
        File imgFileOrig = file;
        Bitmap b = BitmapFactory.decodeFile(imgFileOrig.getAbsolutePath());
        int origWidth = b.getWidth();
        int origHeight = b.getHeight();
        if(origWidth >= 500) {
            int destHeight = origHeight/( origWidth / 500) ;
            Bitmap b2 = Bitmap.createScaledBitmap(b, 500, destHeight, false);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            b2.compress(Bitmap.CompressFormat.JPEG,100 , outStream);
            File f = new java.io.File(context.getApplicationContext().getFileStreamPath(FileName).getPath());
            f.createNewFile();
            f.setWritable(true);
            f.setReadable(true);
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(outStream.toByteArray());
            fo.close();
            imgFileOrig = f;
        }
        return imgFileOrig;
    }

    public static File RotateImage(String FileName,Activity context, File file){
        try {
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), bounds);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
            int rotationAngle;
            rotationAngle = 90;
            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG,100 , outStream);
            File f = new java.io.File(context
                    .getApplicationContext().getFileStreamPath(FileName)
                    .getPath());
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(outStream.toByteArray());
            fo.close();
            file = f;
        } catch (Exception ignore) { }
        return file;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        if (DocumentsContract.isDocumentUri(context, uri)) {
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
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

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
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

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
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".contains(uri.getAuthority());
    }
}
