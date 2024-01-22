package com.example.car_rental_client;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.car_rental_client.adapters.CarAdapter;
import com.example.car_rental_client.service.CarAPI;
import com.example.car_rental_client.service.CarModel;
import com.example.car_rental_client.utility.NetUtilities;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CarListFragment extends Fragment {
    RecyclerView recyclerView;
    BottomNavigationView navigationView;
    ProgressBar progressBar;

    String username;
    String sessionID;
    int userID;

    public CarListFragment() {
        // Required empty public constructor
    }

    public static CarListFragment newInstance() {
        CarListFragment fragment = new CarListFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            username = getArguments().getString("username");
            sessionID = getArguments().getString("sessionID");
            userID = getArguments().getInt("userID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_car_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progressBar);
        showLoading(true);

        recyclerView = view.findViewById(R.id.recycler_list);
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(llm);

        List<CarModel> resultCarList = new ArrayList<CarModel>();
        CarAdapter adapter = new CarAdapter(resultCarList);

        // Fetch data from api and update recycler view
        Retrofit retrofit = NetUtilities.CreateRetrofitObject();
        CarAPI carAPI = retrofit.create(CarAPI.class);

        Call<List<CarModel>> call = carAPI.getCars();
        call.enqueue(new Callback<List<CarModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<CarModel>> call, @NonNull Response<List<CarModel>> response) {
                assert response.body() != null;
                resultCarList.addAll(response.body());
                recyclerView.setAdapter(adapter);

                // Pass variables to the adapter
                // idk if there's a better way to do this oh pls i hate my janky implementations ._.
                CarAdapter.sessionID = sessionID;
                CarAdapter.username = username;
                CarAdapter.userID = userID;

                //Toast.makeText(getContext(), "Cars fetched successfully!", Toast.LENGTH_SHORT).show();
                showLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<CarModel>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error fetching data!", Toast.LENGTH_SHORT).show();
                showLoading(false);
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}