package com.gemvietnam.trafficgem.user;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import static com.gemvietnam.trafficgem.utils.Constants.URL_PASSWORD;
import static com.gemvietnam.trafficgem.utils.Constants.URL_PROFILE;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.ll_activity_profile_update_user)
    LinearLayout llUpdate;
    @BindView(R.id.civ_activity_profile_avatar)
    CircleImageView civAvatar;
    @BindView(R.id.et_activity_profile_input_address)
    EditText etAddress;
    @BindView(R.id.et_activity_profile_input_email)
    EditText etEmail;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mLastUser = Hawk.get(LAST_USER);
        mCustomToken = Hawk.get(MY_TOKEN);

        AppUtils.loadImage(mLastUser.getPathAvatar(), civAvatar);
        etName.setText(mLastUser.getName());
        etPhone.setText(mLastUser.getPhone());
        etAddress.setText(mLastUser.getAddress());
        etEmail.setText(mLastUser.getEmail());
        sVehicle.setPrompt(mLastUser.getVehicle());
        llChange.setVisibility(View.GONE);

        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();
                String address = etAddress.getText().toString();
                UpdateProfile updateProfile = new UpdateProfile(name, phone, address);

                DataExchange dataExchange = new DataExchange(URL_PROFILE);
                dataExchange.updateProfile(mCustomToken.getToken(), updateProfile);
                String response = dataExchange.getResponse();

                UpdateProfileResponse updateProfileResponse = new UpdateProfileResponse(response);
                if (updateProfileResponse.getSuccess()) {
                    // thanh cong thi thong bao hay lam gi day
                    mLastUser.setName(name);
                    mLastUser.setPhone(phone);
                    mLastUser.setAddress(address);
                    Hawk.put(LAST_USER, mLastUser);
                } else {

                }
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
                Boolean valid = true;
                String oldPass = etOld.getText().toString();
                String md5Old = AppUtils.md5Password(oldPass);
                String newPass = etNew.getText().toString();
                String md5New = AppUtils.md5Password(newPass);
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

                if (valid) {
//                    DataExchange dataExchange = new DataExchange(URL_PASSWORD);
//                    dataExchange.changePassword(mCustomToken.getToken(), md5Old, md5New);
//                    String response = dataExchange.getResponse();
//                    ChangePasswordResponse changePasswordResponse = new ChangePasswordResponse(response);
//                    if (changePasswordResponse.getSuccess()) {
//                        llUpdate.setVisibility(View.VISIBLE);
//                        llChange.setVisibility(View.GONE);
//                        bChangPassword.setVisibility(View.VISIBLE);
//                        //Toast.makeText(getApplicationContext(), "Password Changed", Toast.LENGTH_LONG).show();
//                    } else {
//                        //Toast.makeText(getApplicationContext(), changePasswordResponse.getMessage(), Toast.LENGTH_LONG).show();
//                    }

                    //llUpdate.setVisibility(View.VISIBLE);
                    //llChange.setVisibility(View.GONE);
                    //bChangPassword.setVisibility(View.VISIBLE);
                } else {
                    //Toast.makeText(getApplicationContext(), "Error!!", Toast.LENGTH_LONG);
                    //return;
                }
            }
        });
    }
}
