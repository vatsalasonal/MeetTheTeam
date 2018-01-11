package com.sonal.meettheteam.Utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by HGS on 12/16/2015.
 */
@SuppressWarnings("deprecation")
public class CustomBitmapUtils {

    private static int IMAGE_MAX_SIZE = 512;
    private static float BITMAP_SCALE = 0.5f;

    public static Bitmap getRoundedTopCornersBitmap(Context context,
                                                    Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = convertDipsToPixels(context, pixels);

        // final Rect topRightRect = new Rect(bitmap.getWidth() / 2, 0,
        // bitmap.getWidth(), bitmap.getHeight() / 2);
        final Rect bottomRect = new Rect(0, bitmap.getHeight() / 2,
                bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        // Fill in upper right corner
        // canvas.drawRect(topRightRect, paint);
        // Fill in bottom corners
        canvas.drawRect(bottomRect, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getRoundedBottomCornersBitmap(Context context,
                                                       Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = convertDipsToPixels(context, pixels);

        final Rect topRect = new Rect(0, 0, bitmap.getWidth(),
                bitmap.getHeight() / 2);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawRect(topRect, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap,
                                                int roundDips) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = convertDipsToPixels(context, roundDips);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**

     */
    public static Bitmap create_image(Context context, int id) {

        Bitmap bitmap = null;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true; // declare as purgeable to disk
            bitmap = BitmapFactory.decodeResource(context.getResources(), id,
                    options);
        } catch (RuntimeException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return bitmap;
    }

    public static int convertDipsToPixels(Context context, int dips) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dips * scale + 0.5f);
    }

    public static int converPixelsToDips(Context context, int pixs) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pixs / scale + 0.5f);
    }


    public static Bitmap rotateImage(Bitmap source, float angle) {

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    public static Bitmap getCroppedImage(Bitmap source) {

        float width = source.getWidth();
        float height = source.getHeight();

        float CROP_SIZE = Math.min(width, height);

        float cropWindowX = width / 2 - CROP_SIZE / 2;
        float cropWindowY = height / 2 - CROP_SIZE / 2;

        // Crop the subset from the original Bitmap.
        Bitmap croppedBitmap = Bitmap.createBitmap(source, (int) cropWindowX,
                (int) cropWindowY, (int) CROP_SIZE, (int) CROP_SIZE);

        return croppedBitmap;

    }

    public static boolean saveOutput(File file, Bitmap bitmap) {

        if (bitmap == null)
            return false;

        try {

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            return true;

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {

        Bitmap returnedBitmap = null;

        try {

            InputStream in = context.getContentResolver().openInputStream(uri);

            BitmapFactory.Options bitOpt = new BitmapFactory.Options();
            bitOpt.inJustDecodeBounds = true;

            // get width and height of bitmap
            BitmapFactory.decodeStream(in, null, bitOpt);
            in.close();

            int inSampleSize = BitmapUtils.calculateInSampleSize(bitOpt,
                    250, 250);

            bitOpt = new BitmapFactory.Options();
            bitOpt.inSampleSize = inSampleSize;

            // get bitmap
            in = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(in, null, bitOpt);

            in.close();

            ExifInterface ei = new ExifInterface(uri.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    returnedBitmap = BitmapUtils.rotateImage(bitmap, 90);
                    // Free up the memory
                    bitmap.recycle();
                    bitmap = null;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    returnedBitmap = BitmapUtils.rotateImage(bitmap, 180);
                    // Free up the memory
                    bitmap.recycle();
                    bitmap = null;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    returnedBitmap = BitmapUtils.rotateImage(bitmap, 270);
                    // Free up the memory
                    bitmap.recycle();
                    bitmap = null;
                    break;

                default:
                    returnedBitmap = bitmap;
            }

        } catch (Exception e) {
        }

        return returnedBitmap;
    }

    public static File getOutputMediaFile(Context context) {

        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory() + "/android/data/"
                        + context.getPackageName() + "/temp");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "temp.jpg");

        return mediaFile;
    }

    public static File getOutputMediaFile(Context context, String filename) {

        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory() + "/android/data/"
                        + context.getPackageName() + "/temp");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + filename);

