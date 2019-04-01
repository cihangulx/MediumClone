package com.cihangul.mediumclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.cihangul.mediumclone.MainActivity;
import com.cihangul.mediumclone.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class Login extends AppCompatActivity implements View.OnClickListener , LogInCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() ==  R.id.register_text){

        }
    }

    public void login(View view) {

        TextInputEditText userName = findViewById(R.id.user_name);
        TextInputEditText password = findViewById(R.id.password);

        if (TextUtils.isEmpty(userName.getText())){
            ((TextInputLayout) findViewById(R.id.user_name)).setError(getString(R.string.user_name_error_text));
            return;
        }

        if (TextUtils.isEmpty(password.getText())){
            ((TextInputLayout) findViewById(R.id.password_il)).setError(getString(R.string.password_error_text));
            return;
        }

        ParseUser.logInInBackground(userName.getText().toString().trim(),password.getText().toString().trim(),this);
    }


    @Override
    public void done(ParseUser user, ParseException e) {
        if (e == null){
            startActivity(new Intent(this, MainActivity.class));
        }else {
            Toast.makeText(this,"Wrong password or password",Toast.LENGTH_LONG).show();
        }
    }
}
