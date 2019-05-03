package com.gemvietnam.trafficgem.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gemvietnam.trafficgem.BuildConfig;
import com.gemvietnam.trafficgem.R;
import com.squareup.picasso.Picasso;

import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import static com.gemvietnam.trafficgem.utils.Constants.CHANEL_ID;
import static com.gemvietnam.trafficgem.utils.Constants.CHANEL_NAME;
import static com.gemvietnam.trafficgem.utils.Constants.SALT_BCRYPT;
import static com.gemvietnam.trafficgem.utils.Constants.TOKEN;

public class AppUtils {

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
     * View content with custom Toast.
     *
     * @param context the context
     * @param message
     * @param length
     */
    public static void showCustomAlert(Context context, String message, int length) {
        // Create layout inflator object to inflate toast.xml file
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Call toast.xml file for toast layout
        View toastRoot = inflater.inflate(R.layout.toast, null);
        TextView contentTextView = (TextView) toastRoot.findViewById(R.id.content_textview);
        contentTextView.setText(message);
        Toast toast = new Toast(context);

        // Set layout to toast
        toast.setView(toastRoot);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(length);
        toast.show();
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
     * Return MD5 hash string
     * @param pass
     * @return
     */
    public static String md5PasswordLogin(String pass, String time) {
        String tokenCodeMd5 = "";
        // Check
        if (!TextUtils.isEmpty(pass)) {
            // Ca 2 deu rong
            tokenCodeMd5 = AppUtils.md5((md5(pass.getBytes())+time).getBytes());
        }

        return tokenCodeMd5;
    }

    public static String md5PasswordRegister(String pass){
        String hashCode = "";
        if(!TextUtils.isEmpty(pass)){
            hashCode = md5(pass.getBytes());
        }
        return hashCode;
    }
    /**
     * Return bcrypt hash
     * @param pass
     * @return hash
     */
    public static String bcryptPassword(String pass) {
        return BCrypt.hashpw(pass, SALT_BCRYPT);
    }

    /**
     * Execute https connection (HttpsURLConnection)
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
//            connection.setSSLSocketFactory(sc.getSocketFactory());
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
    
    /**
     * Execute post http connection (HttpURLConnection)
     *
     * @param targetURL     the target url
     * @param urlParameters the url parameters
     * @return the string
     */
    public static String executePostHttp(String targetURL, String urlParameters) {
        URL url;
        HttpURLConnection connection = null;
        StringBuffer response = new StringBuffer();
        try {
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(urlParameters.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length",
                    "" + Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setConnectTimeout(30000);

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
            Log.d("Test-response", response.toString());
            rd.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return response.toString();
    }

    /**
     * Execute get http connection (HttpURLConnection)
     *
     * @param targetURL     the target url
     * @param urlParameters the url parameters
     * @return the string
     */
    public static String executeGetHttp(String targetURL, String urlParameters) {
        URL url;
        HttpURLConnection connection = null;
        StringBuffer response = new StringBuffer();
        try {
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty(TOKEN, urlParameters);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setConnectTimeout(30000);

            // Send request
            //OutputStream wr = new BufferedOutputStream(connection.getOutputStream());
            //wr.write(urlParameters.getBytes());
            //wr.flush();
            //wr.close();
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
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return response.toString();
    }
    
    /**
     * Check network
     *
     * @param context the ctx
     * @return true, if successful
     */
    public static boolean networkOk(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = cm.getActiveNetworkInfo();
        if (nwInfo != null && nwInfo.isConnected()) {
            return true;
        }
        return false;
    }
    
    /**
     * View no network alert
     */
    public static void showAlertNetwork(final Context activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("TrafficGEM");
        builder.setMessage("There is no Internet connection. Do you want to set up now?");
        builder.setPositiveButton("WiFi",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.startActivity(new Intent(
                                android.provider.Settings.ACTION_WIFI_SETTINGS));
                    }
                });
        builder.setNeutralButton("Mobile Network",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.startActivity(new Intent(
                                android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    }
                });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    public static void loadImage(String url, ImageView imageView) {
        Picasso.get()
                .load(url)
                .centerCrop()
                .resize(100, 100)
                .into(imageView);
    }
}
