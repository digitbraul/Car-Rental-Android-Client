package com.example.car_rental_client.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.car_rental_client.R;
import com.example.car_rental_client.service.BookingAPI;
import com.example.car_rental_client.service.BookingModel;
import com.example.car_rental_client.service.ResponseObject;
import com.example.car_rental_client.utility.NetUtilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.MyViewHolder> {
    private List<BookingModel> bookingsList;
    public static String sessionID;
    public static int userID;

    public BookingsAdapter(List<BookingModel> bookingsList) {
        this.bookingsList = bookingsList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView bookingIDText;
        TextView carMakeText;
        TextView carModelText;
        TextView startDateText;
        TextView endDateText;

        CardView cardView;

        boolean performedAction;

        int bookingID;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            bookingID = -1;
            bookingIDText = itemView.findViewById(R.id.booking_id_text);
            carMakeText = itemView.findViewById(R.id.car_make_text);
            carModelText = itemView.findViewById(R.id.car_model_text);
            startDateText = itemView.findViewById(R.id.start_date_text);
            endDateText = itemView.findViewById(R.id.end_date_text);

            cardView = itemView.findViewById(R.id.card); // TODO: an on lick listener to view (and update / delete) booking

            Retrofit retrofit = NetUtilities.CreateRetrofitObject();
            BookingAPI bookingAPI = retrofit.create(BookingAPI.class);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View popupView = LayoutInflater.from(v.getContext()).inflate(R.layout.popup_update_booking, null);
                    final PopupWindow popupWindow = new PopupWindow(popupView,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            true);
                    popupWindow.setElevation(20);
                    popupWindow.setAnimationStyle(androidx.appcompat.R.style.Animation_AppCompat_Dialog);

                    // check if user actually did anything
                    performedAction = false;

                    // get xml views
                    EditText startDate = popupView.findViewById(R.id.start_date_field);
                    EditText endDate = popupView.findViewById(R.id.end_date_field);

                    Button updateBooking = popupView.findViewById(R.id.update_booking_button);
                    Button deleteBooking = popupView.findViewById(R.id.delete_booking_button);

                    // populate? ofc not why would we BAHAHAHAHAHA
                    // show the popup and listen for dismissal
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            if (!performedAction) {
                                Toast.makeText(v.getContext(), "Operation cancelled by user!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // update booking button on click listener
                    updateBooking.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // do the net stuff :) (it actually got the sessionID after some tacky maneuvering yay!)
                            // ye check the values first :p
                            if (startDate.getText().toString().isEmpty() && endDate.getText().toString().isEmpty()) {
                                Toast.makeText(v.getContext(), "No dates provided!", Toast.LENGTH_SHORT).show();
                            }
                            // format the date and create a date object from current timestamp for CHECKING BAHAHAHAHHAHAHAH
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy"); // the user inserts in this format
                            Date cDate = new Date();
                            Date convertedStartDate = new Date();
                            Date convertedEndDate = new Date();

                            // TODO: check dates :) what if the end date < start date? or do we travel backwards in time? heheh

                            // jsonify in this format
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat sqlDate = new SimpleDateFormat("yyyy-MM-dd");

                            try {
                                // T_T I'm trying to unit test but without unit testing ._. help
                                convertedStartDate = dateFormat.parse(startDate.getText().toString());
                                convertedEndDate = dateFormat.parse(endDate.getText().toString());
                            } catch (ParseException e) {
                                Toast.makeText(v.getContext(), "Invalid date format!", Toast.LENGTH_SHORT).show();
                            }

                            // if successful (idek at this point) we do the net stuff (forget it this won't work)
                            performedAction = true;

                            assert convertedStartDate != null;
                            assert convertedEndDate != null;

                            Call<ResponseObject> call = bookingAPI.updateBooking(sessionID, bookingID, sqlDate.format(convertedStartDate), sqlDate.format(convertedEndDate)); // pass dates as strings (my head hurts oof)
                            call.enqueue(new Callback<ResponseObject>() {
                                @Override
                                public void onResponse(@NonNull Call<ResponseObject> call, @NonNull Response<ResponseObject> response) {
                                    if (response.code() == 200) {
                                        Toast.makeText(v.getContext(), response.body() != null ? response.body().getJSONMessage() : "Message body empty!", Toast.LENGTH_SHORT).show();
                                        popupWindow.dismiss();
                                    } else {
                                        Gson gson = new GsonBuilder().create();
                                        try {
                                            assert response.errorBody() != null;
                                            ResponseObject errorResponse = gson.fromJson(response.errorBody().string(), ResponseObject.class);
                                            Toast.makeText(v.getContext(), errorResponse.getJSONMessage(), Toast.LENGTH_SHORT).show();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<ResponseObject> call, @NonNull Throwable t) {
                                    Toast.makeText(v.getContext(), "Error occurred!", Toast.LENGTH_SHORT).show();
                                    Log.w("Connection Error", t);
                                }
                            });
                        }
                    });
                    // delete booking button on click listener
                    deleteBooking.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            performedAction = true;

                            Call<ResponseObject> call = bookingAPI.deleteBooking(sessionID, bookingID);
                            call.enqueue(new Callback<ResponseObject>() {
                                @Override
                                public void onResponse(@NonNull Call<ResponseObject> call, @NonNull Response<ResponseObject> response) {
                                    if (response.code() == 200) {
                                        Toast.makeText(v.getContext(), response.body() != null ? response.body().getJSONMessage() : "Message body empty!", Toast.LENGTH_SHORT).show();
                                        popupWindow.dismiss();
                                    } else {
                                        Gson gson = new GsonBuilder().create();
                                        try {
                                            assert response.errorBody() != null;
                                            ResponseObject errorResponse = gson.fromJson(response.errorBody().string(), ResponseObject.class);
                                            Toast.makeText(v.getContext(), errorResponse.getJSONMessage(), Toast.LENGTH_SHORT).show();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<ResponseObject> call, @NonNull Throwable t) {
                                    Toast.makeText(v.getContext(), "Error occurred!", Toast.LENGTH_SHORT).show();
                                    Log.w("Connection Error", t);
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_row, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingsAdapter.MyViewHolder holder, int position) {
        BookingModel booking = bookingsList.get(position);

        holder.bookingID = booking.getBookingID();
        holder.bookingIDText.setText("Booking ID: " + holder.bookingID);
        holder.carMakeText.setText(booking.getBookedCar().getCarMake());
        holder.carModelText.setText(booking.getBookedCar().getCarModel());
        holder.startDateText.setText(booking.getStartDate());
        holder.endDateText.setText(booking.getEndDate());
        // TODO: idk
    }

    @Override
    public int getItemCount() {
        return bookingsList.size();
    }
}
