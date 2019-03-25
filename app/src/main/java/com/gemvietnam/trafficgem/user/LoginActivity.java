package com.gemvietnam.trafficgem.user;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.screen.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    // use later
    public static final String KEY_USER_TO_MAIN = "KEY_USER_TO_MAIN";
    public static final String KEY_PASSWORD_TO_MAIN = "KEY_PASSWORD_TO_MAIN";
    public static final String KEY_USER_FROM_REGISTER = "KEY_USER_FROM_REGISTER";
    public static final String KEY_PASSWORD_FROM_REGISTER = "KEY_PASSWORD_FROM_REGISTER";

    public static final int REQUEST_CODE_LOGIN = 0;
    public static final int REQUEST_CODE_REGISTER = 1;

    @BindView(R.id.et_activity_login_input_email)
    EditText etEditMail;
    @BindView(R.id.et_activity_login_input_password)
    EditText etEditPassword;
    @BindView(R.id.b_activity_login_login)
    AppCompatButton bLogin;
    @BindView(R.id.tv_activity_login_link_register)
    TextView tvRegisterLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // hide keyboard
        //ViewUtils.hideKeyBoard(this);

        // set on click login button
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
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
     * doing login, It's not completely yet
     */
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        // when login, can't press button login
        bLogin.setEnabled(false);

        // create progress dialog to make color =))
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        // use email, password to check on server, do it later
        String email = etEditMail.getText().toString();
        String password = etEditPassword.getText().toString();

        // TODO: Implement your own authentication logic here.
        // do something here, below is test

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 1000);
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
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    /**
     * override method when click back on navigation bar
     */
    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    /**
     * do something when login successfully
     */
    public void onLoginSuccess() {
        bLogin.setEnabled(true);
        MainActivity.isLoginSuccess = true;
        finish();
    }

    /**
     * do something when login failed
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
