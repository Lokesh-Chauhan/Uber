package com.example.uber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        if (edtDorP.getText().toString().equals("Driver") || edtDorP.getText().toString().equals("Passenger")) {
            if (ParseUser.getCurrentUser() == null) {
                ParseAnonymousUtils.logIn(new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null && e == null) {
                            user.put("as", edtDorP.getText().toString());
                            Toast.makeText(getApplicationContext(), "Succesfull Log In", Toast.LENGTH_LONG).show();
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                }
                            });
                        }
                    }
                });
            }

        }
    }

    enum State {
        LOGIN, SIGNUP
    }

    private State state;
    private EditText edtUsername, edtPassword, edtDorP;
    private Button btnSignUp, btnOneTimeLogin;
    private RadioButton rdoDriver, rdoPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       if (ParseUser.getCurrentUser()!=null){
           if (ParseUser.getCurrentUser().get("as").equals("Passenger")) {

               passenger();
           }
           if (ParseUser.getCurrentUser().get("as").equals("Driver")) {

               driver();
           }
       }
        setTitle("SignUp");

        state = state.SIGNUP;

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.estPassword);
        edtDorP = findViewById(R.id.edtDorP);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnOneTimeLogin = findViewById(R.id.btnOneTimeLogin);
        rdoDriver = findViewById(R.id.rdoDriver);
        rdoPassenger = findViewById(R.id.rdoPassenger);
        btnOneTimeLogin.setOnClickListener(this);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == state.SIGNUP) {
                    if (rdoDriver.isChecked() == false && rdoPassenger.isChecked() == false) {
                        Toast.makeText(MainActivity.this, "Specify driver or Passenger ?", Toast.LENGTH_LONG).show();
                        return;
                    }
                    ParseUser parseUser = new ParseUser();
                    parseUser.put("username", edtUsername.getText().toString());
                    parseUser.put("password", edtPassword.getText().toString());
                    if (rdoDriver.isChecked()) {
                        parseUser.put("as", "Driver");
                    } else if (rdoPassenger.isChecked()) {
                        parseUser.put("as", "Passenger");
                    }
                    parseUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "SignedUp", Toast.LENGTH_LONG).show();
                                if (ParseUser.getCurrentUser().get("as").equals("Passenger")) {

                                    passenger();
                                }
                                if (ParseUser.getCurrentUser().get("as").equals("Driver")) {

                                    driver();
                                }
                            }
                                }
                            });
                } else if (state == state.LOGIN) {
                    ParseUser.logInInBackground(edtUsername.getText().toString(), edtPassword.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null && e == null) {
                                Toast.makeText(getApplicationContext(), "Succesfully LoggedIn", Toast.LENGTH_LONG).show();
                                if (ParseUser.getCurrentUser().get("as").equals("Passenger")) {

                                    passenger();
                                }
                                if (ParseUser.getCurrentUser().get("as").equals("Driver")) {

                                    driver();
                                }
                                }
                            }
                    });

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signup_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logInItem:
                if (state == state.SIGNUP) {
                    state = state.LOGIN;
                    setTitle("LogIn");
                    item.setTitle("SignIn");
                    btnSignUp.setText("LogIn");
                } else if (state == state.LOGIN) {
                    state = state.SIGNUP;
                    setTitle("SignUp");
                    item.setTitle("LogIn");
                    btnSignUp.setText("SignUp");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void passenger() {
        if (ParseUser.getCurrentUser() != null) {
            if (ParseUser.getCurrentUser().get("as").equals("Passenger")) {
                Intent intent = new Intent(MainActivity.this, PassengerActivity.class);
                startActivity(intent);
            }
        }
    }

    private void driver(){
        if (ParseUser.getCurrentUser()!=null){
            if (ParseUser.getCurrentUser().get("as").equals("Driver"));
            Intent intent=new Intent(MainActivity.this,DriverActivity.class);
            startActivity(intent);
        }
    }
}