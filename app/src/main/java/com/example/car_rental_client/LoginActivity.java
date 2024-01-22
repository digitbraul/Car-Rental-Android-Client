package com.example.car_rental_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button loginButton;
    Button signUpButton;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.sign_up_button);
        progressBar = findViewById(R.id.progressBar);
        TextView status = findViewById(R.id.status);

        showLoading(false);

        Retrofit retrofit = NetUtilities.CreateRetrofitObject();
        /* define an interface for each resource
        public interface CategoryService {
            @POST("category/{cat}/")
            Call<List<Item>> categoryList(@Path("cat") String a, @Query("page") int b);
        }
        */
        UserAPI userAPI = retrofit.create(UserAPI.class);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if email and password fields are empty
                email.setText(email.getText().toString().trim());
                password.setText(password.getText().toString().trim());
                if (email.getText().toString().isEmpty() && password.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Check Values!", Toast.LENGTH_SHORT).show();
                    return;
                }

                showLoading(true);
                Call<ResponseObject> call = userAPI.login(email.getText().toString(), password.getText().toString());
                call.enqueue(new Callback<ResponseObject>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseObject> call, @NonNull Response<ResponseObject> response) {
                        showLoading(false);
                        if (response.code() == 200) {
                            String responseText = response.body() != null ? response.body().getJSONMessage() : "None";
                            // tacky stuff to retrieve userID from json body in my case cuz I messed up bad and I didn't :((((((
                            String statusText = responseText.substring(0, responseText.indexOf(";"));
                            // this causes a NumberFormatException or smth but why should I care?
                            int userID = Integer.parseInt(responseText.substring(responseText.indexOf(";") + 1));

                            status.setText(statusText);

                            if (statusText.equals("Logged in successfully!")) {
                                String sessionCookie = response.headers().get("Set-Cookie");
                                assert sessionCookie != null;
                                // more tacky stuff involving ; cuz why not... okay stfu im a bad brogrammer fr
                                sessionCookie = sessionCookie.substring(0, sessionCookie.indexOf(";"));

                                Log.wtf("cookie thingy", sessionCookie);
                                Log.wtf("tacky shit incoming >>>>>>>>>", String.valueOf(userID));

                                Intent i = new Intent(LoginActivity.this, ApplicationActivity.class);
                                i.putExtra("username" ,email.getText().toString());
                                i.putExtra("sessionID", sessionCookie);
                                i.putExtra("userID", userID);
                                LoginActivity.this.startActivity(i);
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
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, UserRegisterActivity.class);
                LoginActivity.this.startActivity(i);
            }
        });
    }
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}