package io.jawware.noty;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.security.auth.login.LoginException;

public class LogIn extends AppCompatActivity {

    private String email,password;
    private Button logIn;
    private EditText notyId,notyPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        mAuth = FirebaseAuth.getInstance();

        notyId = (EditText) findViewById(R.id.notyid);
        notyPassword = (EditText) findViewById(R.id.notypass);
        logIn = (Button) findViewById(R.id.login);



        logIn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                int greyColor = ContextCompat.getColor(LogIn.this, R.color.grey);

                logIn.setTextColor(greyColor);

                email = notyId.getText().toString();
                password = notyPassword.getText().toString();

                if(email.isEmpty()){
                    notyId.setError("ID is empty");
                }else if(password.length() < 6){
                    notyPassword.setError("Should be atleast 6 characters");
                }else{
                    authenticateMe(email,password);
                }

            }
        });

    }


    private void authenticateMe(String username, String password){

        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(LogIn.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Intent startit = new Intent(LogIn.this, Dashboard.class);
                startActivity(startit);
            }
        }).addOnFailureListener(LogIn.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LogIn.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            startActivity(new Intent(LogIn.this,Dashboard.class));

            startService(new Intent(LogIn.this, NotyService.class));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            startActivity(new Intent(LogIn.this,Dashboard.class));
        }
    }


}
