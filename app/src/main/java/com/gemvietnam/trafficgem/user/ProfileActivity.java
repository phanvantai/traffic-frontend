package com.gemvietnam.trafficgem.user;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.library.UpdateProfile;
import com.gemvietnam.trafficgem.library.User;
import com.gemvietnam.trafficgem.library.responseMessage.ChangePasswordResponse;
import com.gemvietnam.trafficgem.library.responseMessage.Constants;
import com.gemvietnam.trafficgem.library.responseMessage.UpdateAvatarResponse;
import com.gemvietnam.trafficgem.library.responseMessage.UpdateProfileResponse;
import com.gemvietnam.trafficgem.service.DataExchange;
import com.gemvietnam.trafficgem.utils.AppUtils;
import com.orhanobut.hawk.Hawk;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.gemvietnam.trafficgem.R.id.civ_activity_profile_avatar;
import static com.gemvietnam.trafficgem.utils.Constants.LAST_USER;

public class ProfileActivity extends AppCompatActivity {
    public static final int SELECT_GALLERY_IMAGE = 12;
    @BindView(R.id.ll_activity_profile_update_user)
    LinearLayout llUpdate;
    @BindView(civ_activity_profile_avatar)
    CircleImageView civAvatar;
    @BindView(R.id.et_activity_profile_input_address)
    EditText etAddress;
    @BindView(R.id.et_activity_profile_input_name)
    EditText etName;
    @BindView(R.id.et_activity_profile_input_phone)
    EditText etPhone;
    @BindView(R.id.s_activity_profile_vehicle)
    Spinner sVehicle;
    @BindView(R.id.b_activity_profile_update)
    Button bUpdate;
    @BindView(R.id.b_activity_profile_change_password)
    Button bChangPassword;
    @BindView(R.id.ll_activity_profile_change_password)
    LinearLayout llChange;
    @BindView(R.id.b_activity_profile_Ok)
    Button bOk;
    @BindView(R.id.et_activity_profile_input_old_password)
    EditText etOld;
    @BindView(R.id.et_activity_profile_input_new_password)
    EditText etNew;
    @BindView(R.id.et_activity_profile_input_reNew_password)
    EditText etReNew;

    private static volatile User mLastUser;
    String currentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        mLastUser = Hawk.get(LAST_USER);
        Log.d("test-avatar", mLastUser.getPathAvatar());
        if(mLastUser.getPathAvatar() != "" && mLastUser.getPathAvatar() != null){
//            AppUtils.loadImage(mLastUser.getPathAvatar(), civAvatar);
            File imgFile = new File(mLastUser.getPathAvatar());
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                ImageView myImage = (ImageView) findViewById(R.id.civ_activity_profile_avatar);
//                myImage.setImageBitmap(myBitmap);
            }
        }


        etName.setText(mLastUser.getName());
        etPhone.setText(mLastUser.getPhone());
        etAddress.setText(mLastUser.getAddress());
        sVehicle.setPrompt(mLastUser.getVehicle());
        llChange.setVisibility(View.GONE);

        civAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFroGallery();
            }
        });

        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppUtils.networkOk(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "NO INTERNET !!!", Toast.LENGTH_LONG).show();
                    return;
                }
                doUpdateProfile();
            }
        });

        bChangPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llUpdate.setVisibility(View.GONE);
                llChange.setVisibility(View.VISIBLE);
                bChangPassword.setVisibility(View.GONE);
            }
        });

        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!AppUtils.networkOk(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "NO INTERNET !!!", Toast.LENGTH_LONG).show();
                    return;
                }
                doChangePassword();
            }
        });
    }

    private void getImageFroGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_GALLERY_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == SELECT_GALLERY_IMAGE){
            Uri selectedImage = data.getData();
            if(selectedImage != null){
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                currentPhotoPath = cursor.getString(columnIndex);
                cursor.close();
//                Log.d("test-path-image", currentPhotoPath);
                civAvatar.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void doUpdateProfile(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                    // get user information edited
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();
                String address = etAddress.getText().toString();
                String vehicle = sVehicle.getSelectedItem().toString();
                    // create updateProfile Object
                UpdateProfile updateProfile = new UpdateProfile(name, phone, address, vehicle);
                    // send user information edited
                DataExchange dataExchange = new DataExchange();
                String getResponse = dataExchange.updateProfile(mLastUser.getToken(), updateProfile.exportStringFormatJson());
                UpdateProfileResponse updateProfileResponse = new UpdateProfileResponse(getResponse);
                updateProfileResponse.analysis();
                if(updateProfileResponse.getSuccess()){
                        // save user information updated
                    mLastUser.setName(name);
                    mLastUser.setPhone(phone);
                    mLastUser.setAddress(address);
                    mLastUser.setVehicle(vehicle);
                    if(currentPhotoPath != null){
                            // get avatar
                        DataExchange updateAvatar = new DataExchange();
                        String fileName = currentPhotoPath.substring(currentPhotoPath.lastIndexOf("/")+1);
                        String getResponseUpdateAvatar = updateAvatar.sendPicture(mLastUser.getToken(), currentPhotoPath);
                        UpdateAvatarResponse updateAvatarResponse = new UpdateAvatarResponse(demoUpdateResponse());
                        updateAvatarResponse.analysis();
                        if(updateAvatarResponse.getSuccess()){
                            mLastUser.setAvatar(currentPhotoPath);
                        }
                    }
                    Hawk.put(LAST_USER, mLastUser);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_LONG).show();
                        }
                    });
                }   else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Profile Failed", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                finish();
            }
        }).start();
    }

    public void doChangePassword(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                    // get old password when user enter
                String oldPass = etOld.getText().toString();
                String md5Old = AppUtils.md5PasswordRegister(oldPass);  // encode pass
                    // get new password when user enter
                String newPass = etNew.getText().toString();
                String md5New = AppUtils.md5PasswordRegister(newPass);  // encode pass
                String reNew = etReNew.getText().toString();

                if (validate()) {   // check pass
                        // perform send request update password
                    DataExchange dataExchange = new DataExchange();
                    String getResposeChangePassword = dataExchange.changePassword(mLastUser.getToken(), md5Old, md5New);
                    final ChangePasswordResponse changePasswordResponse = new ChangePasswordResponse(getResposeChangePassword);
                    changePasswordResponse.analysis();
                    if(changePasswordResponse.getSuccess()){
                        Hawk.put(Constants.Password, md5New);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Password Changed", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), changePasswordResponse.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    finish();
                }
            }
        }).start();
    }

    public boolean validate(){
        boolean valid = true;
        String oldPass = etOld.getText().toString();
        String newPass = etNew.getText().toString();
        String reNew = etReNew.getText().toString();

        if(!AppUtils.md5PasswordRegister(oldPass).equals(Hawk.get(Constants.Password))){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etOld.setError("Password does no match!");
                }
            });
            valid = false;
        }

        if (oldPass.isEmpty() || oldPass.length() < 8) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etOld.setError(getApplicationContext().getString(R.string.rule_password));
                }
            });
            valid = false;
        }

        if (newPass.isEmpty() || newPass.length() < 8 ) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etNew.setError(getApplicationContext().getString(R.string.rule_password));
                }
            });
            valid = false;
//        } else {
//            etNew.setError(null);
        }

        if (!newPass.equals(reNew)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etReNew.setError(getApplicationContext().getString(R.string.rule_repassword));
                }
            });
            valid = false;
//        } else {
//            etReNew.setError(null);
        }
        return valid;
    }

    public String demoUpdateResponse(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.Success, true);
            jsonObject.put(Constants.Message, "success");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
