package com.gemvietnam.trafficgem.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppUtils {

    public static final String TRAFFIC_LOG_FILE = "traffic_log.txt";

    public static synchronized void writeLog(String log) {
//        Log.e("WRITE_LOG", log);
        String sdCardStatus = "", folderPath = "";
        try {
            sdCardStatus = Environment.getExternalStorageState();
            folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Traffic/";
            if (Environment.MEDIA_MOUNTED.equals(sdCardStatus)) {
                // Check Traffic folder
                File trafficDir = new File(folderPath);
                if (!trafficDir.exists()) {
                    if (!trafficDir.mkdirs()) {
                        Log.e("ERROR", "Create file error: " + trafficDir.getAbsolutePath());
                    }
                }
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(folderPath + TRAFFIC_LOG_FILE, "rw");
            File f = new File(folderPath + TRAFFIC_LOG_FILE);
            file.seek(f.length());
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
            //String dateWithoutTime = sdf.format(new Date());
            String tmp = log + "\n";
            file.write(tmp.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
