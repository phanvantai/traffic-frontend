package com.gemvietnam.trafficgem.user;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.library.Credential;
import com.gemvietnam.trafficgem.library.User;
import com.gemvietnam.trafficgem.library.responseMessage.Constants;
import com.gemvietnam.trafficgem.library.responseMessage.GetProfileResponse;
import com.gemvietnam.trafficgem.library.responseMessage.LoginResponse;
import com.gemvietnam.trafficgem.screen.main.MainActivity;
import com.gemvietnam.trafficgem.service.DataExchange;
import com.gemvietnam.trafficgem.utils.AppUtils;
import com.orhanobut.hawk.Hawk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.gemvietnam.trafficgem.utils.Constants.LAST_USER;
import static com.gemvietnam.trafficgem.utils.Constants.LOGIN_TIME_FORMAT;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    public static final int REQUEST_CODE_REGISTER = 1;

    @BindView(R.id.et_activity_login_input_email)
    EditText etEditMail;
    @BindView(R.id.et_activity_login_input_password)
    EditText etEditPassword;
    @BindView(R.id.b_activity_login_login)
    AppCompatButton bLogin;
    @BindView(R.id.tv_activity_login_link_register)
    TextView tvRegisterLink;

    User mLastUser;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (!Hawk.isBuilt()) {
            Hawk.init(getApplicationContext()).build();
        }

        checkPermissions();

        // hide keyboard
        //ViewUtils.hideKeyBoard(this);

        // kiểm tra xem đã có user đăng nhập chưa
        if (Hawk.contains(LAST_USER)) {
            // nếu có thì kiểm tra phiên đăng nhập
            mLastUser = Hawk.get(LAST_USER);
            if (!mLastUser.isExpired()) {
                // nếu còn thời gian thì vào thẳng MainAcitivity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }

        // Thưc hiện hành động khi bấm vào nút Login
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // kiểm tra xem có mạng hay không đã
                if (!AppUtils.networkOk(getApplicationContext())) {
                    AppUtils.showAlertNetwork(LoginActivity.this);
//                    AppUtils.showAlertNetwork(LoginA);
                } else {
                    doLogin();
                }
            }
        });

        // Thưc hiện hành động khi bấm vào Register link
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the register activity
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REQUEST_CODE_REGISTER);
            }
        });
    }

    /**
     * check and request all permission if needs
     */
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CAMERA}, 56);
        }
    }

    /**
     * doing login
     */
    public void doLogin() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }
        // create progress dialog to make color =))
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        // TODO: Implement your own authentication logic here.

        new Thread(new Runnable() {
            @Override
            public void run() {
                // get credential when user enters
                String email = etEditMail.getText().toString();     // get email
                String password = etEditPassword.getText().toString();  // get password
                String loginTime = LOGIN_TIME_FORMAT.format(new Date());
                String md5Password = AppUtils.md5PasswordLogin(password, loginTime);    // encode password
                    // create credential object
                Credential credential = new Credential(email, md5Password, loginTime);
                    // perform send credential
                DataExchange login = new DataExchange();
                String responseLoginFormatString = login.sendCredential(credential.exportStringFormatJson());
                final LoginResponse loginResponse = new LoginResponse(responseLoginFormatString);
                loginResponse.analysis();
                if (loginResponse.getSuccess()) {
                    Hawk.put(Constants.Password, AppUtils.md5PasswordRegister(password));   // save password
                        // perform get user' profile
                    DataExchange getUserProfile = new DataExchange();
                    String userProfileResponse = getUserProfile.getUserProfile(loginResponse.getToken());   // get token
                    GetProfileResponse userProfile = new GetProfileResponse(userProfileResponse);
                    userProfile.analysis();
                        // save user's profile
                    mLastUser = userProfile.getMobileUser();
                    mLastUser.setLastLogin(System.currentTimeMillis());
                    mLastUser.setToken(loginResponse.getToken());   // save token
                    Hawk.put(email, mLastUser);
                    Hawk.put(LAST_USER, mLastUser);
                        // switch to the main screen
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), loginResponse.getMessage() , Toast.LENGTH_LONG).show();
                        }
                    });
                    progressDialog.dismiss();
                }
            }
        }).start();

    }


    /**
     * override method, handle intent contain data from register activity
     * @param requestCode request register code
     * @param resultCode result code : ok/ canceled/ first_user
     * @param data data from register activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_REGISTER) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful register logic here
                // If register is successfully, automatically doLogin and go to MainActivity
                this.finish();
            }
        }
    }

    /**
     * do something when doLogin failed
     */
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        bLogin.setEnabled(true);
    }

    /**
     * check email and password is validate or invalid
     * @return
     */
    public boolean validate() {
        boolean valid = true;

        String email = etEditMail.getText().toString();
        String password = etEditPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEditMail.setError(this.getString(R.string.rule_email));
            valid = false;
        } else {
            etEditMail.setError(null);
        }

        if (password.isEmpty() || password.length() < 8) {
            etEditPassword.setError(this.getString(R.string.rule_password));
            valid = false;
        } else {
            etEditPassword.setError(null);
        }

        return valid;
    }

    public String demoLoginResponse(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.Success, true);
            jsonObject.put(Constants.Message, "success");
            jsonObject.put(Constants.Token, "1111111111");
        } catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String demoUserProfileResponse(){
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {
            jsonObject.put(Constants.Success, true);
            jsonObject.put(Constants.Message, "success");

            jsonData.put(Constants.Email, "thanh@gmail.com");
            jsonData.put(Constants.Name, "thanh");
            jsonData.put(Constants.Phone, "123456");
            jsonData.put(Constants.Address, "ha noi");
            jsonData.put(Constants.Vehicle, "cars");
            jsonData.put(Constants.pathImage, "image");

            jsonObject.put("data", jsonData);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}