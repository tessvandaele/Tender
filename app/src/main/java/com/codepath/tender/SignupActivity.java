package com.codepath.tender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

/* user can sign up for a new tender account */

public class SignupActivity extends AppCompatActivity {

    private EditText etUsernameSignup;
    private EditText etPasswordSignup;
    private EditText etNameSignup;
    private Button btnCreateAccount;
    private ImageButton ibBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsernameSignup = findViewById(R.id.etUsernameSignup);
        etPasswordSignup = findViewById(R.id.etPasswordSignup);
        etNameSignup = findViewById(R.id.etNameSignup);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        ibBack = findViewById(R.id.ibBack);

        //create account button creates a new user in Parse
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create a parse user
                ParseUser user = new ParseUser();

                //set core properties of user
                user.setUsername(etUsernameSignup.getText().toString());
                user.setPassword(etPasswordSignup.getText().toString());

                //invoke sign up in background
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null) { //exception occurred
                            Log.e("SignupActivity", "Sign up unsuccessful", e);
                            return;
                        }

                        //save name of user
                        user.put("name", etNameSignup.getText().toString());
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                //create intent back to log in screen so user can log in with new account
                                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            }
                        });
                    }
                });
            }
        });

        //back button for return navigation
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create intent back to log in screen so user can log in with new account
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}