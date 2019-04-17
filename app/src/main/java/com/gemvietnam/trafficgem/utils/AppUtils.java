package com.gemvietnam.trafficgem.utils;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Button;

import com.gemvietnam.trafficgem.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AppUtils {

    public static final String CHANEL_ID = "chanel_traffic";
    public static final String CHANEL_NAME = "traffic_gem";
    public static final String START_SERVICE = "start";
    public static final String STOP_SERVICE = "stop";
    public static final int ONGOING_NOTIFICATION_ID = 0211;


    // Format date and time in JsonObject
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    // cache file name .json
    public static final String TRAFFIC_LOG_FILE = "traffic.json";

    /**
     * Create Notification Chanel
     * @param context
     */
    public static void createNotificationChanel(Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(
                    CHANEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            try {
                notificationManager.createNotificationChannel(channel);
            } catch (Exception e) {
                //
            }

        }
    }

    public static synchronized void writeLog(String log) {
//        Log.e("WRITE_LOG", log);
        String sdCardStatus, folderPath;
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


    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static void createDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.sorry_about_somthing));
        builder.setTitle("TrafficGEM");
        builder.setCancelable(true);
//        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    
    /**
     * Decode sampled bitmap from resource.
     *
     * @param res       the res
     * @param resId     the res id
     * @param reqWidth  the req width
     * @param reqHeight the req height
     * @return the bitmap
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Calculate in sample size.
     *
     * @param options   the options
     * @param reqWidth  the req width
     * @param reqHeight the req height
     * @return the int
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
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
    
    /**
     * Md5.
     *
     * @param bytes the bytes
     * @return the string
     */
    public static String md5(byte[] bytes) {
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            digester.update(bytes);
            byte[] digest = digester.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < digest.length; i++) {
                String hex = Integer.toHexString(0xFF & digest[i]);
                if (hex.length() == 1)
                    hexString.append('0');

                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }
    
    /**
     * Thuc hien viec ma hoa MD5 cua String voi chuoi cho truoc
     * @param pass
     * @return
     */
    public static String md5Password(String pass) {
        String tokenCodeMd5 = "";
        // Check
        if (!TextUtils.isEmpty(pass)) {
            // Ca 2 deu rong
            tokenCodeMd5 = AppUtils.md5(pass.getBytes());
        }

        return tokenCodeMd5;
    }
    
    /**
     * Execute https connection
     *
     * @param targetURL     the target url
     * @param urlParameters the url parameters
     * @return the string returned by server
     */
    @SuppressLint("TrulyRandom")
    public static String executePost(String targetURL, String urlParameters) {
        URL url;
        HttpsURLConnection connection = null;
        StringBuffer response = new StringBuffer();
        try {
            url = new URL(targetURL);
            connection = (HttpsURLConnection) url.openConnection();
            // Create the SSL connection
            SSLContext sc;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, null, new SecureRandom());
            connection.setSSLSocketFactory(sc.getSocketFactory());
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(urlParameters.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);

            // Send request
            OutputStream wr = new BufferedOutputStream(connection.getOutputStream());
            wr.write(urlParameters.getBytes());
            wr.flush();
            wr.close();
            // Get Response
            InputStream is = new BufferedInputStream(connection.getInputStream());
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append("\n");
            }
            rd.close();
        } catch (MalformedURLException e) {
            if (BuildConfig.DEBUG) {
                Log.e("executePost", "MalformedURLException: " + e.getLocalizedMessage());
            }
            return null;
        } catch (SocketTimeoutException e) {
            if (BuildConfig.DEBUG) {
                Log.e("executePost", "SocketTimeoutException: " + e.getLocalizedMessage());
            }
            return null;
        }catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.e("executePost", "IOException: " + e.getLocalizedMessage());
            }
            return null;
        } catch (NoSuchAlgorithmException e) {
            if (BuildConfig.DEBUG) {
                Log.e("executePost", "NoSuchAlgorithmException: " + e.getLocalizedMessage());
            }
            return null;
        } catch (KeyManagementException e) {
            if (BuildConfig.DEBUG) {
                Log.e("executePost", "KeyManagementException: " + e.getLocalizedMessage());
            }
            return null;
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e("executePost", "Exception: " + e.getLocalizedMessage());
            }
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response.toString();
    }
}
