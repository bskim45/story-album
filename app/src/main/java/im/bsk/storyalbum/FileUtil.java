package im.bsk.storyalbum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


public class FileUtil {
    public static final String TAG = FileUtil.class.getSimpleName();

    @SuppressLint("DefaultLocale")
    @Nullable
    public static File createInternalFile(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        for (int i = 1; i < 10 && file.exists(); i++) {
            file = new File(context.getFilesDir(), String.format("%s-%d.%s",
                    getNameWithoutExtension(fileName), i, getExtension(fileName)));
        }

        if (file.exists())
            file = null;

        return file;
    }

    public static String getNameWithoutExtension(String fileName) {
        int pos = fileName.lastIndexOf(".");
        if (pos > 0) {
            fileName = fileName.substring(0, pos);
        }

        return fileName;
    }

    public static String getExtension(String fileName) {
        String ext = "";
        int i = fileName.lastIndexOf('.');

        if (i > 0 && i < fileName.length() - 1) {
            ext = fileName.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public static File getExternalFile(Context context, String fileName) {
        if (!isExternalStorageWritable()) {
            Toast.makeText(context, R.string.error_no_external_storage, Toast.LENGTH_SHORT).show();
        }
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name));

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create external directory");
                return null;
            }
        }

        return new File(mediaStorageDir.getPath(), fileName);
    }

    // Checks if external storage is available for read and write
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static void copy(File src, File dst) throws IOException {
        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            inputChannel = new FileInputStream(src).getChannel();
            outputChannel = new FileOutputStream(dst).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
        } finally {
            try {
                if (inputChannel != null) inputChannel.close();
            } catch (IOException ignored) {}

            try {
                if (outputChannel != null) outputChannel.close();
            } catch (IOException ignored) {}
        }
    }
}
