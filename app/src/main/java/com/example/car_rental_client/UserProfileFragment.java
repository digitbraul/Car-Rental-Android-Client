package com.example.car_rental_client;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserProfileFragment extends Fragment {
    ProgressBar progressBar;

    EditText passwordField;
    Button updatePasswordButton;
    Button signOutButton;
    TextView usernameTextView;
    TextView status;

    String username;
    String sessionID;

    public UserProfileFragment() {
        // Required empty public constructor
    }
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            username = getArguments().getString("username");
            sessionID = getArguments().getString("sessionID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        passwordField = view.findViewById(R.id.passwordInput);
        updatePasswordButton = view.findViewById(R.id.update_password_button);
        signOutButton = view.findViewById(R.id.signout_button);
        usernameTextView = view.findViewById(R.id.userIDText);
        status = view.findViewById(R.id.status);

        progressBar = view.findViewById(R.id.progressBar);
        showLoading(false);

        usernameTextView.setText(username);

        Retrofit retrofit = NetUtilities.CreateRetrofitObject();
        UserAPI userAPI = retrofit.create(UserAPI.class);

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: just put a text view here idk i don't wanna make another intent ._.
                // update: done, just go get some sleep pls
                String password = passwordField.getText().toString().trim();
                if (password.isEmpty()) {
                    Toast.makeText(getContext(), "New password cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                showLoading(true);

                Call<ResponseObject> call = userAPI.updatePassword(sessionID, username, password);
                call.enqueue(new Callback<ResponseObject>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseObject> call, @NonNull Response<ResponseObject> response) {
                        showLoading(false);
                        if (response.code() == 200) {
                            status.setText(response.body() != null ? response.body().getJSONMessage() : "Message body empty!");
                        } else {
                        Gson gson = new GsonBuilder().create();
                            try {
                                assert response.errorBody() != null;
                                ResponseObject errorResponse = gson.fromJson(response.errorBody().string(), ResponseObject.class);
                                Toast.makeText(getContext(), errorResponse.getJSONMessage(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseObject> call, @NonNull Throwable t) {
                        // let's cross our fingers w nchallah it won't fail
                        // we'll handle this when it actually fails i wanna sleeeeeeeepepepppepepepepepepeppepeeppepe
                        Toast.makeText(getContext(), "Error occurred!", Toast.LENGTH_SHORT).show();
                        Log.w("Connection Error", t);
                        showLoading(false);
                    }
                });
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading(true);
                Call<ResponseObject> call = userAPI.logout(sessionID);
                call.enqueue(new Callback<ResponseObject>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseObject> call, @NonNull Response<ResponseObject> response) {
                        showLoading(false);
                        if (response.code() == 200) {
                            status.setText(response.body() != null ? response.body().getJSONMessage() : "Message body empty!");
                            if (response.body().getJSONMessage().equals("Logged out!")) {
                                // since finish() may produce a NullPointerException, let's just intent our way to the login screen (:
                                Intent i = new Intent(getActivity(), LoginActivity.class);
                                try {
                                    requireActivity().startActivity(i);
                                } catch (NullPointerException e) {
                                    status.setText("Unable to start activity.");
                                }
                            } else {
                                Gson gson = new GsonBuilder().create();
                                try {
                                    assert response.errorBody() != null;
                                    ResponseObject errorResponse = gson.fromJson(response.errorBody().string(), ResponseObject.class);
                                    Toast.makeText(getContext(), errorResponse.getJSONMessage(), Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseObject> call, @NonNull Throwable t) {
                        // f*ck off im not handling this
                    }
                });
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}