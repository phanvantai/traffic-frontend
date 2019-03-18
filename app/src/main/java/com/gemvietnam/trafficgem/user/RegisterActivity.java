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

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    public static final int SELECT_GALLERY_IMAGE = 12;

    @Bind(R.id.et_activity_register_input_name)
    EditText etName;
    @Bind(R.id.et_activity_register_input_email)
    EditText etEmail;
    @Bind(R.id.et_activity_register_input_password)
    EditText etPassword;
    @Bind(R.id.et_activity_register_input_rePassword)
    EditText etRePassword;
    @Bind(R.id.s_activity_register_vehicle)
    Spinner sVehicle;
    @Bind(R.id.b_activity_register_register)
    Button bRegister;
    @Bind(R.id.tv_activity_register_link_login)
    TextView tvLoginLink;
    @Bind(R.id.civ_activity_register_avatar)
    CircleImageView civAvatar;

    private String mVehicle;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

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
                register();
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
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_GALLERY_IMAGE) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                civAvatar.setImageBitmap(BitmapFactory.decodeFile(picturePath));
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

        bRegister.setEnabled(false);

        // create progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        // use later
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String vehicle = sVehicle.getSelectedItem().toString();
        Log.d(TAG, vehicle);

        // TODO: Implement your own register logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onRegisterSuccess or onRegisterFailed
                        // depending on success
                        onRegisterSuccess();
                        // onRegisterFailed();
                        progressDialog.dismiss();
                    }
                }, 1000);
    }


    /**
     * do something when register successfully
     */
    public void onRegisterSuccess() {
        bRegister.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onRegisterFailed() {
        Toast.makeText(getBaseContext(), "Register failed", Toast.LENGTH_LONG).show();

        bRegister.setEnabled(true);
    }

    /**
     * Check inform is valid or invalid, not complete yet
     * @return
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