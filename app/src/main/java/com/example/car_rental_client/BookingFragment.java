package com.example.car_rental_client;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.car_rental_client.adapters.BookingsAdapter;
import com.example.car_rental_client.service.BookingAPI;
import com.example.car_rental_client.service.BookingModel;
import com.example.car_rental_client.utility.NetUtilities;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingFragment extends Fragment {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    String username;
    String sessionID;
    int userID;

    public BookingFragment() {
        // Required empty public constructor
    }
    public static BookingFragment newInstance() {
        BookingFragment fragment = new BookingFragment();
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
        return inflater.inflate(R.layout.fragment_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progressBar);
        showLoading(true);

        recyclerView = view.findViewById(R.id.recycler_list);
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(llm);

        List<BookingModel> resultBookingList = new ArrayList<BookingModel>();
        BookingsAdapter adapter = new BookingsAdapter(resultBookingList);

        // Fetch data from api and update recycler view
        Retrofit retrofit = NetUtilities.CreateRetrofitObject();
        BookingAPI bookingAPI = retrofit.create(BookingAPI.class);

        Call<List<BookingModel>> call = bookingAPI.getBookingsForUserID(userID);
        call.enqueue(new Callback<List<BookingModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<BookingModel>> call, @NonNull Response<List<BookingModel>> response) {
                assert response.body() != null;
                resultBookingList.addAll(response.body());
                recyclerView.setAdapter(adapter);

                // TODO: pass variables to the adapter here too
                BookingsAdapter.sessionID = sessionID;
                showLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<BookingModel>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error fetching data!", Toast.LENGTH_SHORT).show();
                showLoading(false);
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}