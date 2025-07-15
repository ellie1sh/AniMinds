package com.uilover.project2252.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.uilover.project2252.R;

public class LoginActivity extends AppCompatActivity {

    TextView btnSignUp;
    EditText inputPasswordLogin, inputEmailLogin;
    Button btnLogin;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        btnSignUp = findViewById(R.id.btn_SignUp);
        inputEmailLogin = findViewById(R.id.inputEmailLogin);
        inputPasswordLogin = findViewById(R.id.inputPasswordLogin);
        btnLogin = findViewById(R.id.LoginBtn);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStore = FirebaseFirestore.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAuth();
            }
        });
    }

    private void performAuth() {
        String email = inputEmailLogin.getText().toString();
        String password = inputPasswordLogin.getText().toString();

        if (!email.matches(emailPattern)) {
            inputEmailLogin.setError("Invalid email format");
        } else if (password.isEmpty() || password.length() < 7) {
            inputPasswordLogin.setError("Password must be at least 7 characters");
        } else {
            progressDialog.setMessage("Please wait while checking credentials.");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        checkUserType(task.getResult().getUser().getUid());
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void checkUserType(String uid) {
        DocumentReference df = mStore.collection("Users").document(uid);
        df.get().addOnSuccessListener(documentSnapshot -> {
            Log.d("TAG", "onSuccess: " + documentSnapshot.getData());

            if (documentSnapshot.getString("isBuyer") != null) {
                sendUserToBuyerActivity();
                finish();
            } else if (documentSnapshot.getString("isSeller") != null) {
                if (documentSnapshot.getString("isSeller").equals("1")) {
                    String company = documentSnapshot.getString("FarmName");
                    String email = documentSnapshot.getString("Email");
                    String number = documentSnapshot.getString("PhoneNumber");
                    String password = documentSnapshot.getString("Password");
                    sendUserToSellerActivity(company, email, number, password);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Seller account pending approval. Please wait for admin approval.",
                            Toast.LENGTH_LONG).show();
                }
            } else if (documentSnapshot.getString("isAdmin") != null) {
                sendUserToAdminActivity();
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "User type not recognized", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendUserToBuyerActivity() {
       Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void sendUserToSellerActivity(String company, String email, String number, String password) {
        Intent intent = new Intent(LoginActivity.this, SellerProfileActivity.class);
        intent.putExtra("company", company);
        intent.putExtra("email", email);
        intent.putExtra("number", number);
        intent.putExtra("password", password);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendUserToAdminActivity(){
        Intent intent = new Intent(LoginActivity.this, AdminSellerApplicationsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showError(EditText input, String message) {
        input.setError(message);
        input.requestFocus();
    }
}
