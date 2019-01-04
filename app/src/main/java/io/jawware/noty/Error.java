package io.jawware.noty;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Error extends AppCompatActivity {

    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        errorText = (TextView) findViewById(R.id.error_text);

        errorText.setText("Oops! your lost...");

        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                errorText.setText("Redirecting to home in " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                errorText.setText("Bye!");
                startActivity(new Intent(Error.this,LogIn.class));
            }

        }.start();


    }

}
