package im.bsk.storyalbum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;

import java.io.File;


public class FileUtil {
    @SuppressLint("DefaultLocale")
    @Nullable
    public static File createInternalFile(Context context, String fileName) {
        File file = new File(context.getCacheDir(), fileName);
        for(int i = 1; i < 10 && file.exists(); i++) {
            file = new File(context.getFilesDir(), String.format("%s-%d.%s",
                    getNameWithoutExtension(fileName), i, getExtension(fileName)));
        }

        if(file.exists())
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
}
