package com.example.car_rental_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.car_rental_client.service.ResponseObject;
import com.example.car_rental_client.service.UserAPI;
import com.example.car_rental_client.utility.NetUtilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserRegisterActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    EditText passwordCheck;
    Button registerButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        username = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        passwordCheck = findViewById(R.id.password_checkInput);
        registerButton = findViewById(R.id.register_button);

        TextView status = findViewById(R.id.status);

        progressBar = findViewById(R.id.progressBar);
        showLoading(false);

        // TODO put this bit in a separate class and instantiate it whenever you need to use Retrofit
        Retrofit retrofit = NetUtilities.CreateRetrofitObject();

        UserAPI userAPI = retrofit.create(UserAPI.class);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if fields are empty
                username.setText(username.getText().toString().trim());
                password.setText(password.getText().toString().trim());
                passwordCheck.setText(passwordCheck.getText().toString().trim());
                if (username.getText().toString().isEmpty() && password.getText().toString().isEmpty() && passwordCheck.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Check Values!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if password check doesn't match actual password
                if (!password.getText().toString().equals(passwordCheck.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Passwords don't match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                showLoading(true);
                Call<ResponseObject> call = userAPI.signUp(username.getText().toString(), password.getText().toString());
                call.enqueue(new Callback<ResponseObject>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseObject> call, @NonNull Response<ResponseObject> response) {
                        showLoading(false);
                        if (response.code() == 200) {
                            status.setText(response.body() != null ? response.body().getJSONMessage() : "Message body empty!");
                            if (response.body().getJSONMessage().equals("User added successfully!")) {
                                UserRegisterActivity.this.finish();
                            }
                        } else {
                            Gson gson = new GsonBuilder().create();
                            try {
                                assert response.errorBody() != null;
                                ResponseObject errorResponse = gson.fromJson(response.errorBody().string(), ResponseObject.class);
                                Toast.makeText(getApplicationContext(), errorResponse.getJSONMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseObject> call, @NonNull Throwable t) {
                        showLoading(false);
                        status.setText(t.getMessage());
                    }
                });
            }
        });

    }
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}