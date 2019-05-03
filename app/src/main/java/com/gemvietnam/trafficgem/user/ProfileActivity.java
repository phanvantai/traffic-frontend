package com.gemvietnam.trafficgem.user;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.library.UpdateProfile;
import com.gemvietnam.trafficgem.library.User;
import com.gemvietnam.trafficgem.library.responseMessage.ChangePasswordResponse;
import com.gemvietnam.trafficgem.library.responseMessage.UpdateProfileResponse;
import com.gemvietnam.trafficgem.service.DataExchange;
import com.gemvietnam.trafficgem.utils.AppUtils;
import com.gemvietnam.trafficgem.utils.CustomToken;
import com.orhanobut.hawk.Hawk;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.gemvietnam.trafficgem.utils.Constants.LAST_USER;
import static com.gemvietnam.trafficgem.utils.Constants.MY_TOKEN;
import static com.gemvietnam.trafficgem.utils.Constants.URL_AVATAR;
import static com.gemvietnam.trafficgem.utils.Constants.URL_PASSWORD;
import static com.gemvietnam.trafficgem.utils.Constants.URL_EDIT_PROFILE;

public class ProfileActivity extends AppCompatActivity {
    public static final int SELECT_GALLERY_IMAGE = 12;
    @BindView(R.id.ll_activity_profile_update_user)
    LinearLayout llUpdate;
    @BindView(R.id.civ_activity_profile_avatar)
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

    User mLastUser;
    CustomToken mCustomToken;
    String currentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mLastUser = Hawk.get(LAST_USER);
        mCustomToken = Hawk.get(MY_TOKEN);

//        AppUtils.loadImage(mLastUser.getPathAvatar(), civAvatar);
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

                civAvatar.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath));

//                doUpdataAvatar();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    public void doUpdataAvatar(){
//        new Thread()
//    }

    public void doUpdateProfile(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();
                String address = etAddress.getText().toString();
                String vehicle = sVehicle.getSelectedItem().toString();

                UpdateProfile updateProfile = new UpdateProfile(name, phone, address, vehicle);
                Log.d("test-up","test");
                DataExchange dataExchange = new DataExchange(URL_EDIT_PROFILE);
                Log.d("test-update-profile", updateProfile.exportStringFormatJson());
                dataExchange.updateProfile(mCustomToken.getToken(), updateProfile);

                UpdateProfileResponse updateProfileResponse = new UpdateProfileResponse(dataExchange.getResponse());
                updateProfileResponse.analysis();
                if(updateProfileResponse.getSuccess()){
                    mLastUser.setName(name);
                    mLastUser.setPhone(phone);
                    mLastUser.setAddress(address);
                    mLastUser.setVehicle(vehicle);
                    Hawk.put(LAST_USER, mLastUser);

                }

                String pathImage = "";      //
                DataExchange updateAvatar = new DataExchange(URL_AVATAR);
                updateAvatar.sendPicture(mCustomToken.getToken(), pathImage);
                String responseUpdateAvatar = updateAvatar.getResponse();
//                Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_LONG).show();
                finish();
            }
        }).start();
    }

    public void doChangePassword(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String oldPass = etOld.getText().toString();
                String md5Old = AppUtils.md5PasswordRegister(oldPass);

                String newPass = etNew.getText().toString();
                String md5New = AppUtils.md5PasswordRegister(newPass);
                String reNew = etReNew.getText().toString();

                if (validate()) {
                    DataExchange dataExchange = new DataExchange(URL_PASSWORD);
                    dataExchange.changePassword(mCustomToken.getToken(), md5Old, md5New);
                    dataExchange.getResponse();
                    Log.d("test-pass", "test");
//                    Toast.makeText(getApplicationContext(), "Password Changed", Toast.LENGTH_LONG).show();
                    finish();
                    //llUpdate.setVisibility(View.VISIBLE);
                    //llChange.setVisibility(View.GONE);
                    //bChangPassword.setVisibility(View.VISIBLE);
                } else {
                    //Toast.makeText(getApplicationContext(), "Error!!", Toast.LENGTH_LONG).show();
                    //return;
                }
            }
        }).start();
    }

    public boolean validate(){
        boolean valid = true;
        String oldPass = etOld.getText().toString();
        String newPass = etNew.getText().toString();
        String reNew = etReNew.getText().toString();
        if (oldPass.isEmpty() || oldPass.length() < 4 || oldPass.length() > 10) {
            etOld.setError(getApplicationContext().getString(R.string.rule_password));
            valid = false;
        } else {
            etOld.setError(null);
        }

        if (newPass.isEmpty() || newPass.length() < 4 || newPass.length() > 10) {
            etNew.setError(getApplicationContext().getString(R.string.rule_password));
            valid = false;
        } else {
            etNew.setError(null);
        }

        if (!newPass.equals(reNew)) {
            etReNew.setError(getApplicationContext().getString(R.string.rule_repassword));
            valid = false;
        } else {
            etReNew.setError(null);
        }
        return valid;
    }
}