        return mediaFile;
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {

        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null,
                null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static Bitmap loadOrientationAdjustedBitmap(String p_strFileName) {

        int p_nMaxWidth = IMAGE_MAX_SIZE;
        int p_nMaxHeight = IMAGE_MAX_SIZE;
        //

        //
        BitmapFactory.Options w_opt = new BitmapFactory.Options();
        w_opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(p_strFileName, w_opt);
        w_opt.inSampleSize = calculateInSampleSize(w_opt, p_nMaxWidth, p_nMaxHeight);
        w_opt.inJustDecodeBounds = false;
        Bitmap w_bmpCaptured = BitmapFactory.decodeFile(p_strFileName, w_opt);

        //

        //
        ExifInterface w_exif = null;
        try {
            w_exif = new ExifInterface(p_strFileName);
        } catch (Exception e) {
            e.printStackTrace();
            return w_bmpCaptured;
        }
        int w_nOrientation = w_exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        int w_nRotateAngle = 0;
        switch (w_nOrientation) {
            case ExifInterface.ORIENTATION_UNDEFINED:
            case ExifInterface.ORIENTATION_NORMAL:
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                w_nRotateAngle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                w_nRotateAngle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                w_nRotateAngle = 270;
                break;
        }
        w_bmpCaptured = rotateImage(w_bmpCaptured, w_nRotateAngle);
        return w_bmpCaptured;
    }

    /**
     * Do image size limit to max width or height.
     * If the image is bigger than the threshold(500 * 500), it resizes to fit the threshold and save it to the temp folder and return the temp file path.
     * If the image is smaller than the threshold, it returns nothing(null).
     *
     * @param p_bitmap
     * @return
     */
    public static String getSizeLimitedImageFilePath(Bitmap p_bitmap) {

        int w_nMaxWidth = IMAGE_MAX_SIZE;

        int w_nBmpWidth = p_bitmap.getWidth();
        int w_nBmpHeight = p_bitmap.getHeight();

        Bitmap w_bmpSizeLimited = p_bitmap;

        if (w_nBmpWidth > w_nMaxWidth || w_nBmpHeight > w_nBmpHeight) {
            //
            // Resize.
            //
            float rate = 0.0f;
            if (w_nBmpWidth > w_nBmpHeight) {
                rate = w_nMaxWidth / (float) w_nBmpWidth;
                w_nBmpHeight = (int) (w_nBmpHeight * rate);
                w_nBmpWidth = w_nMaxWidth;
            } else {
                rate = w_nMaxWidth / (float) w_nBmpHeight;
                w_nBmpWidth = (int) (w_nBmpWidth * rate);
                w_nBmpHeight = w_nMaxWidth;
            }

            w_bmpSizeLimited = Bitmap.createScaledBitmap(p_bitmap, w_nBmpWidth, w_nBmpHeight, true);

        }
            //
            // Save to the temp folder
            //
        String w_strFilePath = getTempFolderPath() + "photo_temp.jpg";
        try {
            File w_file = new File(w_strFilePath);
            w_file.createNewFile();

            FileOutputStream w_out = new FileOutputStream(w_file);
            w_bmpSizeLimited.compress(Bitmap.CompressFormat.JPEG, 100, w_out);
            w_out.flush();
            w_out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return w_strFilePath;



    }


    public static String getUploadImageFilePath(Bitmap p_bitmap, String filename) {

        int w_nMaxWidth = IMAGE_MAX_SIZE;

        int w_nBmpWidth = p_bitmap.getWidth();
        int w_nBmpHeight = p_bitmap.getHeight();

        Bitmap w_bmpSizeLimited = p_bitmap;

        if (w_nBmpWidth > w_nMaxWidth || w_nBmpHeight > w_nBmpHeight) {
            //
            // Resize.
            //
            float rate = 0.0f;
            if (w_nBmpWidth > w_nBmpHeight) {
                rate = w_nMaxWidth / (float) w_nBmpWidth;
                w_nBmpHeight = (int) (w_nBmpHeight * rate);
                w_nBmpWidth = w_nMaxWidth;
            } else {
                rate = w_nMaxWidth / (float) w_nBmpHeight;
                w_nBmpWidth = (int) (w_nBmpWidth * rate);
                w_nBmpHeight = w_nMaxWidth;
            }

            w_bmpSizeLimited = Bitmap.createScaledBitmap(p_bitmap, w_nBmpWidth, w_nBmpHeight, true);
        }
            //
            // Save to the temp folder
            //
        String w_strFilePath = getUploadFolderPath() + filename;
        try {
            File w_file = new File(w_strFilePath);
            w_file.createNewFile();

            FileOutputStream w_out = new FileOutputStream(w_file);
            w_bmpSizeLimited.compress(Bitmap.CompressFormat.JPEG, 100, w_out);
            w_out.flush();
            w_out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return w_strFilePath;

    }

    public static Bitmap getSizeLimitedBitmap(Bitmap p_bitmap) {

        int w_nMaxWidth = IMAGE_MAX_SIZE;

        int w_nBmpWidth = p_bitmap.getWidth();
        int w_nBmpHeight = p_bitmap.getHeight();

        if (w_nBmpWidth > w_nMaxWidth || w_nBmpHeight > w_nBmpHeight) {

            float rate = 0.0f;
            if (w_nBmpWidth > w_nBmpHeight) {
                rate = w_nMaxWidth / (float) w_nBmpWidth;
                w_nBmpHeight = (int) (w_nBmpHeight * rate);
                w_nBmpWidth = w_nMaxWidth;
            } else {
                rate = w_nMaxWidth / (float) w_nBmpHeight;
                w_nBmpWidth = (int) (w_nBmpWidth * rate);
                w_nBmpHeight = w_nMaxWidth;
            }
            Bitmap w_bmpSizeLimited = Bitmap.createScaledBitmap(p_bitmap, w_nBmpWidth, w_nBmpHeight, true);

            return w_bmpSizeLimited;
        }

        return p_bitmap;
    }

    public static String getSizeLimitedImageFilePath(String p_strBmpFilePath) {
        Bitmap w_bmpSource = decodeFile(p_strBmpFilePath);
        return getSizeLimitedImageFilePath(w_bmpSource);
    }

    public static Bitmap decodeFile(String path) {
        Bitmap b = null;
        File f = new File(path);

        if (!f.exists())
            return b;

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }

    public static String getLocalFolderPath() {

        String w_strTempFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Meettheteam/";
        File w_fTempFolder = new File(w_strTempFolderPath);
        if (w_fTempFolder.exists() && w_fTempFolder.isDirectory()) {
            return w_strTempFolderPath;
        } else {
            w_fTempFolder.mkdir();
            return w_strTempFolderPath;
        }
    }

    public static String getVideoThumbFolderPath() {

        String tempFolderPath = getLocalFolderPath();

        String w_strTempFolderPath = tempFolderPath + "VideoThumb/";
        File w_fTempFolder = new File(w_strTempFolderPath);
        if (w_fTempFolder.exists() && w_fTempFolder.isDirectory()) {
            return w_strTempFolderPath;
        } else {
            w_fTempFolder.mkdir();
            return w_strTempFolderPath;
        }
    }

    public static String getDownloadFolderPath() {

        String tempFolderPath = getLocalFolderPath();

        String w_strTempFolderPath = tempFolderPath + "Download/";
        File w_fTempFolder = new File(w_strTempFolderPath);
        if (w_fTempFolder.exists() && w_fTempFolder.isDirectory()) {
            return w_strTempFolderPath;
        } else {
            w_fTempFolder.mkdir();
            return w_strTempFolderPath;
        }
    }

    public static String getUploadFolderPath() {

        String tempFolderPath = getLocalFolderPath();

        String w_strTempFolderPath = tempFolderPath + "Upload/";
        File w_fTempFolder = new File(w_strTempFolderPath);
        if (w_fTempFolder.exists() && w_fTempFolder.isDirectory()) {
            return w_strTempFolderPath;
        } else {
            w_fTempFolder.mkdir();
            return w_strTempFolderPath;
        }
    }

    public static String getTempFolderPath() {

        String tempFolderPath = getLocalFolderPath();

        String w_strTempFolderPath = tempFolderPath + "Temp/";
        File w_fTempFolder = new File(w_strTempFolderPath);
        if (w_fTempFolder.exists() && w_fTempFolder.isDirectory()) {
            return w_strTempFolderPath;
        } else {
            w_fTempFolder.mkdir();
            return w_strTempFolderPath;
        }
    }


    public static Bitmap retriveVideoFrameFromVideo(String videoPath) {

        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    public static void copyFile(String srcPath, String destPath) {

        try {

            FileInputStream fis = new FileInputStream(srcPath);
            FileOutputStream fos = new FileOutputStream(destPath);

            byte[] buf = new byte[1024];
            int len;
            while ((len = fis.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            fis.close();
            fos.close();

        } catch (Exception e) {
        }

    }

    public static void  setLocked(ImageView v)
    {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        v.setColorFilter(cf);
        v.setAlpha(192);   // 128 = 0.5
    }

    public static void  setUnlocked(ImageView v)
    {
        v.setColorFilter(null);
        v.setAlpha(255);
    }

}
