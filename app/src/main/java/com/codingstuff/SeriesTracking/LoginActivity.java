package com.codingstuff.SeriesTracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button checkButton;
    private Button logoutButton;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        checkButton = findViewById(R.id.checkButton);
        logoutButton = findViewById(R.id.logoutButton);
        resultTextView = findViewById(R.id.resultTextView);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLoginStatus();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            resultTextView.setText("Login com sucesso");
                            logoutButton.setVisibility(View.VISIBLE);
                            startActivity(new Intent(LoginActivity.this , MainActivity.class));
                        } else {
                            resultTextView.setText("Login falhou");
                            logoutButton.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void checkLoginStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String displayName = currentUser.getEmail();
            String status = "LOGADO";

            if (displayName != null) {
                status += ": " + displayName;
            }

            resultTextView.setText(status);
        } else {
            resultTextView.setText("NÃO LOGADO");
        }
    }

    private void logoutUser() {
        mAuth.signOut();
        emailEditText.setText("");
        passwordEditText.setText("");
        resultTextView.setText("");
        logoutButton.setVisibility(View.GONE);
    }
}
