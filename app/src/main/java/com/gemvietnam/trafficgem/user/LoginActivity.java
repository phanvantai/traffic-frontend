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
import com.gemvietnam.trafficgem.library.User;
import com.gemvietnam.trafficgem.screen.main.MainActivity;
import com.gemvietnam.trafficgem.utils.AppUtils;
import com.gemvietnam.trafficgem.utils.CustomToken;
import com.orhanobut.hawk.Hawk;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.gemvietnam.trafficgem.utils.Constants.LAST_USER;
import static com.gemvietnam.trafficgem.utils.Constants.MY_TOKEN;
import static com.gemvietnam.trafficgem.utils.Constants.URL_SERVER;

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
    CustomToken mCustomToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Hawk.init(getApplicationContext()).build();
        // hide keyboard
        //ViewUtils.hideKeyBoard(this);

        // get myToken object
        if (Hawk.contains(MY_TOKEN)) {
            mCustomToken = Hawk.get(MY_TOKEN);
        } else {
            mCustomToken = CustomToken.getInstance();
        }

        checkPermissions();

        if (!mCustomToken.isExpired()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            mCustomToken.removeToken();
        }

        // set on click doLogin button
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppUtils.networkOk(getApplicationContext())) {
                    AppUtils.showAlertNetwork(LoginActivity.this);
                } else {
                    doLogin();
                }
            }
        });

        // set on click register link
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
     * doing login, It's not completely yet
     */
    public void doLogin() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        // when doLogin, can't press button doLogin
        //bLogin.setEnabled(false);

        // create progress dialog to make color =))
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();



        // TODO: Implement your own authentication logic here.
        // do something here, below is test
        new Thread(new Runnable() {
            @Override
            public void run() {
                // use email, password to check on server
                String email = etEditMail.getText().toString();
                String password = etEditPassword.getText().toString();
                String md5Password = AppUtils.md5Password(password);
                JSONObject object = new JSONObject();
                try {
                    object.put("password", md5Password);
                    object.put("email", email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String loginUrl = URL_SERVER + "/api/login";
                String params = object.toString();

                String response = AppUtils.executePost(loginUrl, params);

                if (response != null) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        JSONObject userObject = jsonObject.getJSONObject(response);
                        boolean success = userObject.getBoolean("success");
                        String message = userObject.getString("message");
                        if (success) {
                            String token = userObject.getString("remember_token");
                            String name = userObject.getString("name");
                            String picturePath = userObject.getString("image");
                            String user_email = userObject.getString("email");
                            String vehicle = userObject.getString("vehicle");

                            User user = new User(user_email, name, vehicle, picturePath);

                            // On complete call either onLoginSuccess or onLoginFailed
                            mCustomToken.setDate(System.currentTimeMillis());
                            mCustomToken.setToken(token);

                            // add to hawk
                            Hawk.put(LAST_USER, user);
                            Hawk.put(MY_TOKEN, mCustomToken);

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            //intent.putExtra("user", user);
                            startActivity(intent);
                        }// else {
                            // neu khong thanh cong thong bao loi voi noi dung message tu server
                            //AppUtils.showCustomAlert(getApplicationContext(), message, Toast.LENGTH_LONG);
                        //}
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // temp
                User user = new User();
                user.setEmail(email);
                // On complete call either onLoginSuccess or onLoginFailed
                mCustomToken.setDate(System.currentTimeMillis());
                mCustomToken.setToken(md5Password);

                // add to hawk
                Hawk.put(LAST_USER, user);
                Hawk.put(MY_TOKEN, mCustomToken);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //intent.putExtra("user", user);
                startActivity(intent);

                // onLoginFailed();
                progressDialog.dismiss();
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

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            etEditPassword.setError(this.getString(R.string.rule_password));
            valid = false;
        } else {
            etEditPassword.setError(null);
        }

        return valid;
    }
}
