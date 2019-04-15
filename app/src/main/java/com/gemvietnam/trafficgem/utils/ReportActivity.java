package com.gemvietnam.trafficgem.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.library.Message;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PICTURE_CODE = 234;
    String mUrl = "";

    String mPathPicture = "";

    @BindView(R.id.s_activity_report_list)
    Spinner sListReport;
    @BindView(R.id.b_activity_report_ok)
    Button bOk;
    @BindView(R.id.b_activity_report_cancel)
    Button bCancel;
    @BindView(R.id.iv_activity_report_take_picture)
    ImageView ivTakePicture;
    @BindView(R.id.iv_activity_report_image_preview)
    ImageView ivImagePreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        //ivImagePreview.setVisibility(View.GONE);
        ivTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_TAKE_PICTURE_CODE);
            }
        });
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create report and send
                createMessage();
            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void createMessage() {
        String content = sListReport.getSelectedItem().toString();
        int id;
        if (content.equals("Traffic accident")) {
            id = 1;
        } else if (content.equals("Rain")) {
            id = 2;
        } else if (content.equals("Traffic Jam")) {
            id = 3;
        } else if (content.equals("No Traffic Jam")) {
            id = 4;
        } else if (content.equals("Police")) {
            id = 5;
        } else {
            id = 6;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Date date = new Date();
        Message message = new Message(id, location, mPathPicture, date);
        sendReport(mUrl, id, location, mPathPicture, date);
        finish();
    }

    public static void sendReport(String myurl, int idMsg, Location location, String picturePath, Date date){
        DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
        FileBody localFileBody = new FileBody(new File(picturePath));
        HttpPost localHttpPost = new HttpPost(myurl);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        MultipartEntity localMultiPartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        try {
            localMultiPartEntity.addPart("ID", new StringBody(Integer.toString(idMsg)));
            localMultiPartEntity.addPart("Latitude", new StringBody(Double.toString(location.getLatitude())));
            localMultiPartEntity.addPart("Longitude", new StringBody(Double.toString(location.getLongitude())));
            File file = new File(picturePath);
            localMultiPartEntity.addPart("Picture", new FileBody(file));
            localMultiPartEntity.addPart("Date", new StringBody(dateFormat.format(date)));

            localHttpPost.setEntity(localMultiPartEntity);
            HttpResponse response = localDefaultHttpClient.execute(localHttpPost);
            System.out.println("response code "+response.getStatusLine());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e){
            Log.d("Exception", e.toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PICTURE_CODE) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ivImagePreview.setImageBitmap(bitmap);
                ivTakePicture.setVisibility(View.GONE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
