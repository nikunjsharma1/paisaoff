package com.example.paisaoff;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, GoogleApiClient.ConnectionCallbacks {
    private EditText Email, password,first,middle,last,phoneNo,Alternate,Repassword;
    private Button register;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    String[] users = { "Male", "Female", "Other"};
    CheckBox checkbox;
    GoogleApiClient googleApiClient;
    String key="6LcLIMoZAAAAAA7nHbLH96uImwfzbbyizkRlKnbv";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();

        register = (Button) findViewById(R.id.register);
        first = (EditText) findViewById(R.id.first);
        middle = (EditText) findViewById(R.id.middle);
        last = (EditText) findViewById(R.id.last);
        phoneNo = (EditText) findViewById(R.id.phoneNo);
        Alternate = (EditText) findViewById(R.id.Alternate);
        Repassword = (EditText) findViewById(R.id.Repassword);
        Email = (EditText) findViewById(R.id.Email);
        password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);



        checkbox=findViewById(R.id.checkbox);




        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = Email.getText().toString().trim();
                String Password = password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(Password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, Password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(RegisterActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);

        googleApiClient=new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(RegisterActivity.this)
                .build();
        googleApiClient.connect();
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkbox.isChecked()){
                    SafetyNet.SafetyNetApi.verifyWithRecaptcha(googleApiClient,key)
                            .setResultCallback(new ResultCallback<SafetyNetApi.RecaptchaTokenResult>() {
                                @Override
                                public void onResult(@NonNull SafetyNetApi.RecaptchaTokenResult recaptchaTokenResult) {
                                    Status status=recaptchaTokenResult.getStatus();
                                    if( (status!=null)&&status.isSuccess()){
                                        Toast.makeText(getApplicationContext(),"successfully verified",Toast.LENGTH_SHORT).show();
                                        checkbox.setTextColor(Color.GREEN);
                                    }
                                }
                            });
                }else {
                    checkbox.setTextColor(Color.BLACK);
                }
            }
        });

        Spinner spin = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO - Custom Code
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
