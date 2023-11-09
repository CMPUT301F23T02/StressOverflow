package com.example.StressOverflow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextView back_button;
    private EditText email_field;
    private Button reset_password_button;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_page);
        mAuth = FirebaseAuth.getInstance();

        this.back_button = findViewById(R.id.back_button);
        this.email_field = findViewById(R.id.email_field);
        this.reset_password_button = findViewById(R.id.reset_password_button);

        this.back_button.setOnClickListener((v) -> {
            Intent i = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
            startActivity(i);
        });

        this.reset_password_button.setOnClickListener((v) -> {
            String emailToSend = email_field.getText().toString();
            if (emailToSend.isEmpty()) {
                email_field.setError("This field cannot be blank");
                return;
            }

            mAuth.sendPasswordResetEmail(emailToSend)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("RESET PASSWORD EMAIL STATUS:", "Email sent.");
                                Intent i = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                                startActivity(i);
                            } else {
                                Log.w("RESET PASSWORD EMAIL STATUS:", "Reset password:failure", task.getException());
                                Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset password email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

    }
}
