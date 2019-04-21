package com.gemvietnam.trafficgem.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.library.Credential;
import com.gemvietnam.trafficgem.library.User;
import com.gemvietnam.trafficgem.library.responseMessage.Constants;
import com.gemvietnam.trafficgem.library.responseMessage.LoginResponse;
import com.gemvietnam.trafficgem.library.responseMessage.RegisterResponse;
import com.gemvietnam.trafficgem.service.DataExchange;
import com.gemvietnam.trafficgem.utils.AppUtils;
import com.gemvietnam.trafficgem.utils.CustomToken;
import com.orhanobut.hawk.Hawk;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.gemvietnam.trafficgem.utils.Constants.LAST_USER;
import static com.gemvietnam.trafficgem.utils.Constants.MY_TOKEN;
import static com.gemvietnam.trafficgem.utils.Constants.URL_LOGIN;
import static com.gemvietnam.trafficgem.utils.Constants.URL_REGISTER;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    public static final int SELECT_GALLERY_IMAGE = 12;

    @BindView(R.id.et_activity_register_input_name)
    EditText etName;
    @BindView(R.id.et_activity_register_input_phone)
    EditText etPhone;
    @BindView(R.id.et_activity_register_input_address)
    EditText etAddress;
    @BindView(R.id.et_activity_register_input_email)
    EditText etEmail;
    @BindView(R.id.et_activity_register_input_password)
    EditText etPassword;
    @BindView(R.id.et_activity_register_input_rePassword)
    EditText etRePassword;
    @BindView(R.id.s_activity_register_vehicle)
    Spinner sVehicle;
    @BindView(R.id.b_activity_register_register)
    Button bRegister;
    @BindView(R.id.tv_activity_register_link_login)
    TextView tvLoginLink;
    @BindView(R.id.civ_activity_register_avatar)
    CircleImageView civAvatar;

    String currentPhotoPath;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        civAvatar.setVisibility(View.GONE);

        sVehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // do something
                //mVehicle =
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do something
            }
        });

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppUtils.networkOk(getApplicationContext())) {
                    AppUtils.showAlertNetwork(RegisterActivity.this);
                } else {
                    register();
                }
            }
        });

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

        civAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
            }
        });
    }

    /**
     * open new activity to choose image
     */
    private void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_GALLERY_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK &&requestCode == SELECT_GALLERY_IMAGE) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                currentPhotoPath = cursor.getString(columnIndex);
                cursor.close();

                civAvatar.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * do register, it's not completely yet
     */
    public void register() {
        Log.d(TAG, "Register");

        if (!validate()) {
            onRegisterFailed();
            return;
        }

        //bRegister.setEnabled(false);

        // create progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        // TODO: Implement your own register logic here.

        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();
                String address = etAddress.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String vehicle = sVehicle.getSelectedItem().toString();
                String response, token = "";
                RegisterResponse registerResponse = null;

                String md5Password = AppUtils.md5Password(password);
                User user = new User(email, name, md5Password, vehicle, phone, address);

                DataExchange dataExchange = new DataExchange(URL_REGISTER);

                // demo response
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(Constants.Success, true);
                    jsonObject.put(Constants.Message, "success");
                    //jsonObject.put(Constants.Token,"fdaiojfad");
                } catch (JSONException e){
                    e.printStackTrace();
                }
                try {
                    //dataExchange.sendRegistrationInfo(user);
//                    response = dataExchange.getResponse();
                    response = jsonObject.toString();
                    Log.d("test-response-register", response);
                    registerResponse = new RegisterResponse(response);
                    registerResponse.analysist();
                    //token = registerResponse.getToken();
                } catch (NullPointerException e){
                    e.printStackTrace();
                }

                // gui anh kieu gi day, path currentPhotoPath, token chua co
                //

                //bRegister.setEnabled(true);
                Log.d("Test-success", String.valueOf(registerResponse.getSuccess()));
                if (registerResponse.getSuccess()) {
                    Credential credential = new Credential(email, "");
                    DataExchange dataExchange1 = new DataExchange(URL_LOGIN);
                    dataExchange1.sendCredential(credential);
                    String response1 = dataExchange1.getResponse();

                    LoginResponse loginResponse = new LoginResponse(response1);
                    loginResponse.analysis();

                    Hawk.put(LAST_USER, user);
                    CustomToken customToken;
                    if (Hawk.contains(MY_TOKEN)) {
                        customToken = Hawk.get(MY_TOKEN);
                    } else {
                        customToken = CustomToken.getInstance();
                    }
                    // doan nay cung khong co token nen khong khoi tao dc session, tinh thoi gian dang nhap
                    customToken.setDate(System.currentTimeMillis());
                    customToken.setToken(loginResponse.getToken());
                    Hawk.put(MY_TOKEN, customToken);
                    setResult(RESULT_OK, null);
                    progressDialog.dismiss();
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), registerResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }).start();
    }

    public void onRegisterFailed() {
        Toast.makeText(getBaseContext(), "Register failed", Toast.LENGTH_LONG).show();

        bRegister.setEnabled(true);
    }

    /**
     * Check inform is valid or invalid, not complete yet
     * @return isValid
     */
    public boolean validate() {
        boolean valid = true;

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String rePassword = etRePassword.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            etName.setError(this.getString(R.string.rule_name));
            valid = false;
        } else {
            etName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(this.getString(R.string.rule_email));
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            etPassword.setError(this.getString(R.string.rule_password));
            valid = false;
        } else {
            etPassword.setError(null);
        }

        if (!password.equals(rePassword)) {
            etRePassword.setError(this.getString(R.string.rule_repassword));
            valid = false;
        } else {
            etRePassword.setError(null);
        }

        return valid;
    }
}